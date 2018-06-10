package net.brentwalther.controllermod.ui;

import net.brentwalther.controllermod.ui.screen.ModScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

public class GuiScreenUtil {

  private GuiScreenUtil() {}

  /**
   * Push an arbitrary {@link GuiScreen}. If the screen is a {@link ModScreen}, we will keep track
   * of the current screen so that is can be replaced later with a call to{@link #popScreen()}.
   */
  public static void pushScreen(GuiScreen screen) {
    Minecraft minecraft = Minecraft.getMinecraft();
    if (screen instanceof ModScreen) {
      ((ModScreen) screen).setParent(minecraft.currentScreen);
    }
    minecraft.displayGuiScreen(screen);
  }

  /**
   * Pops the current {@link ModScreen} (if one is present) and displays the screen that was being
   * shown prior.
   */
  public static void popScreen() {
    Minecraft minecraft = Minecraft.getMinecraft();
    if (!(minecraft.currentScreen instanceof ModScreen)) {
      return;
    }
    minecraft.displayGuiScreen(((ModScreen) minecraft.currentScreen).getParent());
  }

  /**
   * Refreshes the current screen. This will call {@link GuiScreen#initGui()} on the screen again.
   */
  public static void refreshCurrentScreen() {
    Minecraft minecraft = Minecraft.getMinecraft();
    minecraft.displayGuiScreen(minecraft.currentScreen);
  }
}
