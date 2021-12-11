package de.re.voxelgame.engine.chunk;

import de.re.voxelgame.engine.BlockGeometry;
import de.re.voxelgame.engine.Vertex;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

import static de.re.voxelgame.engine.chunk.Chunk.CHUNK_SIZE;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public final class ChunkLoader {
  private ChunkLoader() {
  }

  public static Chunk loadChunk(Vector2f position) {
    List<Vertex> translatedVertices = new ArrayList<>();

    for (int x = 0; x < CHUNK_SIZE; x++) {
      for (int z = 0; z < CHUNK_SIZE; z++) {
        // Max. size in x/z direction
        int SX = CHUNK_SIZE-1;
        int SZ = CHUNK_SIZE-1;

        if ((x == 0 && z == 0) || (x == 0 && z == SZ) || (x == SX && z == 0) || (x == SX && z == SZ)) { // One of 4 corner cases
          if (x == 0 && z == 0) { // NW
            translatedVertices.addAll(translateBackLeftCorner(x, z));
          } else if (x == 0 && z == SZ) { // SW
            translatedVertices.addAll(translateFrontLeftCorner(x, z));
          } else if (x == SX && z == 0) { // NE
            translatedVertices.addAll(translateBackRightCorner(x, z));
          } else if (x == SX && z == SZ) { // SE
            translatedVertices.addAll(translateFrontRightCorner(x, z));
          }
        } else if (((x == 0 || x == SX) && (z > 0 && z < SZ)) || ((z == 0 || z == SZ) && (x > 0 && x < SX))) { // One of 2x2 edge cases
          if (z > 0 && z < SZ) { // W or E
            if (x == 0) { // W
              translatedVertices.addAll(translateLeftEdge(x, z));
            } else if (x == SX) { // E
              translatedVertices.addAll(translateRightEdge(x, z));
            }
          } else if (x > 0 && x < SX) { // N or S
            if (z == 0) { // N
              translatedVertices.addAll(translateBackEdge(x, z));
            } else if (z == SZ) { // S
              translatedVertices.addAll(translateFrontEdge(x, z));
            }
          }
        } else if ((x > 0 && x < SX) && (z > 0 && z < SZ)) { // Remaining middle cases
          translatedVertices.addAll(translateMiddleBlock(x, z));
        }
      }
    }

    float[] vertices = new float[translatedVertices.size() * 5];
    for (int i = 0; i < translatedVertices.size() * 5; i+=5) {
      Vertex v = translatedVertices.get((int) Math.floor(i / 5.0));
      vertices[i] = v.getPositions().x;
      vertices[i+1] = v.getPositions().y;
      vertices[i+2] = v.getPositions().z;
      vertices[i+3] = v.getTextures().x;
      vertices[i+4] = v.getTextures().y;
    }

    int vertexCount = vertices.length;

    int vaoId = glGenVertexArrays();
    glBindVertexArray(vaoId);

    int vbo = glGenBuffers();
    glBindBuffer(GL_ARRAY_BUFFER, vbo);
    glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

    glEnableVertexAttribArray(0);
    glVertexAttribPointer(0, 3, GL_FLOAT, false, 5 * 4, 0L);
    glEnableVertexAttribArray(1);
    glVertexAttribPointer(1, 2, GL_FLOAT, false, 5 * 4, 3 * 4);

    glBindVertexArray(0);

    return new Chunk(position, vaoId, vertexCount);
  }

  private static List<Vertex> translateLeftEdge(int x, int z) {
    List<Vertex> translated = new ArrayList<>();

    translated.addAll(translateVertices(BlockGeometry.LEFT, x, z));
    translated.addAll(translateVertices(BlockGeometry.TOP, x, z));
    translated.addAll(translateVertices(BlockGeometry.BOTTOM, x, z));

    return translated;
  }

  private static List<Vertex> translateRightEdge(int x, int z) {
    List<Vertex> translated = new ArrayList<>();

    translated.addAll(translateVertices(BlockGeometry.RIGHT, x, z));
    translated.addAll(translateVertices(BlockGeometry.TOP, x, z));
    translated.addAll(translateVertices(BlockGeometry.BOTTOM, x, z));

    return translated;
  }

  private static List<Vertex> translateFrontEdge(int x, int z) {
    List<Vertex> translated = new ArrayList<>();

    translated.addAll(translateVertices(BlockGeometry.FRONT, x, z));
    translated.addAll(translateVertices(BlockGeometry.TOP, x, z));
    translated.addAll(translateVertices(BlockGeometry.BOTTOM, x, z));

    return translated;
  }

  private static List<Vertex> translateBackEdge(int x, int z) {
    List<Vertex> translated = new ArrayList<>();

    translated.addAll(translateVertices(BlockGeometry.BACK, x, z));
    translated.addAll(translateVertices(BlockGeometry.TOP, x, z));
    translated.addAll(translateVertices(BlockGeometry.BOTTOM, x, z));

    return translated;
  }

  private static List<Vertex> translateBackLeftCorner(int x, int z) {
    List<Vertex> translated = new ArrayList<>();

    translated.addAll(translateVertices(BlockGeometry.BACK, x, z));
    translated.addAll(translateVertices(BlockGeometry.LEFT, x, z));
    translated.addAll(translateVertices(BlockGeometry.TOP, x, z));
    translated.addAll(translateVertices(BlockGeometry.BOTTOM, x, z));

    return translated;
  }

  private static List<Vertex> translateFrontLeftCorner(int x, int z) {
    List<Vertex> translated = new ArrayList<>();

    translated.addAll(translateVertices(BlockGeometry.FRONT, x, z));
    translated.addAll(translateVertices(BlockGeometry.LEFT, x, z));
    translated.addAll(translateVertices(BlockGeometry.TOP, x, z));
    translated.addAll(translateVertices(BlockGeometry.BOTTOM, x, z));

    return translated;
  }

  private static List<Vertex> translateBackRightCorner(int x, int z) {
    List<Vertex> translated = new ArrayList<>();

    translated.addAll(translateVertices(BlockGeometry.BACK, x, z));
    translated.addAll(translateVertices(BlockGeometry.RIGHT, x, z));
    translated.addAll(translateVertices(BlockGeometry.TOP, x, z));
    translated.addAll(translateVertices(BlockGeometry.BOTTOM, x, z));

    return translated;
  }

  private static List<Vertex> translateFrontRightCorner(int x, int z) {
    List<Vertex> translated = new ArrayList<>();

    translated.addAll(translateVertices(BlockGeometry.FRONT, x, z));
    translated.addAll(translateVertices(BlockGeometry.RIGHT, x, z));
    translated.addAll(translateVertices(BlockGeometry.TOP, x, z));
    translated.addAll(translateVertices(BlockGeometry.BOTTOM, x, z));

    return translated;
  }

  private static List<Vertex> translateMiddleBlock(int x, int z) {
    List<Vertex> translated = new ArrayList<>();

    translated.addAll(translateVertices(BlockGeometry.TOP, x, z));
    translated.addAll(translateVertices(BlockGeometry.BOTTOM, x, z));

    return translated;
  }

  private static List<Vertex> translateVertices(Vertex[] vertices, int x, int z) {
    List<Vertex> translated = new ArrayList<>();

    for (Vertex v : vertices) {
      Vertex vertex = new Vertex(v.getPositions().x + x, v.getPositions().y, v.getPositions().z + z, v.getTextures().x, v.getTextures().y);
      translated.add(vertex);
    }

    return translated;
  }
}
