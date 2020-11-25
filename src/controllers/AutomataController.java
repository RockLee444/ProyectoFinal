package controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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

public class AutomataController implements Initializable {

    @FXML
    private TextArea inputTextArea;

    @FXML
    private Button buttonPlay;


    private HashMap<String, String> wordsMap;
    private HashMap<String,String> conditionSymbolsMap;

    private String currentState;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        wordsMap = new HashMap<>();
        conditionSymbolsMap = new HashMap<>();
        fillMap();
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
        boolean finished = false, error = false;
        int position = 0;
        currentState = "q0";
        String[] inputArray = input.split("\n");

        if(!principal(inputArray)){
            error = true;
        }

        if(!error){
            showAlert("HECHO", "Todo salió bien :)",AlertType.CONFIRMATION);
        } else {
            showAlert("ERROR", "Hay un error!",AlertType.ERROR);
        }
    }

    public boolean principal(String[] text){
        boolean isValid = true, recursive = false;
        int position = 1, newPosition = 0;

        if(!text[0].equals("program(){")){
            isValid = false;
        }

        while(position < text.length -1){
            String currentData = text[position], result = "";
            String[] resultArray = null;
            //Verifying which instruction it is...
            if(currentData.contains("data")){

            } else if(currentData.contains("enter")){
                //result = enter();
            } else if(currentData.contains("condition")){
                result = condicion(text, position);
                resultArray = result.split(" ");
                recursive = Boolean.parseBoolean(resultArray[0]);
                position = Integer.parseInt(resultArray[1]);
            } else if(currentData.contains("ignore")){

            } else if(currentData.contains("output")){

            } else if(currentData.isEmpty() || currentData.isBlank()){
                recursive = true;
            } else {
                isValid = false;
            }
            position++;
        }

        if(position < text.length) {
            if (!text[position].equals("}") || !(isValid && recursive)) {
                isValid = false;
            }
        } else {
            if(!(isValid && recursive)){
                isValid = false;
            }
        }

        return isValid;
    }

    public String condicion(String[] text, int position){
        boolean isValid = true, recursive = false,finished = false;
        String result = "";
        int iteration = 0;

        //Evaluating conditions inside parentheses
        for(int i=0;i<2;i++){
            switch (i){
                case 0:
                    System.out.println("SUBSTRING: " + text[position].substring(0,10));
                    if(text[position].substring(0,10).equals("condition(")){
                        isValid = true;
                    }
                break;

                case 1:
                    if(isValid){
                        int j=0;
                        String[] conditions = text[position].substring(10).split(" ");
                        while(!finished){
                            if(j + 3 < conditions.length && !(conditions[j].isBlank() || conditions[j].isEmpty())){
                                if(iteration == 0) {
                                    Pattern identifier = Pattern.compile("(^[a-zA-Z_]+[\\w]*|[\\d]+)$");
                                    Matcher verified = identifier.matcher(conditions[j]);
                                    if (verified.find()) {
                                        j++;
                                        if (conditionSymbolsMap.get(conditions[j]) != null) {
                                            j++;
                                            verified = identifier.matcher(conditions[j]);
                                            if (!verified.find()) {
                                                isValid = false;
                                            }
                                            iteration++;
                                        } else {
                                            isValid = false;
                                        }

                                    } else {
                                        isValid = false;
                                    }
                                    j++;
                                } else {
                                    if( !(conditions[j].equals("||") || conditions[j].equals("&&")) ){
                                        isValid = false;
                                    }
                                    iteration = 0;
                                    j++;
                                }
                            } else if(conditions[j].isBlank() || conditions[j].isEmpty()){
                                j++;
                            } else {
                                finished = true;
                            }
                        }
                        if(conditions[j].equals(")")){
                            j++;
                            if(!conditions[j].equals("{")) {
                                isValid = false;
                            }
                        } else {
                            isValid = false;
                        }
                    }
                break;
            }
        }
        //END OF CONDITION EVALUATION

        //EVALUATING CONTENT INSIDE IT
        position++;
        String currentData = text[position];

        String[] contentResult = contenido(text, position).split(" ");
        recursive = Boolean.parseBoolean(contentResult[0]);
        position = Integer.parseInt(contentResult[1]);

        if(text[position].equals("}")){
            position++;
        } else {
            isValid = false;
        }

        if(!(isValid && recursive)){
            isValid = false;
        }

        //TODO Add fail

        result = isValid + " " + position;
        return result;
    }

    public String contenido(String[] text, int position){
        String currentData = text[position],result = "";
        boolean isValid = true, recursive = false;

        if(currentData.contains("data")){

        } else if(currentData.contains("enter")){
            //result = enter();
        } else if(currentData.contains("condition")){
            result = condicion(text, position);
            String[] resultArray = result.split(" ");
            recursive = Boolean.parseBoolean(resultArray[0]);
            position = Integer.parseInt(resultArray[1]);
        } else if(currentData.contains("ignore")){

        } else if(currentData.contains("output")){

        } else if(currentData.isEmpty() || currentData.isBlank()){
            position++;
            recursive = true;
        } else {
            isValid = false;
        }

        if(!(isValid && recursive)){
            isValid = false;
        }

        return isValid + " " + position;
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

    private void fillMap(){
        wordsMap.put("data","q2");
        wordsMap.put("contenido","q6");
        wordsMap.put("declaracion","q7");
        wordsMap.put("asignación","q8");
        wordsMap.put("condición","q9");
        wordsMap.put("impresión","q10");
        wordsMap.put("entrada","q11");
        wordsMap.put("contenido","q6");
        wordsMap.put("ignore","q12");
        wordsMap.put("ignore","q12");
        wordsMap.put("","vacio");

        conditionSymbolsMap.put("<","<");
        conditionSymbolsMap.put(">",">");
        conditionSymbolsMap.put("<=","<=");
        conditionSymbolsMap.put(">=",">=");
        conditionSymbolsMap.put("==","==");
    }
}
