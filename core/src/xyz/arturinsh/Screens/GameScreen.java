package xyz.arturinsh.Screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.viewport.StretchViewport;

import xyz.arturinsh.GameWorld.GameWorld;
import xyz.arturinsh.Helpers.AssetsLoader;
import xyz.arturinsh.gameclient.MainGame;

public class GameScreen implements Screen {
	protected Stage stage;
	protected GameWorld world;
	protected MainGame game;
	protected Label worldMessage;
	private Dialog worldDialog;

	public GameScreen(GameWorld _world) {
		world = _world;
		game = world.getGame();
		stage = new Stage(new StretchViewport(1024, 576));
		
		worldMessage = new Label("", AssetsLoader.getSkin());
		worldDialog = new Dialog("", AssetsLoader.getSkin());
		worldDialog.button("Close", false);
		worldDialog.text(worldMessage);
	}

	public void showDialog(String message) {
		worldMessage.setText(message);
		worldDialog.show(stage);
	}

	public Stage getStage() {
		return stage;
	}
	
	public void changeScreen(GameScreen screen){
		game.setScreen(screen);
	}
	
	@Override
	public void show() {

	}

	@Override
	public void render(float delta) {

	}

	@Override
	public void resize(int width, int height) {

	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void hide() {

	}

	@Override
	public void dispose() {
		stage.dispose();
	}

}
