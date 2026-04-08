package Visualization;

import Tree.RedBlackTree;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.Group;
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

    double gridLastX, gridLastY;

    double nodeLastX, nodeLastY;

    double scale;

    int LEVEL_HEIGHT = 150;
    int CIRCLE_RADIUS = 20;
    int MAX_DIM = 1000;
    double ZOOM_FACTOR = 1.05;



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        gridLastX = 0;
        gridLastY = 0;
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
            gridLastX =  e.getSceneX();
            gridLastY = e.getSceneY();
        });

        canvas.setOnMouseDragged(e -> {
            double dx = (e.getSceneX() - gridLastX);
            double dy = (e.getSceneY() - gridLastY);

            gridLastX = e.getSceneX();
            gridLastY = e.getSceneY();

            canvas.setTranslateX(canvas.getTranslateX() + dx);
            canvas.setTranslateY(canvas.getTranslateY() + dy);

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

    public Group draw(RedBlackTree.RedBlackNode<Integer> root, int minX, int maxX, int depth){
        if (tree.isNil(root)){
            return null;
        }
        int targetX = (minX + maxX)/2;
        int targetY = depth * LEVEL_HEIGHT;

        Group nodeGp = new Group();
        nodeGp.setLayoutX(targetX);
        nodeGp.setLayoutY(targetY);

        Circle node = new Circle(0, 0, CIRCLE_RADIUS); //relative to the group
        Text label = new Text(String.valueOf(root.data));
        label.setX(- label.getLayoutBounds().getWidth() / 2); //relative to the gp
        label.setY(label.getLayoutBounds().getHeight() / 4);

        nodeGp.getChildren().addAll(node, label);
        canvas.getChildren().add(nodeGp);


        nodeGp.setOnMousePressed(e -> {
            Point2D p = canvas.sceneToLocal(e.getSceneX(), e.getSceneY());
            nodeLastX = p.getX();
            nodeLastY = p.getY();
            e.consume();
        });

        nodeGp.setOnMouseDragged(e -> {
            Point2D p = canvas.sceneToLocal(e.getSceneX(), e.getSceneY());

            double dx = p.getX() - nodeLastX;
            double dy = p.getY() - nodeLastY;

            nodeGp.setTranslateX(nodeGp.getTranslateX() + dx);
            nodeGp.setTranslateY(nodeGp.getTranslateY() + dy);

            nodeLastX = p.getX();
            nodeLastY = p.getY();

            e.consume();
        });




        label.setFill(Color.WHITE);
        node.getStyleClass().add((root.color == RedBlackTree.Color.BLACK)? "black-node" : "red-node");



        if (!tree.isNil(root.left)){

            Group leftNode = draw((RedBlackTree.RedBlackNode<Integer>) root.left, minX, targetX, depth + 1);
            connectNodes(nodeGp, leftNode);
        }

        if (!tree.isNil(root.right)){
            Group rightNode = draw((RedBlackTree.RedBlackNode<Integer>) root.right, targetX, maxX, depth + 1);
            connectNodes(nodeGp, rightNode);
        }
        return nodeGp;
    }

    private void connectNodes(Group first, Group second) {
        Line line = new Line();

        line.startXProperty().bind(
                first.layoutXProperty().add(first.translateXProperty())
        );
        line.startYProperty().bind(
                first.layoutYProperty().add(first.translateYProperty())
        );

        line.endXProperty().bind(
                second.layoutXProperty().add(second.translateXProperty())
        );
        line.endYProperty().bind(
                second.layoutYProperty().add(second.translateYProperty())
        );

        canvas.getChildren().addFirst(line);
    }

}
