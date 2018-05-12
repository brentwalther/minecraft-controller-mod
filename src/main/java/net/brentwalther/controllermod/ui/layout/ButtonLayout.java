package net.brentwalther.controllermod.ui.layout;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

public class ButtonLayout extends AbstractLayoutImpl {
  private final String text;
  private final float relativeWeight;
  private final int minHeight;
  private final int minWidth;
  private final Runnable callback;
  private GuiButton button;

  public ButtonLayout(String text, float relativeWeight, Runnable callback) {
    this.text = text;
    this.relativeWeight = relativeWeight;
    // Arbitrary constant adopted from GuiButton
    this.minHeight = 20;
    // Use the length of the text + (4+4) units of width for margins.
    this.minWidth = Minecraft.getMinecraft().fontRenderer.getStringWidth(text) + 8;
    this.callback = callback;
  }

  @Override
  public void initGui() {
    this.button = new GuiButton(id, x, y, width, height, text);
  }

  @Override
  public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    button.drawButton(Minecraft.getMinecraft(), mouseX, mouseY, partialTicks);
  }

  @Override
  public boolean handleClick(int mouseX, int mouseY, int mouseButton) {
    if (button.mousePressed(Minecraft.getMinecraft(), mouseX, mouseY)) {
      callback.run();
      return true;
    }
    return false;
  }

  @Override
  public float getRelativeWeight() {
    return relativeWeight;
  }

  @Override
  public int getMinHeight() {
    return minHeight;
  }

  @Override
  public int getMinWidth() {
    return minWidth;
  }

  @Override
  public int getMaxHeight() {
    return 20;
  }

  @Override
  public int getMaxWidth() {
    return Integer.MAX_VALUE;
  }
}
