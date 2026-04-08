package Visualization;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class PannableGridApp extends Application {
    private double offsetX = 0;
    private double offsetY = 0;

    private double lastMouseX;
    private double lastMouseY;

    private static final double GRID_SIZE = 40;

    @Override
    public void start(Stage stage) {
        Canvas canvas = new Canvas(800, 600);
        Pane root = new Pane(canvas);

        drawGrid(canvas);

        canvas.setOnMousePressed(this::handleMousePressed);
        canvas.setOnMouseDragged(e -> {
            double dx = e.getX() - lastMouseX;
            double dy = e.getY() - lastMouseY;

            offsetX += dx;
            offsetY += dy;

            lastMouseX = e.getX();
            lastMouseY = e.getY();

            drawGrid(canvas);
        });

        Scene scene = new Scene(root, 800, 600);
        stage.setScene(scene);
        stage.show();
    }

    private void handleMousePressed(MouseEvent e) {
        lastMouseX = e.getX();
        lastMouseY = e.getY();
    }

    private void drawGrid(Canvas canvas) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        double width = canvas.getWidth();
        double height = canvas.getHeight();

        gc.clearRect(0, 0, width, height);

        double startX = mod(offsetX, GRID_SIZE);
        double startY = mod(offsetY, GRID_SIZE);

        for (double x = startX; x < width; x += GRID_SIZE) {
            gc.strokeLine(x, 0, x, height);
        }

        for (double y = startY; y < height; y += GRID_SIZE) {
            gc.strokeLine(0, y, width, y);
        }
    }

    private double mod(double value, double modulus) {
        double result = value % modulus;
        return result < 0 ? result + modulus : result;
    }

    public static void main(String[] args) {
        launch(args);
    }


}
