package net.brentwalther.controllermod.ui.layout;

import java.util.List;

public abstract class AbstractLayoutImpl implements Layout {
  protected final int id;
  protected int x;
  protected int y;
  protected int width;
  protected int height;

  public AbstractLayoutImpl(int id) {
    this.id = id;
  }

  @Override
  public void setBounds(int x, int y, int width, int height) {
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
  }

  @Override
  public abstract void initGui();

  @Override
  public abstract void drawScreen(int mouseX, int mouseY, float partialTicks);

  @Override
  public abstract List<Layout> getClickedComponents(int mouseX, int mouseY, int mouseButton);

  @Override
  public int getId() {
    return id;
  }

  @Override
  public abstract float getRelativeWeight();

  @Override
  public abstract int getMinHeight();

  @Override
  public abstract int getMinWidth();
}
