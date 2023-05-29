package cn.edu.sustech.cs110.snake.model;

import cn.edu.sustech.cs110.snake.enums.Direction;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.List;

public class Wall {
    private final List<Position> wall = new ArrayList<>(15 * 15 / 2);

    @java.lang.SuppressWarnings("all")
    public List<Position> getWall() {
        return this.wall;
    }
    public Wall(int map){
        if(map==1){
                for(int i =8; i<17; i++){
                    wall.add(new Position(15,i));
                }
                for(int j =8; j<21; j++){
                    wall.add(new Position(j,7));
                }
                for(int j =8; j<21; j++){
                    wall.add(new Position(j,17));
                }
        }
        else if (map==2) {
                for(int i =8; i<21; i++){
                    wall.add(new Position(i,5));
                }
                for(int j =6; j<19; j++){
                    wall.add(new Position(20,j));
                }
        }
        else{
            for(int j=8; j<20;j++){
                wall.add(new Position(7,j));
            }
            for(int i = 8; i<21;i++){
                wall.add(new Position(i,15));
            }
        }
    }

    public Wall(){}

    @java.lang.SuppressWarnings("all")
    public List<Position> getThisWall() {
        return this.wall;
    }
}
