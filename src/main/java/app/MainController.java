package app;

import app.DataStructures.DataItem;
import app.DataStructures.JsonEntry;
import app.Workers.Downloader;
import app.Workers.JsonReader;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;


import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.concurrent.atomic.AtomicInteger;


public class MainController {

    @FXML
    public TextField statusText;


    @FXML
    private TextField jsonPathField;

    private String downloadDir;
    @FXML
    private TextField downloadDirField;

    @FXML
    private TextField numberOfEntriesField;

    // Table View
    @FXML
    private TableView<DataItem> tableView;

    public TableView<DataItem> getTableView () {
        return tableView;
    }

    @FXML
    private TableColumn<DataItem, Integer> number;
    @FXML
    private TableColumn<DataItem, String> Title;
    @FXML
    private TableColumn<DataItem, String> URL;
    @FXML
    private TableColumn<DataItem, ImageView> Thumbnail;
    @FXML
    private TableColumn<DataItem, Integer> playCount;
    @FXML
    private TableColumn<DataItem, Button> buttonCol;

    private boolean readYT = true;
    private boolean readMusic = true;
    private String jsonPath;
    private LinkedHashMap<JsonEntry, Integer> historyEntries;
    private LinkedHashMap<JsonEntry, Integer> combinedEntries;



    @FXML
    protected void chooseJsonFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open YouTube History JSON File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON Files", "*.json"));
        File selectedFile = fileChooser.showOpenDialog(new Stage());

