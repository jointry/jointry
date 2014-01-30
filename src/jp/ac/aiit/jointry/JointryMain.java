package jp.ac.aiit.jointry;

import java.util.Locale;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextInputControl;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import jp.ac.aiit.jointry.controllers.MainController;
import jp.ac.aiit.jointry.services.broker.app.JointryDialogBase;
import org.comtel.javafx.control.KeyBoardPopup;
import org.comtel.javafx.control.KeyBoardPopupBuilder;
import org.comtel.javafx.robot.RobotFactory;

public class JointryMain extends Application {

    private KeyBoardPopup popup;
    private boolean enableKeyboard = true;

    @Override
    public void start(Stage stage) throws Exception {
        final FXMLLoader loader;
        loader = new FXMLLoader(getClass().getResource("controllers/Main.fxml"));
        Parent parent = (Parent) loader.load();

        final MainController controller = loader.getController();
        JointryDialogBase.install(controller);

        controller.initWindow("new");

        stage.setOnHidden(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                controller.windowClose();
                Platform.exit();
            }
        });

        controller.setJointryMain(this);

        Scene scene = new Scene(parent);
        stage.setScene(scene);
        setupKeyboard(stage);
        popup.show(stage);
        stage.show();
    }

    private void setupKeyboard(Stage stage) {
        // appearance setting
        String fontUrl = this.getClass().getResource("/font/FontKeyboardFX.ttf").toExternalForm();
        Font f = Font.loadFont(fontUrl, -1);
        String css = this.getClass().getResource("/css/KeyboardButtonStyle.css").toExternalForm();
        stage.getScene().getStylesheets().add(css);

        // initialize
        popup = KeyBoardPopupBuilder.create().initScale(2.0).initLocale(Locale.ENGLISH).addIRobot(RobotFactory.createFXRobot()).build();

        // add keyboard scene listener to all text components
        stage.getScene().focusOwnerProperty().addListener(new ChangeListener<Node>() {
            @Override
            public void changed(ObservableValue<? extends Node> value, Node n1, Node n2) {
                if (n2 != null && n2 instanceof TextInputControl) {
                    setPopupVisible(true, (TextInputControl) n2);
                } else {
                    setPopupVisible(false, null);
                }
            }
        });

        popup.getKeyBoard().setOnKeyboardCloseButton(new EventHandler<Event>() {
            public void handle(Event event) {
                setPopupVisible(false, null);
            }
        });
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    private void setPopupVisible(final boolean b, final TextInputControl textNode) {
        if (!isEnableKeyboard()) {
            return;
        }

        Platform.runLater(new Runnable() {
            private Animation fadeAnimation;

            @Override
            public void run() {
                if (b) {
                    if (textNode != null) {
                        Rectangle2D textNodeBounds = new Rectangle2D(textNode.getScene().getWindow().getX()
                                                                     + textNode.getLocalToSceneTransform().getTx(), textNode.getScene().getWindow().getY()
                                                                                                                    + textNode.getLocalToSceneTransform().getTy(), textNode.getWidth(), textNode
                                .getHeight());

                        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
                        if (textNodeBounds.getMinX() + popup.getWidth() > screenBounds.getMaxX()) {
                            popup.setX(screenBounds.getMaxX() - popup.getWidth());
                        } else {
                            popup.setX(textNodeBounds.getMinX());
                        }

                        if (textNodeBounds.getMaxY() + popup.getHeight() > screenBounds.getMaxY()) {
                            popup.setY(textNodeBounds.getMinY() - popup.getHeight() + 20);
                        } else {
                            popup.setY(textNodeBounds.getMaxY() + 40);
                        }
                    }

                }

                if (fadeAnimation != null) {
                    fadeAnimation.stop();
                }
                if (!b) {
                    popup.hide();
                    return;
                }
                if (popup.isShowing()) {
                    return;
                }
                popup.getKeyBoard().setOpacity(0.0);

                FadeTransition fade = new FadeTransition(Duration.seconds(.5), popup.getKeyBoard());
                fade.setToValue(b ? 1.0 : 0.0);
                fade.setOnFinished(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        fadeAnimation = null;
                    }
                });

                ScaleTransition scale = new ScaleTransition(Duration.seconds(.5), popup.getKeyBoard());
                scale.setToX(b ? 1 : 0.8);
                scale.setToY(b ? 1 : 0.8);

                ParallelTransition tx = new ParallelTransition(fade, scale);
                fadeAnimation = tx;
                tx.play();
                if (b) {
                    if (!popup.isShowing()) {
                        popup.show(popup.getOwnerWindow());
                    }
                }

            }
        });
    }

    /**
     * @return the enableKeyboard
     */
    public boolean isEnableKeyboard() {
        return enableKeyboard;
    }

    /**
     * @param enableKeyboard the enableKeyboard to set
     */
    public void setEnableKeyboard(boolean enableKeyboard) {
        this.enableKeyboard = enableKeyboard;
    }
}
