package cn.edu.sustech.cs110.snake.control;

import cn.edu.sustech.cs110.snake.Context;
import cn.edu.sustech.cs110.snake.model.Wall;
import cn.edu.sustech.cs110.snake.view.AdvancedStage;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;

public class ChooseController{
    @FXML
    private Button Easy;

    @FXML
    private Button Medium;

    @FXML
    private Button Hard;

    @FXML
    private Button Map1;

    @FXML
    private Button Map2;

    @FXML
    private Button Map3;

    @FXML
    private Button Back;

    @FXML
    private Button Enter;

    @FXML
    private Text difficultyText;

    @FXML
    private Text mapText;

    @FXML
    private void chooseEasy() {
        Context.INSTANCE.currentGame().setDifficulty(1);
        difficultyText.setText("Easy selected");
        difficultyText.setFill(Color.GREEN);
    }

    @FXML
    private void chooseMedium() {
        Context.INSTANCE.currentGame().setDifficulty(2);
        difficultyText.setText("Medium selected");
        difficultyText.setFill(Color.GREEN);
    }

    @FXML
    private void chooseHard() {
        Context.INSTANCE.currentGame().setDifficulty(3);
        difficultyText.setText("Hard selected");
        difficultyText.setFill(Color.GREEN);
    }

    @FXML
    private void chooseH() {
        Context.INSTANCE.currentGame().setMap(1);
        Context.INSTANCE.currentGame().setWall(new Wall(1));
        mapText.setText("Map H selected");
        mapText.setFill(Color.GREEN);
    }

    @FXML
    private void chooseL() {
        Context.INSTANCE.currentGame().setMap(2);
        Context.INSTANCE.currentGame().setWall(new Wall(2));
        mapText.setText("Map L selected");
        mapText.setFill(Color.GREEN);
    }

    @FXML
    private void chooseT() {
        Context.INSTANCE.currentGame().setMap(3);
        Context.INSTANCE.currentGame().setWall(new Wall(3));
        mapText.setText("Map T selected");
        mapText.setFill(Color.GREEN);
    }

    @FXML
    private void toBack() {
        new AdvancedStage("home.fxml")
                .withTitle("HOME")
                .shows();
        Back.getScene().getWindow().hide();
    }

    @FXML
    private void toEnter() {
        new AdvancedStage("game.fxml")
                .withTitle("Snake")
                .shows();
        Enter.getScene().getWindow().hide();
    }

}
