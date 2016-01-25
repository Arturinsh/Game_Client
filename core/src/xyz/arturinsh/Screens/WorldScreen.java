package xyz.arturinsh.Screens;

import java.util.Date;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import xyz.arturinsh.GameObjects.CharacterInstance;
import xyz.arturinsh.GameWorld.GameWorld;
import xyz.arturinsh.Helpers.AssetsLoader;
import xyz.arturinsh.Helpers.InputHandler;
import xyz.arturinsh.Helpers.PersonCamera;

public class WorldScreen extends GameScreen {

	private PersonCamera camera;
	private ModelBatch modelBatch;
	private Environment environment;
	private CharacterInstance usersCharacterInstance;
	private ModelInstance groundInstance;

	private Button upButton, downButton, leftButton, rightButton;
	private Skin skin;
	private Table table;

	private Date firstDate;
	private Date lastDate;

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
		groundInstance = new ModelInstance(AssetsLoader.getGround());
		groundInstance.transform.translate(0, -0.5f, 0);

		camera = new PersonCamera(75, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), usersCharacterInstance);
		camera.far = 100;
		modelBatch = new ModelBatch();

		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.8f, 0.8f, 0.8f, 1.0f));
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

		modelBatch.begin(camera);
		modelBatch.render(groundInstance, environment);
		modelBatch.render(usersCharacterInstance.getModelInstance(), environment);
		renderOtherPlayers(modelBatch, environment, delta * 100);
		modelBatch.end();
		stage.act(delta);
		stage.draw();
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
		table.setWidth(stage.getWidth());
	}

}
