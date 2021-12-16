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

        int heightNW = noise.voxelNoise2d(tx-1, tz-1);
        int heightSW = noise.voxelNoise2d(tx-1, tz+1);
        int heightNE = noise.voxelNoise2d(tx+1, tz-1);
        int heightSE = noise.voxelNoise2d(tx+1, tz+1);

        float lightLevel = 4.0f;
        if (heightN > height) {
          lightLevel-=0.5f;
        }
        if (heightS > height) {
          lightLevel-=0.5f;
        }
        if (heightE > height) {
          lightLevel-=0.5f;
        }
        if (heightW > height) {
          lightLevel-=0.5f;
        }
        if (heightNW > height) {
          lightLevel-=0.5f;
        }
        if (heightSW > height) {
          lightLevel-=0.5f;
        }
        if (heightNE > height) {
          lightLevel-=0.5f;
        }
        if (heightSE > height) {
          lightLevel-=0.5f;
        }

        for (int y = 0; y < CHUNK_HEIGHT; y++) {
          Block block = new Block(lightLevel);

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

          if (block.hasVertices()) {
            translatedVertices.addAll(block.translate(x, y, z).getVertices());
          }
        }
      }
    }

    float[] vertices = new float[translatedVertices.size() * 6];
    for (int i = 0; i < translatedVertices.size() * 6; i+=6) {
      Vertex v = translatedVertices.get((int) Math.floor(i / 6.0));
      vertices[i] = v.getPosition().x;
      vertices[i+1] = v.getPosition().y;
      vertices[i+2] = v.getPosition().z;
      vertices[i+3] = v.getTexture().x;
      vertices[i+4] = v.getTexture().y;
      vertices[i+5] = v.getLightLevel();
    }

    int vertexCount = vertices.length;

    int vaoId = glGenVertexArrays();
    glBindVertexArray(vaoId);

    int vbo = glGenBuffers();
    glBindBuffer(GL_ARRAY_BUFFER, vbo);
    glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

    glEnableVertexAttribArray(0);
    glVertexAttribPointer(0, 3, GL_FLOAT, false, 6 * 4, 0L);
    glEnableVertexAttribArray(1);
    glVertexAttribPointer(1, 2, GL_FLOAT, false, 6 * 4, 3 * 4);
    glEnableVertexAttribArray(2);
    glVertexAttribPointer(2, 1, GL_FLOAT, false, 6 * 4, 5 * 4);

    glBindVertexArray(0);

    return new Chunk(position, vaoId, vertexCount);
  }
}
