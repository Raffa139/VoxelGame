package de.re.voxelgame.core.ecs.entity;

import java.util.Random;

public class StaticEntity extends Entity {
  private static final Random RAN = new Random();

  private final int randomInt;

  public StaticEntity() {
    randomInt = RAN.nextInt();
  }

  public int getRandomInt() {
    return randomInt;
  }
}
