package xyz.arturinsh.Helpers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class AssetsLoader {
	private static Skin skin;
	private static Model humanModel, ground, dog, testBox;
	private static AssetManager assets;
	private static Texture up, down, right, left, sky, heightMapTexture, touchBackground, touchKnob,
	human1, human2, human3;
	private static Pixmap heightMapData, heightMapSmall;

	public static void initUI() {
		up = new Texture(Gdx.files.internal("triangle_up.png"));
		down = new Texture(Gdx.files.internal("triangle_down.png"));
		right = new Texture(Gdx.files.internal("triangle_right.png"));
		left = new Texture(Gdx.files.internal("triangle_left.png"));
		touchBackground = new Texture(Gdx.files.internal("touchBackground.png"));
		touchKnob = new Texture(Gdx.files.internal("touchKnob.png"));
		heightMapTexture = new Texture(Gdx.files.internal("big512.png"));
		heightMapData = new Pixmap(Gdx.files.internal("big512.png"));
		heightMapSmall = new Pixmap(Gdx.files.internal("small128.png"));
		human1 = new Texture(Gdx.files.internal("TextureHuman.png"));
		human2 = new Texture(Gdx.files.internal("TextureHuman2.png"));
		human3 = new Texture(Gdx.files.internal("TextureHuman3.png"));
		sky = new Texture(Gdx.files.internal("sky1.jpg"));
		skin = new Skin(Gdx.files.internal("uiskin.json"));
		ModelBuilder modelBuilder = new ModelBuilder();
		ground = modelBuilder.createBox(100f, 1f, 100f, new Material(ColorAttribute.createDiffuse(Color.TAN)),
				Usage.Position | Usage.Normal);
		testBox = modelBuilder.createBox(4f, 1f, 4f, new Material(ColorAttribute.createDiffuse(Color.TAN)),
				Usage.Position | Usage.Normal);
		assets = new AssetManager();
		assets.load("human.g3db", Model.class);
		assets.load("tDog.g3db", Model.class);
		//assets.load("testBox.g3db", Model.class);
		assets.finishLoading();
		humanModel = assets.get("human.g3db", Model.class);
		dog = assets.get("tDog.g3db", Model.class);
		//testBox = assets.get("testBox.g3db", Model.class);
		// TODO add dispose
	}

	public static Model getTestBoc() {
		return testBox;
	}

	public static Skin getSkin() {
		return skin;
	}

	public static Model getHumanModel() {
		return humanModel;
	}

	public static Model getGround() {
		return ground;
	}

	public static Texture getUp() {
		return up;
	}

	public static Texture getDown() {
		return down;
	}

	public static Texture getRight() {
		return right;
	}

	public static Texture getLeft() {
		return left;
	}

	public static Model getDog() {
		return dog;
	}

	public static Texture getSky() {
		return sky;
	}

	public static Texture getHeightMapTexture() {
		return heightMapTexture;
	}

	public static Pixmap getHeightMapData() {
		return heightMapData;
	}

	public static Pixmap getHeightMapSmall() {
		return heightMapSmall;
	}

	public static Texture getTouchBackground() {
		return touchBackground;
	}

	public static Texture getTouchKnob() {
		return touchKnob;
	}

	public static Texture getHuman1() {
		return human1;
	}

	public static Texture getHuman2() {
		return human2;
	}

	public static Texture getHuman3() {
		return human3;
	}
}
