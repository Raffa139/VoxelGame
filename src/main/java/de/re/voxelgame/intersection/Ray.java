package de.re.voxelgame.intersection;

import org.joml.Vector3f;

import static de.re.engine.util.Vectors.*;

public class Ray {
  private final Vector3f origin;

  private final Vector3f direction;

  protected Ray(Vector3f origin, Vector3f direction) {
    this.origin = origin;
    this.direction = direction;
  }

  public boolean intersectsAABB(AABB boundingBox) {
    Vector3f boxMin = boundingBox.getMin();
    Vector3f boxMax = boundingBox.getMax();

    Vector3f tMin = div(sub(boxMin, origin), direction);
    Vector3f tMax = div(sub(boxMax, origin), direction);

    Vector3f t1 = min(tMin, tMax);
    Vector3f t2 = max(tMin, tMax);

    float tNear = Math.max(Math.max(t1.x, t1.y), t1.z);
    float tFar = Math.min(Math.min(t2.x, t2.y), t2.z);
    //Vector2f intersection = new Vector2f(tNear, tFar);

    return tNear < tFar;
  }

  public Vector3f getOrigin() {
    return origin;
  }

  public Vector3f getDirection() {
    return direction;
  }
}
