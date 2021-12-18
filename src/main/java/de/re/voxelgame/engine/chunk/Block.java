package de.re.voxelgame.engine.chunk;

import de.re.voxelgame.engine.Vertex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class Block {
  private final List<BlockFace> blockFaces;

  public Block(BlockFace... faces) {
    this();
    blockFaces.addAll(Arrays.asList(faces));
  }

  public Block() {
    blockFaces = new ArrayList<>();
  }

  public Block join(BlockFace face) {
    blockFaces.add(face);
    return this;
  }

  public Block join(float lightLevel, BlockFace face) {
    face.setLightLevel(lightLevel);
    return join(face);
  }

  public Block translate(float x, float y, float z) {
    for (int i = 0; i < blockFaces.size(); i++) {
      BlockFace face = blockFaces.get(i);
      blockFaces.set(i, face.translate(x, y, z));
    }
    return this;
  }

  public boolean hasVertices() {
    return blockFaces.size() > 0;
  }

  public List<Vertex> getVertices() {
    return blockFaces.stream()
        .map(BlockFace::getVertices)
        .flatMap(Collection::stream)
        .collect(Collectors.toList());
  }
}
