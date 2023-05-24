package cn.edu.sustech.cs110.snake.control;

import cn.edu.sustech.cs110.snake.view.AdvancedStage;
import cn.edu.sustech.cs110.snake.Context;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HomeController{
    @FXML
    private Parent root;

    public static void showHomeScreen() throws IOException {
        new AdvancedStage("home.fxml")
                .withTitle("Login in")
                .shows();
    }


    public void playGame() throws IOException {
        GameController.showGameView();
    }
}
