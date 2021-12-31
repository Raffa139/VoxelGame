package de.re.voxelgame.core;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;

public final class MemoryManager {
  private static final List<Integer> VAO_IDS = new ArrayList<>();

  private static final List<Integer> VBO_IDS = new ArrayList<>();

  private MemoryManager() {
  }

  public static VertexArray allocateVao() {
    int vaoId = glGenVertexArrays();
    VAO_IDS.add(vaoId);
    glBindVertexArray(vaoId);
    return new VertexArray(vaoId);
  }

  public static void freeVao() {
    // TODO
  }

  public static void terminate() {
    for (int vaoId : VAO_IDS) {
      glDeleteVertexArrays(vaoId);
    }

    for (int vboId : VBO_IDS) {
      glDeleteBuffers(vboId);
    }
  }

  public static class VertexArray {
    private final int id;

    private VertexArray(int id) {
      this.id = id;
    }

    public ArrayBuffer bufferData(float[] data, int usage) {
      return bufferData((Object) data, usage);
    }

    public ArrayBuffer bufferData(int[] data, int usage) {
      return bufferData((Object) data, usage);
    }

    public int doFinal() {
      glBindVertexArray(0);
      return id;
    }

    private ArrayBuffer bufferData(Object data, int usage) {
      int vboId = glGenBuffers();
      VBO_IDS.add(vboId);
      glBindBuffer(GL_ARRAY_BUFFER, vboId);

      if (data instanceof float[]) {
        glBufferData(GL_ARRAY_BUFFER, (float[]) data, usage);
      } else if (data instanceof int[]) {
        glBufferData(GL_ARRAY_BUFFER, (int[]) data, usage);
      }

      return new ArrayBuffer(this);
    }
  }

  public static class ArrayBuffer {
    private final VertexArray vao;

    private ArrayBuffer(VertexArray vao) {
      this.vao = vao;
    }

    public ArrayBuffer enableAttribArray(int index) {
      glEnableVertexAttribArray(index);
      return this;
    }

    public ArrayBuffer attribPointer(int index, int size, int type, boolean normalized, int stride, long offset) {
      glVertexAttribPointer(index, size, type, normalized, stride, offset);
      return this;
    }

    public int doFinal() {
      return vao.doFinal();
    }
  }
}
