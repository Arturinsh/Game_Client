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
	private static Model monkeyModel, ground, dog;
	private static AssetManager assets;
	private static Texture up, down, right, left, sky, heightMapTexture;
	private static Pixmap heightMapData, heightMapSmall;
	
	public static void initUI() {
		up = new Texture(Gdx.files.internal("triangle_up.png"));
		down = new Texture(Gdx.files.internal("triangle_down.png"));
		right = new Texture(Gdx.files.internal("triangle_right.png"));
		left = new Texture(Gdx.files.internal("triangle_left.png"));
		heightMapTexture = new Texture(Gdx.files.internal("big512.png"));
		heightMapData = new Pixmap(Gdx.files.internal("big512.png"));
		heightMapSmall = new Pixmap(Gdx.files.internal("small128.png"));
		sky = new Texture(Gdx.files.internal("sky1.jpg"));
		skin = new Skin(Gdx.files.internal("uiskin.json"));
		 ModelBuilder modelBuilder = new ModelBuilder();
		 ground = modelBuilder.createBox(100f, 1f, 100f, 
	            new Material(ColorAttribute.createDiffuse(Color.TAN)),
	            Usage.Position | Usage.Normal);
	     
		assets = new AssetManager();
		assets.load("tDog.g3db", Model.class);
		assets.load("tDog2.g3db",Model.class);
		assets.finishLoading();
		monkeyModel = assets.get("tDog.g3db", Model.class);
		dog = assets.get("tDog2.g3db",Model.class);
		//TODO add dispose 
	}

	public static Skin getSkin() {
		return skin;
	}

	public static Model getMonkeyModel() {
		return monkeyModel;
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
	
	public static Texture getSky(){
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
}
