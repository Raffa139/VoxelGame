package de.re.voxelgame.world.chunk;

import de.ren.ecs.engine.objects.GLVertexArrayManager;
import de.re.voxelgame.world.WorldPosition;
import de.re.voxelgame.world.noise.OpenSimplexNoise;
import de.re.voxelgame.world.voxel.Voxel;
import de.re.voxelgame.world.voxel.VoxelFace;
import de.re.voxelgame.world.voxel.VoxelType;
import de.re.voxelgame.world.voxel.VoxelVertex;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static de.re.voxelgame.world.chunk.Chunk.CHUNK_SIZE;
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

  public static Chunk generateChunk(WorldPosition position, OpenSimplexNoise noise) {
    byte[][][] voxelIds = new byte[CHUNK_SIZE][CHUNK_SIZE][CHUNK_SIZE];
    Vector3f pos = position.getVector();

    for (int x = 0; x < CHUNK_SIZE; x++) {
      for (int z = 0; z < CHUNK_SIZE; z++) {
        float tx = x+(pos.x*CHUNK_SIZE);
        float tz = z+(pos.z*CHUNK_SIZE);
        int height = noise.voxelNoise2d(tx, tz);

        for (int y = 0; y < CHUNK_SIZE; y++) {
          int ty = (int) (y + pos.y * CHUNK_SIZE);
          voxelIds[x][y][z] = 0;

          if (ty <= height) {
            if (ty > 44 && ty <= 56) {
              voxelIds[x][y][z] = (byte) VoxelType.SAND.ordinal();
            } else if (ty > 56 && ty <= 85) {
              voxelIds[x][y][z] = (byte) VoxelType.GRASS.ordinal();
            } else if (ty > 85 && ty <= 90) {
              voxelIds[x][y][z] = (byte) VoxelType.DIRT.ordinal();
            } else if (ty > 90 && ty <= 125) {
              voxelIds[x][y][z] = (byte) VoxelType.COBBLESTONE.ordinal();
            } else if (ty > 125) {
              voxelIds[x][y][z] = (byte) VoxelType.SNOW.ordinal();
            } else {
              voxelIds[x][y][z] = (byte) VoxelType.GRAVEL.ordinal();
            }
          }
        }
      }
    }

    for (int x = 0; x < CHUNK_SIZE; x++) {
      for (int z = 0; z < CHUNK_SIZE; z++) {
        for (int y = 0; y < CHUNK_SIZE; y++) {
          int ty = (int) (y + pos.y * CHUNK_SIZE);
          byte voxelIdBelow = voxelIds[x][y > 0 ? y-1 : 0][z];
          byte voxelIdOnTop = voxelIds[x][y < CHUNK_SIZE-1 ? y+1 : 0][z];

          if (ty == 50 && (voxelIdBelow == 0 || voxelIdOnTop == 0)) { // Water level = 50
            voxelIds[x][y][z] = (byte) VoxelType.WATER.ordinal();
          }
        }
      }
    }

    return new Chunk(position, voxelIds);
  }

  public static ChunkMesh[] loadChunkMeshes(Chunk chunk, Vector3f highlightedVoxelPosition, Map<Vector3f, Chunk> chunks) {
    List<VoxelVertex> solidVertices = new ArrayList<>();
    List<VoxelVertex> transparentVertices = new ArrayList<>();
    Vector3f pos = chunk.getRelativePosition().getVector();
    byte[][][] voxelIds = chunk.getVoxelIds();

    for (int x = 0; x < CHUNK_SIZE; x++) {
      for (int z = 0; z < CHUNK_SIZE; z++) {
        for (int y = 0; y < CHUNK_SIZE; y++) {
          byte currentVoxel = voxelIds[x][y][z];

          if (currentVoxel > 0) {
            boolean highlighted = new Vector3f(x, y, z).equals(highlightedVoxelPosition);

            VoxelType currentVoxelType = VoxelType.values()[currentVoxel];
            Voxel voxel = new Voxel(currentVoxelType, highlighted);

            byte voxelS =
                z < CHUNK_SIZE - 1 ? voxelIds[x][y][z + 1] :
                    chunks.get(new Vector3f(pos.x, pos.y, pos.z+1)) != null ?
                        chunks.get(new Vector3f(pos.x, pos.y, pos.z+1)).getVoxelIds()[x][y][0] : -1;
            byte voxelN =
                z > 0 ? voxelIds[x][y][z - 1] :
                    chunks.get(new Vector3f(pos.x, pos.y, pos.z-1)) != null ?
                        chunks.get(new Vector3f(pos.x, pos.y, pos.z-1)).getVoxelIds()[x][y][CHUNK_SIZE-1] : -1;
            byte voxelE =
                x < CHUNK_SIZE - 1 ? voxelIds[x + 1][y][z] :
                    chunks.get(new Vector3f(pos.x+1, pos.y, pos.z)) != null ?
                        chunks.get(new Vector3f(pos.x+1, pos.y, pos.z)).getVoxelIds()[0][y][z] : -1;
            byte voxelW =
                x > 0 ? voxelIds[x - 1][y][z] :
                    chunks.get(new Vector3f(pos.x-1, pos.y, pos.z)) != null ?
                        chunks.get(new Vector3f(pos.x-1, pos.y, pos.z)).getVoxelIds()[CHUNK_SIZE-1][y][z] : -1;
            byte voxelT =
                y < CHUNK_SIZE - 1 ? voxelIds[x][y + 1][z] :
                    chunks.get(new Vector3f(pos.x, pos.y+1, pos.z)) != null ?
                        chunks.get(new Vector3f(pos.x, pos.y+1, pos.z)).getVoxelIds()[x][0][z] : -1;
            byte voxelB =
                y > 0 ? voxelIds[x][y - 1][z] :
                    chunks.get(new Vector3f(pos.x, pos.y-1, pos.z)) != null ?
                        chunks.get(new Vector3f(pos.x, pos.y-1, pos.z)).getVoxelIds()[x][CHUNK_SIZE-1][z] : -1;

            if (voxelT == 0 || (currentVoxelType != VoxelType.WATER && voxelT == VoxelType.WATER.ordinal())) {
              voxel.join(VoxelFace.TOP);
            }
            if (voxelB == 0 && currentVoxelType != VoxelType.WATER) {
              voxel.join(VoxelFace.BOTTOM);
            }
            if (voxelE == 0 || (currentVoxelType != VoxelType.WATER && voxelE == VoxelType.WATER.ordinal())) {
              voxel.join(0.8f, VoxelFace.RIGHT);
            }
            if (voxelW == 0 || (currentVoxelType != VoxelType.WATER && voxelW == VoxelType.WATER.ordinal())) {
              voxel.join(0.8f, VoxelFace.LEFT);
            }
            if (voxelN == 0 || (currentVoxelType != VoxelType.WATER && voxelN == VoxelType.WATER.ordinal())) {
              voxel.join(0.6f, VoxelFace.BACK);
            }
            if (voxelS == 0 || (currentVoxelType != VoxelType.WATER && voxelS == VoxelType.WATER.ordinal())) {
              voxel.join(0.6f, VoxelFace.FRONT);
            }

            if (voxel.hasVertices()) {
              List<VoxelVertex> vertices = voxel.translate(x, y, z).getVertices();

              if (currentVoxel == 6) {
                transparentVertices.addAll(vertices);
              } else {
                solidVertices.addAll(vertices);
              }
            }
          }
        }
      }
    }

    return new ChunkMesh[]{storeAndReturnChunkMesh(solidVertices), storeAndReturnChunkMesh(transparentVertices)};
  }

  public static void unloadChunkMesh(ChunkMesh mesh) {
    if (mesh.containsVertices()) {
      GLVertexArrayManager.get().freeVao(mesh.getVaoId());
    }
  }

  private static ChunkMesh storeAndReturnChunkMesh(List<VoxelVertex> vertices) {
    if (vertices.isEmpty()) {
      return null;
    }

    int[] vertexData = new int[vertices.size()];
    for (int i = 0; i < vertices.size(); i++) {
      VoxelVertex v = vertices.get(i);

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

    int vaoId = GLVertexArrayManager.get()
        .allocateVao()
        .bufferData(vertexData, GL_STATIC_DRAW)
        .enableAttribArray(0)
        .attribPointer(0, 1, GL_FLOAT, false, 4, 0L)
        .doFinal();

    return new ChunkMesh(vaoId, vertexData.length);
  }
}
