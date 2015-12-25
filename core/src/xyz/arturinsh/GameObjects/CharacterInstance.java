package xyz.arturinsh.GameObjects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;

import xyz.arturinsh.Helpers.AssetsLoader;

public class CharacterInstance {

	private AnimationController animController;
	private ModelInstance modelInstance;
	private Model model;

	public CharacterInstance(CharacterClass charClass) {
		model = AssetsLoader.getMonkeyModel();
		changeModelMaterial(charClass);
		modelInstance = new ModelInstance(model);
	}

	public void changeClass(CharacterClass charClass) {
		changeModelMaterial(charClass);
		modelInstance = new ModelInstance(model);
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
}
