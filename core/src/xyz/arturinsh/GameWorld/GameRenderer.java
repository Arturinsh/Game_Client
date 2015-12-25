package xyz.arturinsh.GameWorld;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;

public class GameRenderer {

	private PerspectiveCamera camera;
	private GameWorld world;
	private ModelBatch modelBatch;
	private Model model;
	private ModelInstance modelInstance;
	private Environment environment;
	private AnimationController controller;
	

	public GameRenderer(GameWorld _world) {
		world = _world;
	}
}
