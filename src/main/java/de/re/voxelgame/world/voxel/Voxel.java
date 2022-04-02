package de.re.voxelgame.world.voxel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class Voxel {
  private final List<VoxelFace> voxelFaces;

  private final VoxelType type;

  private final boolean highlighted;

  public Voxel(VoxelType type, boolean highlighted, VoxelFace... faces) {
    this(type, highlighted);
    voxelFaces.addAll(Arrays.asList(faces));
  }

  public Voxel(VoxelType type, boolean highlighted) {
    voxelFaces = new ArrayList<>();
    this.type = type;
    this.highlighted = highlighted;
  }

  public Voxel join(VoxelFace face) {
    voxelFaces.add(face);
    return this;
  }

  public Voxel join(float lightLevel, VoxelFace face) {
    face.setLightLevel(lightLevel);
    return join(face);
  }

  public Voxel translate(float x, float y, float z) {
    Voxel voxel = new Voxel(type, highlighted);
    for (VoxelFace face : voxelFaces) {
      voxel.join(face.translate(x, y, z, textureLayer(face), highlighted));
    }
    return voxel;
  }

  public boolean hasVertices() {
    return voxelFaces.size() > 0;
  }

  public List<VoxelVertex> getVertices() {
    return voxelFaces.stream()
        .map(VoxelFace::getVertices)
        .flatMap(Collection::stream)
        .collect(Collectors.toList());
  }

  private int textureLayer(VoxelFace face) {
    switch (face.getOrientation()) {
      case NORTH:
        return type.getNorthernTextureTile();
      case EAST:
        return type.getEasternTextureTile();
      case SOUTH:
        return type.getSouthernTextureTile();
      case WEST:
        return type.getWesternTextureTile();
      case TOP:
        return type.getTopTextureTile();
      case BOTTOM:
        return type.getBottomTextureTile();
      default:
        throw new IllegalStateException("Unable to compute face orientation");
    }
  }
}
