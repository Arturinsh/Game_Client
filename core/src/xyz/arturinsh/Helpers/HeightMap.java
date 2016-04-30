package xyz.arturinsh.Helpers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class HeightMap {
	private Texture fullTexture;
	private Pixmap boundPixmap, smallPixmap;
	private HeightField renderableField, dataField;
	private Renderable ground;
	private Environment environment;
	private Array<Renderable> groundModels = new Array<Renderable>();

	int fullHeight;
	int fullWidth;
	int columnCount;
	int rowCount;

	public HeightMap(Texture texture, Pixmap boundMap, Pixmap smallPixmap, Environment _environment) {
		this.fullTexture = texture;
		this.boundPixmap = boundMap;
		this.smallPixmap = smallPixmap;
		this.environment = _environment;
		initSmall();
		// initBig();
		// TODO add dispose to assetLoader
	}

	private void initSmall() {
		renderableField = new HeightField(true, true, smallPixmap, true,
				Usage.Position | Usage.Normal | Usage.ColorUnpacked | Usage.TextureCoordinates);

		float width = boundPixmap.getWidth();
		float height = boundPixmap.getHeight();

		renderableField.corner00.set(0f, 0, 0f);
		renderableField.corner10.set(width, 0, 0f);
		renderableField.corner01.set(0f, 0, height);
		renderableField.corner11.set(width, 0, height);
//		renderableField.color00.set(0.3f, 0.5f, 0.3f, 1);
//		renderableField.color01.set(0.3f, 0.5f, 0.3f, 1);
//		renderableField.color10.set(0.3f, 0.5f, 0.3f, 1);
//		renderableField.color11.set(0.3f, 0.5f, 0.3f, 1);
		renderableField.magnitude.set(0f, 30f, 0f);
		renderableField.update();

		ground = new Renderable();
		ground.environment = environment;
		ground.meshPart.mesh = renderableField.mesh;
		ground.meshPart.primitiveType = GL20.GL_TRIANGLES;
		ground.meshPart.offset = 0;
		ground.meshPart.size = renderableField.mesh.getNumIndices();
		ground.meshPart.update();
		ground.material = new Material(TextureAttribute.createDiffuse(fullTexture));
		//ground.material = new Material(ColorAttribute.createDiffuse(Color.FOREST));
		groundModels.add(ground);
	}

	public Array<Renderable> groundRenderableArray() {
		return groundModels;
	}

	public float getHeight2(int x, int y) {
		Vector3 out = new Vector3();
		dataField.getPositionAt(out, x, y);
		return out.y;
	}

	public float getY(int x, int y) {
		Vector3 out = new Vector3();
		renderableField.getPositionAt(out, x, y);
		return out.y;
	}

	public float getHeight(int x, int y) {
		int nx = x / 8;
		int ny = y / 8;
		float lx = x % 8f / 8f;
		float ly = y % 8f / 8f;
		// Vector3 out = new Vector3();
		// renderableField.getPositionAt(out, nx, ny);
		boolean triangle = false;

		if (lx <= (1 - ly))
			triangle = true;

		float actualHeight;
		Vector3 p1, p2, p3;
		Vector2 pos = new Vector2(lx, ly);
		if (triangle) {
			p1 = new Vector3(0, getY(nx, ny), 0);
			p2 = new Vector3(1, getY(nx + 1, ny), 0);
			p3 = new Vector3(0, getY(nx, ny + 1), 1);
			actualHeight = barryCentric(p1, p2, p3, pos);
		} else {
			p1 = new Vector3(1, getY(nx + 1, ny), 0);
			p2 = new Vector3(1, getY(nx + 1, ny + 1), 1);
			p3 = new Vector3(0, getY(nx, ny + 1), 1);
			actualHeight = barryCentric(p1, p2, p3, pos);
		}
		// System.out.println("Y=" + out.y + " aY" + actualHeight + " x=" + x +
		// " y=" + y + " nx=" + nx + " ny=" + ny
		// + " lx=" + lx + " ly=" + ly + " triangle=" + triangle);
		return actualHeight;
	}

	public float barryCentric(Vector3 p1, Vector3 p2, Vector3 p3, Vector2 pos) {
		float det = (p2.z - p3.z) * (p1.x - p3.x) + (p3.x - p2.x) * (p1.z - p3.z);
		float l1 = ((p2.z - p3.z) * (pos.x - p3.x) + (p3.x - p2.x) * (pos.y - p3.z)) / det;
		float l2 = ((p3.z - p1.z) * (pos.x - p3.x) + (p1.x - p3.x) * (pos.y - p3.z)) / det;
		float l3 = 1.0f - l1 - l2;
		return l1 * p1.y + l2 * p2.y + l3 * p3.y;
	}

}
