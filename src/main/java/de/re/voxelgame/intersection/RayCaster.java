package de.re.voxelgame.intersection;

import de.re.engine.Camera;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public final class RayCaster {
  private RayCaster() {
  }

  public static Ray fromCamera(Camera camera) {
    return new Ray(camera.getPos(), camera.getFront().normalize());
  }

  public static Ray fromMousePosition(double mouseX, double mouseY, Camera camera, Matrix4f projection,
                                      float resolutionX, float resolutionY) {
    // Mouse picking
    // https://gist.github.com/DomNomNom/46bb1ce47f68d255fd5d
    // (https://lwjglgamedev.gitbooks.io/3d-game-development-with-lwjgl/content/chapter23/chapter23.html)
    float rx = (float) ((2.0 * mouseX) / resolutionX - 1.0f);
    float ry = (float) ((2.0 * mouseY) / resolutionY - 1.0f);
    float rz = 1.0f;
    Matrix4f inverseView = camera.getViewMatrix().invert(new Matrix4f());
    Matrix4f inverseProjection = projection.invert(new Matrix4f());

    // Ray in normalized device coords
    Vector3f rNdc = new Vector3f(rx, -ry, rz);

    // Ray in projected space
    Vector4f rClip = new Vector4f(rNdc.x, rNdc.y, -1.0f, 1.0f);

    // Ray in view space
    Vector4f rView = rClip.mul(inverseProjection, new Vector4f());
    rView = new Vector4f(rView.x, rView.y, -1.0f, 0.0f); // (only x/y needed to 'un-project')

    // Ray in world space
    Vector4f rWorld = rView.mul(inverseView, new Vector4f());
    Vector3f ray = new Vector3f(rWorld.x, rWorld.y, rWorld.z).normalize();

    return new Ray(camera.getPos(), ray);
  }
}
