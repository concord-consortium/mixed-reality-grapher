package org.concord.mixedreality;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

/**
 *
 * @author Saeid Nourian <snourian@concord.org>
 */
public class MainWindowController implements Initializable {

    private final FileChooser fileChooser = new FileChooser();

    @FXML
    private VBox chartsPanel;

    @Override
    public void initialize(final URL url, final ResourceBundle rb) {
        fileChooser.setTitle("Open File");
        fileChooser.getExtensionFilters().addAll(
                new ExtensionFilter("Text Files", "*.txt"),
                new ExtensionFilter("All Files", "*.*"));
    }

    @FXML
    private void handleFileOpenAction(final ActionEvent event) {
        event.consume();
        final File file = fileChooser.showOpenDialog(MainApplication.getStage());
        if (file != null) {
            fileChooser.setInitialDirectory(file.getParentFile());
            Charts.createCharts(file, chartsPanel);
        }
    }

    @FXML
    private void handleFileExit(final ActionEvent event) {
        System.exit(0);
    }

}
