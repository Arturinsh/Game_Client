package xyz.arturinsh.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;

import xyz.arturinsh.GameObjects.CharacterClass;
import xyz.arturinsh.GameObjects.CharacterInstance;
import xyz.arturinsh.GameWorld.GameWorld;
import xyz.arturinsh.Helpers.AssetsLoader;
import xyz.arturinsh.Helpers.ChaseCamera;
import xyz.arturinsh.Helpers.InputHandler;

public class WorldScreen extends GameScreen {

	private PerspectiveCamera camera;
	private ChaseCamera chaseCamera;
	private ModelBatch modelBatch;
	private Environment environment;
	private CharacterInstance usersCharacterInstance, testInstance;
	private ModelInstance groundInstance;

	public WorldScreen(GameWorld _world) {
		super(_world);
		init3D();
	}

	private void init3D() {
		usersCharacterInstance = new CharacterInstance(CharacterClass.RED);
		testInstance = new CharacterInstance(CharacterClass.GREEN);
		testInstance.setPosition(2, 0, 3);

		groundInstance = new ModelInstance(AssetsLoader.getGround());
		groundInstance.transform.translate(0, -0.5f, 0);
		// camera = new PerspectiveCamera(75, Gdx.graphics.getWidth(),
		// Gdx.graphics.getHeight());
		chaseCamera = new ChaseCamera(75, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		// camera.position.set(0f, 6f, -7f);
		// camera.lookAt(0f, 4f, 0f);
		// camera.near = 0.1f;
		// camera.far = 300.0f;

		chaseCamera.position.set(0f, 6f, -7f);
		chaseCamera.lookAt(0f, 4f, 0f);
		chaseCamera.near = 0.1f;
		chaseCamera.far = 300.0f;

		modelBatch = new ModelBatch();

		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.8f, 0.8f, 0.8f, 1.0f));
		environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

		// camController = new CameraInputController(camera);
		// Gdx.input.setInputProcessor(camController);
		chaseCamera.desiredOffset.set(0, 3, -7);
		chaseCamera.desiredLocation.set(0, 6.1f, -7);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(1, 1, 1, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		chaseCamera.transform = usersCharacterInstance.getTransform();

		// chaseCamera.targetOffset.set(usersCharacterInstance.getPosition());

		chaseCamera.update(delta, true);
		usersCharacterInstance.update(delta);
		modelBatch.begin(chaseCamera);
		modelBatch.render(groundInstance, environment);
		modelBatch.render(usersCharacterInstance.getModelInstance(), environment);
		modelBatch.render(testInstance.getModelInstance(), environment);
		modelBatch.end();
	}

	public void setUsersCharacter(CharacterInstance character) {
		usersCharacterInstance = character;
		world.setUsersCharacterInstance(usersCharacterInstance);
		Gdx.input.setInputProcessor(new InputHandler(world));
	}
}
