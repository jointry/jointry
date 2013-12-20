package jp.ac.aiit.jointry.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import jp.ac.aiit.jointry.models.Sprite;
import broker.core.Agent;
import broker.core.DefaultMonitor;
import java.awt.image.BufferedImage;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import jp.ac.aiit.jointry.models.Costume;
import jp.ac.aiit.jointry.services.broker.app.JointryAccount;
import jp.ac.aiit.jointry.services.broker.app.MainDialog;
import jp.ac.aiit.jointry.services.file.FileManager;
import jp.ac.aiit.jointry.util.StageUtil;
import org.xml.sax.SAXException;

public class MainController extends DefaultMonitor implements Initializable {

    @FXML
    private BorderPane rootPane;
    @FXML
    private MenuItem roomEnter;
    @FXML
    private MenuItem roomExit;
    private BackStageController backStageController;
    private FrontStageController frontStageController;
    private BlocksController blocksController;
    private Agent agent;
    private ListView members = new ListView();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            FXMLLoader ld;
            Class<? extends MainController> klass = getClass();

            // FrontStage
            ld = new FXMLLoader(klass.getResource("FrontStage.fxml"));
            Parent front = (Parent) ld.load();
            FrontStageController fsc = ld.<FrontStageController>getController();
            fsc.setMainController(this);
            rootPane.setRight(front);
            setFrontStageController(fsc);

            // BackStage
            ld = new FXMLLoader(klass.getResource("BackStage.fxml"));
            Parent back = (Parent) ld.load();
            BackStageController bsc = ld.<BackStageController>getController();
            bsc.setMainController(this);
            rootPane.setCenter(back);
            setBackStageController(bsc);

            // Blocks
            ld = new FXMLLoader(klass.getResource("Blocks.fxml"));
            Parent blocks = (Parent) ld.load();
            BlocksController bc = ld.<BlocksController>getController();
            bc.setMainController(this);
            rootPane.setLeft(blocks);
            setBlocksController(bc);

        } catch (IOException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    protected void fsave(ActionEvent event) {
        try {
            new FileManager().save(frontStageController.getSprites());
        } catch (IOException | ParserConfigurationException | TransformerException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    protected void fopen(ActionEvent event) {
        try {
            new FileManager().load(this);
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }

        MainDialog.sendSynchronize();
    }

    @FXML
    protected void startCooperation(ActionEvent event) {
        //協同編集
        Window owner = rootPane.getScene().getWindow(); //画面オーナー
        URL fxml = getClass().getResource("Cooperation.fxml"); //表示するfxml
        final StageUtil stage = new StageUtil(null, owner, fxml, null);

        final CooperationController ctrl = (CooperationController) stage.getController();
        ctrl.setMainController(MainController.this);

        stage.getStage().setOnHidden(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                if (agent == null) {
                    agent = ctrl.getAgent();
                    if (agent != null) {
                        agent.setMonitor(MainController.this);
                        roomEnter.setVisible(false);
                        roomExit.setVisible(true);
                    }
                }
                ctrl.windowClose();
            }
        });

        stage.getStage().show();
    }

    @FXML
    protected void endCooperation(ActionEvent event) {
        if (agent != null) {
            agent.close();
            agent = null;
        }

        initWindow("disconnect");
        roomEnter.setVisible(true);
        roomExit.setVisible(false);
    }

    public void initWindow(String mode) {
        switch (mode) {
            case "new":
                //初期スプライト
                URL path = getClass().getResource("images/scratch_cat1.gif");
                Sprite sprite = new Sprite(path.toString());
                sprite.setMainController(this);
                URL costume_path = getClass().getResource("images/scratch_cat2.gif");
                sprite.addCostume("costume", new Image(costume_path.toString()));
                frontStageController.showSprite(sprite);
                break;

            case "load":
                this.initialize(null, null);

                if (agent != null) {
                    initWindow("connect");
                }
                break;

            case "connect":
                //参加しているメンバー領域の表示
                VBox connectFront = new VBox();
                connectFront.getChildren().addAll(rootPane.getRight(), members);
                rootPane.setRight(connectFront);
                refreshMembers();
                members.setStyle("-fx-border-color: rgb(49, 89, 23)");
                break;

            case "disconnect":
                //参加しているメンバー領域の非表示
                Node disconnectFront = rootPane.getRight();

                if (disconnectFront instanceof VBox) {
                    rootPane.setRight(((VBox) disconnectFront).getChildren().get(0));
                }

                break;

            default:
                break;
        }
    }

    public void refreshMembers() {
        members.setItems(JointryAccount.getUsers());
    }

    public void windowClose() {
        if (agent != null) {
            agent.close();
        }
    }

    public void setBackStageController(BackStageController controller) {
        this.backStageController = controller;
    }

    public void setFrontStageController(FrontStageController controller) {
        this.frontStageController = controller;
    }

    public void setBlocksController(BlocksController blocksController) {
        this.blocksController = blocksController;
    }

    public BackStageController getBackStageController() {
        return this.backStageController;
    }

    public FrontStageController getFrontStageController() {
        return this.frontStageController;
    }

    public BlocksController getBlocksController() {
        return this.blocksController;
    }

    public Agent getAgent() {
        return this.agent;
    }

    @Override
    public void onClose() {
        agent = null; //agentのclose

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                initWindow("disconnect");
                roomEnter.setVisible(true);
                roomExit.setVisible(false);
            }
        });
    }

    @Override
    public void viewImage(String title, final BufferedImage bimage) {
        title = title.substring(0, title.lastIndexOf("."));

        final String[] names = title.split("_");

        for (final Sprite sprite : this.getFrontStageController().getSprites()) {
            if (sprite.getName().equals(names[0])) {
                if (names.length <= 1) {
                    sprite.setIcon(SwingFXUtils.toFXImage(bimage, null));
                } else {
                    final int number = Integer.parseInt(names[2]);

                    for (Costume costume : sprite.getCostumes()) {
                        if (costume.getNumber() == number) {
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    sprite.updateCostume(number, SwingFXUtils.toFXImage(bimage, null));
                                    MainController.this.getBackStageController().showCostumes(sprite);
                                }
                            });

                            return;
                        }
                    }
                }

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        sprite.addCostume(names[1], SwingFXUtils.toFXImage(bimage, null));
                        MainController.this.getBackStageController().showCostumes(sprite);
                    }
                });

                break;
            }
        }
    }
}
