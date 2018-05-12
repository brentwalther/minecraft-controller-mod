package net.brentwalther.controllermod.ui.layout;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiLabel;

public class LabelLayout extends AbstractLayoutImpl {

  private final String text;
  private final int argbColor;
  private final float relativeWeight;
  private final boolean isCentered;
  private final int minHeight;
  private final int minWidth;
  private GuiLabel label;

  public LabelLayout(String text, int argbColor, float relativeWeight, boolean isCentered) {
    this.text = text;
    this.argbColor = argbColor;
    this.relativeWeight = relativeWeight;
    this.isCentered = isCentered;
    // Arbitrary constant adopted from GuiButton
    this.minHeight = 20;
    // Use the length of the text + (4+4) units of width for margins.
    this.minWidth = Minecraft.getMinecraft().fontRenderer.getStringWidth(text) + 8;
  }

  @Override
  public void initGui() {
    this.label =
        new GuiLabel(Minecraft.getMinecraft().fontRenderer, id, x, y, width, height, argbColor);
    this.label.addLine(text);
    if (isCentered) {
      this.label.setCentered();
    }
  }

  @Override
  public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    this.label.drawLabel(Minecraft.getMinecraft(), mouseX, mouseY);
  }

  @Override
  public boolean handleClick(int mouseX, int mouseY, int mouseButton) {
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
    return minHeight;
  }

  @Override
  public int getMaxWidth() {
    return minWidth;
  }
}
