package cn.edu.sustech.cs110.snake.control;

import cn.edu.sustech.cs110.snake.Context;
import cn.edu.sustech.cs110.snake.enums.Direction;
import cn.edu.sustech.cs110.snake.enums.GridState;
import cn.edu.sustech.cs110.snake.events.*;
import cn.edu.sustech.cs110.snake.model.Game;
import cn.edu.sustech.cs110.snake.model.Position;
import cn.edu.sustech.cs110.snake.view.AdvancedStage;
import cn.edu.sustech.cs110.snake.view.components.GameBoard;
import com.google.common.eventbus.Subscribe;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.*;

public class GameController implements Initializable {

    @FXML
    private Parent root;

    @FXML
    private MenuItem menuPause;

    @FXML
    private Button btnPause;

    @FXML
    private Text textCurrentScore;

    @FXML
    private Text textTimeAlive;

    @FXML
    private GameBoard board;

    @FXML
    private Text textPlayerName;

    private static final long MOVE_DURATION = 500;

    @SuppressWarnings("AlibabaThreadPoolCreation")
    final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

    @SuppressWarnings("java:S3077")
    volatile ScheduledFuture<?> gameDaemonTask;
    public Game game;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        scheduler.scheduleAtFixedRate(() -> {
            if (!Context.INSTANCE.currentGame().isPlaying()) {
                return;
            }
            // TODO: add some code here
            else{Context.INSTANCE.currentGame(new Game(15, 15, Context.INSTANCE.getCurrentUser()));
            new AdvancedStage("game.fxml")
                    .withTitle("Snake")
                    .shows();}
        }, 0, 1000, TimeUnit.MILLISECONDS);

        setupDaemonScheduler();

        Platform.runLater(this::bindAccelerators);
        board.paint(Context.INSTANCE.currentGame());
        textPlayerName.setText("Player: "+Context.INSTANCE.getCurrentUser());
    }

    private void bindAccelerators() {
        Scene scene = root.getScene();

        scene.getAccelerators().put(new KeyCodeCombination(KeyCode.P), this::togglePause);
        scene.getAccelerators().put(new KeyCodeCombination(KeyCode.W), this::turnUp);
        scene.getAccelerators().put(new KeyCodeCombination(KeyCode.S), this::turnDown);
        scene.getAccelerators().put(new KeyCodeCombination(KeyCode.A), this::turnLeft);
        scene.getAccelerators().put(new KeyCodeCombination(KeyCode.D), this::turnRight);
    }

    public void togglePause() {
        Context.INSTANCE.currentGame().setPlaying(!Context.INSTANCE.currentGame().isPlaying());
    }

    public void toggleRestart(){
        Context.INSTANCE.currentGame().setPlaying(Context.INSTANCE.currentGame().isPlaying());
    }

    public void doRestart() {
        // TODO: add some code here
    }

    public void doRecover() {
        // TODO: add some code here
    }

    public void doSave() {
        // TODO: add some code here
    }

    public void doQuit() {
        // TODO: add some code here
    }

    public void toggleMusic() {
        // TODO: add some code here
    }

    public void turnLeft() {
        Context.INSTANCE.currentGame().getSnake().setDirection(Direction.LEFT);
    }

    public void turnUp() {
        Context.INSTANCE.currentGame().getSnake().setDirection(Direction.UP);
    }

    public void turnRight() {
        Context.INSTANCE.currentGame().getSnake().setDirection(Direction.RIGHT);
    }

    public void turnDown() {
        Context.INSTANCE.currentGame().getSnake().setDirection(Direction.DOWN);
    }

    public void changeDifficulty() {
        setupDaemonScheduler();
    }

    private void setupDaemonScheduler() {
        if (Objects.nonNull(gameDaemonTask)) {
            gameDaemonTask.cancel(true);
        }
        gameDaemonTask = scheduler.scheduleAtFixedRate(
                new GameDaemonTask(),
                0, MOVE_DURATION,
                TimeUnit.MILLISECONDS
        );
    }

    @Subscribe
    public void rerenderChanges(BoardRerenderEvent event) {
        board.repaint(event.getDiff());
    }

    @Subscribe
    public void beanAte(BeanAteEvent event) {
        Position headFwd = game.getSnake().getBody().get(0).toward(game.getSnake().getDirection());
        board.repaint(event.getDiff());
        Position prep =game.getSnake().getBody().get(game.getSnake().getBody().size()-1);
        game.getSnake().getBody().add(prep);
        Map<Position, GridState> diffs = new HashMap<>(3);
        diffs.put(prep,GridState.SNAKE_ON);
    }

    @Subscribe
    public void gameOver(GameOverEvent event) {
        Context.INSTANCE.currentGame().setPlaying(false);

        // TODO: add some code here
        System.out.println("Game over");
    }

    public static void showGameView() {
        new AdvancedStage("game.fxml")
                .withTitle("Snake")
                .shows();
    }
}