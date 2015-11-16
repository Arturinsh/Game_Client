package xyz.arturinsh.Screens;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController.AnimationDesc;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController.AnimationListener;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.UBJsonReader;

import xyz.arturinsh.GameWorld.GameWorld;
import xyz.arturinsh.Helpers.AssetsLoader;

public class CharacterCreationScreen extends GameScreen {
	private Skin skin;
	private Table table;
	private TextField characterNameField;
	private Label charNameLabel;

	private PerspectiveCamera camera;
	private ModelBatch modelBatch;
	private Model model;
	private ModelInstance modelInstance;
	private Environment environment;
	private AnimationController controller;
	private CameraInputController camController;

	public CharacterCreationScreen(GameWorld _world) {
		super(_world);
		initUI();
	}

	private void initUI() {
		skin = AssetsLoader.getSkin();

		characterNameField = new TextField("", skin);
		charNameLabel = new Label("Name", skin);

		table = new Table();
		table.setWidth(stage.getWidth());
		table.align(Align.center | Align.bottom);
		// table.setPosition(0, Gdx.graphics.getHeight());
		// table.debug();
		table.padBottom(stage.getHeight() / 8);
		table.add(charNameLabel);
		table.row();
		table.add(characterNameField);
		stage.addActor(table);
		Gdx.input.setInputProcessor(stage);
		init3D();
	}

	private void init3D() {
		camera = new PerspectiveCamera(75, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		// Move the camera 5 units back along the z-axis and look at the origin
		camera.position.set(0f, 4f, 7f);
		camera.lookAt(0f, 2f, 0f);
		// Near and Far (plane) represent the minimum and maximum ranges of the
		// camera in, um, units
		camera.near = 0.1f;
		camera.far = 300.0f;

		// A ModelBatch is like a SpriteBatch, just for models. Use it to batch
		// up geometry for OpenGL
		modelBatch = new ModelBatch();

		// Model loader needs a binary json reader to decode
		UBJsonReader jsonReader = new UBJsonReader();
		// Create a model loader passing in our json reader
		G3dModelLoader modelLoader = new G3dModelLoader(jsonReader);
		// Now load the model by name
		// Note, the model (g3db file ) and textures need to be added to the
		// assets folder of the Android proj
		model = modelLoader.loadModel(Gdx.files.getFileHandle("bot_monkey.g3db", FileType.Internal));
		// Now create an instance. Instance holds the positioning data, etc of
		// an instance of your model
		modelInstance = new ModelInstance(model);

		// fbx-conv is supposed to perform this rotation for you... it doesnt
		// seem to
		// modelInstance.transform.rotate(1, 0, 0, -90);
		// move the model down a bit on the screen ( in a z-up world, down is -z
		// ).
		modelInstance.transform.translate(0, 0, 0);
		// Finally we want some light, or we wont see our color. The environment
		// gets passed in during
		// the rendering process. Create one, then create an Ambient (
		// non-positioned, non-directional ) light.
		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.8f, 0.8f, 0.8f, 1.0f));
		environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
		environment.add(new PointLight().set(0.5f, 0.1f, 0.1f, 0f, 2f, 5f, 5f));
		environment.add(new PointLight().set(0.5f, 0.1f, 0.1f, 0f, 2f, -5f, 5f));
		environment.add(new PointLight().set(0.5f, 0.1f, 0.1f, 5f, 2f, 0f, 5f));
		environment.add(new PointLight().set(0.5f, 0.1f, 0.1f, -5f, 2f, 0f, 5f));
		camController = new CameraInputController(camera);
		//Gdx.input.setInputProcessor(camController);
		// You use an AnimationController to um, control animations. Each
		// control is tied to the model instance
		controller = new AnimationController(modelInstance);
		// Pick the current animation by name
		controller.setAnimation("Armature|ArmatureAction", -1, new AnimationListener() {
			@Override
			public void onEnd(AnimationDesc animation) {
				// this will be called when the current animation is done.
				// queue up another animation called "balloon".
				// Passing a negative to loop count loops forever. 1f for speed
				// is normal speed.
				// controller.queue("Armature|Test", -1, 1f, null, 0f);
			}

			@Override
			public void onLoop(AnimationDesc animation) {
				// TODO Auto-generated method stub
				controller.current.speed += 0.1f;
			}

		});
	}

	private void render3D()
	{
		
	}
	
	@Override
	public void render(float delta) {
//		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		//Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		
		stage.act(Gdx.graphics.getDeltaTime());
		stage.draw();
		camera.update();
		camera.rotateAround(Vector3.Zero, new Vector3(0,1,0),1f);
		// You need to call update on the animation controller so it will
		// advance the animation. Pass in frame delta
		controller.update(Gdx.graphics.getDeltaTime());
		// Like spriteBatch, just with models! pass in the box Instance and the
		// environment
		modelBatch.begin(camera);
		modelBatch.render(modelInstance, environment);
		modelBatch.end();
		
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
		// table.setPosition(0, Gdx.graphics.getHeight());

		// table.padTop(Gdx.graphics.getHeight() / 3);
		table.setWidth(stage.getWidth());
	}
}
