package xyz.arturinsh.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import xyz.arturinsh.GameObjects.CharacterClass;
import xyz.arturinsh.GameObjects.CharacterInstance;
import xyz.arturinsh.GameWorld.GameWorld;
import xyz.arturinsh.Helpers.AssetsLoader;
import xyz.arturinsh.Helpers.ChaseCamera;
import xyz.arturinsh.Helpers.InputHandler;
import xyz.arturinsh.Network.Packets.UserCharacter;

public class WorldScreen extends GameScreen {

	private ChaseCamera chaseCamera;
	private ModelBatch modelBatch;
	private Environment environment;
	private CharacterInstance usersCharacterInstance;
	private ModelInstance groundInstance;

	private Button upButton, downButton, leftButton, rightButton;
	private Skin skin;
	private Table table;

	public WorldScreen(GameWorld _world) {
		super(_world);
		init3D();
		initUI();
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
				usersCharacterInstance.moveChar(new Vector3(0, 0, 20));
				return true;
			}

			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				usersCharacterInstance.stopMove();
			}

		});
		downButton = new Button(new Image(downImage), skin);
		downButton.addListener(new InputListener() {

			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				usersCharacterInstance.moveChar(new Vector3(0, 0, -20));
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
		UserCharacter defChar = new UserCharacter();
		defChar.charClass = CharacterClass.RED;
		usersCharacterInstance = new CharacterInstance(defChar);

		groundInstance = new ModelInstance(AssetsLoader.getGround());
		groundInstance.transform.translate(0, -0.5f, 0);
		// camera = new PerspectiveCamera(75, Gdx.graphics.getWidth(),
		// Gdx.graphics.getHeight());
		chaseCamera = new ChaseCamera(75, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

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
		chaseCamera.targetOffset.set(0, 4, -2);
	}

	private void renderOtherPlayers(ModelBatch batch, Environment env) {
		for (CharacterInstance player : world.getOtherPlayers()) {
			batch.render(player.getModelInstance(), env);
		}
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(1, 1, 1, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		chaseCamera.transform = usersCharacterInstance.getTransform();

		// chaseCamera.targetOffset.set(usersCharacterInstance.getPosition());
		chaseCamera.update(delta, true);
		chaseCamera.up.set(0, 1, 0);

		usersCharacterInstance.update(delta);
		//testInstance.setRotation(usersCharacterInstance.getRotation());
		modelBatch.begin(chaseCamera);
		modelBatch.render(groundInstance, environment);
		modelBatch.render(usersCharacterInstance.getModelInstance(), environment);
		//modelBatch.render(testInstance.getModelInstance(), environment);
		renderOtherPlayers(modelBatch, environment);
		modelBatch.end();
		stage.act(Gdx.graphics.getDeltaTime());
		stage.draw();
	}

	public void setUsersCharacter(CharacterInstance character) {
		usersCharacterInstance = character;
		world.setUsersCharacterInstance(usersCharacterInstance);
		InputMultiplexer multiplexer = new InputMultiplexer(new InputHandler(world), stage);
		Gdx.input.setInputProcessor(multiplexer);
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);

		table.setWidth(stage.getWidth());
	}

}
