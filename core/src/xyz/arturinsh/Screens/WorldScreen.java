package xyz.arturinsh.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
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
				System.out.println(pad.getKnobPercentX() + " " + pad.getKnobPercentY());
				float xMove = pad.getKnobPercentX();
				float yMove = pad.getKnobPercentY();

				if (yMove < 0.2f && yMove > -0.2f) {
					usersCharacterInstance.stopMove();
				}else if(yMove>0.1f){
					usersCharacterInstance.moveChar(20);
				}else if(yMove < -0.1f){
					usersCharacterInstance.moveChar(-20);
				}
				
				if (xMove < 0.4f && xMove > -0.4f) {
					usersCharacterInstance.stopRotate();
				}else if(xMove>0.2f){
					usersCharacterInstance.rotate(-360);
				}else if(xMove < -0.2f){
					usersCharacterInstance.rotate(360);
				}
			}
		});

		table = new Table();
		// skin = AssetsLoader.getSkin();
		// TextureRegion upImage = new TextureRegion(AssetsLoader.getUp());
		// TextureRegion downImage = new TextureRegion(AssetsLoader.getDown());
		// TextureRegion leftImage = new TextureRegion(AssetsLoader.getLeft());
		// TextureRegion rightImage = new
		// TextureRegion(AssetsLoader.getRight());
		// upButton = new Button(new Image(upImage), skin);
		// upButton.addListener(new InputListener() {
		//
		// public boolean touchDown(InputEvent event, float x, float y, int
		// pointer, int button) {
		// usersCharacterInstance.moveChar(20);
		// return true;
		// }
		//
		// public void touchUp(InputEvent event, float x, float y, int pointer,
		// int button) {
		// usersCharacterInstance.stopMove();
		// }
		//
		// });
		// downButton = new Button(new Image(downImage), skin);
		// downButton.addListener(new InputListener() {
		//
		// public boolean touchDown(InputEvent event, float x, float y, int
		// pointer, int button) {
		// usersCharacterInstance.moveChar(-20);
		// return true;
		// }
		//
		// public void touchUp(InputEvent event, float x, float y, int pointer,
		// int button) {
		// usersCharacterInstance.stopMove();
		// }
		//
		// });
		// leftButton = new Button(new Image(leftImage), skin);
		// leftButton.addListener(new InputListener() {
		//
		// public boolean touchDown(InputEvent event, float x, float y, int
		// pointer, int button) {
		// usersCharacterInstance.rotate(360);
		// return true;
		// }
		//
		// public void touchUp(InputEvent event, float x, float y, int pointer,
		// int button) {
		// usersCharacterInstance.stopRotate();
		// }
		//
		// });
		// rightButton = new Button(new Image(rightImage), skin);
		// rightButton.addListener(new InputListener() {
		//
		// public boolean touchDown(InputEvent event, float x, float y, int
		// pointer, int button) {
		// usersCharacterInstance.rotate(-360);
		// return true;
		// }
		//
		// public void touchUp(InputEvent event, float x, float y, int pointer,
		// int button) {
		// usersCharacterInstance.stopRotate();
		// }
		//
		// });
		//
		// table.add(upButton);
		// table.add(downButton);
		// table.add(leftButton);
		// table.add(rightButton);
		// table.padBottom(80);

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

		heightMap = new HeightMap(AssetsLoader.getHeightMapTexture(), AssetsLoader.getHeightMapData(),
				AssetsLoader.getHeightMapSmall(), environment);
	}

	// bigDelta = delta *1000
	private void renderOtherPlayers(ModelBatch batch, Environment env, float bigDelta, HeightMap map) {
		for (CharacterInstance player : world.getOtherPlayers()) {
			player.updateOther(bigDelta, map);
			batch.render(player.getModelInstance(), env);
		}
	}

	private void renderMobs(ModelBatch batch, Environment env, float bigDelta, HeightMap map) {
		for (MobInstance mob : world.getMobs()) {
			mob.update(bigDelta, map);
			batch.render(mob.getModelInstance(), env);
		}
	}

	private void renderGround(ModelBatch batch) {
		for (Renderable render : heightMap.groundRenderableArray()) {
			batch.render(render);
		}
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(1, 1, 1, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		usersCharacterInstance.update(delta, heightMap);
		camera.update(delta);

		modelBatch.begin(camera);
		modelBatch.render(usersCharacterInstance.getModelInstance(), environment);
		modelBatch.render(usersCharacterInstance.getTestBoxInstance(), environment);
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
