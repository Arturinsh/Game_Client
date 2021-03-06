package xyz.arturinsh.Helpers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class AssetsLoader {
	private static Preferences preferences;
	private static Skin skin;
	private static Model humanModel, ground, dog, vladinator, attackCage, selecBox, dogAttack, graveStone,
			vladinatorAttack, croco, tentacleAttack;
	private static AssetManager assets;
	private static Texture up, down, right, left, heightMapTexture, touchBackground, touchKnob, human1, human2, human3,
			attack, sound, settings;
	private static Pixmap heightMapSmall, boundingPixmap;
	private static BitmapFont font;
	private static int[][] boundingMap;
	private static BoundingBox playerBoundingBox, dogBoundingBox, vladinatorBoundingBox, crocoBoundingBox;
	private static Music music;

	public static void initUI() {
		preferences = Gdx.app.getPreferences("World of Vladinator");
		up = new Texture(Gdx.files.internal("triangle_up.png"));
		down = new Texture(Gdx.files.internal("triangle_down.png"));
		right = new Texture(Gdx.files.internal("triangle_right.png"));
		left = new Texture(Gdx.files.internal("triangle_left.png"));
		touchBackground = new Texture(Gdx.files.internal("touchBackground.png"));
		touchKnob = new Texture(Gdx.files.internal("touchKnob.png"));
		heightMapTexture = new Texture(Gdx.files.internal("mapTexture.png"));
		attack = new Texture(Gdx.files.internal("attack.png"));
		heightMapSmall = new Pixmap(Gdx.files.internal("small128.png"));
		boundingPixmap = new Pixmap(Gdx.files.internal("MapBounds.png"));
		human1 = new Texture(Gdx.files.internal("TextureHuman.png"));
		human2 = new Texture(Gdx.files.internal("TextureHuman2.png"));
		human3 = new Texture(Gdx.files.internal("TextureHuman3.png"));
		sound = new Texture(Gdx.files.internal("sound.png"));
		settings = new Texture(Gdx.files.internal("settings.png"));
		skin = new Skin(Gdx.files.internal("uiskin.json"));
		font = new BitmapFont(Gdx.files.internal("franklin.fnt"));
		ModelBuilder modelBuilder = new ModelBuilder();
		ground = modelBuilder.createBox(100f, 1f, 100f, new Material(ColorAttribute.createDiffuse(Color.TAN)),
				Usage.Position | Usage.Normal);
		selecBox = modelBuilder.createBox(1f, 0.5f, 1f, new Material(ColorAttribute.createDiffuse(Color.GREEN)),
				Usage.Position | Usage.Normal);
		music = Gdx.audio.newMusic(Gdx.files.internal("Jahzzar_-_10_-_Take_Me_Higher.mp3"));
		assets = new AssetManager();
		assets.load("human.g3db", Model.class);
		assets.load("textDog.g3db", Model.class);
		assets.load("cage.g3db", Model.class);
		assets.load("dogAttack.g3db", Model.class);
		assets.load("gravestone.g3db", Model.class);
		assets.load("vladinators.g3db", Model.class);
		assets.load("vladinatorAttack.g3db", Model.class);
		assets.load("croco.g3db", Model.class);
		assets.load("tentacleAttack.g3db", Model.class);
		// assets.load("testBox.g3db", Model.class);
		assets.finishLoading();
		humanModel = assets.get("human.g3db", Model.class);
		dog = assets.get("textDog.g3db", Model.class);
		attackCage = assets.get("cage.g3db", Model.class);
		dogAttack = assets.get("dogAttack.g3db", Model.class);
		graveStone = assets.get("gravestone.g3db", Model.class);
		vladinator = assets.get("vladinators.g3db", Model.class);
		vladinatorAttack = assets.get("vladinatorAttack.g3db", Model.class);
		croco = assets.get("croco.g3db", Model.class);
		tentacleAttack = assets.get("tentacleAttack.g3db", Model.class);
		// testBox = assets.get("testBox.g3db", Model.class);
		// TODO add dispose
		initBoundingMap();
		initBoundingBoxes();
	}

	private static void initBoundingBoxes() {
		playerBoundingBox = new BoundingBox();
		ModelInstance playerInstance = new ModelInstance(humanModel);
		playerInstance.calculateBoundingBox(playerBoundingBox);

		dogBoundingBox = new BoundingBox();
		ModelInstance dogInstance = new ModelInstance(dog);
		dogInstance.calculateBoundingBox(dogBoundingBox);

		vladinatorBoundingBox = new BoundingBox();
		ModelInstance vladinatorInstance = new ModelInstance(vladinator);
		vladinatorInstance.calculateBoundingBox(vladinatorBoundingBox);
		
		crocoBoundingBox = new BoundingBox();
		ModelInstance crocoInstance = new ModelInstance(croco);
		crocoInstance.calculateBoundingBox(crocoBoundingBox);
	}

	private static void initBoundingMap() {
		int width = boundingPixmap.getWidth();
		int height = boundingPixmap.getHeight();

		boundingMap = new int[height][width];
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				Color pixelColor = new Color(boundingPixmap.getPixel(j, i));
				if (pixelColor.r == 1 && pixelColor.g == 1 && pixelColor.b == 1) {
					boundingMap[j][i] = 0;
				} else {
					boundingMap[j][i] = 1;
				}
			}
		}
	}

	public static int getBoundingMapPoint(int x, int y) {
		if (x >= 0 && x < boundingMap.length && y >= 0 && y < boundingMap[0].length)
			return boundingMap[x][y];
		else
			return 0;
	}

	public static Model getSelectBox() {
		return selecBox;
	}

	public static Model getAttackCage() {
		return attackCage;
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

	public static Texture getHeightMapTexture() {
		return heightMapTexture;
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

	public static Model getDogAttack() {
		return dogAttack;
	}

	public static BitmapFont getFont() {
		return font;
	}

	public static Model getGraveStone() {
		return graveStone;
	}

	public static BoundingBox getPlayerBoundingBox() {
		return playerBoundingBox;
	}

	public static BoundingBox getDogBoundingBox() {
		return dogBoundingBox;
	}

	public static Pixmap getBoundingPixmap() {
		return boundingPixmap;
	}

	public static Model getVladinator() {
		return vladinator;
	}

	public static BoundingBox getVladinatorBoundingBox() {
		return vladinatorBoundingBox;
	}

	public static Model getVladinatorAttack() {
		return vladinatorAttack;
	}

	public static Texture getAttack() {
		return attack;
	}

	public static Music getMusic() {
		return music;
	}

	public static Texture getSound() {
		return sound;
	}

	public static Texture getSettings() {
		return settings;
	}

	public static Preferences getPreferences() {
		return preferences;
	}

	public static Model getCroco() {
		return croco;
	}

	public static Model getTentacleAttack() {
		return tentacleAttack;
	}

	public static BoundingBox getCrocoBoundingBox() {
		return crocoBoundingBox;
	}
}
