package com.dutchaen.kitjson;

import com.google.gson.*;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.stage.FileChooser;

import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.ResourceBundle;

public class KitController implements Initializable {
    @FXML
    private Label welcomeText;

    @FXML
    private TabPane mainTabPane;

    @FXML
    private Button copyJsonFromClipboardButton;

    @FXML
    private Button copyJsonFromClipboardButton2;

    @FXML
    private Button searchButton;

    @FXML
    private Button exportResultsButton;

    @FXML
    private Label statusOfCopyLabel;

    @FXML
    private Label statusOfCopyLabel2;

    @FXML
    private ListView<String> listOfProgrammingLanguagesListView;

    @FXML
    private ListView<String> foundPathsListView;

    @FXML
    private TextArea structTextArea;

    @FXML
    private TextField rootNameTextField;

    @FXML
    private TextField valueTextField;

    @FXML
    private Hyperlink githubHyperlink;

    private JsonElement copiedJsonElement;
    private HashMap<String, String> searchValueResults;



    @FXML
    protected void onCopyJsonFromClipboardButtonClick() {
        String clipboardText = Clipboard.getSystemClipboard().getString();

        try {
            var jsonElement = JsonParser.parseString(clipboardText);
            if (!jsonElement.isJsonArray() && !jsonElement.isJsonObject()) {
                copiedJsonElement = null;
                searchValueResults = null;
                statusOfCopyLabel.setText("Status: JSON invalid.");
                statusOfCopyLabel2.setText("Status: JSON invalid.");
                structTextArea.setText("");
                foundPathsListView.getItems().clear();
                return;
            }

            copiedJsonElement = jsonElement;
            statusOfCopyLabel.setText("Status: JSON copied!");
            statusOfCopyLabel2.setText("Status: JSON copied!");

        }
        catch (JsonSyntaxException syntaxException) {
            copiedJsonElement = null;
            searchValueResults = null;
            statusOfCopyLabel.setText("Status: JSON invalid.");
            statusOfCopyLabel2.setText("Status: JSON invalid.");
            structTextArea.setText("");
            foundPathsListView.getItems().clear();
            return;
        }
    }

    @FXML
    protected void onListOfProgrammingLanguagesListView_MouseClicked() {
        if (copiedJsonElement == null) {
            structTextArea.setText("");
            return;
        }

        String rootNameText = rootNameTextField.getText();
        if (rootNameText.isEmpty()) {
            structTextArea.setText("ⓘ Please enter a name for the root structure.");
            return;
        }

        String selectedLanguageText = listOfProgrammingLanguagesListView
                .getSelectionModel()
                .getSelectedItem();

        structTextArea.setText("You selected '" + selectedLanguageText + "' as your language.");


        String parsedText = "";

        switch (selectedLanguageText) {
            case "Go":
                parsedText = StructParser.parseGo(copiedJsonElement, rootNameText, null, null);
                break;
            case "Rust":
                parsedText = StructParser.parseRust(copiedJsonElement, rootNameText, null, null);
                break;
            case "C#":
                parsedText = StructParser.parseCSharp(copiedJsonElement, rootNameText, null, null);
                break;
            default:
                break;
        }

        structTextArea.setText(parsedText);
    }

    @FXML
    protected void searchButton_OnAction() {
        if (copiedJsonElement == null) {
            return;
        }

        foundPathsListView.getItems().clear();

        String value = valueTextField.getText();
        searchValueResults = JsonSearcher.findValue(copiedJsonElement, value);

        foundPathsListView.getItems().addAll(searchValueResults.keySet());
    }

    @FXML
    protected void exportResultsButton_OnAction() {


        if (searchValueResults == null || searchValueResults.isEmpty()) {
            var alert = new Alert(Alert.AlertType.ERROR, "", ButtonType.OK);
            alert.setHeaderText("Cannot Export");
            alert.setContentText("No search results found");
            alert.setOnCloseRequest(e -> alert.hide());
            alert.show();
            return;
        }

        var gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();

        var window = exportResultsButton.getScene().getWindow();

        var cwd = new File(System.getProperty("user.dir"));

        var fileChooser = new FileChooser();
        fileChooser.setInitialFileName("export.json");
        fileChooser.setInitialDirectory(cwd);
        fileChooser.setTitle("Save search results export");

        File exportResultFile = fileChooser.showSaveDialog(window);
        if (exportResultFile == null) {
            return;
        }

        var text = gson.toJson(searchValueResults)
                .replace("\\u0027", "'");

        if (exportResultFile.exists()) {
            exportResultFile.delete();
        }

        try {
            exportResultFile.createNewFile();
            var fileWriter = new FileWriter(exportResultFile, StandardCharsets.UTF_8);
            fileWriter.write(text);
            fileWriter.close();

            var alert = new Alert(Alert.AlertType.NONE,"", ButtonType.OK);
            alert.setHeaderText("Success");
            alert.setContentText("Successfully exported search results to a file.");
            alert.setOnCloseRequest(e -> alert.hide());
            alert.show();
        }
        catch (Exception ignored) {
            var alert = new Alert(Alert.AlertType.ERROR, "", ButtonType.OK);
            alert.setHeaderText("Cannot Export");
            alert.setContentText("An exception occured while trying to write to the export file.");
            alert.setOnCloseRequest(e -> alert.hide());
            alert.show();
        }

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        listOfProgrammingLanguagesListView.getItems()
                .addAll("Go", "Rust", "C#");
        listOfProgrammingLanguagesListView.setEditable(false);

        foundPathsListView.setCellFactory(view -> {
            ListCell<String> cell = new ListCell<>();
            ContextMenu contextMenu = new ContextMenu();

            MenuItem copyItem = new MenuItem();
            copyItem.textProperty().bind(Bindings.concat("Copy"));
            copyItem.setOnAction(e -> {
                var path = cell.getItem();
                var content = new ClipboardContent();
                content.put(DataFormat.PLAIN_TEXT, path);
                Clipboard.getSystemClipboard().setContent(content);
            });
            contextMenu.getItems().add(copyItem);

            cell.textProperty().bind(cell.itemProperty());

            cell.emptyProperty().addListener((obs, wasEmpty, isNowEmpty) -> {
                if (isNowEmpty) {
                    cell.setContextMenu(null);
                } else {
                    cell.setContextMenu(contextMenu);
                }
            });

            return cell;
        });

        githubHyperlink.setOnAction(e -> {
            try {
                Desktop.getDesktop().browse(new URI("https://www.github.com/dutchaen"));
            }
            catch (Exception ignored) {}
        });
    }
}