package xyz.arturinsh.Helpers;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Vector3;

import xyz.arturinsh.GameObjects.CharacterInstance;

public class PersonCamera extends PerspectiveCamera {

	private CharacterInstance player;
	private float distanceFromPlayer = 0;
	private float angleAroundPlayer = 0;
	
	public PersonCamera(CharacterInstance _player) {
		super();
		this.player = _player;
	}

	public PersonCamera(float fieldOfView, float viewportWidth, float viewportHeight, CharacterInstance _player) {
		super(fieldOfView, viewportWidth, viewportHeight);
		this.player = _player;
	}

	public void update() {
		if (player != null) {
			float rot = player.getRotation();
			float playerRotationRad = (float) Math.toRadians(rot);
			float xOffset = (float) (10 * Math.sin(playerRotationRad));
			float zOffset = (float) (10 * Math.cos(playerRotationRad));

			position.set(player.getPosition()).add(-xOffset, 6, -zOffset);
			lookAt(player.getPosition());
			up.set(Vector3.Y);
		}
		
		super.update();
	}

}
