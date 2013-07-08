/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.ac.aiit.jointry.paint;

import java.net.URL;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

/**
 * List Type UI Template.
 * 
 * @author kanemoto
 */
public class ListSelectOption {

    private ToolOption theOption = null;
    private VBox listOption = new VBox();

    protected void addOption(final ToolOption opt) {
        addOption(opt, false);
    }

    protected void addOption(final ToolOption opt, boolean on) {
        listOption.getChildren().add(opt);

        if (on) {
            selectOption(opt);
        }

        opt.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                selectOption(opt);
            }
        });
    }

    protected void selectOption(ToolOption opt) {
        if (opt == theOption) {
            return;
        }

        if (theOption != null) {
            theOption.setOff();
        }
        opt.setOn();
        theOption = opt;
    }

    protected int getValue() {
        return theOption.value;
    }

    protected Pane getPane() {
        return listOption;
    }

    public class ToolOption extends Button {

        int value;
        private Image onIcon;
        private Image offIcon;

        protected ToolOption(String name, int value) {
            this.value = value;
            setPrefSize(50, 50);
            this.onIcon = makeImageIcon(name + "_a");
            this.offIcon = makeImageIcon(name + "_n");
            super.setGraphic(new ImageView(offIcon));
            //super.setBorder(emptyBoder);
        }

        void setOn() {
            super.setGraphic(new ImageView(onIcon));
        }

        void setOff() {
            super.setGraphic(new ImageView(offIcon));
        }
    }

    private Image makeImageIcon(String resource) {
        URL url = getClass().getResource("img/" + resource + ".png");
        return new Image(url.toString());
    }
}
