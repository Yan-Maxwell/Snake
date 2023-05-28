package cn.edu.sustech.cs110.snake.control;

import cn.edu.sustech.cs110.snake.model.Game;
import cn.edu.sustech.cs110.snake.model.Position;
import cn.edu.sustech.cs110.snake.view.AdvancedStage;
import cn.edu.sustech.cs110.snake.Context;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Optional;
import java.util.Scanner;

public class HomeController {
    @FXML
    private Parent root;

    @FXML
    private Button Begin;

    @FXML
    private Button Load;
    @FXML
    private Button ShowRank;

    public void playGame() throws FileNotFoundException {
        Context.INSTANCE.currentGame(new Game(25, 25, Context.INSTANCE.getCurrentUser()));
        File file = new File(Context.INSTANCE.getCurrentUser()+"Archive.txt");
        if(file.exists()){
            Scanner read = new Scanner(file);
            read.next();
            read.next();
            read.next();
            read.next();
            Context.INSTANCE.currentGame().setHighestScore(read.nextInt());
        }
        Game game =Context.INSTANCE.currentGame();
        for (int j = 0; j < game.getWall().getThisWall().size(); j++) {
            if (game.getBean().equals(game.getWall().getThisWall().get(j))) {
                game.setBean(new Position(Context.INSTANCE.random().nextInt(game.getRow()), Context.INSTANCE.random().nextInt(game.getCol())));
                break;
            }
        }

        new AdvancedStage("choose.fxml")
                .withTitle("Choose your game")
                .shows();
        Begin.getScene().getWindow().hide();
    }

    public void loadGame() throws FileNotFoundException {
        Context.INSTANCE.currentGame(new Game(25, 25, Context.INSTANCE.getCurrentUser()));
        File file = new File(Context.INSTANCE.getCurrentUser()+"Archive.txt");
        if(file.exists()){
            Scanner read = new Scanner(file);
            //读取豆子位置
            Context.INSTANCE.currentGame().setBean(new Position(read.nextInt(), read.nextInt()));
            //读取持续时间
            Context.INSTANCE.currentGame().setDuration(read.next());
            //读取分数
            Context.INSTANCE.currentGame().setScore(read.nextInt());
            //读取该玩家最高分
            Context.INSTANCE.currentGame().setHighestScore(read.nextInt());
            //读取地图
            Context.INSTANCE.currentGame().setMap(read.nextInt());
            //读取难度
            Context.INSTANCE.currentGame().setDifficulty(read.nextInt());
            //读取蛇身位置集合
            while(read.hasNext()){
                Context.INSTANCE.currentGame().getSnake().getBody().add(0,new Position(read.nextInt(), read.nextInt()));
            }
        }else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("New User");
            alert.setHeaderText(null);
            alert.setContentText("You haven't have a game save! Please play a new game.");
            ButtonType backButton = new ButtonType("Back");
            alert.getButtonTypes().setAll(backButton);
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == backButton) {
                return;
            }
        }


        new AdvancedStage("game.fxml")
                .withTitle("Snake")
                .shows();
        Load.getScene().getWindow().hide();
    }

    public void showranking() throws FileNotFoundException {
        File file=new File("rank.txt");
        Scanner read=new Scanner(file);
        String[] nbPlayer=new String[3];
        int[] nbScore=new int[3];
        int i=0;
        while (i<3 && read.hasNext()) {
            String temp=read.next();
            boolean mark=true;
            for(int y=0;y<i;y++){
                if (temp.equals(nbPlayer[y])) {
                    mark = false;
                    break;
                }
            }
            if (mark){
                nbPlayer[i]=temp;
                nbScore[i]=read.nextInt();
            }else {read.nextInt();}
            i++;
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("RANK");
        alert.setHeaderText(null);
        alert.setContentText("1st: " +nbPlayer[0]+ "   " +nbScore[0]+ "points" + "\n" + "2nd: " +nbPlayer[1]+ "   " +nbScore[1]+ "points" + "\n" + "3rd: " +nbPlayer[2]+ "   " +nbScore[2]+ "points" + "\n");
        ButtonType backButton = new ButtonType("Back");
        alert.getButtonTypes().setAll(backButton);
        Optional<ButtonType> result = alert.showAndWait();
    }
}
