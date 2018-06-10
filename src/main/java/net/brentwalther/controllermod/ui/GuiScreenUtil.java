package net.brentwalther.controllermod.ui;

import net.brentwalther.controllermod.ui.screen.ModScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

import java.util.Optional;

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

  /** Returns the currently displayed ModScreen, if any. */
  public static Optional<ModScreen> getCurrentScreen() {
    Minecraft minecraft = Minecraft.getMinecraft();
    if (minecraft.currentScreen instanceof ModScreen) {
      return Optional.of((ModScreen) minecraft.currentScreen);
    }
    return Optional.empty();
  }

  /** Returns an expirable overlay that shows a toast message. */
  public static GuiOverlayWithExpiration makeToastOverlay(String message) {
    return new GuiOverlayWithExpiration() {
      // For now, just hardcode a toast message length of 5 seconds (5000 ms);
      private final long expirationTime = Minecraft.getSystemTime() + 5000;

      @Override
      public boolean isExpired() {
        return Minecraft.getSystemTime() > expirationTime;
      }

      @Override
      public void drawOverlay() {
        getCurrentScreen()
            .ifPresent(
                (screen) -> {
                  int stringWidth = Minecraft.getMinecraft().fontRenderer.getStringWidth(message);
                  int approxNumLines = stringWidth / screen.width + 1;
                  // Either center the string if it is one line or just align it to the left if it
                  // is
                  // multi-line.
                  int x = (approxNumLines > 1 ? 0 : (screen.width - stringWidth) / 2);
                  int y = screen.height - 30 - (20 * approxNumLines);
                  screen.drawHoveringText(message, x, y);
                });
      }
    };
  }
}
