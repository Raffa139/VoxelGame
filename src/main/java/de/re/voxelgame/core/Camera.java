package de.re.voxelgame.core;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import static org.lwjgl.glfw.GLFW.*;

public class Camera {
  private Vector3f pos;
  private Vector3f up;
  private Vector3f front;

  private float pitch;
  private float yaw;

  private float lastPosX;
  private float lastPosY;

  public Camera(Vector3f pos) {
    this.pos = pos;
    this.up = new Vector3f(0.0f, 1.0f, 0.0f);
    this.front = new Vector3f(0.0f, 0.0f, 0.0f);
    this.pitch = 0.0f;
    this.yaw = -90.0f;
  }

  public void update () {
    float speed = 0.075f;
    turn(MouseListener.getLastPosX() * speed, MouseListener.getLastPosY() * speed);

    move();
  }

  public Matrix4f getViewMatrix() {
    Vector3f dir = new Vector3f();
    pos.add(front, dir);

    return new Matrix4f()
            .lookAt(pos, dir, up);
  }

  public Vector3f getPos() {
    return pos;
  }

  public float getPitch() {
    return pitch;
  }

  public float getYaw() {
    return yaw;
  }

  private void turn(double xCurrent, double yCurrent) {
    float xOffset = (float) (xCurrent - lastPosX);
    float yOffset = (float) (lastPosY - yCurrent);
    lastPosX = (float) xCurrent;
    lastPosY = (float) yCurrent;

    yaw += xOffset;
    pitch += yOffset;

    if (pitch > 89.0f) {
      pitch = 89.0f;
    }
    if (pitch < -89.0f) {
      pitch = -89.0f;
    }

    Vector3f dir = new Vector3f();
    dir.x = (float) (Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)));
    dir.y = (float) Math.sin(Math.toRadians(pitch));
    dir.z = (float) (Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)));
    front = new Vector3f(dir);
  }

  private void move() {
    float speed = 0.01f;

    if (KeyListener.keyPressed(GLFW_KEY_W)) {
      Vector3f move = new Vector3f();
      front.mul(speed, move);
      pos.add(move);
    } else if (KeyListener.keyPressed(GLFW_KEY_S)) {
      Vector3f move = new Vector3f();
      front.mul(speed, move);
      pos.sub(move);
    }

    if (KeyListener.keyPressed(GLFW_KEY_A)) {
      Vector3f move = new Vector3f();
      front.mul(speed, move);
      move.cross(up);
      pos.sub(move);
    } else if (KeyListener.keyPressed(GLFW_KEY_D)) {
      Vector3f move = new Vector3f();
      front.mul(speed, move);
      move.cross(up);
      pos.add(move);
    }

    if (KeyListener.keyPressed(GLFW_KEY_SPACE)) {
      Vector3f move = new Vector3f();
      up.mul(speed, move);
      pos.add(move);
    } else if (KeyListener.keyPressed(GLFW_KEY_LEFT_CONTROL)) {
      Vector3f move = new Vector3f();
      up.mul(speed, move);
      pos.sub(move);
    }
  }
}
