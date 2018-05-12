package net.brentwalther.controllermod.ui.layout;

public class IdGenerator {
  private static int nextId = 1;

  /** @return a new arbitrary ID that can be used for a layout. */
  public static int generateId() {
    return nextId++;
  }
}
