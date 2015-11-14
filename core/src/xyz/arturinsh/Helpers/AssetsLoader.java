package xyz.arturinsh.Helpers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class AssetsLoader {
	private static Skin skin;
	
	public static void initUI()
	{
		skin = new Skin(Gdx.files.internal("uiskin.json"));
	}

	public static Skin getSkin() {
		return skin;
	}
}