        if (selectedFile != null) {
            jsonPath = selectedFile.getAbsolutePath();
            jsonPathField.setText(jsonPath);

            // auto load the JSON file
            loadJsonFile();

        } else {
            jsonPathField.setText("No file selected");
        }

    }


    // Load the JSON file
    protected void loadJsonFile() {

        if (jsonPath == null) {
            System.out.println("No JSON file selected");
            return;

            // For testing purposes
//            jsonPath = "D:\Downloads\historia.json";
        } else {
            System.out.println("Reading JSON file...");
        }

        JsonReader jsonReader = new JsonReader();
        historyEntries = jsonReader.readJson(jsonPath);
        combinedEntries = jsonReader.getCombinedEntries(historyEntries);

        if (!historyEntries.isEmpty()) {
//            System.out.println("Done reading JSON file, " + historyEntries.size() + " entries loaded.");

            Platform.runLater(() -> statusText.setText("Done reading JSON file, " + historyEntries.size() + " entries loaded."));
        } else {
            System.out.println("No entries found in the JSON file");
        }


        // auto populate table view
        populateTableView();

    }

    @FXML
    protected void chooseDirectory() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Choose Download Directory");
        File selectedDirectory = directoryChooser.showDialog(new Stage());

        if (selectedDirectory != null) {
            Path selectedDirectoryPath = selectedDirectory.toPath();
            downloadDir = selectedDirectoryPath.toString();
            downloadDirField.setText(downloadDir);
        } else {
            downloadDirField.setText("No directory selected");
        }
    }


    // Populate the table view
    @FXML
    protected void populateTableView() {

        if (jsonPath == null) {
            System.out.println("No JSON file selected");
            return;
        }

        tableView.getItems().clear();
        System.out.println("clearing table view");

        number.setCellValueFactory(cellData -> cellData.getValue().getNumberProperty().asObject());
        Title.setCellValueFactory(cellData -> cellData.getValue().getTitlePropertyProperty());
        URL.setCellValueFactory(cellData -> cellData.getValue().getUrlPropertyProperty());
        Thumbnail.setCellValueFactory(cellData -> cellData.getValue().getImageViewProperty());
        playCount.setCellValueFactory(cellData -> cellData.getValue().getPlayCountProperty().asObject());
        buttonCol.setCellValueFactory(cellData -> cellData.getValue().getButtonProperty());

        // Get the number of entries to display - default is 10
        int numberOfEntries = Integer.parseInt(numberOfEntriesField.getText());

        // custom filter
        String filter;

        // only youtube.com entries
        if (readYT && !readMusic) {
            filter = "https://www.youtube.com";
        }
        // only music.youtube.com entries
        else if (!readYT && readMusic) {
            filter = "https://music.youtube.com";
        }
        // all entries - use the combinedEntries map for combined play count, which stores raw video IDs
        else if (readYT && readMusic) {

//            Platform.runLater(() -> statusText.setText("From yt and ytmusic, " + combinedEntries.size() + " entries loaded."));

            // atomic integer to keep track of the number of entries
            AtomicInteger number = new AtomicInteger(1);

            combinedEntries.entrySet().stream()
                    .limit(numberOfEntries)
                    .forEach(e -> {

                        JsonEntry entry = e.getKey();
                        int playCount = e.getValue();

                        addTableRow(entry, playCount, number.get(), true);

                        // Increment the number
                        number.getAndIncrement();
                    });

            System.out.println("Done populating table view");
            return;
        }
        else {
            System.out.println("No source selected");
            return;
        }

        // atomic integer to keep track of the number of entries
        AtomicInteger number = new AtomicInteger(1);

        historyEntries.entrySet().stream()
                .filter(e -> e.getKey().getUrl().contains(filter))
                .limit(numberOfEntries)
                .forEach(e -> {

                    JsonEntry entry = e.getKey();
                    int playCount = e.getValue();

                    addTableRow(entry, playCount, number.get(), false);

                    // Increment the number
                    number.getAndIncrement();
                });

    }



    private void addTableRow (JsonEntry entry, int playCount, int number, boolean rawVideoId) {
        String title = entry.getTitle();
        String videoId = entry.getUrl();

        if (!rawVideoId) {
            videoId = videoId.substring(videoId.length() - 11);
        }

        /*
        Width	Height	URL
        640	    480	    https://i.ytimg.com/vi//sd1.jpg
        640	    480	    https://i.ytimg.com/vi//sd2.jpg
        640	    480	    https://i.ytimg.com/vi//sd3.jpg
        640	    480	    https://i.ytimg.com/vi//sddefault.jpg
        1280	720	    https://i.ytimg.com/vi//hq720.jpg
        1920	1080	https://i.ytimg.com/vi//maxresdefault.jpg
        */

        String imageUrl = "https://img.youtube.com/vi/" + videoId + "/hq720.jpg";


        DataItem item = new DataItem(number, title, videoId, imageUrl, playCount, this);
        // konstruktor DataItem odpala taska, ktory pobiera obrazek
        tableView.getItems().add(item);
    }

    @FXML
    public void downloadOne (String videoId) {

        if (downloadDir == null) {
            System.out.println("Please specify the download path first.");
            return;
        }

        // Download one entry
        statusText.setText("Downloading: " + videoId);
        Downloader downloader = new Downloader();
        downloader.download(videoId, downloadDir);

    }

    @FXML
    protected void downloadAll () {

        if (downloadDir == null) {
            System.out.println("Please specify the download path first.");
            return;
        }

        if (jsonPath == null) {
            System.out.println("No JSON file selected");
            return;
        }

        if (tableView.getItems().isEmpty()) {
            System.out.println("No entries to download.");
            return;
        }

        // Download all the entries in the table view
        Downloader downloader = new Downloader();
        for (DataItem item : tableView.getItems()) {
            downloader.download(item.getUrlPropertyProperty().get(), downloadDir);
        }

    }


    public void checkYT() {
        readYT = !readYT;
//        System.out.println("YT: " + readYT);
    }

    public void checkMusic() {
        readMusic = !readMusic;
//        System.out.println("Music: " + readMusic);
    }


    public void openLink (ActionEvent actionEvent) {
        try {
            java.awt.Desktop.getDesktop().browse(new URI("https://takeout.google.com/?continue=https://myaccount.google.com/dashboard&hl=pl"));
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}