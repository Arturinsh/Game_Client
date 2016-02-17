package xyz.arturinsh.Helpers;

public class ObjectRotation {
	float rotation = 0;

	public ObjectRotation() {
		rotation = 0;
	}

	public ObjectRotation(float _rot) {
		rotation = _rot;
	}

	public void addToRot(float degrees) {
		rotation += degrees;
		if (rotation > 180) {
			rotation -= 360;
		}

		if (rotation < -180) {
			rotation += 360;
		}
	}

	public float getRotation() {
		return rotation;
	}
}
