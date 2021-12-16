package de.re.voxelgame.engine.chunk;

import de.re.voxelgame.engine.Vertex;
import de.re.voxelgame.engine.noise.OpenSimplexNoise;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

import static de.re.voxelgame.engine.chunk.Chunk.CHUNK_HEIGHT;
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

  public static Chunk loadChunkNoise(Vector3f position, OpenSimplexNoise noise) {
    List<Vertex> translatedVertices = new ArrayList<>();

    for (int x = 0; x < CHUNK_SIZE; x++) {
      for (int z = 0; z < CHUNK_SIZE; z++) {
        float tx = x+(position.x*CHUNK_SIZE);
        float tz = z+(position.z*CHUNK_SIZE);

        int height = noise.voxelNoise2d(tx, tz);
        int heightN = noise.voxelNoise2d(tx, tz-1);
        int heightS = noise.voxelNoise2d(tx, tz+1);
        int heightE = noise.voxelNoise2d(tx+1, tz);
        int heightW = noise.voxelNoise2d(tx-1, tz);

        for (int y = 0; y < CHUNK_HEIGHT; y++) {
          Block block = new Block();

          if (y == height) {
            block.join(BlockFace.TOP);
          }
          if (y > heightN && y <= height) {
            block.join(BlockFace.BACK);
          }
          if (y > heightS && y <= height) {
            block.join(BlockFace.FRONT);
          }
          if (y > heightE && y <= height) {
            block.join(BlockFace.RIGHT);
          }
          if (y > heightW && y <= height) {
            block.join(BlockFace.LEFT);
          }

          translatedVertices.addAll(block.translate(x, y, z).getVertices());
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

  @Deprecated
  public static Chunk loadChunk(Vector3f position) {
    List<Vertex> translatedVertices = new ArrayList<>();

    for (int x = 0; x < CHUNK_SIZE; x++) {
      for (int y = 0; y < CHUNK_SIZE; y++) {
        for (int z = 0; z < CHUNK_SIZE; z++) {
          // Max. size in x/y/z direction
          int SX = CHUNK_SIZE - 1;
          int SY = CHUNK_SIZE - 1;
          int SZ = CHUNK_SIZE - 1;

          if ((x == 0 && z == 0) || (x == 0 && z == SZ) || (x == SX && z == 0) || (x == SX && z == SZ)) { // One of 4 corner cases
            if (x == 0 && z == 0) { // NW
              Block block = new Block(BlockFace.BACK, BlockFace.LEFT);
              if (y == 0) {
                block.join(BlockFace.BOTTOM);
              } else if (y == SY) {
                block.join(BlockFace.TOP);
              }

              translatedVertices.addAll(block.translate(x, y, z).getVertices());
            } else if (x == 0 && z == SZ) { // SW
              Block block = new Block(BlockFace.FRONT, BlockFace.LEFT);
              if (y == 0) {
                block.join(BlockFace.BOTTOM);
              } else if (y == SY) {
                block.join(BlockFace.TOP);
              }

              translatedVertices.addAll(block.translate(x, y, z).getVertices());
            } else if (x == SX && z == 0) { // NE
              Block block = new Block(BlockFace.BACK, BlockFace.RIGHT);
              if (y == 0) {
                block.join(BlockFace.BOTTOM);
              } else if (y == SY) {
                block.join(BlockFace.TOP);
              }

              translatedVertices.addAll(block.translate(x, y, z).getVertices());
            } else if (x == SX && z == SZ) { // SE
              Block block = new Block(BlockFace.FRONT, BlockFace.RIGHT);
              if (y == 0) {
                block.join(BlockFace.BOTTOM);
              } else if (y == SY) {
                block.join(BlockFace.TOP);
              }

              translatedVertices.addAll(block.translate(x, y, z).getVertices());
            }
          } else if (((x == 0 || x == SX) && (z > 0 && z < SZ)) || ((z == 0 || z == SZ) && (x > 0 && x < SX))) { // One of 2x2 edge cases
            if (z > 0 && z < SZ) { // W or E
              if (x == 0) { // W
                Block block = new Block(BlockFace.LEFT);
                if (y == 0) {
                  block.join(BlockFace.BOTTOM);
                } else if (y == SY) {
                  block.join(BlockFace.TOP);
                }

                translatedVertices.addAll(block.translate(x, y, z).getVertices());
              } else if (x == SX) { // E
                Block block = new Block(BlockFace.RIGHT);
                if (y == 0) {
                  block.join(BlockFace.BOTTOM);
                } else if (y == SY) {
                  block.join(BlockFace.TOP);
                }

                translatedVertices.addAll(block.translate(x, y, z).getVertices());
              }
            } else if (x > 0 && x < SX) { // N or S
              if (z == 0) { // N
                Block block = new Block(BlockFace.BACK);
                if (y == 0) {
                  block.join(BlockFace.BOTTOM);
                } else if (y == SY) {
                  block.join(BlockFace.TOP);
                }

                translatedVertices.addAll(block.translate(x, y, z).getVertices());
              } else if (z == SZ) { // S
                Block block = new Block(BlockFace.FRONT);
                if (y == 0) {
                  block.join(BlockFace.BOTTOM);
                } else if (y == SY) {
                  block.join(BlockFace.TOP);
                }

                translatedVertices.addAll(block.translate(x, y, z).getVertices());
              }
            }
          } else if ((x > 0 && x < SX) && (z > 0 && z < SZ)) { // Remaining middle cases
            if (y == 0) {
              Block block = new Block(BlockFace.BOTTOM);
              translatedVertices.addAll(block.translate(x, y, z).getVertices());
            } else if (y == SY) {
              Block block = new Block(BlockFace.TOP);
              translatedVertices.addAll(block.translate(x, y, z).getVertices());
            }

          }
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
}
