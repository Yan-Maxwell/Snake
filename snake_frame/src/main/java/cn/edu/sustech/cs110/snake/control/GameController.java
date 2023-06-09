package cn.edu.sustech.cs110.snake.control;

import cn.edu.sustech.cs110.snake.Context;
import cn.edu.sustech.cs110.snake.enums.Direction;
import cn.edu.sustech.cs110.snake.events.*;
import cn.edu.sustech.cs110.snake.model.Game;
import cn.edu.sustech.cs110.snake.model.Position;
import cn.edu.sustech.cs110.snake.view.AdvancedStage;
import cn.edu.sustech.cs110.snake.view.components.GameBoard;
import com.google.common.eventbus.Subscribe;
import javafx.application.Platform;
import javafx.fxml.FXML;
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

    @FXML
    private Text textPlayerName;
    @FXML
    private Text textPlayerHighest;

    private long startTime; // 记录游戏开始时间
    private long elapsedTime; // 记录游戏已经过去的时间
    private Timer timer; // 用于定时更新游戏时间

    private long MOVE_DURATION;

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
        Context.INSTANCE.currentGame().setPlaying(!Context.INSTANCE.currentGame().isPlaying());
        if (Context.INSTANCE.currentGame().isPlaying()) {
            startTimer();
        } else {
            stopTimer();
        }
    }

    public void doRestart() throws FileNotFoundException {
        // 取消之前的任务
        if (Objects.nonNull(gameDaemonTask)) {
            gameDaemonTask.cancel(true);
        }
        // 创建新的游戏对象
        Context.INSTANCE.currentGame(new Game(25, 25, Context.INSTANCE.getCurrentUser(),Context.INSTANCE.currentGame().getDifficulty(), Context.INSTANCE.currentGame().getMap()));
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
        stopTimer();
        elapsedTime = 0;
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

    public void record() throws IOException {
        File file = new File("records.txt");
        PrintWriter pw = new PrintWriter(new FileWriter(file,true));
        pw.println(Context.INSTANCE.currentGame().getPlayer()+" "+Context.INSTANCE.currentGame().getScore());
        pw.close();
    }

    private void updateScoreLabel() {
        int score = Context.INSTANCE.currentGame().getScore();
        Platform.runLater(() -> {
            textCurrentScore.setText("Current score: "+String.valueOf(score));
            Context.INSTANCE.currentGame().setScore(score);
        });
    }
    

    public void doSave() throws IOException {
        File file = new File(Context.INSTANCE.currentGame().getPlayer()+"Archive.txt");
        PrintWriter save = new PrintWriter(file);
        //存豆子位置
        save.println(Context.INSTANCE.currentGame().getBean().getX()+" "+Context.INSTANCE.currentGame().getBean().getY());
        //存持续时间
        save.println(Context.INSTANCE.currentGame().getDuration());
        //存分数
        save.println(Context.INSTANCE.currentGame().getScore());
        //存个人最高分
        File file2 = new File("rank.txt");
        Scanner read = new Scanner(file2);
        int highest = 0;
        while(read.hasNext()){
            if(read.next().equals(Context.INSTANCE.currentGame().getPlayer())){
                int test = read.nextInt();
                if (highest<test){
                    highest=test;
                }

            }
        }
        save.println(highest);
        //存地图
        save.println(Context.INSTANCE.currentGame().getMap());
        //存难度
        save.println(Context.INSTANCE.currentGame().getDifficulty());
        //存蛇身位置集合
        for (int i = Context.INSTANCE.currentGame().getSnake().getBody().size()-1; i > 0 ; i--) {
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

    private void startTimer() {
        startTime = System.currentTimeMillis();
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateElapsedTime();
                updateGameTimeLabel();
            }
        }, 0, 100); // 每100毫秒更新一次时间
    }

    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private void updateElapsedTime() {
        if (Context.INSTANCE.currentGame().isPlaying()) {
            long currentTime = System.currentTimeMillis();
            elapsedTime += currentTime - startTime;
            startTime = currentTime;
        }
    }

    private void updateGameTimeLabel() {
        long totalSeconds = elapsedTime / 1000;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        long milliseconds = elapsedTime % 1000;

        Platform.runLater(() -> {
            textTimeAlive.setText("Time alive: "+String.format("%02d:%02d:%03d", minutes, seconds, milliseconds)+" s");
            Context.INSTANCE.currentGame().setDuration(String.format("%02d:%02d:%03d", minutes, seconds, milliseconds));
        });
    }

    private void setupDaemonScheduler() {
        int difficulty = Context.INSTANCE.currentGame().getDifficulty();

        if(difficulty==1){
            this.MOVE_DURATION=500;
        } else if (difficulty==2) {
            this.MOVE_DURATION=350;
        }
        else{
            this.MOVE_DURATION=200;
        }
        if (Objects.nonNull(gameDaemonTask)) {
            gameDaemonTask.cancel(true);
        }
        gameDaemonTask = scheduler.scheduleAtFixedRate(
                new GameDaemonTask(),
                0, MOVE_DURATION,
                TimeUnit.MILLISECONDS
        );

        textPlayerName.setText("Player: "+Context.INSTANCE.getCurrentUser());
        textPlayerHighest.setText("History highest:"+Context.INSTANCE.currentGame().getHighestScore());
    }

    @Subscribe
    public void rerenderChanges(BoardRerenderEvent event) {
        board.repaint(event.getDiff());
    }

    @Subscribe
    public void beanAte(BeanAteEvent event) {
        board.repaint(event.getDiff());
        updateScoreLabel();
    }

    @Subscribe
    public void gameOver(GameOverEvent event) throws IOException {

        record();

        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("GAME OVER!\n"+"\n"+"Player: "+Context.INSTANCE.getCurrentUser()+"\n"+"Your score: "+Context.INSTANCE.currentGame().getScore()+"\n"+"Time alive: "+Context.INSTANCE.currentGame().getDuration()+" s");
            alert.setContentText("Please choose to go back to the home screen or restart a new game!");

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
                    try {
                        doRestart();
                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
    }
}