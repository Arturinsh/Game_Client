package xyz.arturinsh.Screens;

import java.util.List;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.UBJsonReader;

import xyz.arturinsh.GameObjects.CharacterClass;
import xyz.arturinsh.GameObjects.CharacterInstance;
import xyz.arturinsh.GameWorld.GameWorld;
import xyz.arturinsh.Helpers.AssetsLoader;
import xyz.arturinsh.Network.Packets.UserCharacter;

public class CharacterSelectScreen extends GameScreen {

	private Skin skin;
	private Table mainTable, centerTable, scrollTable, infoTable, leftTable;
	private ScrollPane charScroll;
	private TextButton enterCharacter, createCharacter;
	private Label charInfo;
	private String[] testStrings;

	private PerspectiveCamera camera;
	private ModelBatch modelBatch;
	private CharacterInstance characterInstance;
	private Environment environment;
	private UserCharacter selectedChar;

	public CharacterSelectScreen(GameWorld _world) {
		super(_world);
		init3D();
		initUI();
		selectedChar = null;
	}

	public CharacterSelectScreen(GameWorld _world, String[] test) {
		super(_world);
		testStrings = test;
		initUI();
		for (String string : testStrings) {
			System.out.println(string);
		}
	}

	private void initUI() {
		skin = AssetsLoader.getSkin();

		enterCharacter = new TextButton("Enter", skin);
		enterCharacter.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				// TODO add dialog to show if not seleceted or disable button
				if (selectedChar != null) {
					characterInstance.setCharacter(selectedChar);
					world.setUsersCharacterInstance(characterInstance);
					world.enterWorld(selectedChar);
					WorldScreen worldScreen = new WorldScreen(world);
					game.setScreen(worldScreen);
				}
			}
		});

		createCharacter = new TextButton("Create Character", skin);
		createCharacter.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				game.setScreen(new CharacterCreationScreen(world));
			}
		});

		charInfo = new Label("Info about char", skin);

		mainTable = new Table();
		centerTable = new Table();
		scrollTable = new Table();
		infoTable = new Table();
		leftTable = new Table();

		Pixmap pm1 = new Pixmap(1, 1, Format.RGB565);
		pm1.setColor(0.1f, 0.1f, 0.1f, 1);
		pm1.fill();

		infoTable.add(charInfo);
		infoTable.setBackground(new TextureRegionDrawable(new TextureRegion(new Texture(pm1))));

		centerTable.add(enterCharacter).height(40).width(100);

		charScroll = new ScrollPane(scrollTable);

		scrollTable.setBackground(new TextureRegionDrawable(new TextureRegion(new Texture(pm1))));

		leftTable.add(charScroll).padTop(20).padRight(20).top();
		leftTable.row();
		leftTable.add(createCharacter).bottom().expand().padBottom(20).height(40);

		mainTable.setWidth(stage.getWidth());
		mainTable.setFillParent(true);

		mainTable.add(infoTable).pad(20, 20, 0, 0).align(Align.top | Align.center);
		mainTable.add(centerTable).expand().bottom().center().align(Align.bottom | Align.center).padBottom(20);
		mainTable.add(leftTable).fillY();

		stage.addActor(mainTable);

		Gdx.input.setInputProcessor(stage);
	}

	private void init3D() {
		camera = new PerspectiveCamera(75, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		camera.position.set(0f, 4f, 7f);
		camera.lookAt(0f, 2f, 0f);
		camera.near = 0.1f;
		camera.far = 300.0f;

		modelBatch = new ModelBatch();

		UserCharacter defChar = new UserCharacter();
		defChar.charClass = CharacterClass.GREEN;
		defChar.charName = "default";

		characterInstance = new CharacterInstance(0, 0, 0, 0, defChar);

		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.8f, 0.8f, 0.8f, 1.0f));
		environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
	}

	@Override
	public void show() {
		List<UserCharacter> chars = world.getCharacters();
		for (final UserCharacter userCharacter : chars) {
			TextButton charButton = new TextButton(userCharacter.charName, skin);
			charButton.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					setSelectedChar(userCharacter);
				}
			});
			scrollTable.add(charButton).fillX().height(30).width(80);
			scrollTable.row();
		}
	}

	private void setSelectedChar(UserCharacter userChar) {
		selectedChar = userChar;
		charInfo.setText(selectedChar.charName);
		switch (selectedChar.charClass) {
		case RED:
			characterInstance.changeClass(CharacterClass.RED);
			break;
		case GREEN:
			characterInstance.changeClass(CharacterClass.GREEN);
			break;
		case BLUE:
			characterInstance.changeClass(CharacterClass.BLUE);
			break;
		default:
			selectedChar = null;
			charInfo.setText("");
		}
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		if (selectedChar != null) {

			camera.update();
			camera.rotateAround(Vector3.Zero, new Vector3(0, 1, 0), 1f);

			modelBatch.begin(camera);
			modelBatch.render(characterInstance.getModelInstance(), environment);
			modelBatch.end();
		}

		stage.act(Gdx.graphics.getDeltaTime());
		stage.draw();

	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);

		mainTable.setWidth(stage.getWidth());
	}

}
