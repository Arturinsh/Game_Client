package xyz.arturinsh.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import xyz.arturinsh.GameObjects.CharacterClass;
import xyz.arturinsh.GameObjects.CharacterInstance;
import xyz.arturinsh.GameWorld.GameWorld;
import xyz.arturinsh.Helpers.AssetsLoader;
import xyz.arturinsh.Network.Packets.UserCharacter;

public class CharacterCreationScreen extends GameScreen {
	private Skin skin;
	private Table mainTable, nameTable, classTable, rightTable;
	private TextField characterNameField;
	private TextButton class1, class2, class3, submit, backButton;
	private Button musicButton;
	private Label charNameLabel, rightLabel;

	private PerspectiveCamera camera;
	private ModelBatch modelBatch;
	private CharacterInstance characterInstance;
	private Environment environment;
	private CharacterClass charClass;

	public CharacterCreationScreen(GameWorld _world) {
		super(_world);
		initUI();
		init3D();
		charClass = CharacterClass.RED;
	}

	private void initUI() {
		skin = AssetsLoader.getSkin();
		
		musicButton = new Button(new Image(AssetsLoader.getSound()), AssetsLoader.getSkin(), "toggle");
		musicButton.setPosition(20, 20);
		musicButton.setSize(64, 64);
		if (!world.isMusicPlaying()) {
			musicButton.setChecked(true);
		}
		musicButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (musicButton.isChecked()) {
					world.musicPause();
				} else {
					world.musicResume();
				}
			}
		});
		
		characterNameField = new TextField("", skin);
		charNameLabel = new Label("Name", skin);
		rightLabel = new Label("Text", skin);

		class1 = new TextButton("Green", skin);
		class2 = new TextButton("Red", skin);
		class3 = new TextButton("Blue", skin);
		submit = new TextButton("Submit", skin);
		backButton = new TextButton("Back", skin);

		class1.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				characterInstance.changeClass(CharacterClass.GREEN);
				charClass = CharacterClass.GREEN;
			}
		});

		class2.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				characterInstance.changeClass(CharacterClass.RED);
				charClass = CharacterClass.RED;
			}
		});

		class3.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				characterInstance.changeClass(CharacterClass.BLUE);
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

		backButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				game.setScreen(new CharacterSelectScreen(world));
			}
		});

		mainTable = new Table();
		nameTable = new Table();

		classTable = new Table();

		rightTable = new Table();
		// mainTable.debug();
		mainTable.setWidth(stage.getWidth());
		mainTable.setFillParent(true);

		classTable.add(class1).height(50).width(70);
		classTable.row();
		classTable.add(class2).height(50).width(70);
		classTable.row();
		classTable.add(class3).height(50).width(70);

		nameTable.add(charNameLabel);
		nameTable.row();
		nameTable.add(characterNameField);
		nameTable.row();
		nameTable.add(submit).height(30).width(140);

		rightTable.add(rightLabel);
		rightTable.row();
		rightTable.add(backButton).bottom().expand().padBottom(20).height(50).width(100);

		mainTable.add(classTable).expand().top().padTop(30).padLeft(30).align(Align.left | Align.top);
		mainTable.add(nameTable).expand().bottom().padBottom(30).padLeft(-30).align(Align.center | Align.bottom);
		mainTable.add(rightTable).expand().top().padTop(30).padRight(30).fillY();

		stage.addActor(musicButton);
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
		defChar.charClass = CharacterClass.RED;
		defChar.charName = "";
		defChar.x = 0;
		defChar.y = 0;
		defChar.z = 0;
		defChar.r = 0;
		
		characterInstance = new CharacterInstance(defChar);

		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.8f, 0.8f, 0.8f, 1.0f));
		environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		camera.update();
		camera.rotateAround(Vector3.Zero, new Vector3(0, 1, 0), 1f);
		modelBatch.begin(camera);
		modelBatch.render(characterInstance.getModelInstance(), environment);
		modelBatch.end();
		stage.act(Gdx.graphics.getDeltaTime());
		stage.draw();
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
		mainTable.setWidth(stage.getWidth());
	}
}
