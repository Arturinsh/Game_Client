package xyz.arturinsh.Helpers;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Vector3;

import xyz.arturinsh.GameObjects.CharacterInstance;

public class PersonCamera extends PerspectiveCamera {

	private CharacterInstance player;
	private float distanceFromPlayer = 0;
	private float angleAroundPlayer = 0;
	private boolean rotatePlayer = false;
	private float rotateAngle = 0;

	public PersonCamera(CharacterInstance _player) {
		super();
		this.player = _player;
	}

	public PersonCamera(float fieldOfView, float viewportWidth, float viewportHeight, CharacterInstance _player) {
		super(fieldOfView, viewportWidth, viewportHeight);
		this.player = _player;
	}

	public void update(float delta) {
		if (player != null) {
			float rot = player.getRotation() + angleAroundPlayer;
			float playerRotationRad = (float) Math.toRadians(rot);
			float xOffset = (float) (10 * Math.sin(playerRotationRad));
			float zOffset = (float) (10 * Math.cos(playerRotationRad));

			position.set(player.getPosition()).add(-xOffset, 6, -zOffset);
			lookAt(player.getPosition());
			up.set(Vector3.Y);
		}
		if (rotatePlayer) {
			angleAroundPlayer += rotateAngle * delta;
			if (angleAroundPlayer > 360)
				angleAroundPlayer -= 360;
			if (angleAroundPlayer < 360)
				angleAroundPlayer += 360;
			
			System.out.println(angleAroundPlayer);
		}
		super.update();
	}

	public void rotateAroundPlayer(float angle) {
		rotateAngle = angle;
		rotatePlayer = true;
	}

	public void stopRotate() {
		rotatePlayer = false;
	}

}
