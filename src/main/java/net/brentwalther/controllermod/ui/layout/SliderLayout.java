package net.brentwalther.controllermod.ui.layout;

import net.brentwalther.controllermod.input.VirtualInputAction.PressState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiPageButtonList.GuiResponder;
import net.minecraft.client.gui.GuiSlider;

public class SliderLayout extends AbstractLayoutImpl {

  /** The callback that will be executed when the slider is set to a new value. */
  public interface SliderChangedCallback {
    void onChange(float newValue);
  }

  private final String name;
  private final float min;
  private final float max;
  private final float defaultValue;
  private final SliderChangedCallback callback;

  private DrawableGuiSlider gui;

  public SliderLayout(
      String name, float min, float max, float defaultValue, SliderChangedCallback callback) {
    this.name = name;
    this.min = min;
    this.max = max;
    this.defaultValue = defaultValue;
    this.callback = callback;
  }

  @Override
  public void initGui() {
    // Since the GuiSlider has hardcoded dimensions of 150x20, we need to center it in this layouts
    // bounding box. So, add half of the spare distance between the full width and 150 to the
    // bounding box's x and similar for the y to center the control.
    int guiX = x + (this.width - 150) / 2;
    int guiY = y + (this.height - 20) / 2;
    this.gui = new DrawableGuiSlider(name, guiX, guiY, min, max, defaultValue, callback);
  }

  @Override
  public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    this.gui.drawScreen(mouseX, mouseY, partialTicks);
  }

  @Override
  public boolean handleClick(int mouseX, int mouseY, int mouseButton, PressState state) {
    switch (state) {
      case IS_BECOMING_PRESSED:
        return this.gui.mousePressed(Minecraft.getMinecraft(), mouseX, mouseY);
      case IS_BECOMING_UNPRESSED:
        this.gui.mouseReleased(mouseX, mouseY);
        break;
    }
    return false;
  }

  @Override
  public float getRelativeWeight() {
    return 0;
  }

  @Override
  public int getMinHeight() {
    return 30;
  }

  @Override
  public int getMinWidth() {
    return 150;
  }

  @Override
  public int getMaxHeight() {
    return 30;
  }

  @Override
  public int getMaxWidth() {
    return 150;
  }

  private static class DrawableGuiSlider extends GuiSlider {
    public DrawableGuiSlider(
        String text,
        int x,
        int y,
        float min,
        float max,
        float defaultValue,
        SliderChangedCallback callback) {
      super(
          new GuiResponder() {
            @Override
            public void setEntryValue(int id, boolean value) {}

            @Override
            public void setEntryValue(int id, float value) {
              callback.onChange(value);
            }

            @Override
            public void setEntryValue(int id, String value) {}
          },
          IdGenerator.generateId(),
          x,
          y,
          text,
          min,
          max,
          defaultValue,
          DrawableGuiSlider::generateName);
    }

    public static String generateName(int id, String name, float value) {
      boolean isNearlyAnInt = Math.abs(((int) value) - value) <= .01f;

      if (isNearlyAnInt) {
        return String.format("%s: %.0f", name, value);
        } else {
        return String.format("%s: %.2f", name, value);
      }
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.mouseDragged(Minecraft.getMinecraft(), mouseX, mouseY);
      this.drawButton(Minecraft.getMinecraft(), mouseX, mouseY, partialTicks);
    }
  }
}
