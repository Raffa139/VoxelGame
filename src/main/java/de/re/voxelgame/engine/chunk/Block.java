package de.re.voxelgame.engine.chunk;

import de.re.voxelgame.engine.Vertex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class Block {
  private final List<BlockFace> blockFaces;

  private float lightLevel;

  public Block(BlockFace... faces) {
    this(-1, faces);
  }

  public Block(float lightLevel, BlockFace... faces) {
    this(lightLevel);
    blockFaces.addAll(Arrays.asList(faces));
  }

  public Block() {
    this(-1);
  }

  public Block(float lightLevel) {
    blockFaces = new ArrayList<>();
    this.lightLevel = lightLevel;
  }

  public Block join(BlockFace face) {
    blockFaces.add(face);
    return this;
  }

  public Block translate(float x, float y, float z) {
    Block block = new Block();
    for (BlockFace face : blockFaces) {
      block.join(face.translate(x, y, z, lightLevel));
    }
    return block;
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
