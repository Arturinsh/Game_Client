package xyz.arturinsh.GameObjects;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController.AnimationDesc;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController.AnimationListener;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

import xyz.arturinsh.Helpers.AssetsLoader;
import xyz.arturinsh.Helpers.HeightMap;
import xyz.arturinsh.Helpers.ObjectRotation;
import xyz.arturinsh.Network.Packets.PlayerPositionUpdate;
import xyz.arturinsh.Network.Packets.UserCharacter;

public class CharacterInstance {

	private final float MOVE_SPEED = 20;
	private final float ROTATE_SPEED = 240;

	private AnimationController modelAnimController, attackAnimController;
	private ModelInstance modelInstance, attackInstance, testBoxInstance;
	private Model model, attackCage, testBox;
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
	private boolean casting = false, damaging = false, moveUp = false, moveDown = false, rotateLeft = false,
			rotateRight = false;
	private Decal nameDecal;

	private ObjectRotation realRotation = new ObjectRotation();
	private ArrayList<PlayerPositionUpdate> movementBuffer = new ArrayList<PlayerPositionUpdate>();

	public CharacterInstance(UserCharacter _character) {
		model = AssetsLoader.getHumanModel();
		attackCage = AssetsLoader.getAttackCage();
		testBox = AssetsLoader.getTestBox();
		character = _character;
		changeModelMaterial(_character.charClass);
		modelInstance = new ModelInstance(model);
		attackInstance = new ModelInstance(attackCage);
		testBoxInstance = new ModelInstance(testBox);
		modelAnimController = new AnimationController(modelInstance);
		attackAnimController = new AnimationController(attackInstance);
		updatePositionOrientation(new Vector3(_character.x, _character.y, _character.z), _character.r, 0);
		newPosition = getPosition();
		newRotation = getRotation();
		realRotation = new ObjectRotation(_character.r);
	}

	public void changeClass(CharacterClass charClass) {
		changeModelMaterial(charClass);
		modelInstance = new ModelInstance(model);
		modelAnimController = new AnimationController(modelInstance);
	}

	public ModelInstance getModelInstance() {
		return modelInstance;
	}

	public ModelInstance getAttackInstance() {
		return attackInstance;
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
			model.materials.first().set(new Material(TextureAttribute.createDiffuse(AssetsLoader.getHuman1())));
			break;
		case GREEN:
			model.materials.first().set(new Material(TextureAttribute.createDiffuse(AssetsLoader.getHuman2())));
			break;
		case BLUE:
			model.materials.first().set(new Material(TextureAttribute.createDiffuse(AssetsLoader.getHuman3())));
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
		if (!casting && !damaging)
			moveSpeed = speed;
	}

	public void rotate(float speed) {
		if (!casting && !damaging)
			rotateSpeed = speed;
	}

	public void stopMove() {
		moveSpeed = 0;
	}

	public void stopRotate() {
		rotateSpeed = 0;
	}

