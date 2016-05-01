package xyz.arturinsh.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.decals.CameraGroupStrategy;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalShadowLight;
import com.badlogic.gdx.graphics.g3d.utils.DepthShaderProvider;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad.TouchpadStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;

import xyz.arturinsh.GameObjects.CharacterInstance;
import xyz.arturinsh.GameObjects.MobInstance;
import xyz.arturinsh.GameWorld.GameWorld;
import xyz.arturinsh.Helpers.AssetsLoader;
import xyz.arturinsh.Helpers.HeightField;
import xyz.arturinsh.Helpers.HeightMap;
import xyz.arturinsh.Helpers.InputHandler;
import xyz.arturinsh.Helpers.PersonCamera;

@SuppressWarnings("deprecation")
public class WorldScreen extends GameScreen {

	private PersonCamera camera;
	private ModelBatch modelBatch;
	private DecalBatch decalBatch;
	private SpriteBatch spriteFont;
	private Environment environment;
	private CharacterInstance usersCharacterInstance;
	private ModelInstance groundInstance;

	private Touchpad touchpad;
	private TouchpadStyle touchpadStyle;
	private Skin touchpadSkin;
	private Drawable touchBackground;
	private Drawable touchKnob;
	private Table mainTable, leftTable, midTable, rightTable;
	private Label hpLabel, targetLabel, pingLabel;
	private Button attack;

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

