package xyz.arturinsh.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalShadowLight;
import com.badlogic.gdx.graphics.g3d.utils.DepthShaderProvider;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad.TouchpadStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import xyz.arturinsh.GameObjects.CharacterInstance;
import xyz.arturinsh.GameObjects.MobInstance;
import xyz.arturinsh.GameWorld.GameWorld;
import xyz.arturinsh.Helpers.AssetsLoader;
import xyz.arturinsh.Helpers.HeightField;
import xyz.arturinsh.Helpers.HeightMap;
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

	private Touchpad touchpad;
	private TouchpadStyle touchpadStyle;
	private Skin touchpadSkin;
	private Drawable touchBackground;
	private Drawable touchKnob;

	private Table table;

	HeightField field, field2, field3;
	Renderable ground, ground2, ground3;
	boolean morph = true;
	Texture texture, texture2, texture3;
	HeightMap heightMap;

	DirectionalShadowLight shadowLight;
	ModelBatch shadowBatch;

	public WorldScreen(GameWorld _world) {
		super(_world);

		init3D();
		initUI();

		InputMultiplexer multiplexer = new InputMultiplexer(new InputHandler(world, camera), stage);
		Gdx.input.setInputProcessor(multiplexer);
	}

	private void initUI() {

		touchpadSkin = new Skin();
		touchpadSkin.add("touchBackground", AssetsLoader.getTouchBackground());
		touchpadSkin.add("touchKnob", AssetsLoader.getTouchKnob());
		touchpadStyle = new TouchpadStyle();
		touchBackground = touchpadSkin.getDrawable("touchBackground");
		touchKnob = touchpadSkin.getDrawable("touchKnob");
		touchpadStyle.background = touchBackground;
		touchpadStyle.knob = touchKnob;
		touchpad = new Touchpad(10, touchpadStyle);
		touchpad.setBounds(15, 15, 200, 200);
		touchpad.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Touchpad pad = (Touchpad) actor;
				// System.out.println(pad.getKnobPercentX() + " " +
				// pad.getKnobPercentY());
				float xMove = pad.getKnobPercentX();
				float yMove = pad.getKnobPercentY();

				if (yMove < 0.2f && yMove > -0.2f) {
					usersCharacterInstance.stopMove();
				} else if (yMove > 0.1f) {
					usersCharacterInstance.moveChar(20);
				} else if (yMove < -0.1f) {
					usersCharacterInstance.moveChar(-20);
				}

				if (xMove < 0.4f && xMove > -0.4f) {
					usersCharacterInstance.stopRotate();
				} else if (xMove > 0.2f) {
					usersCharacterInstance.rotate(-360);
				} else if (xMove < -0.2f) {
					usersCharacterInstance.rotate(360);
				}
			}
		});

		table = new Table();

		table.add(touchpad).padBottom(300).padLeft(30);
		table.left();
		stage.addActor(table);
	}

	private void init3D() {
		usersCharacterInstance = world.getUsersCharacterInstance();

		groundInstance = new ModelInstance(AssetsLoader.getGround());
		groundInstance.transform.translate(0, -0.5f, 0);

		camera = new PersonCamera(75, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), usersCharacterInstance);
		camera.far = 1000f;

		modelBatch = new ModelBatch();

		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.8f, 0.8f, 0.8f, 1.0f));
		environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
		environment.add((shadowLight = new DirectionalShadowLight(4096, 4096, 200f, 200f, 1f, 100f)).set(0.8f, 0.8f, 0.8f,
				-1f, -.8f, -.2f));
		environment.shadowMap = shadowLight;
		shadowBatch = new ModelBatch(new DepthShaderProvider());

		heightMap = new HeightMap(AssetsLoader.getHeightMapTexture(), AssetsLoader.getHeightMapData(),
				AssetsLoader.getHeightMapSmall(), environment);
	}

	// bigDelta = delta *1000
	private void renderOtherPlayers(ModelBatch batch, Environment env, float bigDelta, HeightMap map) {
		for (CharacterInstance player : world.getOtherPlayers()) {
			player.updateOther(bigDelta, map);
			player.render(modelBatch, env);
		}
	}

	private void renderMobs(ModelBatch batch, Environment env, float bigDelta, HeightMap map) {
		for (MobInstance mob : world.getMobs()) {
			mob.update(bigDelta, map);
			mob.render(batch, env);
		}
	}

	private void renderGround(ModelBatch batch) {
		for (Renderable render : heightMap.groundRenderableArray()) {
			batch.render(render);
		}
	}
	
	private void renderOtherPlayerShadows(ModelBatch shadowBatch){
		for (CharacterInstance player : world.getOtherPlayers()) {
			shadowBatch.render(player.getModelInstance());
		}
	}
	
	private void renderMobShadows(ModelBatch shadowBatch){
		for (MobInstance mob : world.getMobs()) {
			shadowBatch.render(mob.getModelInstance());
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(1, 1, 1, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		usersCharacterInstance.update(delta, heightMap);
		camera.update(delta);
		
		shadowLight.begin(usersCharacterInstance.getPosition(), camera.direction);
		shadowBatch.begin(shadowLight.getCamera());
		shadowBatch.render(usersCharacterInstance.getModelInstance());
		renderOtherPlayerShadows(shadowBatch);
		renderMobShadows(shadowBatch);
		shadowBatch.end();
		shadowLight.end();
		
		modelBatch.begin(camera);
		usersCharacterInstance.render(modelBatch, environment);
		// modelBatch.render(usersCharacterInstance.getModelInstance(),
		// environment);
		// modelBatch.render(usersCharacterInstance.getTestBoxInstance(),
		// environment);
		renderOtherPlayers(modelBatch, environment, delta * 1000, heightMap);
		renderMobs(modelBatch, environment, delta * 1000, heightMap);
		renderGround(modelBatch);
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
