package xyz.arturinsh.GameObjects;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

import xyz.arturinsh.Helpers.AssetsLoader;
import xyz.arturinsh.Helpers.HeightField;

public class MobInstance {
	private AnimationController animController;
	private ModelInstance modelInstance;
	private Model model;

	private long startTime = 0;
	private long endTime = 0;
	private Vector3 step = new Vector3(0, 0, 0);
	private Vector3 oldPosition = new Vector3(0, 0, 0);
	private Vector3 newPosition = new Vector3(0, 0, 0);
	private float rotationStep = 0;
	private float oldRotation = 0;
	private float newRotation = 0;

	private long ID;
	
	public MobInstance(long id, float x, float y, float z, float rotation){
		ID = id;
		model = AssetsLoader.getDog();
		modelInstance = new ModelInstance(model);
		animController = new AnimationController(modelInstance);
		updatePositionOrientation(new Vector3(x, y, z), rotation, 0);
		newPosition = getPosition();
		newRotation = getRotation();
	}

	public ModelInstance getModelInstance() {
		return modelInstance;
	}

	public void setModelInstance(ModelInstance modelInstance) {
		this.modelInstance = modelInstance;
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

	public void update(float bigDelta, HeightField field) {
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
			animController.setAnimation("Armature|Walk", -1, 1, null);
		} else {
			newPos.set(newPosition);
			animController.setAnimation(null);
		}
		animController.update(bigDelta / 1000);
		
		Vector3 out = new Vector3();
		int x = (int) getPosition().x;
		int y = (int) getPosition().z;
		field.getPositionAt(out, x, y);
		
		updatePositionOrientation(newPos, newRot, out.y);
	}

	public void updateMob(float x, float y, float z, float rotation, long time) {
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

	private void updatePositionOrientation(Vector3 position, float r, float height) {
		Quaternion orientation = new Quaternion();
		orientation.setEulerAngles(r, 0, 0);
		position.y = height;
		this.modelInstance.transform.set(position, orientation);
	}

	public long getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}
	
}
