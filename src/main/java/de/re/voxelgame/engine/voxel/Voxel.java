package de.re.voxelgame.engine.voxel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class Voxel {
  private final List<VoxelFace> voxelFaces;

  private final int type;

  public Voxel(int type, VoxelFace... faces) {
    this(type);
    voxelFaces.addAll(Arrays.asList(faces));
  }

  public Voxel(int type) {
    voxelFaces = new ArrayList<>();
    this.type = type;
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
    Voxel voxel = new Voxel(type);
    for (VoxelFace face : voxelFaces) {
      voxel.join(face.translate(x, y, z, type));
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
}
