package cn.edu.sustech.cs110.snake;

import cn.edu.sustech.cs110.snake.view.AdvancedStage;
import javafx.application.Application;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        new AdvancedStage("login.fxml")
                .withTitle("Login in")
                .shows();


        //刷新排行榜
        File file = new File("records.txt");
        File file2 = new File("rank.txt");
        Scanner read = new Scanner(file);
        PrintWriter pw = new PrintWriter(file2);
        //创建并录入集合
        ArrayList<String> names = new ArrayList<>();
        ArrayList<Integer> scores = new ArrayList<>();
        ArrayList<Boolean> marks = new ArrayList<>();
        while(read.hasNext()) {
            names.add(0,read.next());
            scores.add(0,read.nextInt());
            marks.add(0,false);
        }
        int max;
        for (int i = 0 ; i < scores.size() ; i++){
            max=0;
            //遍历找最高分
            for (int i1 = 0; i1 < scores.size(); i1++) {
                if(scores.get(i1)>max && !marks.get(i1)){
                    max=scores.get(i1);
                }
            }
            //再次遍历，记录最高分和玩家，并改变mark
            for (int i1 = 0; i1 < names.size(); i1++) {
                if(scores.get(i1)==max && !marks.get(i1)){
                    pw.println(names.get(i1)+" "+scores.get(i1));
                    marks.set(i1,true);
                }
            }
        }
        pw.close();
        read.close();
    }
}
