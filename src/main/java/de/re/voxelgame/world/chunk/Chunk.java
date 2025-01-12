package de.re.voxelgame.world.chunk;

import de.ren.ecs.engine.util.Vectors;
import de.re.voxelgame.intersection.AABB;
import de.re.voxelgame.world.WorldPosition;
import de.re.voxelgame.world.voxel.VoxelType;

public class Chunk {
  public static final int CHUNK_SIZE = 32;

  private final WorldPosition position;

  private final AABB boundingBox;

  private ChunkMesh solidMesh;

  private ChunkMesh transparentMesh;

  private byte[][][] voxelIds;

  public Chunk(WorldPosition position, byte[][][] voxelIds) {
    this.position = position;
    this.voxelIds = voxelIds;

    WorldPosition worldPos = getWorldPosition();
    this.boundingBox = new AABB(worldPos.getVector(), Vectors.add(worldPos.getVector(), CHUNK_SIZE - 1));
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

  public boolean hasSolidMesh() {
    return solidMesh != null;
  }

  public ChunkMesh getSolidMesh() {
    return solidMesh;
  }

  public void setSolidMesh(ChunkMesh solidMesh) {
    this.solidMesh = solidMesh;
  }

  public boolean hasTransparentMesh() {
    return transparentMesh != null;
  }

  public ChunkMesh getTransparentMesh() {
    return transparentMesh;
  }

  public void setTransparentMesh(ChunkMesh transparentMesh) {
    this.transparentMesh = transparentMesh;
  }

  public byte[][][] getVoxelIds() {
    return voxelIds;
  }

  public byte getVoxelId(int x, int y, int z) {
    return voxelIds[x][y][z];
  }

  public void placeVoxel(int x, int y, int z, VoxelType type) {
    voxelIds[x][y][z] = (byte) type.ordinal();
  }

  public void removeVoxel(int x, int y, int z) {
    voxelIds[x][y][z] = 0;
  }
}
