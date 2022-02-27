package de.re.voxelgame.core;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public abstract class Entity {
  private final Map<Class<? extends Component>, Component> components = new HashMap<>();

  public <T extends Component> void addComponent(Class<T> component) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
    if (!hasComponent(component)) {
      components.put(component, component.getDeclaredConstructor(Entity.class).newInstance(this));
    }
  }

  public <T extends Component> void removeComponent(Class<T> component) {
    components.remove(component);
  }

  public <T extends Component> boolean hasComponent(Class<T> component) {
    return components.containsKey(component);
  }

  public <T extends Component> T getComponent(Class<T> component) {
    if (!hasComponent(component)) {
      throw new IllegalArgumentException(component.getName() + " not found!");
    }

    return component.cast(components.get(component));
  }
}
