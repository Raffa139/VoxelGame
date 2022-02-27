package de.re.voxelgame.core;

import java.util.HashSet;
import java.util.Set;

public abstract class Entity {
  private final Set<Component> components = new HashSet<>();

  public <T extends Component> void addComponent(Class<T> component) {
    if (component.isAssignableFrom(TestComponent.class)) {
      components.add(new TestComponent());
    }
  }

  public <T extends Component> void removeComponent(Class<T> component) {
    if (component.isAssignableFrom(TestComponent.class)) {
      for (Component c : components) {
        if (c.getClass().isAssignableFrom(TestComponent.class)) {
          components.remove(c);
          break;
        }
      }
    }
  }

  public void doLife() {
    for (Component c : components) {
      c.execute();
    }
  }
}
