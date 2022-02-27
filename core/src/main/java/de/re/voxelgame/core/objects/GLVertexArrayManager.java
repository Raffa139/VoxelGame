package de.re.voxelgame.core.objects;

import java.util.*;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;

public class GLVertexArrayManager {
  private static GLVertexArrayManager instant;

  private final Map<Integer, Set<Integer>> vaoIds;

  private GLVertexArrayManager() {
    vaoIds = new HashMap<>();
  }

  public static GLVertexArrayManager get() {
    if (instant == null) {
      instant = new GLVertexArrayManager();
    }

    return instant;
  }

  public VertexArray allocateVao() {
    int vaoId = glGenVertexArrays();
    vaoIds.put(vaoId, new HashSet<>());
    glBindVertexArray(vaoId);
    return new VertexArray(vaoId);
  }

  public void freeVao(int vaoId) {
    Set<Integer> vboIds = vaoIds.getOrDefault(vaoId, Collections.emptySet());
    for (int vboId : vboIds) {
      glDeleteBuffers(vboId);
    }

    glDeleteVertexArrays(vaoId);
    vaoIds.remove(vaoId);
  }

  public void terminate() {
    for (int vaoId : vaoIds.keySet()) {
      Set<Integer> vboIds = vaoIds.getOrDefault(vaoId, Collections.emptySet());
      for (int vboId : vboIds) {
        glDeleteBuffers(vboId);
      }

      glDeleteVertexArrays(vaoId);
    }
  }

  private void appendVboToVao(int vaoId, int vboId) {
    Set<Integer> vboIds = vaoIds.get(vaoId);
    vboIds.add(vboId);
    vaoIds.put(vaoId, vboIds);
  }

  public class VertexArray {
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
      appendVboToVao(id, vboId);
      glBindBuffer(GL_ARRAY_BUFFER, vboId);

      if (data instanceof float[]) {
        glBufferData(GL_ARRAY_BUFFER, (float[]) data, usage);
      } else if (data instanceof int[]) {
        glBufferData(GL_ARRAY_BUFFER, (int[]) data, usage);
      }

      return new ArrayBuffer(this);
    }
  }

  public class ArrayBuffer {
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
