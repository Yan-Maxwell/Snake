package cn.edu.sustech.cs110.snake.control;

import cn.edu.sustech.cs110.snake.Context;
import cn.edu.sustech.cs110.snake.view.AdvancedStage;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Optional;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class LoginController {
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button login;

    public void login() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        try {
            File usersFile = new File(username+".txt");
            //新用户则保存密码并进入home
            if (!usersFile.exists()) {
                PrintWriter output = new PrintWriter(usersFile);
                output.println(password);
                output.close();

                Context.INSTANCE.setCurrentUser(username);
                new AdvancedStage("home.fxml")
                        .withTitle("HOME")
                        .shows();
                login.getScene().getWindow().hide();

            }
            //老用户则校对密码并弹alert或进入home
            else if (usersFile.exists()) {
                Scanner input = new Scanner(usersFile);
                String correctPassword = input.nextLine();
                if (!password.equals(correctPassword)) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Incorrect Password");
                    alert.setHeaderText(null);
                    alert.setContentText("Your password is incorrect!");
                    ButtonType backButton = new ButtonType("Back");
                    alert.getButtonTypes().setAll(backButton);
                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.isPresent() && result.get() == backButton) {
                        return;
                    }
                    input.close();
                }
                else {
                    Context.INSTANCE.setCurrentUser(username);
                    new AdvancedStage("home.fxml")
                            .withTitle("HOME")
                            .shows();
                    login.getScene().getWindow().hide();
                }
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


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
