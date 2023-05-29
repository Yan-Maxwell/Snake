package cn.edu.sustech.cs110.snake.control;

import cn.edu.sustech.cs110.snake.enums.Direction;
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
        File file = new File("rank.txt");
        if(file.exists()){
            Scanner read = new Scanner(file);
            while (read.hasNext()){
                String str=read.next();
                int n= read.nextInt();
                if(str.equals(Context.INSTANCE.currentGame().getPlayer()) && n>Context.INSTANCE.currentGame().getHighestScore()){
                    Context.INSTANCE.currentGame().setHighestScore(n);
                }
            }
        }

        Game game =Context.INSTANCE.currentGame();
        Position BeanPosition = game.getBean();
        do {
            BeanPosition = new Position(Context.INSTANCE.random().nextInt(game.getRow()), Context.INSTANCE.random().nextInt(game.getCol()));
        }
        while (isBeanCollidingWithWall(BeanPosition));
        game.setBean(BeanPosition);


        new AdvancedStage("choose.fxml")
                .withTitle("Choose your game")
                .shows();
        Begin.getScene().getWindow().hide();
    }

    public boolean isBeanCollidingWithWall(Position Bean) {
        Game game = Context.INSTANCE.currentGame();
        for (int j = 0; j < game.getWall().getThisWall().size(); j++) {
            if (Bean.equals(game.getWall().getThisWall().get(j))) {
                return true;
            }
        }
        return false;
    }

    public void loadGame() throws FileNotFoundException {

        File file = new File(Context.INSTANCE.getCurrentUser()+"Archive.txt");
        if(file.exists()){
            Scanner read = new Scanner(file);
            int x =read.nextInt();
            int y = read.nextInt();
            String duration = read.next();
            int score = read.nextInt();
            int map = read.nextInt();
            int difficulty = read.nextInt();
            int xDiff = read.nextInt();
            int yDiff = read.nextInt();
            Direction d;
            if (xDiff==-1){
                d=Direction.UP;
            } else if (xDiff==1) {
                d=Direction.DOWN;
            } else if (yDiff==1) {
                d=Direction.RIGHT;
            } else {
                d=Direction.LEFT;
            }
            Context.INSTANCE.currentGame(new Game(25, 25, Context.INSTANCE.getCurrentUser(),difficulty,map,score));
            //读取豆子位置
            Context.INSTANCE.currentGame().setBean(new Position(x, y));
            //读取持续时间
            Context.INSTANCE.currentGame().setDuration(duration);
            //读取该玩家最高分
            File file1 = new File("rank.txt");
            if(file1.exists()){
                Scanner read1 = new Scanner(file1);
                while (read1.hasNext()){
                    String str=read1.next();
                    int n= read1.nextInt();
                    if(str.equals(Context.INSTANCE.currentGame().getPlayer()) && n>Context.INSTANCE.currentGame().getHighestScore()){
                        Context.INSTANCE.currentGame().setHighestScore(n);
                    }
                }
            }
            //读取方向
            Context.INSTANCE.currentGame().getSnake().setDirection(d);
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