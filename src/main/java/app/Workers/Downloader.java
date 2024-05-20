package app.Workers;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class Downloader extends Thread {

    public void run() {
        System.out.println("Downloader thread started");
    }



    public void download (String videoId, String downloadDir) {

        try {

            String execPath = "yt-dlp/yt-dlp.exe";
            String arg1 = "-f";
            String arg2 = "140";

            String yt = "https://www.youtube.com/watch?v=";
            String url = yt + videoId;

            ProcessBuilder pb = new ProcessBuilder(execPath, arg1, arg2, url);

            pb.directory(new File(downloadDir));

            pb.redirectErrorStream(true);

            Process process = pb.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
//                System.out.println(line);
                if (line.contains("[download]")) {
                    System.out.println(line);
                }
            }

            int exitCode = process.waitFor();

            System.out.println("Downloaded: " + url + " Exit code: " + exitCode);

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }



    }


}
