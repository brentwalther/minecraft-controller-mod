package net.brentwalther.controllermod.ui;

import net.minecraft.client.Minecraft;

public class GuiScreenUtil {

  private GuiScreenUtil() {}

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
