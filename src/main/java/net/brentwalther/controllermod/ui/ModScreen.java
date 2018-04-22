package net.brentwalther.controllermod.ui;

import net.minecraft.client.gui.GuiScreen;

public abstract class ModScreen extends GuiScreen {
  private final GuiScreen parentScreen;

  public ModScreen(GuiScreen parentScreen) {
    this.parentScreen = parentScreen;
  }

  public GuiScreen getParent() {
    return parentScreen;
  }
}
