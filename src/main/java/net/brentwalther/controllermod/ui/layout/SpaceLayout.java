package net.brentwalther.controllermod.ui.layout;

import net.brentwalther.controllermod.input.VirtualInputAction.PressState;

public class SpaceLayout implements Layout {

  private final int height;
  private final int width;

  public SpaceLayout(int width, int height) {
    this.width = width;
    this.height = height;
  }

  @Override
  public void setBounds(int x, int y, int width, int height) {}

  @Override
  public void initGui() {}

  @Override
  public void drawScreen(int mouseX, int mouseY, float partialTicks) {}

  @Override
  public boolean handleClick(int mouseX, int mouseY, int mouseButton, PressState state) {
    return false;
  }

  @Override
  public int getId() {
    return 0;
  }

  @Override
  public float getRelativeWeight() {
    return 0;
  }

  @Override
  public int getMinHeight() {
    return height;
  }

  @Override
  public int getMinWidth() {
    return width;
  }

  @Override
  public int getMaxHeight() {
    return height;
  }

  @Override
  public int getMaxWidth() {
    return width;
  }
}
