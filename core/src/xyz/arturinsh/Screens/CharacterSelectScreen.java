package xyz.arturinsh.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;

import xyz.arturinsh.GameWorld.GameWorld;

public class CharacterSelectScreen extends GameScreen{

	public CharacterSelectScreen(GameWorld _world) {
		super(_world);
		initUI();
	}
	
	private void initUI(){
		
		Gdx.input.setInputProcessor(stage);
	}
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.act(Gdx.graphics.getDeltaTime());
		stage.draw();
	}
}
