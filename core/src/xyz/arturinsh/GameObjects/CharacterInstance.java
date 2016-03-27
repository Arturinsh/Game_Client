package xyz.arturinsh.GameObjects;

import java.util.ArrayList;
import java.util.Date;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

import xyz.arturinsh.Helpers.AssetsLoader;
import xyz.arturinsh.Helpers.HeightMap;
import xyz.arturinsh.Helpers.ObjectRotation;
import xyz.arturinsh.Network.Packets.PlayerPositionUpdate;
import xyz.arturinsh.Network.Packets.UserCharacter;

public class CharacterInstance {

	private AnimationController animController;
	private ModelInstance modelInstance;
	private Model model;
	private float moveSpeed = 0;
	private float rotateSpeed = 0;
	private UserCharacter character;
	private long startTime = 0;
	private long endTime = 0;
	private Vector3 step = new Vector3(0, 0, 0);
	private Vector3 oldPosition = new Vector3(0, 0, 0);
	private Vector3 newPosition = new Vector3(0, 0, 0);
	private float rotationStep = 0;
	private float oldRotation = 0;
	private float newRotation = 0;

	private ObjectRotation realRotation = new ObjectRotation();
	private ArrayList<PlayerPositionUpdate> movementBuffer = new ArrayList<PlayerPositionUpdate>();

	public CharacterInstance(UserCharacter _character) {
		model = AssetsLoader.getMonkeyModel();
		character = _character;
		changeModelMaterial(_character.charClass);
		modelInstance = new ModelInstance(model);
		animController = new AnimationController(modelInstance);
		updatePositionOrientation(new Vector3(_character.x, _character.y, _character.z), _character.r, 0);
		newPosition = getPosition();
		newRotation = getRotation();
		realRotation = new ObjectRotation(_character.r);
	}

	public void changeClass(CharacterClass charClass) {
		changeModelMaterial(charClass);
		modelInstance = new ModelInstance(model);
		animController = new AnimationController(modelInstance);

	}

	public ModelInstance getModelInstance() {
		return modelInstance;
	}

	public void setModelInstance(ModelInstance modelInstance) {
		this.modelInstance = modelInstance;
	}

	public Model getModel() {
		return model;
	}

	public void setModel(Model model) {
		this.model = model;
	}

	private void changeModelMaterial(CharacterClass charClass) {
		switch (charClass) {
		case RED:
			model.materials.first().set(new Material(ColorAttribute.createDiffuse(Color.RED)));
			break;
		case GREEN:
			model.materials.first().set(new Material(ColorAttribute.createDiffuse(Color.GREEN)));
			break;
		case BLUE:
			model.materials.first().set(new Material(ColorAttribute.createDiffuse(Color.BLUE)));
			break;
		default:
			break;
		}
	}

	public Vector3 getPosition() {
		Vector3 position = new Vector3();
		this.modelInstance.transform.getTranslation(position);
		return position;
	}

	public float getRotation() {
		Quaternion rotation = new Quaternion();
		this.modelInstance.transform.getRotation(rotation);
		return rotation.getYaw();
	}

	public void moveChar(float speed) {
		moveSpeed = speed;
	}

	public void rotate(float speed) {
		rotateSpeed = speed;
	}

	public void stopMove() {
		moveSpeed = 0;
	}

	public void stopRotate() {
		rotateSpeed = 0;
	}

	public void update(float delta, HeightMap map) {
		this.modelInstance.transform.translate(0, 0, moveSpeed * delta);
		realRotation.addToRot(rotateSpeed * delta);

		int x = (int) getPosition().x;
		int y = (int) getPosition().z;
		float height = map.getHeight(x, y);

		updatePositionOrientation(getPosition(), realRotation.getRotation(), height);

		if (moveSpeed != 0)
			animController.setAnimation("Armature|Walk", -1, 6, null);
		else
			animController.setAnimation(null);

		animController.update(delta);
	}

	public UserCharacter getCharacter() {
		return character;
	}

	public void setCharacter(UserCharacter character) {
		this.character = character;
	}

	public boolean matchesCharacter(UserCharacter _character) {
		return _character.charName.matches(character.charName) && _character.charClass == character.charClass;
	}

