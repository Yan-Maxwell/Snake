package cn.edu.sustech.cs110.snake.model;

import cn.edu.sustech.cs110.snake.Context;
import cn.edu.sustech.cs110.snake.enums.Direction;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Game {
    private String player;
    private final int row;
    private final int col;
    private final Snake snake;
    public static Position bean;
    private String duration;
    private int score;
    private boolean playing;
    private String[] p;
    private int[] s;

    public Game(int row, int col, String str) throws FileNotFoundException {
        this.row = row;
        this.col = col;
        this.player = str;
        this.setScore(0);
        this.snake = new Snake(Direction.random());
        this.snake.getBody().add(new Position(row / 2, col / 2));
        this.bean = new Position(Context.INSTANCE.random().nextInt(row), Context.INSTANCE.random().nextInt(col));
        Map<String, Integer> rank = new HashMap<>();
        File file = new File("records.txt");
        Scanner read = new Scanner(file);
        //记录每个玩家及其最高分
        while (read.hasNext()){
            String temp1 = read.next();
            int temp2 = read.nextInt();
            if (!rank.containsKey(temp1)){
                rank.put(temp1,temp2);
            } else if (temp2>rank.get(temp1)) {
                rank.put(temp1,temp2);
            }
        }

        int max = 0;
        //更新排行榜
//        for(int i=0;i<2;i++){
//            Collection<Integer> c = rank.values();
//            Object[] obj = c.toArray();
//            Arrays.sort(obj);
//            s[i] = (int) obj[rank.size()-1];
//            p[i] = getKey(rank,s[i]);
//        }

    }

    public String getKey(Map map,int n){
        Iterator<Object> it = map.keySet().iterator();
        while (it.hasNext()){
            String key = it.next().toString();
            if(map.get(key).equals(n)){
                return key;
            }
        }
        return null;
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


}
