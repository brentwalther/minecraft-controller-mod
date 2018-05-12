package net.brentwalther.controllermod.util;

import net.brentwalther.controllermod.proto.ConfigurationProto.GlobalConfig.Position;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

public class PositionOnScreen {

  public static PositionOnScreen from(Position position) {
    return fromMouseCoords(position.getX(), position.getY());
  }

  public static PositionOnScreen fromMouseCoords(int x, int y) {
    return new PositionOnScreen(x, y);
  }

  public static PositionOnScreen middle() {
    return new PositionOnScreen(
        Minecraft.getMinecraft().displayWidth / 2, Minecraft.getMinecraft().displayHeight / 2);
  }

  private final int x;
  private final int y;
  private final int mcX;
  private final int mcY;

  private PositionOnScreen(int x, int y) {
    ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());

    // Clamp the values to the dimensions of the current screen
    this.x = Math.min(Math.max(x, 0), Minecraft.getMinecraft().displayWidth);
    this.y = Math.min(Math.max(y, 0), Minecraft.getMinecraft().displayHeight);

    mcX = x / scaledResolution.getScaleFactor();
    mcY = scaledResolution.getScaledHeight() - y / scaledResolution.getScaleFactor();
  }

  public int getMouseX() {
    return x;
  }

  public int getMouseY() {
    return y;
  }

  public int getDrawX() {
    return mcX;
  }

  public int getDrawY() {
    return mcY;
  }

  @Override
  public String toString() {
    return String.format("x:%d y:%d mcx:%d mcy:%d", x, y, mcX, mcY);
  }
}
