package cn.edu.sustech.cs110.snake.model;

import cn.edu.sustech.cs110.snake.Context;
import cn.edu.sustech.cs110.snake.enums.Direction;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;

public class Game {
    private String player;
    private final int row;
    private final int col;
    private final Snake snake;
    private Wall wall;
    public static Position bean;
    private String duration;
    private int score;
    private int highestScore;
    private boolean playing;
    private int difficulty;
    private int map;

    public Game(int row, int col, String str) {
        this.row = row;
        this.col = col;
        this.player = str;
        this.setScore(0);
        this.setHighestScore(0);
        this.snake = new Snake(Direction.random());
        this.snake.getBody().add(new Position(row / 2, col / 2));
        this.setBean(new Position(Context.INSTANCE.random().nextInt(row), Context.INSTANCE.random().nextInt(col)));
        this.difficulty=1;
        this.wall = new Wall();
        this.setDuration("00:00:000");
    }

    public Game(int row, int col, String str,int difficulty, int map) {
        this.row = row;
        this.col = col;
        this.player = str;
        this.setScore(0);
        this.setHighestScore(0);
        this.snake = new Snake(Direction.random());
        this.snake.getBody().add(new Position(row / 2, col / 2));
        this.bean = new Position(Context.INSTANCE.random().nextInt(row), Context.INSTANCE.random().nextInt(col));
        this.difficulty = difficulty;
        this.map = map;
        this.wall = new Wall(map);
        this.setDuration("00:00:000");
    }

    public Game(int row, int col, String str,int difficulty, int map, int score) {
        this.row = row;
        this.col = col;
        this.player = str;
        this.score = score;
        this.setHighestScore(0);
        this.snake = new Snake(Direction.random());
        this.snake.getBody().add(new Position(row / 2, col / 2));
        this.bean = new Position(Context.INSTANCE.random().nextInt(row), Context.INSTANCE.random().nextInt(col));
        this.difficulty = difficulty;
        this.map = map;
        this.wall = new Wall(map);
    }

    public void setBean(final Position bean) {
        this.bean = bean;
    }

    public void setDuration(final String duration) {
        this.duration = duration;
    }

    public void setPlaying(final boolean playing) {
        this.playing = playing;
    }

    public String getPlayer() {
        return this.player;
    }

    public void setPlayer(String str){
        this.player=str;
    }

    public int getRow() {
        return this.row;
    }

    public int getCol() {
        return this.col;
    }

    public Snake getSnake() {
        return this.snake;
    }

    public Position getBean() {
        return this.bean;
    }

    public String getDuration() {
        return this.duration;
    }

    public boolean isPlaying() {
        return this.playing;
    }

    @Override
    public java.lang.String toString() {
        return "Game(row=" + this.getRow() + ", col=" + this.getCol() + ", snake=" + this.getSnake() + ", bean=" + this.getBean() + ", duration=" + this.getDuration() + ", playing=" + this.isPlaying() + ")";
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }


    public int getHighestScore() {
        return highestScore;
    }

    public void setHighestScore(int highestScore) {
        this.highestScore = highestScore;
    }

    public Wall getWall() {
        return wall;
    }

    public void setWall(Wall wall) {
        this.wall = wall;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public int getMap() {
        return map;
    }

    public void setMap(int map) {
        this.map = map;
    }
}
