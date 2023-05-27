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

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.concurrent.*;
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

    private Button homeQuit;

    @FXML
    private Text textCurrentScore;

    @FXML
    private Text textTimeAlive;

    @FXML
    private GameBoard board;

    private long MOVE_DURATION = 500;

    @SuppressWarnings("AlibabaThreadPoolCreation")
    final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

    @SuppressWarnings("java:S3077")
    volatile ScheduledFuture<?> gameDaemonTask;
    public Game game;
    private Stage gameStage;



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        scheduler.scheduleAtFixedRate(() -> {
            if (!Context.INSTANCE.currentGame().isPlaying()) {
                return;
            }
            gameStage = (Stage) root.getScene().getWindow();
            // TODO: add some code here
        }, 0, 1000, TimeUnit.MILLISECONDS);

        setupDaemonScheduler();

        Platform.runLater(this::bindAccelerators);
        board.paint(Context.INSTANCE.currentGame());
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
        // TODO: change the text in menu's pause item and button
        Context.INSTANCE.currentGame().setPlaying(!Context.INSTANCE.currentGame().isPlaying());
    }

    public void doRestart() {
        // 取消之前的任务
        if (Objects.nonNull(gameDaemonTask)) {
            gameDaemonTask.cancel(true);
        }
        // 创建新的游戏对象
        Context.INSTANCE.currentGame(new Game(15, 15, Context.INSTANCE.getCurrentUser()));
        new AdvancedStage("game.fxml")
                .withTitle("Snake")
                .shows();
        gameStage.close();
    }

    public void QuitToHome() {
        if (Objects.nonNull(gameDaemonTask)) {
            gameDaemonTask.cancel(true);
        }
        // 返回主页
        new AdvancedStage("home.fxml")
                .withTitle("HOME")
                .shows();
        gameStage.close();
    }

    public void record() throws IOException {
        File file = new File("record.txt");
        FileWriter writer = new FileWriter(file,true);
        writer.write(game.getPlayer() + " " + game.getScore());
    }

    public void QuitToLogin() {
        if (Objects.nonNull(gameDaemonTask)) {
            gameDaemonTask.cancel(true);
        }
        // 返回登录界面
        new AdvancedStage("login.fxml")
                .withTitle("Login in")
                .shows();
        gameStage.close();
    }

    public void toggleMusic() {
        // TODO: add some code here
    }

    public void doSave() throws FileNotFoundException {
        // TODO: add some code here
        File file = new File(Context.INSTANCE.currentGame().getPlayer()+"Archive.txt");
        PrintWriter save = new PrintWriter(file);
        //存豆子位置
        save.println(Context.INSTANCE.currentGame().getBean().getX()+" "+Context.INSTANCE.currentGame().getBean().getY());
        //存持续时间
        save.println(Context.INSTANCE.currentGame().getDuration());
        //存分数
        save.println(Context.INSTANCE.currentGame().getScore());
        //存蛇身位置集合
        for (int i = Context.INSTANCE.currentGame().getSnake().getBody().size(); i > 0 ; i--) {
            save.println(Context.INSTANCE.currentGame().getSnake().getBody().get(i).getX()+" "+Context.INSTANCE.currentGame().getSnake().getBody().get(i).getY());
        }

        save.close();
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
        board.repaint(event.getDiff());
    }

    @Subscribe
    public void gameOver(GameOverEvent event) {



        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Game Over");
            alert.setContentText("Game Over！Please choose to go back to the home screen or restart a new game!");

            // 添加两个按钮：回到主页面和重新开始
            ButtonType backToMainButton = new ButtonType("Back to the home screen");
            ButtonType restartButton = new ButtonType("Restart");

            alert.getButtonTypes().setAll(backToMainButton, restartButton);

            // 显示弹窗并等待用户选择
            Optional<ButtonType> result = alert.showAndWait();

            // 根据用户选择执行相应操作
            if (result.isPresent()) {
                if (result.get() == backToMainButton) {
                    alert.close();
                    QuitToHome();
                } else if (result.get() == restartButton) {
                    doRestart();
                }
            }
        });
    }
}