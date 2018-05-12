package net.brentwalther.controllermod.ui.screen;

import net.minecraft.client.gui.GuiScreen;

public abstract class ModScreen extends GuiScreen {
  private GuiScreen parentScreen;

  public GuiScreen getParent() {
    return parentScreen;
  }

  public void setParent(GuiScreen parentScreen) {
    this.parentScreen = parentScreen;
  }
}
