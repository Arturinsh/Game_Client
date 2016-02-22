package xyz.arturinsh.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

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
		camera.far = 1000f;

		modelBatch = new ModelBatch();

		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.8f, 0.8f, 0.8f, 1.0f));
		environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

		heightMap = new HeightMap(AssetsLoader.getHeightMapTexture(), AssetsLoader.getHeightMapData(), environment);

		// texture = new Texture(Gdx.files.internal("heightmap3.png"));
		//
		// Pixmap data = new Pixmap(Gdx.files.internal("heightmap7.png"));
		// field = new HeightField(true, data, true,
		// Usage.Position | Usage.Normal | Usage.ColorUnpacked |
		// Usage.TextureCoordinates);
		// data.dispose();
		// field.corner00.set(0f, 0, 0f);
		// field.corner10.set(180f, 0, 0f);
		// field.corner01.set(0f, 0, 180f);
		// field.corner11.set(180f, 0, 180f);
		// field.color00.set(1, 0, 0, 1);
		// field.color01.set(0, 1, 0, 1);
		// field.color10.set(0, 0, 1, 1);
		// field.color11.set(1, 0, 1, 1);
		// field.magnitude.set(0f, 20f, 0f);
		// field.update();
		//
		// ground = new Renderable();
		// ground.environment = environment;
		// ground.meshPart.mesh = field.mesh;
		// ground.meshPart.primitiveType = GL20.GL_TRIANGLES;
		// ground.meshPart.offset = 0;
		// ground.meshPart.size = field.mesh.getNumIndices();
		// ground.meshPart.update();
		// ground.material = new
		// Material(TextureAttribute.createDiffuse(texture));
		//
		// texture2 = new Texture(Gdx.files.internal("heightmap3.png"));
		//
		// Pixmap data2 = new Pixmap(Gdx.files.internal("heightmap7.png"));
		// field2 = new HeightField(true, data2, true,
		// Usage.Position | Usage.Normal | Usage.ColorUnpacked |
		// Usage.TextureCoordinates);
		// data2.dispose();
		// field2.corner00.set(180f, 0, 0f);
		// field2.corner10.set(360f, 0, 0f);
		// field2.corner01.set(180f, 0, 180f);
		// field2.corner11.set(360f, 0, 180f);
		// field2.color00.set(0, 0, 1, 1);
		// field2.color01.set(1, 0, 1, 1);
		// field2.color10.set(1, 0, 0, 1);
		// field2.color11.set(0, 1, 0, 1);
		// field2.magnitude.set(0f, 20f, 0f);
		// field2.update();
		//
		// ground2 = new Renderable();
		// ground2.environment = environment;
		// ground2.meshPart.mesh = field2.mesh;
		// ground2.meshPart.primitiveType = GL20.GL_TRIANGLES;
		// ground2.meshPart.offset = 0;
		// ground2.meshPart.size = field2.mesh.getNumIndices();
		// ground2.meshPart.update();
		// ground2.material = new
		// Material(TextureAttribute.createDiffuse(texture2));
		//
		// texture3 = new Texture(Gdx.files.internal("heightmap3.png"));
		//
		// Pixmap data3 = new Pixmap(Gdx.files.internal("heightmap7.png"));
		// field3 = new HeightField(true, data3, true,
		// Usage.Position | Usage.Normal | Usage.ColorUnpacked |
		// Usage.TextureCoordinates);
		// data3.dispose();
		// field3.corner00.set(0f, 0, 180f);
		// field3.corner10.set(180f, 0, 180f);
		// field3.corner01.set(0f, 0, 360f);
		// field3.corner11.set(180f, 0, 360f);
		// field3.color00.set(0, 0, 1, 1);
		// field3.color01.set(1, 0, 1, 1);
		// field3.color10.set(1, 0, 0, 1);
		// field3.color11.set(0, 1, 0, 1);
		// field3.magnitude.set(0f, 20f, 0f);
		// field3.update();
		//
		// ground3 = new Renderable();
		// ground3.environment = environment;
		// ground3.meshPart.mesh = field3.mesh;
		// ground3.meshPart.primitiveType = GL20.GL_TRIANGLES;
		// ground3.meshPart.offset = 0;
		// ground3.meshPart.size = field2.mesh.getNumIndices();
		// ground3.meshPart.update();
		// ground3.material = new
		// Material(TextureAttribute.createDiffuse(texture2));

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
