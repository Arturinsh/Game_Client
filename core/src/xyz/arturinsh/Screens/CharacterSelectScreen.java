package xyz.arturinsh.Screens;

import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

import xyz.arturinsh.GameWorld.GameWorld;
import xyz.arturinsh.Helpers.AssetsLoader;
import xyz.arturinsh.NetworkListener.Packets.UserCharacter;

public class CharacterSelectScreen extends GameScreen {

	private Skin skin;
	private Table mainTable, centerTable, scrollTable, infoTable, leftTable;
	private ScrollPane charScroll;
	private TextButton enterCharacter, test, createCharacter;
	private Label charInfo;
	private String[] testStrings;

	public CharacterSelectScreen(GameWorld _world) {
		super(_world);
		initUI();
	}

	public CharacterSelectScreen(GameWorld _world, String[] test) {
		super(_world);
		testStrings = test;
		initUI();
		for (String string : testStrings) {
			System.out.println(string);
		}
	}

	public void setTest(String[] test) {
		testStrings = test;
	}

	private void initUI() {
		skin = AssetsLoader.getSkin();

		enterCharacter = new TextButton("Enter", skin);
		createCharacter = new TextButton("Create Character", skin);
		createCharacter.addListener(new ClickListener(){
			@Override
			public void clicked(InputEvent event, float x, float y) {
				game.setScreen(new CharacterCreationScreen(world));
			}
		});
		
		
		test = new TextButton("test", skin);
		charInfo = new Label("Info about char", skin);

		mainTable = new Table();
		centerTable = new Table();
		scrollTable = new Table();
		infoTable = new Table();
		leftTable = new Table();

//		mainTable.debug();

		TextButton text = new TextButton("This is a short string!", skin);
		TextButton text2 = new TextButton("This is a short string!", skin);
		TextButton text3 = new TextButton("This is a short string!", skin);

		Pixmap pm1 = new Pixmap(1, 1, Format.RGB565);
		pm1.setColor(0.1f, 0.1f, 0.1f, 1);
		pm1.fill();

		infoTable.add(charInfo);
		infoTable.setBackground(new TextureRegionDrawable(new TextureRegion(new Texture(pm1))));

		centerTable.add(enterCharacter);

		charScroll = new ScrollPane(scrollTable);

		scrollTable.add(test);
		scrollTable.row();
		scrollTable.add(text);
		scrollTable.row();
		scrollTable.add(text2);
		scrollTable.row();
		scrollTable.add(text3);
		scrollTable.setBackground(new TextureRegionDrawable(new TextureRegion(new Texture(pm1))));
		
		leftTable.add(charScroll).padTop(20).padRight(20).top();
		leftTable.row();
		leftTable.add(createCharacter).bottom().expand().padBottom(20);
//		leftTable.debug();
		
		mainTable.setWidth(stage.getWidth());
		mainTable.setFillParent(true);

		mainTable.add(infoTable).pad(20, 20, 0, 0).align(Align.top | Align.center);
		mainTable.add(centerTable).expand().bottom().center().align(Align.bottom | Align.center).padBottom(20);
		mainTable.add(leftTable).fillY();
		
		
		stage.addActor(mainTable);

		Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void show() {
		List<UserCharacter> chars = world.getCharacters();
		for (UserCharacter userCharacter : chars) {
			System.out.println(userCharacter.charName);
		}
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		stage.act(Gdx.graphics.getDeltaTime());
		stage.draw();
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);

		mainTable.setWidth(stage.getWidth());
	}

}
