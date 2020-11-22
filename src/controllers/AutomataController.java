package controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert.*;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.StageStyle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.net.URL;
import java.util.*;
import java.util.stream.Stream;

public class AutomataController implements Initializable {

    private String[] words;
    private boolean finish;
    private HashMap<String,String> keywords;
    private HashMap<String,String> symbols;

    @FXML
    private TextArea inputTextArea;

    @FXML
    private Button buttonPlay;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        addImageToButton();
    }

    @FXML
    public void beginVerification(MouseEvent event) {
        String title = "",content = "";
        boolean error = false;
        AlertType alertType = null;
        String text = inputTextArea.getText();
        if( !(text.isEmpty() || text.isBlank()) ){
            verifyInput(text);
        } else {
            title = "ERROR";
            content = "Por favor, no deje el campo en blanco.";
            alertType = AlertType.ERROR;
            error = true;
        }
        if(error){
            showAlert(title,content,alertType);
        }
    }

    public void addImageToButton(){
        Image image = new Image("/images/play_icon.png",26,26,true,true);
        buttonPlay.setGraphic( new ImageView(image) );
    }

    public void verifyInput(String input){
        //TODO
        showAlert("AÚN NO HAY NADA.", "Aún no se ha implementado ninguna funcionalidad!",AlertType.ERROR);
    }

    public void showAlert(String title, String content, AlertType alertType){
        Alert alert = new Alert(alertType);
        alert.setHeaderText(null);
        alert.setTitle(title);

        TextArea area = new TextArea(content);
        area.setWrapText(true);
        area.setEditable(false);

        alert.getDialogPane().setContent(area);
        alert.getDialogPane().getContent().setStyle("-fx-background-color: transparent;");
        alert.setResizable(true);
        alert.initStyle(StageStyle.UTILITY);
        alert.showAndWait();
    }
}
