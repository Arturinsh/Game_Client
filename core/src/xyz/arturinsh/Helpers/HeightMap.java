package xyz.arturinsh.Helpers;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class HeightMap {
	private Texture fullTexture;
	private Pixmap fullPixmap;
	private HeightField field;
	private Renderable ground;
	private Environment environment;

	public HeightMap(Texture texture, Pixmap heigtMap, Environment _environment) {
		this.fullTexture = texture;
		this.fullPixmap = heigtMap;
		this.environment = _environment;
		Init();
	}

	private void Init() {
		field = new HeightField(true, fullPixmap, true,
				Usage.Position | Usage.Normal | Usage.ColorUnpacked | Usage.TextureCoordinates);
		fullPixmap.dispose();
		field.corner00.set(0f, 0, 0f);
		field.corner10.set(180f, 0, 0f);
		field.corner01.set(0f, 0, 180f);
		field.corner11.set(180f, 0, 180f);
		field.color00.set(1, 0, 0, 1);
		field.color01.set(0, 1, 0, 1);
		field.color10.set(0, 0, 1, 1);
		field.color11.set(1, 0, 1, 1);
		field.magnitude.set(0f, 20f, 0f);
		field.update();

		ground = new Renderable();
		ground.environment = environment;
		ground.meshPart.mesh = field.mesh;
		ground.meshPart.primitiveType = GL20.GL_TRIANGLES;
		ground.meshPart.offset = 0;
		ground.meshPart.size = field.mesh.getNumIndices();
		ground.meshPart.update();
		ground.material = new Material(TextureAttribute.createDiffuse(fullTexture));
	}

	public Array<Renderable> groundRenderableArray() {
		Array<Renderable> test = new Array<Renderable>();
		test.add(ground);
		return test;
	}
	
	public float getHeight(int x, int y){
		Vector3 out = new Vector3();
		field.getPositionAt(out, x, y);
		return out.y;
	}

}
