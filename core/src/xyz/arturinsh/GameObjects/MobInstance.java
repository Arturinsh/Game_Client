package xyz.arturinsh.GameObjects;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController.AnimationDesc;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController.AnimationListener;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

import xyz.arturinsh.Helpers.AssetsLoader;
import xyz.arturinsh.Helpers.HeightMap;
import xyz.arturinsh.Network.Packets.MobUpdate;

public class MobInstance {
	private AnimationController modelAnimController, attackAnimController;
	private ModelInstance modelInstance, attackInstance, selectBoxInstance;
	private Model model, attack, selectBox;

	private long startTime = 0;
	private long endTime = 0;
	private Vector3 step = new Vector3(0, 0, 0);
	private Vector3 oldPosition = new Vector3(0, 0, 0);
	private Vector3 newPosition = new Vector3(0, 0, 0);
	private float rotationStep = 0;
	private float oldRotation = 0;
	private float newRotation = 0;
	private int hp = 100;

	private long ID;

	public Vector3 center = new Vector3();;
	public Vector3 dimensions = new Vector3();;
	public float radius;

	private boolean attacking = false, selected = false;
	private String walkModelAnimation = "Armature|Walk";
	private String attackModelAnimation = "Armature|Attack";
	private String attackAttackAnimation = "Cube|Attack";
	private float walkModelAnimSpeed = 1;
	private float attackModelAnimSpeed = 1;
	private float attackAttackAnimSpeed = 1;
	private BoundingBox box;

	private boolean casting = false, damaging = false;

	public MobInstance(long id, float x, float y, float z, float rotation, MobType type) {
		ID = id;
		setMobType(type);

		selectBox = AssetsLoader.getSelectBox();
		selectBoxInstance = new ModelInstance(selectBox);
		modelInstance = new ModelInstance(model);
		attackInstance = new ModelInstance(attack);
		modelAnimController = new AnimationController(modelInstance);
		attackAnimController = new AnimationController(attackInstance);
		updatePositionOrientation(new Vector3(x, y, z), rotation, 0);
		newPosition = getPosition();
		newRotation = getRotation();
		initBounds();
	}

	private void setMobType(MobType type) {
		switch (type) {
		case VLADINATORS:
			model = AssetsLoader.getVladinator();
			attack = AssetsLoader.getVladinatorAttack();
			box = AssetsLoader.getVladinatorBoundingBox();
			walkModelAnimation = "Armature|Walk";
			attackAttackAnimation = "Armature|Attack";
			attackModelAnimation = "Armature|Attack";
			walkModelAnimSpeed = 1;
			attackModelAnimSpeed = 2;
			attackAttackAnimSpeed = 2;
			break;
		case DOG:
			model = AssetsLoader.getDog();
			attack = AssetsLoader.getDogAttack();
			box = AssetsLoader.getDogBoundingBox();
			walkModelAnimation = "Armature|Walk";
			attackAttackAnimation = "Cube|Attack";
			attackModelAnimation = "Armature|Attack";
			walkModelAnimSpeed = 1;
			attackModelAnimSpeed = 4;
			attackAttackAnimSpeed = 2;
			break;
		}
	}

	private void initBounds() {
		box.getCenter(center);
		box.getDimensions(dimensions);
		radius = dimensions.len() / 2f;
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

	public void update(float bigDelta, HeightMap map) {
		Vector3 realStep = new Vector3();
		realStep.set(step);
		Vector3 newPos = getPosition();
		float newRot = getRotation();

		if (oldRotation != newRotation) {
			newRot += rotationStep * bigDelta;
		} else {
			newRot = newRotation;
		}

		if (!oldPosition.equals(newPosition) && !casting && !damaging) {
			realStep.x *= bigDelta;
			realStep.y *= bigDelta;
			realStep.z *= bigDelta;
			newPos.add(realStep);
			modelAnimController.setAnimation(walkModelAnimation, -1, walkModelAnimSpeed, null);
		} else if (!casting && !damaging) {
			newPos.set(newPosition);
			modelAnimController.setAnimation(null);
			attackAnimController.setAnimation(null);
		}
		modelAnimController.update(bigDelta / 1000);
		attackAnimController.update(bigDelta / 1000);

		int x = (int) getPosition().x;
		int y = (int) getPosition().z;
		float height = map.getHeight(x, y);
		
		updatePositionOrientation(newPos, newRot, height);
	}

	public void updateMob(MobUpdate update, long time) {
		float x = update.x;
		float y = update.y;
		float z = update.z;
		float rotation = update.r;
		if (hp != update.hp) {
			this.hp = update.hp;
		}
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

	public void render(ModelBatch batch, Environment env) {
		if (hp > 0) {
			batch.render(this.modelInstance, env);
			if (damaging)
				batch.render(this.attackInstance, env);
		}
		if (selected) {
			batch.render(this.selectBoxInstance, env);
		}
	}

	public void renderShadow(ModelBatch shadowBatch) {
		if (hp > 0) {
			shadowBatch.render(modelInstance);
		}
	}

	private void updatePositionOrientation(Vector3 position, float r, float height) {
		Quaternion orientation = new Quaternion();
		orientation.setEulerAngles(r, 0, 0);
		position.y = height;
		this.selectBoxInstance.transform.set(position, orientation);
		this.modelInstance.transform.set(position, orientation);
		this.attackInstance.transform.set(position, orientation);
	}

	public long getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public void attack() {
		casting = true;
		modelAnimController.setAnimation(null);
		attackAnimController.setAnimation(null);
		showCastingAnimation();
	}

	private void showCastingAnimation() {
		casting = true;
//		System.out.println("cast start");
//		printTime();
		modelAnimController.setAnimation(attackModelAnimation, 1, attackModelAnimSpeed, new AnimationListener() {
			@Override
			public void onEnd(AnimationDesc animation) {
				damaging = true;
				casting = false;
//				System.out.println("cast end");
				showDamageAnimation();
			}

			@Override
			public void onLoop(AnimationDesc animation) {
			}
		});
	}

	private void showDamageAnimation() {
		attackAnimController.setAnimation(attackAttackAnimation, 1, attackAttackAnimSpeed, new AnimationListener() {
			@Override
			public void onEnd(AnimationDesc animation) {
				damaging = false;
//				System.out.println("dmg end");
//				printTime();
			}

			@Override
			public void onLoop(AnimationDesc animation) {
			}
		});
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public int getHP() {
		return hp;
	}

	private void printTime() {
		DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss:SSSS");
		Date date = new Date();
		System.out.println(dateFormat.format(date));
	}
}
