package de.re.voxelgame.core.ecs;

import de.re.voxelgame.core.GLApplication;
import de.re.voxelgame.core.ecs.entity.Entity;
import de.re.voxelgame.core.ecs.system.ApplicationSystem;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

public class EntityComponentSystem {
  private static EntityComponentSystem instant;

  private final GLApplication application;

  private final Map<Class<? extends Entity>, Set<Entity>> entityGroups;

  private final Map<Class<? extends ApplicationSystem>, ApplicationSystem> systems;

  private EntityComponentSystem(GLApplication application) {
    this.application = application;
    entityGroups = new HashMap<>();
    systems = new HashMap<>();
  }

  public static EntityComponentSystem get(GLApplication application) {
    if (instant == null) {
      instant = new EntityComponentSystem(application);
    }

    return instant;
  }

  public void tick() {
    for (Class<? extends ApplicationSystem> system : systems.keySet()) {
      ApplicationSystem instance = systems.get(system);
      instance.invoke();
    }
  }

  public <T extends Entity> void addEntity(T entity) {
    Class<? extends Entity> type = entity.getClass();
    if (!hasEntity(type)) {
      Set<Entity> entities = new HashSet<>();
      entities.add(entity);
      entityGroups.put(type, entities);
    }
    entityGroups.get(type).add(entity);
  }

  public <T extends Entity> void removeEntity(T entity) {
    Class<? extends Entity> type = entity.getClass();
    if (!hasEntity(type)) {
      throw new IllegalArgumentException(type.getName() + " not found!");
    }

    entityGroups.get(type).remove(entity);
  }

  public <T extends Entity> boolean hasEntity(Class<T> entity) {
    return entityGroups.containsKey(entity);
  }

  public <T extends Entity> Set<T> getEntities(Class<T> entity) {
    if (!hasEntity(entity)) {
      throw new IllegalArgumentException(entity.getName() + " not found!");
    }

    return entityGroups.get(entity).stream()
        .map(entity::cast)
        .collect(Collectors.toSet());
  }

  public <T extends ApplicationSystem> void addSystem(Class<T> system) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
    if (!hasSystem(system)) {
      systems.put(system, system.getConstructor(GLApplication.class).newInstance(application));
    }
  }

  public <T extends ApplicationSystem> void removeSystem(Class<T> system) {
    systems.remove(system);
  }

  public <T extends ApplicationSystem> boolean hasSystem(Class<T> system) {
    return systems.containsKey(system);
  }

  public <T extends ApplicationSystem> T getSystem(Class<T> system) {
    if (!hasSystem(system)) {
      throw new IllegalArgumentException(system.getName() + " not found!");
    }

    return system.cast(systems.get(system));
  }
}
