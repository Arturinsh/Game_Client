package xyz.arturinsh.Helpers;

import java.util.List;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;

import xyz.arturinsh.GameObjects.CharacterInstance;
import xyz.arturinsh.GameObjects.MobInstance;
import xyz.arturinsh.GameWorld.GameWorld;

public class InputHandler implements InputProcessor {
	private GameWorld world;
	private CharacterInstance userCharacter;
	private PersonCamera camera;
	private Stage stage;
	private Vector3 position = new Vector3();
	private int startX = 0, startY = 0;
	private boolean stageHit = false;

	public InputHandler(GameWorld _world, PersonCamera _camera, Stage _stage) {
		world = _world;
		userCharacter = world.getUsersCharacterInstance();
		camera = _camera;
		stage = _stage;
	}

	@Override
	public boolean keyDown(int keycode) {
		switch (keycode) {
		case Keys.W:
			userCharacter.setMoveUp(true);
			// userCharacter.moveChar(20);
			break;
		case Keys.A:
			userCharacter.setRotateLeft(true);
			// userCharacter.rotate(360);
			break;
		case Keys.S:
			userCharacter.setMoveDown(true);
			// userCharacter.moveChar(-20);
			break;
		case Keys.D:
			userCharacter.setRotateRight(true);
			// userCharacter.rotate(-360);
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
		case Keys.SPACE:
			world.attack();
			break;
		}
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		switch (keycode) {
		case Keys.W:
			userCharacter.setMoveUp(false);
			// userCharacter.stopMove();
			break;
		case Keys.A:
			userCharacter.setRotateLeft(false);
			// userCharacter.stopRotate();
			break;
		case Keys.S:
			userCharacter.setMoveDown(false);
			// userCharacter.stopMove();
			break;
		case Keys.D:
			userCharacter.setRotateRight(false);
			// userCharacter.stopRotate();
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
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		// System.out.println(screenX + " " + screenY);
		Vector2 temp = new Vector2(screenX, screenY);
		stage.screenToStageCoordinates(temp);
		// System.out.println("Stage " + temp.x + " " + temp.y);
		Actor test = stage.hit(temp.x, temp.y, true);
		if (test == null) {
			stageHit = false;
			List<CharacterInstance> tempPlayerList = world.getOtherPlayers();
			List<MobInstance> tempMobList = world.getMobs();
			int playerIndex = getPlayer(screenX, screenY);
			int mobIndex = getMob(screenX, screenY);
			if (playerIndex >= 0 && playerIndex < tempPlayerList.size()) {
				world.setSelectedMob(null);
				world.setSelectedCharacterInstance(tempPlayerList.get(playerIndex));
			} else if (mobIndex >= 0 && mobIndex < tempMobList.size()) {
				world.setSelectedCharacterInstance(null);
				world.setSelectedMob(tempMobList.get(mobIndex));
			} else {
				world.setSelectedCharacterInstance(null);
				world.setSelectedMob(null);
			}
		} else {
			stageHit = true;
		}

		startX = screenX;
		startY = screenY;
		return false;
	}

	private int getPlayer(int screenX, int screenY) {
		Ray ray = camera.getPickRay(screenX, screenY);
		int result = -1;
		float distance = -1;
		List<CharacterInstance> tempList = world.getOtherPlayers();
		for (int i = 0; i < world.getOtherPlayers().size(); ++i) {
			final CharacterInstance instance = tempList.get(i);
			position = instance.getPosition();
			position.add(instance.center);
			float dist2 = ray.origin.dst2(position);
			if (distance >= 0f && dist2 > distance)
				continue;
			if (Intersector.intersectRaySphere(ray, position, instance.radius, null)) {
				result = i;
				distance = dist2;
			}
		}
		return result;
	}

	private int getMob(int screenX, int screenY) {
		Ray ray = camera.getPickRay(screenX, screenY);
		int result = -1;
		float distance = -1;
		List<MobInstance> tempList = world.getMobs();
		for (int i = 0; i < world.getMobs().size(); ++i) {
			final MobInstance instance = tempList.get(i);
			position = instance.getPosition();
			position.add(instance.center);
			float dist2 = ray.origin.dst2(position);
			if (distance >= 0f && dist2 > distance)
				continue;
			if (Intersector.intersectRaySphere(ray, position, instance.radius, null)) {
				result = i;
				distance = dist2;
			}
		}
		return result;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		if (!stageHit) {
			int deltaX = startX - screenX;
			startX = screenX;
			camera.addAngle(deltaX / 2);
		}
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}

}
