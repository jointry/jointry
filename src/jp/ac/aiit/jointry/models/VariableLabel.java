package jp.ac.aiit.jointry.models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Label;

public class VariableLabel extends Label {

    private String name;
    private StringProperty value = new SimpleStringProperty("");

    public VariableLabel(String name, String val) {
        super();
        this.value.setValue(val);
        this.name = name;
        this.setText(getLabel());

        this.value.addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> ov, String t, String t1) {
                setText(getLabel());
            }
        });
    }

    public StringProperty getValueProperty() {
        return this.value;
    }

    public String getName() {
        return this.name;
    }

    public String getLabel() {
        return name + ":" + value.getValue();
    }
}
