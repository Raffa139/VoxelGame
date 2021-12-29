package de.re.voxelgame.core;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import static de.re.voxelgame.core.util.Vectors.*;
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
    this.yaw = 0.0f;
  }

  public void update(float deltaTime, boolean allowTurn) {
    float speed = 0.075f;

    double xCurrent = MouseListener.getLastPosX() * speed;
    double yCurrent = MouseListener.getLastPosY() * speed;
    if (!MouseListener.hasMouseMoved()) {
      lastPosX = (float) xCurrent;
      lastPosY = (float) yCurrent;
    }
    if (allowTurn) {
      turn(xCurrent, yCurrent);
    }
    lastPosX = (float) xCurrent;
    lastPosY = (float) yCurrent;

    move(deltaTime);
  }

  public Matrix4f getViewMatrix() {
    return new Matrix4f().lookAt(pos, add(pos, front), up);
  }

  public Vector3f getPos() {
    return pos;
  }

  public Vector3f getFront() {
    return front;
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

  private void move(float deltaTime) {
    float speed = 55.0f * deltaTime;

    if (KeyListener.keyPressed(GLFW_KEY_W)) {
      pos.add(mul(new Vector3f(front.x, 0.0f, front.z), speed));
    } else if (KeyListener.keyPressed(GLFW_KEY_S)) {
      pos.sub(mul(new Vector3f(front.x, 0.0f, front.z), speed));
    }

    if (KeyListener.keyPressed(GLFW_KEY_A)) {
      Vector3f move = cross(front, up).normalize();
      pos.sub(mul(move, speed));
    } else if (KeyListener.keyPressed(GLFW_KEY_D)) {
      Vector3f move = cross(front, up).normalize();
      pos.add(mul(move, speed));
    }

    if (KeyListener.keyPressed(GLFW_KEY_SPACE)) {
      pos.add(mul(up, speed));
    } else if (KeyListener.keyPressed(GLFW_KEY_LEFT_CONTROL)) {
      pos.sub(mul(up, speed));
    }
  }
}
