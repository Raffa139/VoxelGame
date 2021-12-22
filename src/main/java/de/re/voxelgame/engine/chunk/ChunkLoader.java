package de.re.voxelgame.engine.chunk;

import de.re.voxelgame.engine.Vertex;
import de.re.voxelgame.engine.noise.OpenSimplexNoise;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static de.re.voxelgame.engine.chunk.Chunk.CHUNK_SIZE;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public final class ChunkLoader {
  private static final Map<Integer, Integer> VERTEX_TEXTURE_INDICES = Map.of(
      0, 0,
      1, 1,
      2, 2,
      3, 2,
      4, 3,
      5, 0
  );

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

        for (int y = 0; y < CHUNK_SIZE; y++) {
          int ty = (int) (y + position.y * CHUNK_SIZE);
          Block block = new Block();

          if (ty == height) {
            block.join(BlockFace.TOP);
          }
          if (ty > heightE && ty <= height) {
            block.join(0.8f, BlockFace.RIGHT);
          }
          if (ty > heightW && ty <= height) {
            block.join(0.8f, BlockFace.LEFT);
          }
          if (ty > heightN && ty <= height) {
            block.join(0.6f, BlockFace.BACK);
          }
          if (ty > heightS && ty <= height) {
            block.join(0.6f, BlockFace.FRONT);
          }

          if (block.hasVertices()) {
            translatedVertices.addAll(block.translate(x, y, z).getVertices());
          }
        }
      }
    }

    return storeAndReturnChunk(translatedVertices, position);
  }

  private static Chunk storeAndReturnChunk(List<Vertex> translatedVertices, Vector3f position) {
    if (translatedVertices.size() == 0) {
      return new Chunk(position, -1, -1);
    }

    int[] vertexData = new int[translatedVertices.size()];
    for (int i = 0; i < translatedVertices.size(); i++) {
      Vertex v = translatedVertices.get(i);

      vertexData[i] = (int) v.getPosition().x << 26;
      vertexData[i] = vertexData[i] | (int) v.getPosition().y << 20;
      vertexData[i] = vertexData[i] | (int) v.getPosition().z << 14;

      int textureCoordIndex = (int) Math.floor(i % 6.0);
      vertexData[i] = vertexData[i] | VERTEX_TEXTURE_INDICES.get(textureCoordIndex) << 12;

      vertexData[i] = vertexData[i] | 1 << 3;

      vertexData[i] = vertexData[i] | (int) (v.getLightLevel() * 5);
    }

    int vertexCount = vertexData.length;

    int vaoId = glGenVertexArrays();
    glBindVertexArray(vaoId);

    int vbo = glGenBuffers();
    glBindBuffer(GL_ARRAY_BUFFER, vbo);
    glBufferData(GL_ARRAY_BUFFER, vertexData, GL_STATIC_DRAW);

    glEnableVertexAttribArray(0);
    glVertexAttribPointer(0, 1, GL_FLOAT, false, 4, 0L);

    glBindVertexArray(0);

    return new Chunk(position, vaoId, vertexCount);
  }
}
