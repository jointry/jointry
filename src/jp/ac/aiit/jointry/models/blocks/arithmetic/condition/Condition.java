package jp.ac.aiit.jointry.models.blocks.arithmetic;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import jp.ac.aiit.jointry.models.blocks.Block;
import jp.ac.aiit.jointry.models.blocks.Connector;
import jp.ac.aiit.jointry.models.blocks.procedure.codeblock.If;

public class Condition extends Block {

    protected final Rectangle rect;
    public If mother;

    public Condition() {
        super();
        /*
         // Use Filter (not Handler) to fire first.
         addEventFilter(MouseEvent.MOUSE_DRAGGED, new EventHandler<MouseEvent>() {
         @Override
         public void handle(MouseEvent mouseEvent) {
         // Initialize
         myBlock.initializeLink();

         // Move
         double dx = mouseEvent.getSceneX() + anchorX;
         double dy = mouseEvent.getSceneY() + anchorY;
         myBlock.move(dx, dy);

         Connector con = getCollision();
         if (con == null) {
         return;
         }
         myBlock.con = con;

         // 内部の接続
         if (con.getPosition() == Connector.Position.LEFT) {
         if (con.getHolder() instanceof If
         && myBlock instanceof Condition) {
         If target = (If) con.getHolder();
         target.addEmbryo((Condition) myBlock);
         }
         }
         }
         });*/

        rect = new Rectangle();
        rect.setWidth(250);
        rect.setHeight(50);
        rect.setArcWidth(10);
        rect.setArcHeight(10);
        rect.setStroke(Color.GRAY);
        rect.setFill(getColor());

        AnchorPane.setTopAnchor(rect, 0.0);
        Label lb = getLabel();
        AnchorPane.setTopAnchor(lb, 10.0);
        AnchorPane.setLeftAnchor(lb, 150.0);

        getChildren().addAll(rect, lb);

        // コネクタを全面に出すために
        rect.toBack();
    }

    public static Color getColor() {
        return Color.GREEN;
    }

    public Label getLabel() {
        return new Label("じょうけん");
    }

    protected Connector getCollision() {
        Connector connector = null;
        BorderPane root = (BorderPane) getScene().getRoot();
        TabPane tabs = (TabPane) root.getCenter();

        for (Tab tab : tabs.getTabs()) {
            if (tab == null) {
                continue;
            }
            if (!"scriptPane".equals(tab.getContent().getId())) {
                continue;
            }

            // Inside scriptPane
            /*
             AnchorPane scriptPane = (AnchorPane) tab.getContent();
             for (Node node : scriptPane.getChildren()) {
             if (node == myBlock) {
             continue;
             }
             if (!(node instanceof Block)) {
             continue;
             }

             // Inside Block
             Block target = (Block) node;
             for (Node n : target.getChildren()) {
             if (n instanceof Connector) {
             Connector con = (Connector) n;
             con.setFill(Color.TRANSPARENT);
             Shape intersect = null;

             // 内部の接触
             intersect = Shape.intersect(con, myBlock.rightCon);
             if (intersect.getBoundsInLocal().getWidth() != -1) {
             connector = con;
             break;
             }

             }
             }
             * */
//        }
        }
        return connector;
    }
}
