package jp.ac.aiit.jointry.parser.env;

import javafx.scene.image.ImageView;

public interface Environment {
	void put(String name, Object value);
	Object get(String name);
	void putNew(String name, Object value);
	Environment where(String name);
	void setOuter(Environment e);
        ImageView getImage();
        public void setImage(ImageView image);
}
