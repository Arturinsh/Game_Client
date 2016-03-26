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
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder.VertexInfo;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class HeightMap {
	private Texture fullTexture;
	private Pixmap fullPixmap, smallPixmap;
	private HeightField renderableField, dataField;
	private Renderable ground;
	private Environment environment;
	private Array<Renderable> groundModels = new Array<Renderable>();

	int fullHeight;
	int fullWidth;
	int columnCount;
	int rowCount;

	public HeightMap(Texture texture, Pixmap heigtMap, Pixmap smallPixmap, Environment _environment) {
		this.fullTexture = texture;
		this.fullPixmap = heigtMap;
		this.smallPixmap = smallPixmap;
		this.environment = _environment;
		initSmall();
		initBig();
		// TODO add dispose to assetLoader
	}

	private void initSmall() {
		renderableField = new HeightField(true, true, smallPixmap, true,
				Usage.Position | Usage.Normal | Usage.ColorUnpacked | Usage.TextureCoordinates);
		
		float width = fullPixmap.getWidth();
		float height = fullPixmap.getHeight();
		
		renderableField.corner00.set(0f, 0, 0f);
		renderableField.corner10.set(width, 0, 0f);
		renderableField.corner01.set(0f, 0, height);
		renderableField.corner11.set(width, 0, height);
		renderableField.color00.set(1, 0, 0, 1);
		renderableField.color01.set(0, 1, 0, 1);
		renderableField.color10.set(0, 0, 1, 1);
		renderableField.color11.set(1, 0, 1, 1);
		renderableField.magnitude.set(0f, 20f, 0f);
		renderableField.update();

		ground = new Renderable();
		ground.environment = environment;
		ground.meshPart.mesh = renderableField.mesh;
		ground.meshPart.primitiveType = GL20.GL_TRIANGLES;
		ground.meshPart.offset = 0;
		ground.meshPart.size = renderableField.mesh.getNumIndices();
		ground.meshPart.update();
		ground.material = new Material(TextureAttribute.createDiffuse(fullTexture));
		groundModels.add(ground);
	}

	private void initBig() {
		dataField = new HeightField(false, true, fullPixmap, true,
				Usage.Position | Usage.Normal | Usage.ColorUnpacked | Usage.TextureCoordinates);
		
		float width = fullPixmap.getWidth();
		float height = fullPixmap.getHeight();
		
		dataField.corner00.set(0f, 0, 0f);
		dataField.corner10.set(width, 0, 0f);
		dataField.corner01.set(0f, 0, height);
		dataField.corner11.set(width, 0, height);
		dataField.color00.set(1, 0, 0, 1);
		dataField.color01.set(0, 1, 0, 1);
		dataField.color10.set(0, 0, 1, 1);
		dataField.color11.set(1, 0, 1, 1);
		dataField.magnitude.set(0f, 20f, 0f);
	}

	public Array<Renderable> groundRenderableArray() {
		return groundModels;
	}

	public float getHeight(int x, int y) {
		Vector3 out = new Vector3();
		dataField.getPositionAt(out, x, y);
		return out.y;
	}

}
