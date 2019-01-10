package com.iu3.mytexteditor;

import com.iu3.mytexteditor.model.BMSUDocument;
import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.IndexRange;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class MainController implements Initializable {

    static Stage STAGE;

    BMSUDocument document;

    private TextArea textArea;
    private TableView tableArea;

    @FXML
    private BorderPane borderPane;

    @FXML
    private void setBoldText(ActionEvent event){
        if (textArea != null){
            IndexRange range = textArea.getSelection();
            
    }
    }
    @FXML
    private void openFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Открыть файл");

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Documents", "*.doc", "*.docx", "*.xls", "*.xlsx", "*.txt"),
                new FileChooser.ExtensionFilter("Doc", "*.doc"),
                new FileChooser.ExtensionFilter("Docx", "*.docx"),
                new FileChooser.ExtensionFilter("Xls", "*.xls"),
                new FileChooser.ExtensionFilter("Xlsx", "*.xlsx"),
                new FileChooser.ExtensionFilter("Txt", "*.txt")
        );
        File file = fileChooser.showOpenDialog(STAGE);
        document = new BMSUDocument(file.getAbsolutePath());
        if (!document.isWorkTable()) {
            textArea = new TextArea();
            textArea.appendText(document.getText());

            borderPane.setCenter(textArea);
        } else {
            tableArea = new TableView();
            List<String[]> tables = document.getTables();
            
            int cellNum = 0;
            for (String[] rowValue : tables) {
                for (; cellNum < rowValue.length; cellNum++) {
                    final int colNum = cellNum;
                    TableColumn col = new TableColumn(colNum + "");
                    col.setCellValueFactory(p -> {
                        return new SimpleStringProperty(((CellDataFeatures<String[], String>) p).getValue()[colNum]);
                    });
                    tableArea.getColumns().add(col);
                }
            };
            tableArea.setItems(FXCollections.observableArrayList(tables));
            

            borderPane.setCenter(tableArea);
        }

    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }
}
