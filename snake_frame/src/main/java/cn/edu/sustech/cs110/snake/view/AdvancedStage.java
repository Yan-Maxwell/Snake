package cn.edu.sustech.cs110.snake.view;

import cn.edu.sustech.cs110.snake.Context;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import java.net.URL;
import java.util.Objects;

public class AdvancedStage extends Stage {
    public AdvancedStage(String filename) {
        try {
            URL fxml = getClass().getResource(filename);
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(fxml);
            Scene scene = new Scene(fxmlLoader.load());
            super.setScene(scene);
            Object controller = fxmlLoader.getController();
            if (Objects.nonNull(controller)) {
                Context.INSTANCE.eventBus().register(controller);
                setOnCloseRequest(event -> Context.INSTANCE.eventBus().unregister(controller));
            }
        } catch (final java.lang.Throwable $ex) {
            throw lombok.Lombok.sneakyThrow($ex);
        }
    }

    public AdvancedStage withTitle(String title) {
        setTitle(title);
        return this;
    }

    public AdvancedStage fitScreen(double minWidthPct, double maxWidthPct) {
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        setMinWidth(screenBounds.getWidth() * minWidthPct);
        setMaxWidth(screenBounds.getWidth() * maxWidthPct);
        return this;
    }

    public AdvancedStage fixedRatio(double whRatio) {
        minHeightProperty().bind(widthProperty().divide(whRatio));
        maxHeightProperty().bind(widthProperty().divide(whRatio));
        return this;
    }

    public void shows() {
        show();
        centerOnScreen();
    }
}
