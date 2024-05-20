package app.DataStructures;

import javafx.application.Platform;
import javafx.scene.control.TextField;

import java.io.PrintStream;

public class SystemOutPassThrough extends PrintStream {
    private final TextField statusText;

    public SystemOutPassThrough (TextField statusText) {
        super(System.out);
        this.statusText = statusText;
    }

    @Override
    public void write(byte[] buf, int off, int len) {
        final String message = new String(buf, off, len);
        Platform.runLater(() -> statusText.setText(message));
//        statusText.setText(message);
    }
}
