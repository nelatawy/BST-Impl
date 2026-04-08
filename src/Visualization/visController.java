package Visualization;

import Tree.RedBlackTree;
import Tree.TreeNode;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;


public class visController implements Initializable {

    @FXML
    Pane viewport;

    Pane canvas;

    @FXML
    TextField value;

    RedBlackTree<Integer> tree;

    double offsetX, offsetY, lastX, lastY;
    double scale;

    int LEVEL_HEIGHT = 150;
    int CIRCLE_RADIUS = 20;
    int MAX_DIM = 1000;
    double ZOOM_FACTOR = 1.05;



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        offsetX = 0;
        offsetY = 0;
        lastX = 0;
        lastY = 0;
        scale= 1;

        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(viewport.widthProperty());
        clip.heightProperty().bind(viewport.heightProperty());
        viewport.setClip(clip);

        canvas = new Pane();
        canvas.setPrefWidth(MAX_DIM);
        canvas.setPrefHeight(MAX_DIM);
        canvas.setScaleX(1);
        canvas.setScaleY(1);

        viewport.setOnScroll(e -> {
            if (e.getDeltaY() > 0) {
                scale *= ZOOM_FACTOR;   // zoom in
            } else if(e.getDeltaY() < 0){
                scale /= ZOOM_FACTOR;   // zoom out
            }
            canvas.setScaleX(scale);
            canvas.setScaleY(scale);
        });

        canvas.setOnMousePressed(e -> {
            lastX =  e.getSceneX();
            lastY = e.getSceneY();
        });

        canvas.setOnMouseDragged(e -> {
            offsetX += (e.getSceneX() - lastX);
            offsetY += (e.getSceneY() - lastY);

            lastX = e.getSceneX();
            lastY = e.getSceneY();

            canvas.setTranslateX(offsetX);
            canvas.setTranslateY(offsetY);

        });

        viewport.getChildren().add(canvas);

        value.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d*") && newText.length() <= 4) {
                return change;
            }
            return null;
        }));
        tree = new RedBlackTree<>();

    }

    @FXML
    public void insertInTree(){
        try{
            Integer val = Integer.parseInt(value.getText());
            tree.insert(val);
            canvas.getChildren().clear();
            draw((RedBlackTree.RedBlackNode<Integer>) tree.getRoot(), 0, MAX_DIM, 1);
        } catch (Exception e){
            System.err.println("An error occurred while trying to insert");
        }

    }

    @FXML
    public void deleteFromTree(){
        try{
            Integer val = Integer.parseInt(value.getText());
            tree.delete(val);
            canvas.getChildren().clear();
            draw((RedBlackTree.RedBlackNode<Integer>) tree.getRoot(), 0, MAX_DIM, 1);
        } catch (Exception e){
            System.err.println("An error occurred while trying to delete");
        }

    }

    public void draw(RedBlackTree.RedBlackNode<Integer> root, int minX, int maxX, int depth){
        if (tree.isNil(root)){
            return;
        }
        int targetX = (minX + maxX)/2;
        int targetY = depth * LEVEL_HEIGHT;
        Circle node = new Circle(targetX, targetY, CIRCLE_RADIUS);
        Text label = new Text(String.valueOf(root.data));
        label.setX(targetX - label.getLayoutBounds().getWidth() / 2);
        label.setY(targetY + label.getLayoutBounds().getHeight() / 4);


        label.setFill(Color.WHITE);
        node.setFill((root.color == RedBlackTree.Color.BLACK)? Color.BLACK : Color.RED);


        canvas.getChildren().addAll(node, label);
        if (!tree.isNil(root.left)){
            Line linel = new Line(targetX, targetY + CIRCLE_RADIUS, (double) (minX + targetX) /2, (depth + 1)*LEVEL_HEIGHT - CIRCLE_RADIUS);
            canvas.getChildren().add(linel);
            draw((RedBlackTree.RedBlackNode<Integer>) root.left, minX, targetX, depth + 1);
        }

        if (!tree.isNil(root.right)){
            Line liner = new Line(targetX, targetY + CIRCLE_RADIUS, (double) (maxX + targetX) /2, (depth + 1)*LEVEL_HEIGHT - CIRCLE_RADIUS);
            canvas.getChildren().add(liner);
            draw((RedBlackTree.RedBlackNode<Integer>) root.right, targetX, maxX, depth + 1);
        }
    }

}
