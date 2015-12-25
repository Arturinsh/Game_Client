package xyz.arturinsh.Helpers;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.UBJsonReader;

public class AssetsLoader {
	private static Skin skin;
	private static Model monkeyModel;

	public static void initUI() {
		skin = new Skin(Gdx.files.internal("uiskin.json"));
		UBJsonReader jsonReader = new UBJsonReader();
		G3dModelLoader modelLoader = new G3dModelLoader(jsonReader);
		monkeyModel = modelLoader.loadModel(Gdx.files.getFileHandle("bot_monkey.g3db", FileType.Internal));
	}

	public static Skin getSkin() {
		return skin;
	}

	public static Model getMonkeyModel() {
		return monkeyModel;
	}
}
