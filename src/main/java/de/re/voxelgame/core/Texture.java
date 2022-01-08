package de.re.voxelgame.core;

import static org.lwjgl.opengl.GL11.glDeleteTextures;

public abstract class Texture {
  protected final int id;

  protected Texture(int id) {
    this.id = id;
  }

  public void cleanup() {
    glDeleteTextures(id);
  }

  public int getId() {
    return id;
  }

  public abstract void bind(int index);
}
