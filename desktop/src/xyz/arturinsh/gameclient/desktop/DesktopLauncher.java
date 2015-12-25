package xyz.arturinsh.gameclient.desktop;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import xyz.arturinsh.gameclient.MainGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Nice Game";
		config.width = 1024;
		config.height = 576;
		config.addIcon("icon.png", FileType.Internal);
		config.addIcon("icon2.png", FileType.Internal);
		new LwjglApplication(new MainGame(), config);
	}
}
