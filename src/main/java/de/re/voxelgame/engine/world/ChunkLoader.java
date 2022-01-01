package de.re.voxelgame.engine.world;

import de.re.voxelgame.core.MemoryManager;
import de.re.voxelgame.engine.noise.OpenSimplexNoise;
import de.re.voxelgame.engine.voxel.Voxel;
import de.re.voxelgame.engine.voxel.VoxelFace;
import de.re.voxelgame.engine.voxel.VoxelType;
import de.re.voxelgame.engine.voxel.VoxelVertex;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static de.re.voxelgame.engine.world.Chunk.CHUNK_SIZE;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;

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

  public static Chunk loadChunkNoise(WorldPosition position, OpenSimplexNoise noise, Vector3f highlightVoxelPosition) {
    List<VoxelVertex> translatedVertices = new ArrayList<>();
    Vector3f pos = position.getVector();

    for (int x = 0; x < CHUNK_SIZE; x++) {
      for (int z = 0; z < CHUNK_SIZE; z++) {
        float tx = x+(pos.x*CHUNK_SIZE);
        float tz = z+(pos.z*CHUNK_SIZE);

        int height = noise.voxelNoise2d(tx, tz);
        int heightN = noise.voxelNoise2d(tx, tz-1);
        int heightS = noise.voxelNoise2d(tx, tz+1);
        int heightE = noise.voxelNoise2d(tx+1, tz);
        int heightW = noise.voxelNoise2d(tx-1, tz);

        for (int y = 0; y < CHUNK_SIZE; y++) {
          boolean highlighted = new Vector3f(x, y, z).equals(highlightVoxelPosition);

          int ty = (int) (y + pos.y * CHUNK_SIZE);
          Voxel voxel = new Voxel(VoxelType.WATER, highlighted);

          // Water level = 50
          if (ty != 50 && (ty > 44 && ty <= 56)) {
            // Sand
            voxel = new Voxel(VoxelType.SAND, highlighted);
          } else if (ty > 56 && ty <= 85) {
            // Grass
            voxel = new Voxel(VoxelType.GRASS, highlighted);
          } else if (ty > 85 && ty <= 90) {
            // Dirt
            voxel = new Voxel(VoxelType.DIRT, highlighted);
          } else if (ty > 90) {
            // Stone
            voxel = new Voxel(VoxelType.COBBLESTONE, highlighted);
          } else if (ty <= 44) {
            // Gravel
            voxel = new Voxel(VoxelType.GRAVEL, highlighted);
          }

          if (ty == height || (ty == 50 && ty > height)) {
            voxel.join(VoxelFace.TOP);
          }
          if (ty > heightE && ty <= height) {
            voxel.join(0.8f, VoxelFace.RIGHT);
          }
          if (ty > heightW && ty <= height) {
            voxel.join(0.8f, VoxelFace.LEFT);
          }
          if (ty > heightN && ty <= height) {
            voxel.join(0.6f, VoxelFace.BACK);
          }
          if (ty > heightS && ty <= height) {
            voxel.join(0.6f, VoxelFace.FRONT);
          }

          if (voxel.hasVertices()) {
            translatedVertices.addAll(voxel.translate(x, y, z).getVertices());
          }
        }
      }
    }

    return storeAndReturnChunk(translatedVertices, position);
  }

  public static void unloadChunk(Chunk chunk) {
    if (chunk.containsVertices()) {
      MemoryManager.freeVao(chunk.getVaoId());
    }
  }

  private static Chunk storeAndReturnChunk(List<VoxelVertex> translatedVertices, WorldPosition position) {
    if (translatedVertices.size() == 0) {
      return new Chunk(position, -1, -1);
    }

    int[] vertexData = new int[translatedVertices.size()];
    for (int i = 0; i < translatedVertices.size(); i++) {
      VoxelVertex v = translatedVertices.get(i);

      // Push position bits according to vertex data schematics
      vertexData[i] = (int) v.getPosition().x << 26;
      vertexData[i] = vertexData[i] | (int) v.getPosition().y << 20;
      vertexData[i] = vertexData[i] | (int) v.getPosition().z << 14;

      // Push index of texture coordinate according to vertex data schematics
      int textureCoordIndex = (int) Math.floor(i % 6.0);
      vertexData[i] = vertexData[i] | VERTEX_TEXTURE_INDICES.get(textureCoordIndex) << 12;

      // Push texture tile id (voxel-type) according to vertex data schematics
      vertexData[i] = vertexData[i] | v.getTextureLayer() << 4;

      // Push light level according to vertex data schematics
      vertexData[i] = vertexData[i] | (int) (v.getLightLevel() * 5) << 1;

      // Push highlighted flag according to vertex data schematics
      vertexData[i] = vertexData[i] | (v.isHighlighted() ? 1 : 0);
    }

    int vaoId = MemoryManager
        .allocateVao()
        .bufferData(vertexData, GL_STATIC_DRAW)
        .enableAttribArray(0)
        .attribPointer(0, 1, GL_FLOAT, false, 4, 0L)
        .doFinal();

    return new Chunk(position, vaoId, vertexData.length);
  }
}
