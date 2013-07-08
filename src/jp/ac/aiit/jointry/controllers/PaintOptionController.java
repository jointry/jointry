/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.ac.aiit.jointry.controllers;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import jp.ac.aiit.jointry.paint.PaintTool;
import jp.ac.aiit.jointry.paint.PtEraser;
import jp.ac.aiit.jointry.paint.PtPencil;

/**
 *
 * @author kanemoto
 */
public class PaintOptionController implements Initializable {

    @FXML
    private VBox option;
    private GridPane paintToolChooser = new GridPane();
    private CardDeck optionCardDeck = new CardDeck(105, 158);
    private PaintTool thePaintTool = null;

    public PaintTool getPaintTool() {
        return thePaintTool;
    }

    private static class CardDeck extends AnchorPane {

        private Map<String, Node> map = new HashMap();

        CardDeck(int width, int height) {
            this.setPrefSize(width, height);
        }

        void addTool(PaintTool ptool) {
            map.put(ptool.getClass().getSimpleName(), ptool.getOptionPane());
        }

        void showCard(PaintTool ptool) {
            this.getChildren().clear();
            this.getChildren().add(map.get(ptool.getClass().getSimpleName()));
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        paintToolChooser.setHgap(4);
        paintToolChooser.setVgap(4);
        option.getChildren().add(paintToolChooser);
        option.getChildren().add(optionCardDeck);
        addTool(new PtPencil("pencil", "鉛筆"), 0, 0, true);
        addTool(new PtEraser("eraser", "消しゴム"), 1, 0);
    }

    private void addTool(final PaintTool ptool, int cIndex, int rIndex) {
        addTool(ptool, cIndex, rIndex, false);
    }

    private void addTool(final PaintTool ptool, int cIndex, int rIndex, boolean on) {
        paintToolChooser.add(ptool, cIndex, rIndex);

        //対応するオプション領域 デフォルトON
        optionCardDeck.addTool(ptool);
        if (on) {
            selectTool(ptool);
        }

        ptool.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                selectTool(ptool);
            }
        });
    }

    private void selectTool(PaintTool ptool) {
        if (ptool == thePaintTool) {
            return;
        }

        thePaintTool = ptool;
        optionCardDeck.showCard(ptool);
    }
}
