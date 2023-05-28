package cn.edu.sustech.cs110.snake.control;

import cn.edu.sustech.cs110.snake.Context;
import cn.edu.sustech.cs110.snake.model.Rank;
import javafx.fxml.FXML;
import javafx.scene.text.Text;

public class RankController {
    @FXML
    private Text textFirst;
    @FXML
    private Text textSecond;
    @FXML
    private Text textThird;
    public void ranking(){
        textFirst.setText("Champion: "+ Rank.s1 + "score: "+ Rank.i1);
        textSecond.setText("Champion: "+ Rank.s2 + "score: "+ Rank.i2);
        textThird.setText("Champion: "+ Rank.s3 + "score: "+ Rank.i3);
    }
}
