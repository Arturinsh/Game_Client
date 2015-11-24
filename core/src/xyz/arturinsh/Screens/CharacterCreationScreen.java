package xyz.arturinsh.Screens;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController.AnimationDesc;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController.AnimationListener;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.UBJsonReader;

import xyz.arturinsh.GameObjects.CharacterClass;
import xyz.arturinsh.GameWorld.GameWorld;
import xyz.arturinsh.Helpers.AssetsLoader;

public class CharacterCreationScreen extends GameScreen {
	private Skin skin;
	private Table mainTable, nameTable, classTable, rightTable;
	private TextField characterNameField;
	private TextButton class1, class2, class3, submit;
	private Label charNameLabel, rightLabel;

	private PerspectiveCamera camera;
	private ModelBatch modelBatch;
	private Model model;
	private ModelInstance modelInstance;
	private Environment environment;
	private AnimationController controller;
	private CameraInputController camController;
	private CharacterClass charClass;

	public CharacterCreationScreen(GameWorld _world) {
		super(_world);
		initUI();
		init3D();
		charClass = CharacterClass.GREEN;
	}

	private void initUI() {
		skin = AssetsLoader.getSkin();

		characterNameField = new TextField("", skin);
		charNameLabel = new Label("Name", skin);
		rightLabel = new Label("Text", skin);

		class1 = new TextButton("Green", skin);
		class2 = new TextButton("Red", skin);
		class3 = new TextButton("Blue", skin);
		submit = new TextButton("Submit", skin);

		class1.setHeight(100);

		class1.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				model.materials.first().set(new Material(ColorAttribute.createDiffuse(Color.GREEN)));
				modelInstance = new ModelInstance(model);
				charClass = CharacterClass.GREEN;
			}
		});

		class2.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				model.materials.first().set(new Material(ColorAttribute.createDiffuse(Color.RED)));
				modelInstance = new ModelInstance(model);
				charClass = CharacterClass.RED;
			}
		});

		class3.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				model.materials.first().set(new Material(ColorAttribute.createDiffuse(Color.BLUE)));
				modelInstance = new ModelInstance(model);
				charClass = CharacterClass.BLUE;
			}
		});

		submit.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				String charName = characterNameField.getText();
				world.createCharacter(charName, charClass);
			}
		});

		mainTable = new Table();
		nameTable = new Table();

		classTable = new Table();

		rightTable = new Table();
		// mainTable.debug();
		mainTable.setWidth(stage.getWidth());
		mainTable.setFillParent(true);
		// mainTable.align(Align.center | Align.bottom);
		// table.setPosition(0, Gdx.graphics.getHeight());
		// table.debug();
		// table.padBottom(stage.getHeight() / 8);
		// table.add(class1).align(Align.left);
		// table.add(class2).align(Align.left);
		// table.add(class3).align(Align.left);
		// table.row();
		// table.add(charNameLabel).colspan(3);
		// table.row();
		// table.add(characterNameField).colspan(3);

		classTable.add(class1).height(50).width(70);
		classTable.row();
		classTable.add(class2).height(50).width(70);
		classTable.row();
		classTable.add(class3).height(50).width(70);

		nameTable.add(charNameLabel);
		nameTable.row();
		nameTable.add(characterNameField);
		nameTable.row();
		nameTable.add(submit);

		rightTable.add(rightLabel);

		mainTable.add(classTable).expand().top().padTop(30).padLeft(30).align(Align.left | Align.top);
		mainTable.add(nameTable).expand().bottom().padBottom(30).padLeft(-30).align(Align.center | Align.bottom);
		mainTable.add(rightTable).expand().top().padTop(30).padRight(30);

		stage.addActor(mainTable);

		Gdx.input.setInputProcessor(stage);

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
		System.out.println("Hello" + model.materials.size);

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
		// environment.add(new PointLight().set(0.5f, 0.1f, 0.1f, 0f, 2f, 5f,
		// 5f));
		// environment.add(new PointLight().set(0.5f, 0.1f, 0.1f, 0f, 2f, -5f,
		// 5f));
		// environment.add(new PointLight().set(0.5f, 0.1f, 0.1f, 5f, 2f, 0f,
		// 5f));
		// environment.add(new PointLight().set(0.5f, 0.1f, 0.1f, -5f, 2f, 0f,
		// 5f));
		camController = new CameraInputController(camera);
		// Gdx.input.setInputProcessor(camController);
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
				// controller.current.speed += 0.1f;
			}

		});
		model.materials.first().set(new Material(ColorAttribute.createDiffuse(Color.GREEN)));
		modelInstance = new ModelInstance(model);
	}

	@Override
	public void render(float delta) {
		// Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(),
		// Gdx.graphics.getHeight());
		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		camera.update();
		camera.rotateAround(Vector3.Zero, new Vector3(0, 1, 0), 1f);
		// You need to call update on the animation controller so it will
		// advance the animation. Pass in frame delta
		controller.update(Gdx.graphics.getDeltaTime());
		// Like spriteBatch, just with models! pass in the box Instance and the
		// environment
		modelBatch.begin(camera);
		modelBatch.render(modelInstance, environment);
		modelBatch.end();
		stage.act(Gdx.graphics.getDeltaTime());
		stage.draw();
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
		// table.setPosition(0, Gdx.graphics.getHeight());

		// table.padTop(Gdx.graphics.getHeight() / 3);
		mainTable.setWidth(stage.getWidth());
	}
}