	private void updatePositionOrientation(Vector3 position, float r, float height) {
		Quaternion orientation = new Quaternion();
		orientation.setEulerAngles(r, 0, 0);
		if (height != 0)
			position.y = height;
		this.modelInstance.transform.set(position, orientation);
	}

	public void updatePlayer(float x, float y, float z, float rotation, long time) {
		startTime = endTime;
		endTime = time;

		oldPosition = newPosition;
		newPosition = new Vector3(x, y, z);

		oldRotation = newRotation;
		newRotation = rotation;

		if (endTime != 0) {
			long timeDifference = (endTime - startTime);
			float tx, ty, tz, trot;
			tx = newPosition.x - oldPosition.x;
			ty = newPosition.y - oldPosition.y;
			tz = newPosition.z - oldPosition.z;
			trot = newRotation - oldRotation;

			tx = tx / timeDifference;
			ty = ty / timeDifference;
			tz = tz / timeDifference;

			if (trot > 180) {
				trot -= 360;
			}

			if (trot < -180) {
				trot += 360;
			}

			rotationStep = trot / timeDifference;
			step = new Vector3(tx, ty, tz);
		}
	}

	// bigDelta = delta *1000
	public void updateOther(float bigDelta, HeightMap map) {
		Vector3 realStep = new Vector3();
		realStep.set(step);
		Vector3 newPos = getPosition();
		float newRot = getRotation();

		if (oldRotation != newRotation) {
			newRot += rotationStep * bigDelta;
		} else {
			newRot = newRotation;
		}

		if (!oldPosition.equals(newPosition)) {
			realStep.x *= bigDelta;
			realStep.y *= bigDelta;
			realStep.z *= bigDelta;
			newPos.add(realStep);
			animController.setAnimation("Armature|Walk", -1, 6, null);
		} else {
			newPos.set(newPosition);
			animController.setAnimation(null);
		}
		animController.update(bigDelta / 1000);

		int x = (int) getPosition().x;
		int y = (int) getPosition().z;
		float height = map.getHeight(x, y);

		updatePositionOrientation(newPos, newRot, height);
	}

	// public void testPosition(float x, float y, float z, float r) {
	//
	// if (getPosition().x == x && getPosition().y == y && getPosition().z == z
	// && (int)getRotation() == (int)r)
	// System.out.println("True pos");
	// else
	// System.out.println("Different pos");
	// }
	//
	// public float roundedFloat(float d, int decimalPlace) {
	// return BigDecimal.valueOf(d).setScale(decimalPlace,
	// BigDecimal.ROUND_HALF_UP).floatValue();
	// }

	public void addMovementToBuffer(PlayerPositionUpdate update) {
		movementBuffer.add(update);
//		System.out.println("Add to buffer");
	}

	public void checkMovement(PlayerPositionUpdate update) {
//		System.out.println(" Buffer size = " + movementBuffer.size());
		int index = movementBuffer.indexOf(update);
		if (index > -1) {
			PlayerPositionUpdate toTest = movementBuffer.get(index);
			if (!positionUpdateCheck(toTest, update)) {
				Vector3 positionCorrection = new Vector3(update.character.x, update.character.y, update.character.z);
				updatePositionOrientation(positionCorrection, update.character.r, getPosition().y);
//				System.out.println("Incorrect. Buffer size = " + movementBuffer.size());
			} else {
//				System.out.println("Correct. Buffer size = " + movementBuffer.size());
			}
			clearMovementBuffer(update.timestamp);
//			System.out.println("After clear = " + movementBuffer.size());
		} else {
			// System.out.println("Not found");
		}
	}

	private boolean positionUpdateCheck(PlayerPositionUpdate pos1, PlayerPositionUpdate pos2) {
		return pos1.character.x == pos2.character.x && pos1.character.y == pos2.character.y
				&& pos1.character.z == pos2.character.z && pos1.character.r == pos2.character.r;
	}

	private void clearMovementBuffer(Date timestamp) {
		ArrayList<PlayerPositionUpdate> toDelete = new ArrayList<PlayerPositionUpdate>();
		for (PlayerPositionUpdate update : movementBuffer) {
			if (update.timestamp.getTime() <= timestamp.getTime()) {
				toDelete.add(update);
			}
		}
		for (PlayerPositionUpdate deleteUpdate : toDelete) {
			movementBuffer.remove(deleteUpdate);
		}
	}
}
