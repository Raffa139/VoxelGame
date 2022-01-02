package de.re.voxelgame.engine.world;

import de.re.voxelgame.core.util.Vectors;
import de.re.voxelgame.engine.intersection.AABB;
import de.re.voxelgame.engine.voxel.VoxelType;

public class Chunk {
  public static final int CHUNK_SIZE = 32;

  private final WorldPosition position;

  private final AABB boundingBox;

  private ChunkMesh mesh;

  private byte[][][] voxelIds;

  public Chunk(WorldPosition position, byte[][][] voxelIds) {
    this.position = position;
    this.voxelIds = voxelIds;

    WorldPosition worldPos = getWorldPosition();
    this.boundingBox = new AABB(worldPos.getVector(), Vectors.add(worldPos.getVector(), CHUNK_SIZE-1));
  }

  public WorldPosition getRelativePosition() {
    return position;
  }

  public WorldPosition getWorldPosition() {
    return new WorldPosition(Vectors.mul(position.getVector(), CHUNK_SIZE));
  }

  public AABB getBoundingBox() {
    return boundingBox;
  }

  public boolean hasMesh() {
    return mesh != null;
  }

  public ChunkMesh getMesh() {
    return mesh;
  }

  public void setMesh(ChunkMesh mesh) {
    this.mesh = mesh;
  }

  public byte[][][] getVoxelIds() {
    return voxelIds;
  }

  public void placeVoxel(int x, int y, int z, VoxelType type) {
    voxelIds[x][y][z] = (byte) type.ordinal();
  }

  public void removeVoxel(int x, int y, int z) {
    voxelIds[x][y][z] = 0;
  }
}
