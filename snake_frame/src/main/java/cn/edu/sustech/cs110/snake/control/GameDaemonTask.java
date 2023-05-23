package cn.edu.sustech.cs110.snake.control;

import cn.edu.sustech.cs110.snake.Context;
import cn.edu.sustech.cs110.snake.enums.GridState;
import cn.edu.sustech.cs110.snake.events.BeanAteEvent;
import cn.edu.sustech.cs110.snake.events.BoardRerenderEvent;
import cn.edu.sustech.cs110.snake.events.GameOverEvent;
import cn.edu.sustech.cs110.snake.model.Game;
import cn.edu.sustech.cs110.snake.model.Position;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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

        // TODO: manage the `diffs` map, you should add the correct changes into it


        if (headFwd.equals(game.getBean())) {
            game.getSnake().getBody().add(0, headFwd);
            diffs.put(headFwd, GridState.SNAKE_ON);

            Position newTail = game.getSnake().getBody().get(game.getSnake().getBody().size() - 1);
            diffs.put(newTail, GridState.SNAKE_ON);

            Position newOne = new Position(Context.INSTANCE.random().nextInt(game.getRow()), Context.INSTANCE.random().nextInt(game.getCol()));
            game.setBean(newOne);
            diffs.put(newOne,GridState.BEAN_ON);

            Context.INSTANCE.eventBus().post(new BeanAteEvent(diffs));
        }
        else {
            game.getSnake().getBody().add(0, headFwd);
            diffs.put(headFwd, GridState.SNAKE_ON);
            diffs.put(game.getSnake().getBody().get(game.getSnake().getBody().size() - 1), GridState.EMPTY);
            game.getSnake().getBody().remove(game.getSnake().getBody().size() - 1);
        }
        Context.INSTANCE.eventBus().post(new BoardRerenderEvent(diffs));
    }
}
