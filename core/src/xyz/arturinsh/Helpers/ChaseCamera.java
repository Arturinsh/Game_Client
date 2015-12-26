package xyz.arturinsh.Helpers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

public class ChaseCamera extends PerspectiveCamera {
	/** enable or disable chasing */
	public boolean chasing = true;
	/** The object (zero based transformation) to follow */
	public Matrix4 transform;
	/** The location of target to look at relative to the object (i.e. use to always look slight in front of the object when rotated) */
	public final Vector3 targetLocation = new Vector3();
	/** The offset to location for the target to look at (i.e. use to always look slight above the object, regardless rotation) */ 
	public final Vector3 targetOffset = new Vector3();
	/** The desired location relative to the object (i.e. use this to always place the cam behind the object when rotated) */ 
	public final Vector3 desiredLocation = new Vector3();
	/** The offset for the camera to the location (i.e. use this to always place the cam above the object regardless rotation) */ 
	public final Vector3 desiredOffset = new Vector3();
	/** The absolute bounds (world coordinates) the camera cannot get out of */
	public final BoundingBox bounds = new BoundingBox();
	/** The bounds for the camera to respect relative to the location, regardless rotation */
	public final BoundingBox offsetBounds = new BoundingBox();
	/** The maximum acceleration (units per square second) the camera can move */
	public float acceleration = 20f;
	/** The maximum speed (in degrees per second) to rotate around the object.
	 * 0 = no rotation, just chase (as if the cam is connected to the object with a rope)
	 * <1 = maximum rotation, don't interpolate (as if the cam is fixed to the object)
	 * */
	public float rotationSpeed = 72f;
	/** The squared minimum distance required to rotate around the object (must be greater than zero)  */
	public float rotationOffsetSq = 1f;
	/** Read this to get the current speed and direction (units per second) */
	public final Vector3 speed = new Vector3();
	/** Read this to get the current absolute speed (units per second) */
	public float absoluteSpeed = 0;
	
	public ChaseCamera() {
		super();
	}

	public ChaseCamera(float fieldOfView, float viewportWidth, float viewportHeight) {
		super(fieldOfView, viewportWidth, viewportHeight);
	}
	
	private final static Vector3 current = new Vector3();
	private final static Vector3 desired = new Vector3();
	private final static Vector3 target = new Vector3();
	private final static Vector3 rotationAxis = new Vector3();
	private final static Matrix4 rotationMatrix = new Matrix4();

	public void update(final float delta, final boolean updateFrustum) {
		if (chasing && transform != null) {
			transform.getTranslation(direction);
			
			current.set(position).sub(direction);
			desired.set(desiredLocation).rot(transform).add(desiredOffset);
			final float desiredDistance = desired.len();
			if (rotationSpeed < 0)
				current.set(desired).nor().scl(desiredDistance);
			else if (rotationSpeed == 0 || new Vector3().set(current).dst2(desired) < rotationOffsetSq) 
				current.nor().scl(desiredDistance);
			else {
				current.nor();
				desired.nor();
				rotationAxis.set(current).crs(desired);
				float angle = (float)Math.acos(current.dot(desired)) * MathUtils.radiansToDegrees;
				final float maxAngle = rotationSpeed * delta;
				if (Math.abs(angle) > maxAngle) {
					angle = (angle < 0) ? -maxAngle : maxAngle;
				}
				current.rot(rotationMatrix.idt().rotate(rotationAxis, angle));
				current.scl(desiredDistance);
			}

			current.add(direction);
			absoluteSpeed = Math.min(absoluteSpeed + acceleration, current.dst(position) / delta);
			position.add(speed.set(current).sub(position).nor().scl(absoluteSpeed * delta));
			if (bounds.isValid()) {
				if (position.x < bounds.min.x) position.x = bounds.min.x;
				if (position.x > bounds.max.x) position.x = bounds.max.x;
				if (position.y < bounds.min.y) position.y = bounds.min.y;
				if (position.y > bounds.max.y) position.y = bounds.max.y;
				if (position.z < bounds.min.z) position.z = bounds.min.z;
				if (position.z > bounds.max.z) position.z = bounds.max.z;
			}
			if (offsetBounds.isValid()) {
				Vector3 temp = new Vector3();
				temp.set(position).sub(direction);
				//Vector3.tmp.set(position).sub(direction);
				if (temp.x < offsetBounds.min.x) position.x = offsetBounds.min.x + direction.x;
				if (temp.x > offsetBounds.max.x) position.x = offsetBounds.max.x + direction.x;
				if (temp.y < offsetBounds.min.y) position.y = offsetBounds.min.y + direction.y;
				if (temp.y > offsetBounds.max.y) position.y = offsetBounds.max.y + direction.y;
				if (temp.z < offsetBounds.min.z) position.z = offsetBounds.min.z + direction.z;
				if (temp.z > offsetBounds.max.z) position.z = offsetBounds.max.z + direction.z;
			}

			direction.add(target.set(targetLocation).rot(transform).add(targetOffset)).sub(position).nor();
		}
		super.update(updateFrustum);		
	}

	@Override
	public void update() {
		update(true);
	}
	
	@Override
	public void update(final boolean updateFrustum) {
		update(Gdx.graphics.getDeltaTime(), updateFrustum);
	}
	
	public void update(final float delta) {
		update(delta, true);
	}
}
