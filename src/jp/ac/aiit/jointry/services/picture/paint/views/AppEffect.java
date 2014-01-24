package jp.ac.aiit.jointry.services.picture.paint.views;

import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Lighting;
import javafx.scene.paint.Color;

public class AppEffect {

    public static final Lighting RAISED;
    public static final DropShadow LOWERED;

    static {
        RAISED = new Lighting();
        RAISED.setSurfaceScale(2.0);

        LOWERED = new DropShadow();
        LOWERED.setColor(Color.GREY);
        LOWERED.setOffsetX(1.0);
        LOWERED.setOffsetY(1.0);
    }
}
