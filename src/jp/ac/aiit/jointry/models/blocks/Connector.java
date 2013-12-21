package jp.ac.aiit.jointry.models.blocks;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import jp.ac.aiit.jointry.models.blocks.statement.codeblock.CodeBlock;

public class Connector extends Rectangle {

    private Block holder;
    private Position position;

    public enum Position {

        TOP, BOTTOM, LEFT, RIGHT, CENTER,
        INSIDE_LEFT, INSIDE_RIGHT, INSIDE
    };

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public Block getHolder() {
        return holder;
    }

    public void setHolder(Block holder) {
        this.holder = holder;
    }

    public void touch() {
        this.setFill(Color.GOLD);
    }

    public void detouch() {
        this.setFill(Color.TRANSPARENT);
    }
}
