package cn.edu.sustech.cs110.snake.control;

import cn.edu.sustech.cs110.snake.enums.Direction;
import cn.edu.sustech.cs110.snake.model.Game;
import cn.edu.sustech.cs110.snake.control.ChooseController;
import cn.edu.sustech.cs110.snake.model.Position;
import cn.edu.sustech.cs110.snake.view.AdvancedStage;
import cn.edu.sustech.cs110.snake.Context;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ResourceBundle;
import java.net.URL;
import java.util.Scanner;

public class HomeController {
    @FXML
    private Parent root;

    @FXML
    private Button Begin;

    @FXML
    private Button Load;


    public void playGame() {
        Context.INSTANCE.currentGame(new Game(25, 25, Context.INSTANCE.getCurrentUser()));
        new AdvancedStage("choose.fxml")
                .withTitle("Choose your game")
                .shows();
        Begin.getScene().getWindow().hide();
    }

    public void loadGame() throws FileNotFoundException {
        Context.INSTANCE.currentGame(new Game(25, 25, Context.INSTANCE.getCurrentUser()));
        File file = new File(Context.INSTANCE.getCurrentUser()+"Archive.txt");
        Scanner read = new Scanner(file);
        //读取豆子位置
        Context.INSTANCE.currentGame().setBean(new Position(read.nextInt(), read.nextInt()));
        //读取持续时间

        Context.INSTANCE.currentGame().setDuration(read.next());

        //读取蛇身位置集合
        while(read.hasNext()){
            Context.INSTANCE.currentGame().getSnake().getBody().add(0,new Position(read.nextInt(), read.nextInt()));
        }




        new AdvancedStage("game.fxml")
                .withTitle("Snake")
                .shows();
        Begin.getScene().getWindow().hide();
    }
}