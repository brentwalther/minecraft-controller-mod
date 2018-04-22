package net.brentwalther.controllermod.ui.layout;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

import java.util.List;

public class Button extends AbstractLayoutImpl {
  private final String text;
  private final float relativeWeight;
  private final int minHeight;
  private final int minWidth;
  private GuiButton button;

  public Button(int id, String text, float relativeWeight) {
    super(id);
    this.text = text;
    this.relativeWeight = relativeWeight;
    // Arbitrary constant adopted from GuiButton
    this.minHeight = 20;
    // Use the length of the text + (4+4) units of width for margins.
    this.minWidth = Minecraft.getMinecraft().fontRenderer.getStringWidth(text) + 8;
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
  public List<Layout> getClickedComponents(int mouseX, int mouseY, int mouseButton) {
    if (button.mousePressed(Minecraft.getMinecraft(), mouseX, mouseY)) {
      return ImmutableList.of(this);
    }
    return ImmutableList.of();
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
}
