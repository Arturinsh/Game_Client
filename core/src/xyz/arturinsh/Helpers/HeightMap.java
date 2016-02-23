package xyz.arturinsh.Helpers;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class HeightMap {
	private final int FIELD_SIZE = 128;
	private Texture fullTexture;
	private Pixmap fullPixmap;
	private HeightField field;
	private Renderable ground;
	private Environment environment;
	private Array<Renderable> groundModels = new Array<Renderable>();
	private HeightField[][] fields;

	int fullHeight;
	int fullWidth;
	int columnCount;
	int rowCount;

	public HeightMap(Texture texture, Pixmap heigtMap, Environment _environment) {
		this.fullTexture = texture;
		this.fullPixmap = heigtMap;
		this.environment = _environment;
		init();
		// TODO add dispose to assetLoader
	}

	private void init() {
		fullHeight = fullPixmap.getHeight();
		fullWidth = fullPixmap.getWidth();

		columnCount = partCount(fullWidth, FIELD_SIZE);
		rowCount = partCount(fullHeight, FIELD_SIZE);

		fields = new HeightField[columnCount][rowCount];

		for (int x = 0; x < columnCount; x++) {
			for (int y = 0; y < rowCount; y++) {
				Pixmap tempPixmap = new Pixmap(FIELD_SIZE, FIELD_SIZE, Format.RGB888);
				int xOffset = x * FIELD_SIZE;
				int yOffset = y * FIELD_SIZE;

				int leftWidth = fullWidth - xOffset;
				int leftHeight = fullHeight - yOffset;

				if (leftWidth > FIELD_SIZE) {
					leftWidth = FIELD_SIZE;
				}

				if (leftHeight > FIELD_SIZE) {
					leftHeight = FIELD_SIZE;
				}

				tempPixmap.drawPixmap(fullPixmap, 0, 0, xOffset, yOffset, FIELD_SIZE, FIELD_SIZE);

				HeightField tempField = new HeightField(false, tempPixmap, true,
						Usage.Position | Usage.Normal | Usage.ColorUnpacked | Usage.TextureCoordinates);

				tempField.corner00.set(x * FIELD_SIZE, 0, y * FIELD_SIZE);
				tempField.corner10.set(x * FIELD_SIZE + FIELD_SIZE, 0, y * FIELD_SIZE);
				tempField.corner01.set(x * FIELD_SIZE, 0, y * FIELD_SIZE + FIELD_SIZE);
				tempField.corner11.set(x * FIELD_SIZE + FIELD_SIZE, 0, y * FIELD_SIZE + FIELD_SIZE);
				tempField.color00.set(0, 1, 0, 1);
				tempField.color01.set(0, 1, 0, 1);
				tempField.color10.set(0, 1, 0, 1);
				tempField.color11.set(0, 1, 0, 1);
				tempField.magnitude.set(0f, 20f, 0f);
				tempField.update();

				fields[x][y] = tempField;

				Texture tempTexture = new Texture(tempPixmap, Format.RGB888, false);

				Renderable tempGround = new Renderable();
				tempGround.environment = environment;
				tempGround.meshPart.mesh = tempField.mesh;
				tempGround.meshPart.primitiveType = GL20.GL_TRIANGLES;
				tempGround.meshPart.offset = 0;
				tempGround.meshPart.size = tempField.mesh.getNumIndices();
				tempGround.meshPart.update();
				tempGround.material = new Material(TextureAttribute.createDiffuse(tempTexture));

				tempPixmap.dispose();

				groundModels.add(tempGround);
			}
		}
	}

	public Array<Renderable> groundRenderableArray() {
		return groundModels;
	}

	public float getHeight(int x, int y) {
		Vector3 out = new Vector3();

		int xIndex = getFieldsIndex(x);
		int yIndex = getFieldsIndex(y);

		if (xIndex > columnCount || yIndex > rowCount)
			return 0;

		fields[xIndex][yIndex].getPositionAt(out, x % FIELD_SIZE, y % FIELD_SIZE);
		System.out.println(xIndex + " "+yIndex+" "+out.y);
		return out.y;
	}

	private int getFieldsIndex(int coordinate) {
		int total = coordinate;
		int count = 0;
		while (total > FIELD_SIZE) {
			count++;
			total -= FIELD_SIZE;
		}
		return count;
	}

	private int partCount(int allSize, int partSize) {
		int counts = allSize / partSize;
		if (allSize % partSize > 0)
			counts++;

		return counts;
	}
}
