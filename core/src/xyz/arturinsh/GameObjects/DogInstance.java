package xyz.arturinsh.GameObjects;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;

import xyz.arturinsh.Helpers.AssetsLoader;

public class DogInstance {
	private AnimationController animController;
	private ModelInstance modelInstance;
	private Model model;
	private float moveSpeed = 0;
	private float rotateSpeed = 0;

	public DogInstance() {
		model = AssetsLoader.getDog();
		modelInstance = new ModelInstance(model);
		animController = new AnimationController(modelInstance);
	}

	public ModelInstance getModelInstance() {
		return modelInstance;
	}

	public void setModelInstance(ModelInstance modelInstance) {
		this.modelInstance = modelInstance;
	}

	public void update(float delta) {
		animController.setAnimation("Armature|Walk", -1, 1, null);
		animController.update(delta);
	}
}
