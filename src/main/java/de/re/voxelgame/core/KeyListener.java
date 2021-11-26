package de.re.voxelgame.core;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public final class KeyListener {
  private static final boolean[] KEYS = new boolean[350];

  private KeyListener() {
  }

  public static boolean keyPressed(int key) {
    return KEYS[key];
  }

  static void keyCallback(long window, int key, int scancode, int action, int mods) {
    if (action == GLFW_PRESS) {
      KEYS[key] = true;
    } else if (action == GLFW_RELEASE) {
      KEYS[key] = false;
    }
  }
}
