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
        //撞墙则Game Over
        else if (headFwd.getX() < 0 || headFwd.getX() > game.getRow() || headFwd.getY() < 0 || headFwd.getY() > game.getCol()) {
            Context.INSTANCE.eventBus().post(new GameOverEvent());
            game.setPlaying(false);
        }
        //没吃豆继续跑
        else {
            game.getSnake().getBody().add(0, headFwd);
            diffs.put(headFwd, GridState.SNAKE_ON);
            diffs.put(game.getSnake().getBody().get(game.getSnake().getBody().size() - 1), GridState.EMPTY);
            game.getSnake().getBody().remove(game.getSnake().getBody().size() - 1);
            Context.INSTANCE.eventBus().post(new BoardRerenderEvent(diffs));
        }
        //头撞到身体则Game Over
        for (int i = 1; i < game.getSnake().getBody().size(); i++) {
            if(headFwd.equals(game.getSnake().getBody().get(i))) {
                Context.INSTANCE.eventBus().post(new GameOverEvent());
                game.setPlaying(false);
            }
        }

        Context.INSTANCE.eventBus().post(new BoardRerenderEvent(diffs));
    }
}
