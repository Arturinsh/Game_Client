package xyz.arturinsh.Helpers;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector3;

import xyz.arturinsh.GameObjects.CharacterInstance;
import xyz.arturinsh.GameWorld.GameWorld;

public class InputHandler implements InputProcessor {
	private GameWorld world;
	private CharacterInstance userCharacter;

	public InputHandler(GameWorld _world) {
		world = _world;
		userCharacter = world.getUsersCharacterInstance();
	}

	@Override
	public boolean keyDown(int keycode) {
		switch (keycode) {
		case Keys.W:
			userCharacter.moveChar(new Vector3(0, 0, 20));
			break;
		case Keys.A:
			userCharacter.rotate(360);
			break;
		case Keys.S:
			userCharacter.moveChar(new Vector3(0, 0, -20));
			break;
		case Keys.D:
			userCharacter.rotate(-360);
			break;
		}
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		switch (keycode) {
		case Keys.W:
			userCharacter.stopMove();
			break;
		case Keys.A:
			userCharacter.stopRotate();
			break;
		case Keys.S:
			userCharacter.stopMove();
			break;
		case Keys.D:
			userCharacter.stopRotate();
			break;
		}
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}

}
