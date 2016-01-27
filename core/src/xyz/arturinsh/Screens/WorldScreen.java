package xyz.arturinsh.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import xyz.arturinsh.GameObjects.CharacterInstance;
import xyz.arturinsh.GameObjects.DogInstance;
import xyz.arturinsh.GameWorld.GameWorld;
import xyz.arturinsh.Helpers.AssetsLoader;
import xyz.arturinsh.Helpers.InputHandler;
import xyz.arturinsh.Helpers.PersonCamera;

public class WorldScreen extends GameScreen {

	private PersonCamera camera;
	private ModelBatch modelBatch;

	// DirectionalShadowLight shadowLight;
	// ModelBatch shadowBatch;

	private Environment environment;
	private CharacterInstance usersCharacterInstance;
	private ModelInstance groundInstance, sphereInstance;

	private Button upButton, downButton, leftButton, rightButton;
	private Skin skin;
	private Table table;

	private DogInstance dogInstance;


	public WorldScreen(GameWorld _world) {
		super(_world);

		init3D();
		initUI();

		InputMultiplexer multiplexer = new InputMultiplexer(new InputHandler(world, camera), stage);
		Gdx.input.setInputProcessor(multiplexer);
	}

	private void initUI() {
		table = new Table();
		skin = AssetsLoader.getSkin();
		TextureRegion upImage = new TextureRegion(AssetsLoader.getUp());
		TextureRegion downImage = new TextureRegion(AssetsLoader.getDown());
		TextureRegion leftImage = new TextureRegion(AssetsLoader.getLeft());
		TextureRegion rightImage = new TextureRegion(AssetsLoader.getRight());
		upButton = new Button(new Image(upImage), skin);
		upButton.addListener(new InputListener() {

			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				usersCharacterInstance.moveChar(20);
				return true;
			}

			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				usersCharacterInstance.stopMove();
			}

		});
		downButton = new Button(new Image(downImage), skin);
		downButton.addListener(new InputListener() {

			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				usersCharacterInstance.moveChar(-20);
				return true;
			}

			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				usersCharacterInstance.stopMove();
			}

		});
		leftButton = new Button(new Image(leftImage), skin);
		leftButton.addListener(new InputListener() {

			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				usersCharacterInstance.rotate(360);
				return true;
			}

			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				usersCharacterInstance.stopRotate();
			}

		});
		rightButton = new Button(new Image(rightImage), skin);
		rightButton.addListener(new InputListener() {

			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				usersCharacterInstance.rotate(-360);
				return true;
			}

			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				usersCharacterInstance.stopRotate();
			}

		});
		table.add(upButton);
		table.add(downButton);
		table.add(leftButton);
		table.add(rightButton);
		table.padBottom(80);
		stage.addActor(table);
	}

	private void init3D() {
		usersCharacterInstance = world.getUsersCharacterInstance();

		dogInstance = new DogInstance();

		groundInstance = new ModelInstance(AssetsLoader.getGround());
		groundInstance.transform.translate(0, -0.5f, 0);

		sphereInstance = createSkySphere(400, AssetsLoader.getSky(), null);

		camera = new PersonCamera(75, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), usersCharacterInstance);
		camera.far = 1000f;

		modelBatch = new ModelBatch();

		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.8f, 0.8f, 0.8f, 1.0f));
		// environment.add((shadowLight = new DirectionalShadowLight(1024, 1024,
		// 30f, 30f, 1f, 500f)).set(0.8f, 0.8f, 0.8f,
		// -1f, -.8f, -.2f));
		//
		// environment.shadowMap = shadowLight;

		// shadowBatch = new ModelBatch(new DepthShaderProvider());
		environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
	}

	// bigDelta = delta *1000
	private void renderOtherPlayers(ModelBatch batch, Environment env, float bigDelta) {
		for (CharacterInstance player : world.getOtherPlayers()) {
			player.updateOther(bigDelta);
			batch.render(player.getModelInstance(), env);
		}
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(1, 1, 1, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		usersCharacterInstance.update(delta);
		camera.update(delta);

		// shadowLight.begin(Vector3.Zero, camera.direction);
		// shadowBatch.begin(shadowLight.getCamera());
		// shadowBatch.render(usersCharacterInstance.getModelInstance());
		// shadowBatch.render(dogInstance.getModelInstance());
		// shadowBatch.end();
		// shadowLight.end();

		modelBatch.begin(camera);
		modelBatch.render(groundInstance, environment);
		modelBatch.render(usersCharacterInstance.getModelInstance(), environment);
		modelBatch.render(dogInstance.getModelInstance(), environment);
		// modelBatch.render(sphereInstance);
		dogInstance.update(delta);
		renderOtherPlayers(modelBatch, environment, delta * 1000);
		modelBatch.end();
		stage.act(delta);
		stage.draw();
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
		table.setWidth(stage.getWidth());
	}

	private ModelInstance createSkySphere(float r, Texture t, Color c) {
		Material sphereMaterial = new Material();
		if (t != null)
			sphereMaterial.set(TextureAttribute.createDiffuse(t));
		if (c != null)
			sphereMaterial.set(ColorAttribute.createDiffuse(c));
		int usageCode = Usage.Position + Usage.ColorPacked + Usage.Normal + Usage.TextureCoordinates;

		ModelBuilder builder = new ModelBuilder();
		Model sphereModel = builder.createSphere(r, r, r, 32, 32, sphereMaterial, usageCode);

		for (Mesh m : sphereModel.meshes)
			m.scale(1, 1, -1);

		Vector3 position = new Vector3(0, 0, 0);

		ModelInstance sphere = new ModelInstance(sphereModel, position);
		return sphere;
	}



}
