package app.DataStructures;

import app.MainController;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.ListChangeListener;
import javafx.concurrent.Task;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DataItem {
    private MainController controllerListener;

    private final IntegerProperty numberProperty;
    private final StringProperty titleProperty;
    private final StringProperty urlProperty;
    private final ObjectProperty<ImageView> imageViewProperty;
    private final IntegerProperty playCountProperty;
    private final ObjectProperty<Button> buttonProperty;

    public DataItem(int number, String title, String url, String imageUrl, int playCount, MainController controllerListener) {
        this.numberProperty = new SimpleIntegerProperty(number);
        this.titleProperty = new SimpleStringProperty(title);
        this.urlProperty = new SimpleStringProperty(url);

        this.imageViewProperty = new SimpleObjectProperty<>(loadImage(imageUrl));
        // load image task
        Task<ImageView> loadImageTask;

        this.playCountProperty = new SimpleIntegerProperty(playCount);
        this.buttonProperty = new SimpleObjectProperty<>(new Button("Download this only"));

        // Add button functionality here
        this.buttonProperty.get().setOnAction(event -> buttonAction());

        this.controllerListener = controllerListener;
    }

    private ImageView loadImage(String imageUrl) {
        try {

//            System.out.println("fetching image... " + imageUrl);

//            String message = "Progress: " + (i + 1) * 10 + "%";
//            Platform.runLater(() -> statusText.setText(message));

            URL url = new URL(imageUrl);
            InputStream stream = url.openStream();
            Image image = new Image(stream, 500, 100, true, true);
            return new ImageView(image);

//            return new ImageView();
        } catch (Exception e) {
//            e.printStackTrace();
            System.out.println("cannot fetch thumbnail: " + imageUrl);
            return new ImageView(); // Return empty ImageView if image loading fails
        }
    }

    // Task for loading the images (TODO):

//    private static final ExecutorService executorService = Executors.newFixedThreadPool(5);

//    private ImageView loadImage(String imageUrl) {
//        try {
//            // Create a Task for loading the image
//            Task<ImageView> loadImageTask = new Task<>() {
//                @Override
//                protected ImageView call() throws Exception {
//                    URL url = new URL(imageUrl);
//                    InputStream stream = url.openStream();
//                    Image image = new Image(stream, 500, 100, true, true);
//                    return new ImageView(image);
//
//                    // tutaj powinien byc update na tableview
//                }
//            };
//
//            // Start the task asynchronously
//            executorService.submit(loadImageTask);
//            update();
//
//            // Return a placeholder ImageView while the image is being loaded
//            return new ImageView();
//        } catch (Exception e) {
//
////            System.out.println("Cannot fetch thumbnail: " + imageUrl);
//            return new ImageView(); // Return empty ImageView if image loading fails
//        }
//
//
//
//
//    }

    public void update() {
        // Listen for changes in the items list of the table view
        controllerListener.getTableView().getItems().addListener((ListChangeListener<DataItem>) c -> {
            while (c.next()) {
                if (c.wasAdded()) {
                    c.getAddedSubList().forEach(item -> {
                        if (item.getImageViewProperty().get().getImage() != null) {
                            // If the image is already loaded, update the cell graphic
                            item.getImageViewProperty().get().setFitWidth(100); // Set appropriate width
                            item.getImageViewProperty().get().setFitHeight(100); // Set appropriate height
                            // Update the cell graphic
                            controllerListener.getTableView().refresh(); // Refresh the table view to reflect changes
                        }
                    });
                }
            }
        });
    }





    public IntegerProperty getNumberProperty() {
        return numberProperty;
    }
    public StringProperty getTitlePropertyProperty () {
        return titleProperty;
    }
    public StringProperty getUrlPropertyProperty () {
        return urlProperty;
    }
    public ObjectProperty<ImageView> getImageViewProperty () {
        return imageViewProperty;
    }
    public IntegerProperty getPlayCountProperty() {
        return playCountProperty;
    }
    public ObjectProperty<Button> getButtonProperty () {
        return buttonProperty;
    }



    public void buttonAction() {
        System.out.println("Downloading: " + titleProperty.get());
        controllerListener.downloadOne(urlProperty.get());
    }




}

