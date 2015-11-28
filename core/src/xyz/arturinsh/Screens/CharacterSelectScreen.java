package xyz.arturinsh.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

import xyz.arturinsh.GameWorld.GameWorld;
import xyz.arturinsh.Helpers.AssetsLoader;

public class CharacterSelectScreen extends GameScreen {

	private Skin skin;
	private Table mainTable, centerTable, scrollTable, infoTable;
	private ScrollPane charScroll;
	private TextButton enterCharacter, test;
	private Label charInfo;

	public CharacterSelectScreen(GameWorld _world) {
		super(_world);
		initUI();
	}

	private void initUI() {
		skin = AssetsLoader.getSkin();

		enterCharacter = new TextButton("Enter", skin);
		test = new TextButton("test", skin);
		charInfo = new Label("Info about char", skin);

		mainTable = new Table();
		centerTable = new Table();
		scrollTable = new Table();
		infoTable = new Table();

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

		mainTable.setWidth(stage.getWidth());
		mainTable.setFillParent(true);

		mainTable.add(infoTable).pad(20, 20, 0, 0).align(Align.top | Align.center);
		mainTable.add(centerTable).expand().bottom().center().align(Align.bottom | Align.center).padBottom(20);
		mainTable.add(charScroll).align(Align.right | Align.top).padTop(20).padRight(20);

		stage.addActor(mainTable);

		Gdx.input.setInputProcessor(stage);
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
