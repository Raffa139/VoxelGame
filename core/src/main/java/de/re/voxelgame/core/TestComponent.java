package de.re.voxelgame.core;

public class TestComponent implements Component {
  @Override
  public void execute() {
    System.out.println("Hello Component Test!");
  }
}
