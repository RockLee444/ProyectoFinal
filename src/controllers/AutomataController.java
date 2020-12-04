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
    private int lineError = 0;
    private String mensajeError = " ";

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
        currentState = "q0";
        String[] inputArray = input.split("\n");

        if(!principal(inputArray)){
            error = true;
        }

        if(!error){
            showAlert("HECHO", "Todo salió bien :)",AlertType.CONFIRMATION);
        } else {
            showAlert("ERROR", "Hay un error! En la linea: "+"\n"+mensajeError,AlertType.ERROR);
            mensajeError = " ";
        }
    }

    public boolean principal(String[] text){
        boolean isValid = true, recursive = false;
        int position = 0, wordLength = 10;

        int programPosition = text[position].indexOf("program(){");
        if(programPosition >= 0){
            for(int i=0;i<programPosition;i++){
                String currentValue = "" + text[position].charAt(i);
                if (!(currentValue.isEmpty() || currentValue.isBlank())) {
                    lineError=position+1;
                    mensajeError = mensajeError+"\nError Principal No.1 Linea: "+lineError;
                    System.out.println("Error Principal No.1 Linea: "+lineError);
                    isValid = false;
                }
            }
            if(programPosition + wordLength <= text[position].length()) {
                if (!text[position].substring(programPosition, programPosition + wordLength).equals("program(){")) {
                    lineError=position+1;
                    mensajeError = mensajeError+"\nError Principal No.2 Linea: "+lineError;
                    System.out.println("Error Principal No.2 Linea: "+lineError);
                    isValid = false;
                }
            } else {
                lineError=position+1;
                mensajeError = mensajeError+"\nError Principal No.3 Linea: "+lineError;
                System.out.println("Error Principal No.3 Linea: "+lineError);
                isValid = false;
            }
        } else {
            lineError=position+1;
            mensajeError = mensajeError+"\nError Principal No.4 Linea: "+lineError;
            System.out.println("Error Principal No.4 Linea: "+lineError);
            isValid = false;
        }
        position++;

        while(position < text.length -1){
            String currentData = text[position], result = "";
            String[] resultArray = null;
            //Verifying which instruction it is...
            if(currentData.contains("data")){
                result = data(text, position);
                resultArray = result.split(" ");
                recursive = Boolean.parseBoolean(resultArray[0]);
                position = Integer.parseInt(resultArray[1]) - 1;
            } else if(currentData.contains("condition")){
                result = condition(text, position);
                resultArray = result.split(" ");
                recursive = Boolean.parseBoolean(resultArray[0]);
                position = Integer.parseInt(resultArray[1]) - 1;
            } else if(currentData.contains("=")){
                result = assignation(text, position);
                resultArray = result.split(" ");
                recursive = Boolean.parseBoolean(resultArray[0]);
                position = Integer.parseInt(resultArray[1]) - 1;
            } else if(currentData.contains("enter")){
                //result = enter();
                result = enter(text, position);
                resultArray = result.split(" ");
                recursive = Boolean.parseBoolean(resultArray[0]);
                position = Integer.parseInt(resultArray[1]) - 1;
            } else if(currentData.contains("ignore")){
                result = ignore(text,position);
                resultArray = result.split(" ");
                recursive = Boolean.parseBoolean(resultArray[0]);
                position = Integer.parseInt(resultArray[1])-1;
            } else if(currentData.contains("output")){
                result = output(text, position);
                resultArray = result.split(" ");
                recursive = Boolean.parseBoolean(resultArray[0]);
                position = Integer.parseInt(resultArray[1]) - 1;
            } else if(currentData.isEmpty() || currentData.isBlank()){
                recursive = true;
            } else {
                lineError=position+1;
                mensajeError = mensajeError+"\nError Principal No.5 Linea: "+lineError;
                System.out.println("Error Principal No.5 Linea: "+lineError);
                isValid = false;
            }
            position++;

            if(!(isValid && recursive)){
                break;
            }
        }

        int closingSymbols = 0;

        if(position < text.length) {
            String[] lastLine = text[position].split("");
            for(int i = 0; i<lastLine.length ;i++) {
                if (!lastLine[i].equals("}") || !(isValid && recursive)) {
                    if(!(lastLine[i].isBlank() || lastLine[i].isEmpty())){
                        lineError=position+1;
                        mensajeError = mensajeError+"\nError Principal No.6 Linea: "+lineError;
                        System.out.println("Error Principal No.6 Linea: "+lineError);
                        isValid = false;
                    }
                } else {
                    closingSymbols++;
                }
            }
        } else {
            lineError=position+1;
            mensajeError = mensajeError+"\nError Principal No.7 Linea: "+lineError;
            System.out.println("Error Principal No.7 Linea: "+lineError);
            isValid = false;
        }

        if(closingSymbols > 1){
            lineError=position+1;
            mensajeError = mensajeError+"\nError Principal No.8 Linea: "+lineError;
            System.out.println("Error Principal No.8 Linea: "+lineError);
            isValid = false;
        }

        return isValid;
    }

    public String assignation(String[] text, int position){
        boolean isValid = true, finished = false;
        String identifier="";
        Pattern patternId;
        Matcher verified;
        String result;
        String[] resultArray;
        String txt="";
        int auxP=0;
        String currentData[]  = text[position].split(" ");
        for(int i=0; i<currentData.length - 1; i++){
            if(!currentData[i].isBlank() && txt.isBlank()){
                txt += currentData[i] + " ";
                auxP = i;
                i=currentData.length;
            }
        }
        for(int i=auxP+1; i<currentData.length - 1; i++){
            if (currentData[i].isBlank()){
                txt+=" ";
            }
            txt += currentData[i] + " ";
        }
        txt += currentData[currentData.length - 1];
        currentData  = txt.split(" ");

        if(currentData.length >= 4){
            if(currentData.length == 4 && currentData[3].equals(";") && currentData[1].equals("=")){
                patternId = Pattern.compile("(^[a-zA-Z_]+[\\w]*|[\\d]+)$");
                verified = patternId.matcher(currentData[0]);
                if(!verified.find()){
                    lineError=position+1;
                    mensajeError = mensajeError+"\nError Assignation No.1 Linea: "+lineError;
                    System.out.println("Error Assignation No.1 Linea: "+lineError);
                    isValid = false;
                }
                if(currentData[2].equals("enter()")){
                    result = enter(text, position);
                    resultArray = result.split(" ");
                    isValid = Boolean.parseBoolean(resultArray[0]);
                    position = Integer.parseInt(resultArray[1]) - 1;
                } else {
                    identifier = currentData[2];
                    if (currentData[2].charAt(0) == '"' && currentData[2].charAt(currentData[2].length() - 1) == '"'){
                        identifier = currentData[2].substring(1,currentData[2].length() - 1);
                        patternId = Pattern.compile("([\\w])$");
                    }
                    verified = patternId.matcher(identifier);
                    if(!verified.find()){
                        lineError=position+1;
                        mensajeError = mensajeError+"\nError Assignation No.2 Linea: "+lineError;
                        System.out.println("Error Assignation No.2 Linea: "+lineError);
                        isValid = false;
                    }
                }
                position++;
            } else if(currentData[currentData.length - 1].equals(";") && !currentData[1].isBlank()){
                txt = text[position];
                String strIdentifiers = txt.substring(txt.indexOf("=") + 2, txt.indexOf(";") + 1);
                String identifiers[] = strIdentifiers.split(" ");
                patternId = Pattern.compile("(^[a-zA-Z_]+[\\w]*|[\\d]+)$");
                for(int i=0; i<identifiers.length; i++){
                    identifier = identifiers[i];
                    if (!identifiers[i].equals("+") && !identifiers[i].isBlank() && !identifiers[i].equals(";")){
                        if (identifiers[i].charAt(0) == '"' && identifiers[i].charAt(identifiers[i].length() - 1) == '"'){
                            identifier = identifiers[i].substring(1,identifiers[i].length() - 1);
                            patternId = Pattern.compile("([\\w])$");
                        }
                        verified = patternId.matcher(identifier);
                        if(!verified.find()){
                            lineError=position+1;
                            mensajeError = mensajeError+"\nError Assignation No.3 Linea: "+lineError;
                            System.out.println("Error Assignation No.3 Linea: "+lineError);
                            isValid = false;
                        }
                    } else if(identifiers[i].isBlank()){
                        lineError=position+1;
                        mensajeError = mensajeError+"\nError Assignation No.4 Linea: "+lineError;
                        System.out.println("Error Assignation No.4 Linea: "+lineError);
                        isValid = false;
                    }
                }
                position++;
            } else {
                lineError=position+1;
                mensajeError = mensajeError+"\nError Assignation No.5 Linea: "+lineError;
                System.out.println("Error Assignation No.5 Linea: "+lineError);
                position++;
                isValid = false;
            }
        }else{
            lineError=position+1;
            mensajeError = mensajeError+"\nError Assignation No.6 Linea: "+lineError;
            System.out.println("Error Assignation No.6 Linea: "+lineError);
            position++;
            isValid = false;
        }
        return isValid + " " + position;
    }

    public String data(String[] text, int position){
        String result;
        String[] resultArray;
        String txt = text[position].substring(text[position].indexOf("data"),text[position].length());
        Pattern patternId;
        Matcher verified;
        String[] currentData = txt.split(" ");
        if(txt.lastIndexOf('=') > 0){
            if(txt.charAt(txt.lastIndexOf('=') - 1) != ' ' && txt.charAt(txt.lastIndexOf('=') + 1) != ' ' ){
                txt = txt.substring(0,txt.lastIndexOf('=')) +" = "+txt.substring(txt.lastIndexOf('=')+1, txt.length());
            } else if(txt.charAt(txt.lastIndexOf('=') - 1) == ' ' && txt.charAt(txt.lastIndexOf('=') + 1) != ' ' ){
                txt = txt.substring(0,txt.lastIndexOf('=')) +"= "+txt.substring(txt.lastIndexOf('=')+1, txt.length());
            }else if(txt.charAt(txt.lastIndexOf('=') - 1) != ' ' && txt.charAt(txt.lastIndexOf('=') + 1) == ' ' ){
                txt = txt.substring(0,txt.lastIndexOf('=')) +" ="+txt.substring(txt.lastIndexOf('=')+1, txt.length());
            }
            currentData = txt.split(" ");
        }
        if (currentData.length>4 && currentData[0].equals("data") && currentData[2].equals("=")){
            txt = currentData[0]+" "+currentData[1]+" "+currentData[2]+" ";
            for (int i=3; i<currentData.length;i++){
                txt += currentData[i];
            }
            currentData = txt.split(" ");
        }
        if (currentData.length>2){
            txt = currentData[0]+" "+currentData[1]+";";
            currentData = txt.split(" ");
        }
        boolean isValid = true, finished = false;
        if (currentData.length > 1 && currentData[0].equals("data")){
            if(currentData.length == 2 && currentData[1].lastIndexOf(';') > 0){
                String identifier = currentData[1].substring(0, currentData[1].lastIndexOf(';'));
                patternId = Pattern.compile("(^[a-zA-Z_]+[\\w]*|[\\d]+)$");
                verified = patternId.matcher(identifier);
                if (!verified.find()){
                    lineError=position+1;
                    mensajeError = mensajeError+"\nError Data No.1 Linea: "+lineError;
                    System.out.println("Error Data No.1 Linea: "+lineError);
                    isValid = false;
                }
                if(currentData[1].lastIndexOf(';') != currentData[1].length() - 1 ){
                    lineError=position+1;
                    mensajeError = mensajeError+"\nError Data No.2 Linea: "+lineError;
                    System.out.println("Error Data No.2 Linea: "+lineError);
                    isValid = false;
                }
                position++;
            } else if(currentData.length > 3 ){
                patternId = Pattern.compile("(^[a-zA-Z_]+[\\w]*|[\\d]+)$");
                verified = patternId.matcher(currentData[1]);
                if(!verified.find()){
                    lineError=position+1;
                    mensajeError = mensajeError+"\nError Data No.3 Linea: "+lineError;
                    System.out.println("Error Data No.3 Linea: "+lineError);
                    isValid = false;
                }
                if(!currentData[2].equals("=")){
                    lineError=position+1;
                    mensajeError = mensajeError+"\nError Data No.4 Linea: "+lineError;
                    System.out.println("Error Data No.4 Linea: "+lineError);
                    isValid = false;
                }
                if(currentData[3].indexOf("enter") > -1){
                    result = enter(text, position);
                    resultArray = result.split(" ");
                    isValid = Boolean.parseBoolean(resultArray[0]);
                    position = Integer.parseInt(resultArray[1]);
                }else if(currentData[3].lastIndexOf(';') == currentData[3].length()-1){
                    String parameter = currentData[3].substring(0,currentData[3].lastIndexOf(';'));
                    String[] parameters = parameter.split("\\+");
                    for(int i = 0; i < parameters.length; i++){
                        patternId = Pattern.compile("(^[a-zA-Z_]+[\\w]*|[\\d]+)$");
                        if(parameters[i].charAt(0) == '"' && parameters[i].charAt(parameters[i].length()-1) == '"'){
                            parameters[i] = parameters[i].substring(1, parameters[i].length()-1);
                            patternId = Pattern.compile("([\\w])$");
                        }
                        verified = patternId.matcher(parameters[i]);
                        if(!verified.find()){
                            lineError=position+1;
                            mensajeError = mensajeError+"\nError Data No.5 Linea: "+lineError;
                            System.out.println("Error Data No.5 Linea: "+lineError);
                            isValid = false;
                        }
                    }
                    position++;
                }else{
                    lineError=position+1;
                    mensajeError = mensajeError+"\nError Data No.6 Linea: "+lineError;
                    System.out.println("Error Data No.6 Linea: "+lineError);
                    position++;
                    isValid = false;
                }
            }else{
                lineError=position+1;
                mensajeError = mensajeError+"\nError Data No.7 Linea: "+lineError;
                System.out.println("Error Data No.7 Linea: "+lineError);
                position++;
                isValid = false;
            }
        }else{
            lineError=position+1;
            mensajeError = mensajeError+"\nError Data No.8 Linea: "+lineError;
            System.out.println("Error Data No.8 Linea: "+lineError);
            position++;
            isValid = false;
        }
        return isValid + " " + position;
    }

    public String enter(String[] text, int position){
        String currentData = text[position];
        boolean isValid = true;
        String complement = currentData.substring(currentData.indexOf("enter")+5, currentData.length());
        if(complement.length() >= 3){
            if(complement.charAt(0) != '('){
                lineError=position+1;
                mensajeError = mensajeError+"\nError Enter No.1 Linea: "+lineError;
                System.out.println("Error Enter No.1 Linea: "+lineError);
                isValid = false;
            }
            if(complement.charAt(1) != ')'){
                lineError=position+1;
                mensajeError = mensajeError+"\nError Enter No.2 Linea: "+lineError;
                System.out.println("Error Enter No.2 Linea: "+lineError);
                isValid = false;
            }
            if(complement.charAt(complement.length() - 1) != ';'){
                lineError=position+1;
                mensajeError = mensajeError+"\nError Enter No.3 Linea: "+lineError;
                System.out.println("Error Enter No.3 Linea: "+lineError);
                isValid = false;
            }
        }else{
            lineError=position+1;
            mensajeError = mensajeError+"\nError Enter No.4 Linea: "+lineError;
            System.out.println("Error Enter No.4 Linea: "+lineError);
            isValid = false;
        }
        position++;
        return isValid + " " + position;
    }

    public String ignore(String[] text, int position){
        String currentData = text[position];
        boolean isValid = true, finished = false;
        int outputPosition = 0, iteration = 0, wordLength = 7;
        while(!finished){
            for(int i=0;i<2;i++){
                switch (i){
                    case 0:
                        outputPosition = text[position].indexOf("ignore(");
                        if(outputPosition >= 0) {
                            for (int x = 0; x < outputPosition; x++) {
                                String currentValue = "" + text[position].charAt(x);
                                if (!(currentValue.isEmpty() || currentValue.isBlank())) {
                                    lineError=position+1;
                                    mensajeError = mensajeError+"\nError Ignore No.1 Linea: "+lineError;
                                    System.out.println("Error Ignore No.1 Linea: "+lineError);
                                    isValid = false;
                                }
                            }
                            if (!text[position].substring(outputPosition, outputPosition + wordLength).equals("ignore(")) {
                                lineError=position+1;
                                mensajeError = mensajeError+"\nError Ignore No.2 Linea: "+lineError;
                                System.out.println("Error Ignore No.2 Linea: "+lineError);
                                isValid = false;
                            }
                        } else {
                            lineError=position+1;
                            mensajeError = mensajeError+"\nError Ignore No.3 Linea: "+lineError;
                            System.out.println("Error Ignore No.3 Linea: "+lineError);
                            isValid = false;
                        }
                        break;

                    case 1:
                        if(isValid){
                            int j=0;
                            String[] outputs = text[position].substring(outputPosition + wordLength).split(" ");
                            if(outputs.length < 3){
                                lineError=position+1;
                                mensajeError = mensajeError+"\nError Ignore No.4 Linea: "+lineError;
                                System.out.println("Error Enter No.4 Linea: "+lineError);
                                isValid = false;
                            }
                            while(!finished){
                                if(j + 2 < outputs.length && !(outputs[j].isBlank() || outputs[j].isEmpty())){
                                    if(iteration == 0) {
                                        Pattern identifier = Pattern.compile("(^[a-zA-Z_]+[\\w]*|[\\d]+)$|([\\\"][\\w|\\W]*[\\\"])$");
                                        Matcher verified = identifier.matcher(outputs[j]);
                                        if (verified.find()) {
                                            iteration++;
                                        } else {
                                            lineError=position+1;
                                            mensajeError = mensajeError+"\nError Ignore No.5 Linea: "+lineError;
                                            System.out.println("Error Ignore No.5 Linea: "+lineError);
                                            isValid = false;
                                        }
                                        j++;
                                    }
                                } else if(outputs[j].isBlank() || outputs[j].isEmpty()){
                                    j++;
                                } else {
                                    finished = true;
                                }
                            }
                            if(outputs[j].equals(")")){
                                j++;
                                if(!outputs[j].equals(";")){
                                    lineError=position+1;
                                    mensajeError = mensajeError+"\nError Ignore No.6 Linea: "+lineError;
                                    System.out.println("Error Ignore No.6 Linea: "+lineError);
                                    isValid = false;
                                }
                            } else {
                                lineError=position+1;
                                mensajeError = mensajeError+"\nError Ignore No.7 Linea: "+lineError;
                                System.out.println("Error Ignore No.7 Linea: "+lineError);
                                isValid = false;
                            }
                        }
                        break;
                }
            }
            if(!isValid){
                finished = true;
            }
        }
        position++;
        return isValid + " " + position;
    }

    public String condition(String[] text, int position){
        boolean isValid = true, recursive = false,finished = false;
        String result = "";
        int iteration = 0,conditionPosition = 0, wordLength = 10;

        //Evaluating conditions inside parentheses
        for(int i=0;i<2;i++){
            switch (i){
                case 0:
                    conditionPosition = text[position].indexOf("condition(");
                    if(conditionPosition>=0) {
                        for (int x = 0; x < conditionPosition; x++) {
                            String currentValue = "" + text[position].charAt(x);
                            if (!(currentValue.isEmpty() || currentValue.isBlank())) {
                                lineError=position+1;
                                mensajeError = mensajeError+"\nError Condition No.1 Linea: "+lineError;
                                System.out.println("Error Condition No.1 Linea: "+lineError);
                                isValid = false;
                            }
                        }

                        if(conditionPosition + wordLength <= text[position].length()) {
                            if (!text[position].substring(conditionPosition, conditionPosition + wordLength).equals("condition(")) {
                                lineError=position+1;
                                mensajeError = mensajeError+"\nError Condition No.2 Linea: "+lineError;
                                System.out.println("Error Condition No.2 Linea: "+lineError);
                                isValid = false;
                            }
                        } else {
                            lineError=position+1;
                            mensajeError = mensajeError+"\nError Condition No.3 Linea: "+lineError;
                            System.out.println("Error Condition No.3 Linea: "+lineError);
                            isValid = false;
                        }
                    }  else {
                        lineError=position+1;
                        mensajeError = mensajeError+"\nError Condition No.4 Linea: "+lineError;
                        System.out.println("Error Condition No.4 Linea: "+lineError);
                        isValid = false;
                    }
                break;

                case 1:
                    if(isValid){
                        int j=0;
                        String[] conditions = null;
                        if(conditionPosition + wordLength < text[position].length()) {
                            conditions = text[position].substring(conditionPosition + wordLength).split(" ");
                            if (conditions.length < 3) {
                                lineError=position+1;
                                mensajeError = mensajeError+"\nError Condition No.5 Linea: "+lineError;
                                System.out.println("Error Condition No.5 Linea: "+lineError);
                                isValid = false;
                            }
                        } else {
                            lineError=position+1;
                            mensajeError = mensajeError+"\nError Condition No.6 Linea: "+lineError;
                            System.out.println("Error Condition No.6 Linea: "+lineError);
                            isValid = false;
                        }
                        while(!finished && isValid){
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
                                                lineError=position+1;
                                                mensajeError = mensajeError+"\nError Condition No.7 Linea: "+lineError;
                                                System.out.println("Error Condition No.7 Linea: "+lineError);
                                                isValid = false;
                                            }
                                            iteration++;
                                        } else {
                                            lineError=position+1;
                                            mensajeError = mensajeError+"\nError Condition No.8 Linea: "+lineError;
                                            System.out.println("Error Condition No.8 Linea: "+lineError);
                                            isValid = false;
                                        }

                                    } else {
                                        lineError=position+1;
                                        mensajeError = mensajeError+"\nError Condition No.9 Linea: "+lineError;
                                        System.out.println("Error Condition No.9 Linea: "+lineError);
                                        isValid = false;
                                    }
                                    j++;
                                } else {
                                    if( !(conditions[j].equals("||") || conditions[j].equals("&&")) ){
                                        lineError=position+1;
                                        mensajeError = mensajeError+"\nError Condition No.10 Linea: "+lineError;
                                        System.out.println("Error Condition No.10 Linea: "+lineError);
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
                        if(j +1 < conditions.length) {
                            if (conditions[j].equals(")")) {
                                j++;
                                if (!conditions[j].equals("{")) {
                                    lineError=position+1;
                                    mensajeError = mensajeError+"\nError Condition No.11 Linea: "+lineError;
                                    System.out.println("Error Condition No.11 Linea: "+lineError);
                                    isValid = false;
                                }
                            } else {
                                lineError=position+1;
                                mensajeError = mensajeError+"\nError Condition No.12 Linea: "+lineError;
                                System.out.println("Error Condition No.12 Linea: "+lineError);
                                isValid = false;
                            }
                        } else {
                            lineError=position+1;
                            mensajeError = mensajeError+"\nError Condition No.13 Linea: "+lineError;
                            System.out.println("Error Condition No.13 Linea: "+lineError);
                            isValid =false;
                        }

                    }
                break;
            }
        }
        //END OF CONDITION EVALUATION

        //EVALUATING CONTENT INSIDE IT
        position++;

        String currentData = text[position];
        int counter = 0, closingSymbols = 0;
        boolean finishedIterating = false, hasContent = false;

        while(!finishedIterating){
            counter = 0;
            closingSymbols = 0;
            if(position < text.length) {
                String[] finalPart = text[position].split("");
                hasContent = true;
                if( !(text[position].isBlank() || text[position].isEmpty()) ) {
                    for (int i = 0; i < finalPart.length; i++) {
                        if (!finalPart[i].equals("}")){
                            if (!(finalPart[i].isEmpty() || finalPart[i].isBlank())) {
                                counter++;
                                break;
                            }
                        } else {
                            closingSymbols++;
                        }
                    }
                    if (counter == 0) {
                        hasContent = false;
                    }

                    if(closingSymbols > 1){
                        lineError=position+1;
                        mensajeError = mensajeError+"\nError Condition No.14 Linea: "+lineError;
                        System.out.println("Error Condition No.14 Linea: "+lineError);
                        isValid = false;
                    }
                }
                if (hasContent) {
                    String[] contentResult = contenido(text, position).split(" ");
                    recursive = Boolean.parseBoolean(contentResult[0]);
                    position = Integer.parseInt(contentResult[1]);
                    if (!(isValid && recursive)) {
                        lineError = position+1;
                        mensajeError = mensajeError+"\nError Condition No.15 Linea: "+lineError;
                        System.out.println("Error Condition No.15 Linea: "+lineError);
                        isValid = false;
                    }
                } else {
                    finishedIterating = true;
                }
            } else {
                finishedIterating = true;
                position--;
            }
        }

        position++;

        //TODO Add fail

        if(position < text.length){
            conditionPosition = text[position].indexOf("fail{");
            if(conditionPosition >= 0) {
                position++;
                boolean finishedIterating2 = false, hasContent2;
                while (!finishedIterating2) {
                    closingSymbols = 0;
                    if (position < text.length) {
                        String[] finalPart = text[position].split("");
                        hasContent2 = true;
                        counter = 0;
                        //TODO AQUI
                        if( !(text[position].isBlank() || text[position].isEmpty()) ) {
                            for (int i = 0; i < finalPart.length; i++) {
                                if ((!finalPart[i].equals("}"))) {
                                    if (!(finalPart[i].isEmpty() || finalPart[i].isBlank())) {
                                        counter++;
                                    }
                                } else {
                                    closingSymbols++;
                                }
                            }
                            if (counter == 0) {
                                hasContent2 = false;
                            }

                            if(closingSymbols > 1){
                                lineError=position+1;
                                mensajeError = mensajeError+"\nError Condition/Fail No.16 Linea: "+lineError;
                                System.out.println("Error Condition/Fail No.16 Linea: "+lineError);
                                isValid = false;
                            }
                        }
                        if (hasContent2) {
                            String[] contentResult = contenido(text, position).split(" ");
                            recursive = Boolean.parseBoolean(contentResult[0]);
                            position = Integer.parseInt(contentResult[1]);
                            if (!(isValid && recursive)) {
                                lineError = position+1;
                                mensajeError = mensajeError+"\nError Condition/Fail No.17 Linea: "+lineError;
                                System.out.println("Error Condition/Fail No.17 Linea: "+lineError);
                                isValid = false;
                            }
                        } else {
                            finishedIterating2 = true;
                        }
                    } else {
                        finishedIterating2 = true;
                        position--;
                    }
                }
            } else {
                position--;
            }
        }

        position++;
        result = isValid + " " + position;
        return result;
    }

    public String output(String[] text, int position){
        String currentData = text[position];
        boolean isValid = true, finished = false;
        int outputPosition = 0, iteration = 0, wordLength = 7;
        while(!finished){
            for(int i=0;i<2;i++){
                switch (i){
                    case 0:
                        outputPosition = text[position].indexOf("output(");
                        if(outputPosition >= 0) {
                            for (int x = 0; x < outputPosition; x++) {
                                String currentValue = "" + text[position].charAt(x);
                                if (!(currentValue.isEmpty() || currentValue.isBlank())) {
                                    lineError=position+1;
                                    mensajeError = mensajeError+"\nError Output No.1 Linea: "+lineError;
                                    System.out.println("Error Output No.1 Linea: "+lineError);
                                    isValid = false;
                                }
                            }
                            if(outputPosition + wordLength < text[position].length()) {
                                if (!text[position].substring(outputPosition, outputPosition + wordLength).equals("output(")) {
                                    lineError=position+1;
                                    mensajeError = mensajeError+"\nError Output No.2 Linea: "+lineError;
                                    System.out.println("Error Output No.2 Linea: "+lineError);
                                    isValid = false;
                                }
                            } else {
                                lineError=position+1;
                                mensajeError = mensajeError+"\nError Output No.3 Linea: "+lineError;
                                System.out.println("Error Output No.3 Linea: "+lineError);
                                isValid = false;
                            }
                        } else {
                            lineError=position+1;
                            mensajeError = mensajeError+"\nError Output No.4 Linea: "+lineError;
                            System.out.println("Error Output No.4 Linea: "+lineError);
                            isValid = false;
                        }
                    break;

                    case 1:
                        if(isValid){
                            int j=0;
                            String[] outputs = text[position].substring(outputPosition + wordLength).split(" ");
                            while(!finished){
                                if(j + 2 < outputs.length && !(outputs[j].isBlank() || outputs[j].isEmpty())){
                                    if(iteration == 0) {
                                        Pattern identifier = Pattern.compile("(^[a-zA-Z_]+[\\w]*|[\\d]+)$|([\\\"][\\w|\\W]*[\\\"])$");
                                        Matcher verified = identifier.matcher(outputs[j]);
                                        if (verified.find()) {
                                            iteration++;
                                        } else {
                                            lineError=position+1;
                                            mensajeError = mensajeError+"\nError Output No.5 Linea: "+lineError;
                                            System.out.println("Error Output No.5 Linea: "+lineError);
                                            isValid = false;
                                        }
                                        j++;
                                    } else {
                                        if( !(outputs[j].equals("+")) ){
                                            lineError=position+1;
                                            mensajeError = mensajeError+"\nError Output No.6 Linea: "+lineError;
                                            System.out.println("Error Output No.6 Linea: "+lineError);
                                            isValid = false;
                                        }
                                        iteration = 0;
                                        j++;
                                    }
                                } else if(outputs[j].isBlank() || outputs[j].isEmpty()){
                                    j++;
                                } else {
                                    finished = true;
                                }
                            }
                            if(outputs[j].equals(")")){
                                j++;
                                if(!outputs[j].equals(";")){
                                    lineError=position+1;
                                    mensajeError = mensajeError+"\nError Output No.7 Linea: "+lineError;
                                    System.out.println("Error Output No.7 Linea: "+lineError);
                                    isValid = false;
                                }
                            } else {
                                lineError=position+1;
                                mensajeError = mensajeError+"\nError Output No.8 Linea: "+lineError;
                                System.out.println("Error Output No.8 Linea: "+lineError);
                                isValid = false;
                            }
                        }
                    break;
                }
            }
            if(!isValid){
                finished = true;
            }
        }

        position++;
        return isValid + " " + position;
    }

    public String contenido(String[] text, int position){
        String currentData = text[position],result = "";
        boolean isValid = true, recursive = false;

        if(currentData.contains("data")){
            result = data(text, position);
            String[] resultArray = result.split(" ");
            recursive = Boolean.parseBoolean(resultArray[0]);
            position = Integer.parseInt(resultArray[1]);
        } else if(currentData.contains("condition")){
            result = condition(text, position);
            String[] resultArray = result.split(" ");
            recursive = Boolean.parseBoolean(resultArray[0]);
            position = Integer.parseInt(resultArray[1]);
        } else if(currentData.contains("=")){
            result = assignation(text, position);
            String[] resultArray = result.split(" ");
            recursive = Boolean.parseBoolean(resultArray[0]);
            position = Integer.parseInt(resultArray[1]);
        } else if(currentData.contains("enter")){
            //result = enter();
            result = enter(text, position);
            String[] resultArray = result.split(" ");
            recursive = Boolean.parseBoolean(resultArray[0]);
            position = Integer.parseInt(resultArray[1]);
        } else if(currentData.contains("ignore")){
            result = ignore(text,position);
            String[] resultArray = result.split(" ");
            recursive = Boolean.parseBoolean(resultArray[0]);
            position = Integer.parseInt(resultArray[1]);
        } else if(currentData.contains("output")){
            result = output(text, position);
            String[] resultArray = result.split(" ");
            recursive = Boolean.parseBoolean(resultArray[0]);
            position = Integer.parseInt(resultArray[1]);
        } else if(currentData.isEmpty() || currentData.isBlank()){
            position++;
            recursive = true;
        } else {
            lineError=position+1;
            mensajeError = mensajeError+"\nError Contenido No.1 Linea: "+lineError;
            System.out.println("Error Contendio No.1 Linea: "+lineError);
            isValid = false;
            position++;
        }

        if(!(isValid && recursive)){
            lineError=position+1;
            mensajeError = mensajeError+"\nError Contenido No.2 Linea: "+lineError;
            System.out.println("Error Contenido No.2 Linea: "+lineError);
            isValid = false;
        }

        return isValid + " " + position;
    }

    public void showAlert(String title, String content, AlertType alertType){
        Alert alert = new Alert(alertType);
        alert.setHeaderText(null);
        alert.setTitle(title);

        TextArea area = new TextArea();
        area.setText(content);
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
