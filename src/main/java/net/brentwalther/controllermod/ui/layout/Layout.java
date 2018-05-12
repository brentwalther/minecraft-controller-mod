package net.brentwalther.controllermod.ui.layout;

public interface Layout {
  /**
   * Instructs the layout to draw itself using the specified bounds. Until this is called, the
   * bounds of a newly added layout should be 0,0,0,0.
   *
   * @param x the upper-left corner x value of the bounding box
   * @param y the upper-left corner y value of the bounding box
   * @param width the width of the bounding box
   * @param height the height of the bounding box
   */
  void setBounds(int x, int y, int width, int height);

  /** Called when this layout should prepare all of it's components to be drawn. */
  void initGui();

  /** Called when this layout should draw all its components on screen. */
  void drawScreen(int mouseX, int mouseY, float partialTicks);

  /**
   * Called when the layout can attempt to use a click.
   *
   * @return true if the layout consumed the click.
   */
  boolean handleClick(int mouseX, int mouseY, int mouseButton);

  int getId();

  float getRelativeWeight();

  int getMinHeight();

  int getMinWidth();

  int getMaxHeight();

  int getMaxWidth();
}
