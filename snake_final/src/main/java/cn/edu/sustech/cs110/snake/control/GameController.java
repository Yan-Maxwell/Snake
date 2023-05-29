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

    private long startTime;
    private long elapsedTime;
    private Timer timer;

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
        elapsedTime = convertDurationToMilliseconds(Context.INSTANCE.currentGame().getDuration());
        setupDaemonScheduler();
        textTimeAlive.setText("Time alive: "+Context.INSTANCE.currentGame().getDuration()+" s");
        textCurrentScore.setText("Current score: "+Context.INSTANCE.currentGame().getScore());
        Platform.runLater(this::bindAccelerators);
        board.paint(Context.INSTANCE.currentGame());
    }

    private long convertDurationToMilliseconds(String duration) {
        String[] timeParts = duration.split(":");
        long minutes = Long.parseLong(timeParts[0]);
        long seconds = Long.parseLong(timeParts[1]);
        long milliseconds = Long.parseLong(timeParts[2]);

        return (minutes * 60 + seconds) * 1000 + milliseconds;
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
        } while (isBeanCollidingWithWall(BeanPosition));
        game.setBean(BeanPosition);
        gameStage.close();
        stopTimer();
        elapsedTime = 0;
        new AdvancedStage("game.fxml")
                .withTitle("Snake")
                .shows();
    }
    public static boolean isBeanCollidingWithWall(Position Bean) {
        Game game = Context.INSTANCE.currentGame();
        for (int j = 0; j < game.getWall().getThisWall().size(); j++) {
            if (Bean.equals(game.getWall().getThisWall().get(j))) {
                return true;
            }
        }
        return false;
    }
    public static boolean isBeanCollidingWithSnake(Position Bean) {
        Game game = Context.INSTANCE.currentGame();
        for (int j = 0; j < game.getSnake().getBody().size(); j++) {
            if (Bean.equals(game.getSnake().getBody().get(j))) {
                return true;
            }
        }
        return false;
    }
    public void QuitToHome() throws FileNotFoundException {
        if (Objects.nonNull(gameDaemonTask)) {
            gameDaemonTask.cancel(true);
        }

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
        //存地图
        save.println(Context.INSTANCE.currentGame().getMap());
        //存难度
        save.println(Context.INSTANCE.currentGame().getDifficulty());
        //存方向
        save.println(Context.INSTANCE.currentGame().getSnake().getDirection().getXDiff()+" "+Context.INSTANCE.currentGame().getSnake().getDirection().getYDiff());
        //存蛇身位置集合
        for (int i = Context.INSTANCE.currentGame().getSnake().getBody().size()-1 ; i >= 0 ; i--) {
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

        //记录本局游戏
        record();

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
                    try {
                        QuitToHome();
                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    }
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