package xyz.arturinsh.Helpers;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector3;

import xyz.arturinsh.GameObjects.CharacterInstance;
import xyz.arturinsh.GameWorld.GameWorld;

public class InputHandler implements InputProcessor {
	private GameWorld world;
	private CharacterInstance userCharacter;
	private PersonCamera camera;

	public InputHandler(GameWorld _world, PersonCamera _camera) {
		world = _world;
		userCharacter = world.getUsersCharacterInstance();
		camera = _camera;
	}

	@Override
	public boolean keyDown(int keycode) {
		switch (keycode) {
		case Keys.W:
			userCharacter.moveChar(20);
			break;
		case Keys.A:
			userCharacter.rotate(360);
			break;
		case Keys.S:
			userCharacter.moveChar(-20);
			break;
		case Keys.D:
			userCharacter.rotate(-360);
			break;
		case Keys.Q:
			camera.rotateAroundPlayer(-180);
			break;
		case Keys.E:
			camera.rotateAroundPlayer(180);
			break;
		case Keys.UP:
			camera.moveHeight(+5);
			break;
		case Keys.DOWN:
			camera.moveHeight(-5);
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
		case Keys.Q:
			camera.stopRotate();
			break;
		case Keys.E:
			camera.stopRotate();
			break;
		case Keys.UP:
			camera.stopHeight();
			break;
		case Keys.DOWN:
			camera.stopHeight();
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
		// System.out.println(screenX+" "+ screenY+" "+ pointer+" "+button);
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// System.out.println("drag");
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
