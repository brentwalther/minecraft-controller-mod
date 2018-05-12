package net.brentwalther.controllermod.ui;

import net.brentwalther.controllermod.ControllerMod;
import net.brentwalther.controllermod.input.VirtualMouse;
import net.brentwalther.controllermod.util.PositionOnScreen;
import net.minecraft.client.gui.Gui;
import org.lwjgl.opengl.GL11;

public class MenuPointer {
  private boolean lastGlDepthTestValue;
  private boolean lastGlBlendValue;

  private int x;
  private int y;
  private boolean isHidden;

  public MenuPointer() {
    ControllerMod.getLogger().info("Intializing MenuPointer");
    this.isHidden = true;
  }

  public void unhide(PositionOnScreen pos) {
    this.isHidden = false;
    this.x = pos.getMouseX();
    this.y = pos.getMouseY();
  }

  public void hide() {
    this.isHidden = true;
    this.x = -100;
    this.y = -100;
  }

  public void draw() {
    if (isHidden) {
      return;
    }
    this.x = VirtualMouse.INSTANCE.getMousePosition().getDrawX();
    this.y = VirtualMouse.INSTANCE.getMousePosition().getDrawY();

    setGlOptions();
    Gui.drawRect(x - 4, y - 1, x + 4, y + 1, 0x99ffffff);
    Gui.drawRect(x - 1, y - 4, x + 1, y + 4, 0x99ffffff);
    restoreGlOptions();
  }

  private void setGlOptions() {
    // Set color to 100% transparent white
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

    lastGlDepthTestValue = GL11.glIsEnabled(GL11.GL_DEPTH_TEST);
    GL11.glDisable(GL11.GL_DEPTH_TEST);

    lastGlBlendValue = GL11.glIsEnabled(GL11.GL_BLEND);
    GL11.glEnable(GL11.GL_BLEND);
  }

  private void restoreGlOptions() {
    if (lastGlDepthTestValue) {
      GL11.glEnable(GL11.GL_DEPTH_TEST);
    } else {
      GL11.glDisable(GL11.GL_DEPTH_TEST);
    }
    if (lastGlBlendValue) {
      GL11.glEnable(GL11.GL_BLEND);
    } else {
      GL11.glDisable(GL11.GL_BLEND);
    }
  }

  public PositionOnScreen getPosition() {
    return VirtualMouse.INSTANCE.getMousePosition();
  }
}
