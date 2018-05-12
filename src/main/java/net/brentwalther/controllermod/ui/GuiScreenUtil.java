package net.brentwalther.controllermod.ui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

public class GuiScreenUtil {

  private GuiScreenUtil() {}

  public static void pushScreen(GuiScreen screen) {
    Minecraft minecraft = Minecraft.getMinecraft();
    if (screen instanceof ModScreen) {
      ((ModScreen) screen).setParent(minecraft.currentScreen);
    }
    minecraft.displayGuiScreen(screen);
  }

  public static void popScreen() {
    Minecraft minecraft = Minecraft.getMinecraft();
    if (!(minecraft.currentScreen instanceof ModScreen)) {
      return;
    }
    minecraft.displayGuiScreen(((ModScreen) minecraft.currentScreen).getParent());
  }

  public static void refreshCurrentScreen() {
    Minecraft minecraft = Minecraft.getMinecraft();
    minecraft.displayGuiScreen(minecraft.currentScreen);
  }
}
