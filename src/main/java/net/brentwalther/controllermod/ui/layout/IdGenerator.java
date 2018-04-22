package net.brentwalther.controllermod.ui.layout;

import net.brentwalther.controllermod.ui.constants.GuiButtonId;

public class IdGenerator {
  // Start the IDs at the end of the defined IDs so nothing collides.
  private static int nextId = GuiButtonId.values().length;

  /**
   * @return a new arbitrary ID that can be used for a layout.
   */
  public static int generateId() {
    return nextId++;
  }
}
