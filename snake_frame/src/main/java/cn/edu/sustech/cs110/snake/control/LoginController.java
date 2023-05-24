package cn.edu.sustech.cs110.snake.control;

import cn.edu.sustech.cs110.snake.Context;
import cn.edu.sustech.cs110.snake.model.User;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;

import java.io.File;
import java.io.FileWriter;
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
            File usersFile = new File("users.txt");
            if (!usersFile.exists()) {
                // If the file doesn't exist, create a new file
                usersFile.createNewFile();
            }

            // Read user data from the file using Scanner
            Scanner in = new Scanner(usersFile);
            boolean userExists = false;
            boolean passwordCorrect = false;

            while (in.hasNextLine()) {
                String line = in.nextLine();
                String[] userFields = line.split(",");
                String existingUsername = userFields[0];
                String existingPassword = userFields[1];

                if (username.equals(existingUsername)) {
                    userExists = true;
                    if (password.equals(existingPassword)) {
                        passwordCorrect = true;
                        break;
                    }
                }
            }

            in.close();

            if (!userExists) {
                // Create a new user
                User newUser = new User(username, password);
                // Append the new user to the file
                FileWriter fileWriter = new FileWriter(usersFile, true);
                fileWriter.write(newUser.toString() + "\n");
                fileWriter.close();
            } else if (!passwordCorrect) {
                // Password is incorrect
                // TODO: Handle incorrect password
                return;
            }

            // Proceed to home screen
            Context.INSTANCE.setCurrentUser(username);
            HomeController.showHomeScreen();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
