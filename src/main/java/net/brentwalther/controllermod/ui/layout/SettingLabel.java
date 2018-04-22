package net.brentwalther.controllermod.ui.layout;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiLabel;

public class SettingLabel extends GuiLabel {

  public SettingLabel(int id, int x, int y, int width, int height, int argbColor, String text) {
    super(Minecraft.getMinecraft().fontRenderer, id, x, y, width, height, argbColor);
    addLine(text);
  }
}
