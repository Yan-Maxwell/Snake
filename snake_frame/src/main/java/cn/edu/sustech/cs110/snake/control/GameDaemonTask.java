package cn.edu.sustech.cs110.snake.control;

import cn.edu.sustech.cs110.snake.Context;
import cn.edu.sustech.cs110.snake.enums.GridState;
import cn.edu.sustech.cs110.snake.events.BeanAteEvent;
import cn.edu.sustech.cs110.snake.events.BoardRerenderEvent;
import cn.edu.sustech.cs110.snake.events.GameOverEvent;
import cn.edu.sustech.cs110.snake.model.Game;
import cn.edu.sustech.cs110.snake.model.Position;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class GameDaemonTask implements Runnable {

    @Override
    public void run() {
        Game game = Context.INSTANCE.currentGame();
        if (!game.isPlaying()) {
            return;
        }
        System.out.println(game);

        Position headFwd = game.getSnake().getBody().get(0).toward(game.getSnake().getDirection());

        Map<Position, GridState> diffs = new HashMap<>(3);


        //吃豆并生成
        if (headFwd.equals(game.getBean())) {
            game.getSnake().getBody().add(0, headFwd);
            diffs.put(headFwd, GridState.SNAKE_ON);

            Position newTail = game.getSnake().getBody().get(game.getSnake().getBody().size() - 1);
            diffs.put(newTail, GridState.SNAKE_ON);

            boolean coincide;
            Position newOne;
            //若豆子与蛇重叠则重新生成
            do {
                coincide = false;
                newOne = new Position(Context.INSTANCE.random().nextInt(game.getRow()), Context.INSTANCE.random().nextInt(game.getCol()));
                for (int i = 0; i < game.getSnake().getBody().size(); i++) {
                    if (newOne.equals(game.getSnake().getBody().get(i))) {
                        coincide = true;
                        break;
                    }
                }
            }while (coincide);
            game.setBean(newOne);
            diffs.put(newOne,GridState.BEAN_ON);

            Context.INSTANCE.eventBus().post(new BeanAteEvent(diffs));
        }
        //没吃豆继续跑
        else {
            game.getSnake().getBody().add(0, headFwd);
            diffs.put(headFwd, GridState.SNAKE_ON);
            diffs.put(game.getSnake().getBody().get(game.getSnake().getBody().size() - 1), GridState.EMPTY);
            game.getSnake().getBody().remove(game.getSnake().getBody().size() - 1);
            Context.INSTANCE.eventBus().post(new BeanAteEvent(diffs));
        }

        //判定撞边并停止游戏
        if (headFwd.getX() < 0 || headFwd.getX() > game.getRow() || headFwd.getY() < 0 || headFwd.getY() > game.getCol()){
            game.setPlaying(false);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("gameOver");
            alert.setHeaderText(null);
            alert.setContentText("Game over!");
            ButtonType backButton = new ButtonType("Back");
            alert.getButtonTypes().setAll(backButton);
            Optional<ButtonType> result = alert.showAndWait();
            Context.INSTANCE.eventBus().post(new GameOverEvent(diffs));
        }


        Context.INSTANCE.eventBus().post(new BoardRerenderEvent(diffs));
    }
}