	public void update(float delta, HeightMap map) {
		moveSpeed = 0;
		rotateSpeed = 0;
		if (!casting && !damaging) {
			if (moveUp)
				moveSpeed = MOVE_SPEED;
			if (moveDown)
				moveSpeed = -1 * MOVE_SPEED;
			if (rotateLeft)
				rotateSpeed = ROTATE_SPEED;
			if (rotateRight)
				rotateSpeed = -1 * ROTATE_SPEED;
		}

		this.modelInstance.transform.translate(0, 0, moveSpeed * delta);
		realRotation.addToRot(rotateSpeed * delta);

		int x = (int) getPosition().x;
		int y = (int) getPosition().z;
		float height = map.getHeight(x, y);

		updatePositionOrientation(getPosition(), realRotation.getRotation(), height);

		if (moveSpeed != 0) {
			modelAnimController.setAnimation("Armature|Walk", -1, 6, null);
			attackAnimController.setAnimation(null);
		} else if (!casting && !damaging) {
			modelAnimController.setAnimation(null);
			attackAnimController.setAnimation(null);
		}

		modelAnimController.update(delta);
		attackAnimController.update(delta);
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

		if (nameDecal != null)
			nameDecal.setPosition(new Vector3().set(position).add(0, 7, 0));

		this.attackInstance.transform.set(position, orientation);
		this.testBoxInstance.transform.set(position, orientation);
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

		if (!oldPosition.equals(newPosition) && !casting && !damaging) {
			realStep.x *= bigDelta;
			realStep.y *= bigDelta;
			realStep.z *= bigDelta;
			newPos.add(realStep);
			modelAnimController.setAnimation("Armature|Walk", -1, 6, null);
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

	public void addMovementToBuffer(PlayerPositionUpdate update) {
		PlayerPositionUpdate temp = new PlayerPositionUpdate();
		temp.character = new UserCharacter();

		temp.character.x = update.character.x;
		temp.character.y = update.character.y;
		temp.character.z = update.character.z;
		temp.character.r = update.character.r;

		movementBuffer.add(temp);
		// System.out.println("ADD x=" + update.character.x + " y=" +
		// update.character.y + " z=" + update.character.z
		// + " r=" + update.character.r);
	}

	public void checkMovement(PlayerPositionUpdate update) {
		int deleteIndex = -1;
		for (int i = movementBuffer.size() - 1; i >= 0; i--) {
			if (positionUpdateCheck(movementBuffer.get(i), update)) {
				// System.out.println("nice " + i);
				deleteIndex = i;
				break;
			} else if (i == 0 && movementBuffer.size()>5) {
//				System.out.println("x=" + movementBuffer.get(i).character.x + "|" + update.character.x + " y="
//						+ movementBuffer.get(i).character.y + "|" + update.character.y + " z="
//						+ movementBuffer.get(i).character.z + "|" + update.character.z + " r="
//						+ movementBuffer.get(i).character.r + "|" + update.character.r);
//				System.out.println("Correct");
				Vector3 positionCorrection = new Vector3(update.character.x, update.character.y, update.character.z);
				updatePositionOrientation(positionCorrection, update.character.r, getPosition().y);
			}
		}
//		System.out.println("buffer size=" + movementBuffer.size() + " del index=" + deleteIndex);
		if (deleteIndex > 1) {
			clearMovementBuffer(deleteIndex);
		}
		// System.out.println("After clear = " + movementBuffer.size());
	}

	private boolean positionUpdateCheck(PlayerPositionUpdate pos1, PlayerPositionUpdate pos2) {
		// System.out.println("x=" + pos1.character.x + "|" + pos2.character.x +
		// " y=" + pos1.character.y + "|"
		// + pos2.character.y + " z=" + pos1.character.z + "|" +
		// pos2.character.z + " r=" + pos1.character.r + "|"
		// + pos2.character.r);

		return pos1.character.x < pos2.character.x + 0.5f && pos1.character.x > pos2.character.x - 0.5f
				&& pos1.character.y < pos2.character.y + 0.5f && pos1.character.y > pos2.character.y - 0.5f
				&& pos1.character.z < pos2.character.z + 0.5f && pos1.character.z > pos2.character.z - 0.5f
				&& pos1.character.r < pos2.character.r + 90 && pos1.character.r > pos2.character.r - 90;
	}

	private void clearMovementBuffer(int index) {
		for (int i = index; i >= 0; i--) {
			movementBuffer.remove(0);
		}
	}

	public void attack() {
		// printTime();
		stopMove();
		stopRotate();
		modelAnimController.setAnimation(null);
		showCastingAnimation();
	}

	public void render(ModelBatch batch, Environment env) {
		batch.render(this.modelInstance, env);
		// batch.render(this.testBoxInstance, env);

		if (damaging) {
			batch.render(this.attackInstance, env);
		}
	}

	private void showCastingAnimation() {
		casting = true;
		// printTime();
		modelAnimController.setAnimation("Armature|Hit", 1, 5, new AnimationListener() {
			@Override
			public void onEnd(AnimationDesc animation) {
				damaging = true;
				casting = false;
				showDamageAnimation();
			}

			@Override
			public void onLoop(AnimationDesc animation) {
			}
		});
	}

	private void showDamageAnimation() {
		// printTime();
		attackAnimController.setAnimation("Cube|Raise", 1, 4, new AnimationListener() {
			@Override
			public void onEnd(AnimationDesc animation) {
				damaging = false;
				// System.out.println(animation.duration);
				// printTime();
			}

			@Override
			public void onLoop(AnimationDesc animation) {
			}
		});
	}

	public boolean canAttack() {
		return !damaging && !casting;
	}

	private void printTime() {
		DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss:SSSS");
		Date date = new Date();
		System.out.println(dateFormat.format(date));
	}

	public void setMoveUp(boolean moveUp) {
		this.moveUp = moveUp;
	}

	public void setMoveDown(boolean moveDown) {
		this.moveDown = moveDown;
	}

	public void setRotateLeft(boolean rotateLeft) {
		this.rotateLeft = rotateLeft;
	}

	public void setRotateRight(boolean rotateRight) {
		this.rotateRight = rotateRight;
	}

	public Decal getNameDecal() {
		return nameDecal;
	}

	public void initDecal(SpriteBatch spriteFont) {
		BitmapFont font = AssetsLoader.getFont();
		GlyphLayout glyphLayout = new GlyphLayout();
		glyphLayout.setText(font, character.charName);
		int w = (int) glyphLayout.width;
		int h = (int) glyphLayout.height;
		FrameBuffer fbo = new FrameBuffer(Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
		fbo.begin();
		spriteFont.begin();
		font.draw(spriteFont, character.charName, 0, h);
		spriteFont.end();
		fbo.end();
		TextureRegion fboRegion = new TextureRegion(fbo.getColorBufferTexture(), 0, 0, w, h + 1);
		fboRegion.flip(false, true);
		nameDecal = Decal.newDecal(w / h, 1, fboRegion, true);
		nameDecal.setPosition(0, 0, 0);
	}
}
