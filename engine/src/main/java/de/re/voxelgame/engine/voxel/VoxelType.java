package de.re.voxelgame.engine.voxel;

public enum VoxelType {
  AIR(-1),
  COBBLESTONE(0),
  DIRT(1),
  SAND(2),
  GRASS(4, 1, 3),
  MISSING(5),
  WATER(6),
  WOOD(7),
  LEAVES(8),
  CACTUS(10, 11, 9),
  GRAVEL(12);

  private final int northernTextureTile;
  private final int easternTextureTile;
  private final int southernTextureTile;
  private final int westernTextureTile;
  private final int topTextureTile;
  private final int bottomTextureTile;

  VoxelType(int textureTile) {
    this(textureTile, textureTile);
  }

  VoxelType(int topTextureTile, int textureTile) {
    this(topTextureTile, textureTile, textureTile);
  }

  VoxelType(int topTextureTile, int bottomTextureTile, int textureTile) {
    this(textureTile, textureTile, textureTile, textureTile, topTextureTile, bottomTextureTile);
  }

  VoxelType(int northernTextureTile, int easternTextureTile, int southernTextureTile,
            int westernTextureTile, int topTextureTile, int bottomTextureTile) {
    this.northernTextureTile = northernTextureTile;
    this.easternTextureTile = easternTextureTile;
    this.southernTextureTile = southernTextureTile;
    this.westernTextureTile = westernTextureTile;
    this.topTextureTile = topTextureTile;
    this.bottomTextureTile = bottomTextureTile;
  }

  public int getNorthernTextureTile() {
    return northernTextureTile;
  }

  public int getEasternTextureTile() {
    return easternTextureTile;
  }

  public int getSouthernTextureTile() {
    return southernTextureTile;
  }

  public int getWesternTextureTile() {
    return westernTextureTile;
  }

  public int getTopTextureTile() {
    return topTextureTile;
  }

  public int getBottomTextureTile() {
    return bottomTextureTile;
  }
}
