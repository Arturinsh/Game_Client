package xyz.arturinsh.GameObjects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

import xyz.arturinsh.Helpers.AssetsLoader;

public class CharacterInstance {

	private AnimationController animController;
	private ModelInstance modelInstance;
	private Model model;
	private boolean move, rotate;
	private String name;
	private float rotateDegrees;
	private Vector3 moveVector;

	public CharacterInstance(CharacterClass charClass) {
		model = AssetsLoader.getMonkeyModel();
		changeModelMaterial(charClass);
		modelInstance = new ModelInstance(model);
		move = false;
		rotate = false;
		name = "Unknown Name";
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

	public void setPosition(float x, float y, float z) {
		this.modelInstance.transform.setTranslation(new Vector3(x, y, z));
	}

	public Vector3 getPosition() {
		Vector3 position = new Vector3();
		this.modelInstance.transform.getTranslation(position);
		return position;
	}

	public Quaternion getRotation() {
		Quaternion rotation = new Quaternion(0, 0, 0, 0);
		this.modelInstance.transform.getRotation(rotation, false);
		return rotation;
	}

	public Matrix4 getTransform() {
		return this.modelInstance.transform;
	}

	public void moveChar(Vector3 moveV) {
		moveVector = moveV;
		move = true;
		Quaternion test = new Quaternion();
		this.modelInstance.transform.getRotation(test);
		//System.out.println("w=" + test.w + " x=" + test.x + " y=" + test.y + " z=" + test.z);
	}

	public void rotate(float degrees) {
		rotateDegrees = degrees;
		rotate = true;
		Quaternion test = new Quaternion();
		this.modelInstance.transform.getRotation(test);
		//System.out.println("w=" + test.w + " x=" + test.x + " y=" + test.y + " z=" + test.z + " l=");

	}

	public Quaternion rotation() {
		Quaternion test = new Quaternion();
		this.modelInstance.transform.getRotation(test);
		return test;
	}

	public void setRotation(Quaternion rot) {
		this.modelInstance.transform.set(rot);
	}

	public void stopMove() {
		move = false;
	}

	public void stopRotate() {
		rotate = false;
	}

	public void update(float delta) {
		if (rotate)
			this.modelInstance.transform.rotate(Vector3.Y, rotateDegrees * delta);
		if (move)
			this.modelInstance.transform.translate(moveVector.x * delta, moveVector.y * delta, moveVector.z * delta);

		if (move || rotate)
			animController.setAnimation("Armature|ArmatureAction", -1, 6, null);
		else
			animController.setAnimation(null);

		animController.update(delta);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
