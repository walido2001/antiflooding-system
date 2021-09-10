package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import java.lang.*;
import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        var sp = SerialPortService.getSerialPort("COM3");
        var oS = sp.getOutputStream();

        int enbAuto = 254;
        int disAuto = 255;

        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Walid AlDari - 218 375 162");

        /////////////////////////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////////////////
        //Part 1: JavaFX Button that switches between modes (Automatic/Manual)
        //Automatic: pump out water without the need for manual activation
        //Manual: do not pump out water unless the button is pressed
        Label modeLabel = new Label("Current Mode: Automatic");
        Button modeButton = new Button("Disable Automatic");
        Button  mode2Button = new Button("Enable Automatic");
        modeButton.setOnMousePressed(value -> {
            modeLabel.setText("Mode: Manual");
            try{
                oS.write(disAuto);
            } catch (IOException e){
                e.printStackTrace();
            }
        });
        mode2Button.setOnMousePressed(value -> {
            modeLabel.setText("Mode: Automatic");
            try{
                oS.write(enbAuto);
            } catch (IOException e){
                e.printStackTrace();
            }
        });


        HBox modeRow = new HBox();
        modeRow.getChildren().addAll(modeButton, mode2Button);
        modeRow.setSpacing(10);

        VBox modeColumn = new VBox();
        modeColumn.getChildren().addAll(modeLabel, modeRow);
        modeColumn.setSpacing(10);

        /////////////////////////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////////////////
        //Part 2: JavaFX Slider; value adjusts buzzer volume

        Label volumeSliderLabel = new Label("Buzzer Volume: 0");

        Slider volumeSlider = new Slider();
        volumeSlider.setMin(100);
        volumeSlider.setMax(1000);

        volumeSlider.valueProperty().addListener((observableValue, oldValue, newValue) -> {
            volumeSliderLabel.setText("Buzzer Volume: " + String.valueOf(newValue.intValue()));
            if( (newValue.intValue() == enbAuto) || (newValue.intValue() == disAuto) )
            {
                try{
                    oS.write(oldValue.byteValue());
                } catch (IOException e){
                    e.printStackTrace();
                }
            }
            else {
                try {
                    oS.write(newValue.byteValue());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        HBox sliderRow = new HBox();
        sliderRow.getChildren().addAll(volumeSliderLabel, volumeSlider);
        sliderRow.setSpacing(10);

        /////////////////////////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////////////////


        //Java element organization

        var vbox = new VBox();
        vbox.getChildren().addAll(modeColumn, sliderRow);


        var scene = new Scene(vbox, 600, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