		InputMultiplexer multiplexer = new InputMultiplexer(new InputHandler(world, camera, stage), stage);
		Gdx.input.setInputProcessor(multiplexer);

	}

	private void initUI() {
		spriteFont = new SpriteBatch();

		// start init left table
		touchpadSkin = new Skin();
		touchpadSkin.add("touchBackground", AssetsLoader.getTouchBackground());
		touchpadSkin.add("touchKnob", AssetsLoader.getTouchKnob());
		touchpadStyle = new TouchpadStyle();
		touchBackground = touchpadSkin.getDrawable("touchBackground");
		touchKnob = touchpadSkin.getDrawable("touchKnob");
		touchpadStyle.background = touchBackground;
		touchpadStyle.knob = touchKnob;
		touchpad = new Touchpad(10, touchpadStyle);
		touchpad.setBounds(15, 15, 300, 300);
		touchpad.setSize(300, 300);
		touchpad.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Touchpad pad = (Touchpad) actor;
				// System.out.println(pad.getKnobPercentX() + " " +
				// pad.getKnobPercentY());
				float xMove = pad.getKnobPercentX();
				float yMove = pad.getKnobPercentY();

				if (yMove < 0.2f && yMove > -0.2f) {
					usersCharacterInstance.setMoveUp(false);
					usersCharacterInstance.setMoveDown(false);
				} else if (yMove > 0.1f) {
					usersCharacterInstance.setMoveUp(true);
				} else if (yMove < -0.1f) {
					usersCharacterInstance.setMoveDown(true);
				}

				if (xMove < 0.4f && xMove > -0.4f) {
					usersCharacterInstance.setRotateLeft(false);
					usersCharacterInstance.setRotateRight(false);
				} else if (xMove > 0.2f) {
					usersCharacterInstance.setRotateRight(true);
				} else if (xMove < -0.2f) {
					usersCharacterInstance.setRotateLeft(true);
				}
			}
		});

		hpLabel = new Label("HP:100", AssetsLoader.getSkin());
		hpLabel.setFontScale(2);
		leftTable = new Table();
		leftTable.add(hpLabel).padTop(20).padLeft(30).top().left();
		leftTable.row();
		leftTable.add(touchpad).width(300).height(300).bottom().expand().padBottom(30).padLeft(30);

		// Start init mid table
		targetLabel = new Label("", AssetsLoader.getSkin());
		targetLabel.setFontScale(2);
		midTable = new Table();
		midTable.add(targetLabel).padTop(20);

		pingLabel = new Label("Exp:100", AssetsLoader.getSkin());
		pingLabel.setFontScale(2);

		// Start Init right table
		TextureRegion attackRegion = new TextureRegion(AssetsLoader.getAttack());
		attack = new Button(new Image(attackRegion), AssetsLoader.getSkin());
		attack.setColor(1, 0, 0, 0.6f);
		attack.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				world.attack();
			}
		});

		rightTable = new Table();
		rightTable.add(pingLabel).padTop(20).padRight(20).top();
		rightTable.row();
		rightTable.add(attack).height(200).width(200).bottom().expand().padRight(30).padBottom(80);

		mainTable = new Table();
		mainTable.setWidth(stage.getWidth());
		mainTable.setFillParent(true);

		mainTable.add(leftTable).fillY();
		mainTable.add(midTable).expand().align(Align.top | Align.center);
		mainTable.add(rightTable).fillY();

		stage.addActor(mainTable);
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
		environment.add((shadowLight = new DirectionalShadowLight(4096, 4096, 200f, 200f, 1f, 100f)).set(0.8f, 0.8f,
				0.8f, -1f, -.8f, -.2f));
		environment.shadowMap = shadowLight;
		shadowBatch = new ModelBatch(new DepthShaderProvider());
		decalBatch = new DecalBatch(new CameraGroupStrategy(camera));
		heightMap = new HeightMap(AssetsLoader.getHeightMapTexture(), AssetsLoader.getBoundingPixmap(),
				AssetsLoader.getHeightMapSmall(), environment);
	}

	// bigDelta = delta *1000
	private void renderOtherPlayers(ModelBatch batch, Environment env, float bigDelta, HeightMap map) {
		for (CharacterInstance player : world.getOtherPlayers()) {
			player.updateOther(bigDelta, map);
			player.render(modelBatch, env, camera);
		}
	}

	private void renderMobs(ModelBatch batch, Environment env, float bigDelta, HeightMap map) {
		for (MobInstance mob : world.getMobs()) {
			mob.update(bigDelta, map);
			mob.render(batch, env, camera);
		}
	}

	private void renderGround(ModelBatch batch) {
		for (Renderable render : heightMap.groundRenderableArray()) {
			batch.render(render);
		}
	}

	private void renderOtherPlayerShadows(ModelBatch shadowBatch) {
		for (CharacterInstance player : world.getOtherPlayers()) {
			player.renderShadow(shadowBatch);
		}
	}

	private void renderMobShadows(ModelBatch shadowBatch) {
		for (MobInstance mob : world.getMobs()) {
			mob.renderShadow(shadowBatch);

		}
	}

	private void drawOtherPlayerDecals() {
		for (CharacterInstance player : world.getOtherPlayers()) {
			if (player.getNameDecal() == null) {
				player.initDecal(spriteFont);
			}
			player.getNameDecal().lookAt(camera.position, camera.up);
			decalBatch.add(player.getNameDecal());
		}
	}

	private void drawOtherMobDecals() {
		for (MobInstance mob : world.getMobs()) {
			if (mob.getNameDecal() == null) {
				mob.initDecal(spriteFont);
			}
			mob.getNameDecal().lookAt(camera.position, camera.up);
			decalBatch.add(mob.getNameDecal());
		}
	}

	private void updateUI() {
		int hp = usersCharacterInstance.getHP();
		int exp = usersCharacterInstance.getExperience();
		if (world.getSelectedPlayer() != null) {
			int otherHP = world.getSelectedPlayer().getHP();
			targetLabel.setText("Target HP:" + otherHP);
		} else if (world.getSelectedMob() != null) {
			int otherHP = world.getSelectedMob().getHP();
			targetLabel.setText("Target HP:" + otherHP);
		} else {
			targetLabel.setText("");
		}
		hpLabel.setText("HP:" + hp + "\nEXP:" + exp);
		pingLabel.setText("Ping: " + world.getPing() + "\nFPS:" + Gdx.graphics.getFramesPerSecond());
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0.2f, 0.2f, 0.9f, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		usersCharacterInstance.update(delta, heightMap);
		camera.update(delta);
		shadowLight.begin(usersCharacterInstance.getPosition(), camera.direction);
		shadowBatch.begin(shadowLight.getCamera());
		usersCharacterInstance.renderShadow(shadowBatch);
		renderOtherPlayerShadows(shadowBatch);
		renderMobShadows(shadowBatch);
		shadowBatch.end();
		shadowLight.end();

		modelBatch.begin(camera);
		usersCharacterInstance.render(modelBatch, environment, camera);
		renderOtherPlayers(modelBatch, environment, delta * 1000, heightMap);
		renderMobs(modelBatch, environment, delta * 1000, heightMap);
		renderGround(modelBatch);
		modelBatch.end();

		updateUI();

		drawOtherPlayerDecals();
		drawOtherMobDecals();
		decalBatch.flush();
		stage.act(delta);
		stage.draw();
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
		mainTable.setWidth(stage.getWidth());
	}

}
