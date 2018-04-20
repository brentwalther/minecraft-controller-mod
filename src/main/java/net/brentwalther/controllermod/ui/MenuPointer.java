package net.brentwalther.controllermod.ui;

import net.brentwalther.controllermod.ControllerMod;
import net.brentwalther.controllermod.input.VirtualMouse;
import net.brentwalther.controllermod.util.ThrottleRunnable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class MenuPointer {
  public int width;
  public int height;
  public int textureWidth;
  public int textureHeight;
  public int reticleColor = 0xFFFFFFFF;
  private String imageLocation = null;

  private ResourceLocation resource = null;
  private Minecraft mc;
  private boolean lastGlDepthTestValue;
  private boolean lastGlBlendValue;

  private final Runnable logRunnable;
  private int x;
  private int y;
  private boolean isHidden;

  public MenuPointer() {
    ControllerMod.getLogger().info("Intializing MenuPointer");
    this.logRunnable =
        new ThrottleRunnable(
            () -> {
              ControllerMod.getLogger()
                  .info("Drawing at " + MenuPointer.this.x + " " + MenuPointer.this.y);
            },
            500);
    this.isHidden = true;
  }

  public void unhide() {
    this.isHidden = false;
    this.x = VirtualMouse.INSTANCE.getMousePosition().getMouseX();
    this.y = VirtualMouse.INSTANCE.getMousePosition().getMouseY();
  }

  public void hide() {
    this.isHidden = true;
    this.x = -100;
    this.y = -100;
  }

  public void setLocation(
      String path, int inWidth, int inHeight, int inImageWidth, int inImageHeight) {
    resource = new ResourceLocation(path);
    width = inWidth;
    height = inHeight;
    textureWidth = inImageWidth;
    textureHeight = inImageHeight;
    mc = Minecraft.getMinecraft();
  }

  public void draw() {
    if (isHidden) {
      return;
    }
    this.x = VirtualMouse.INSTANCE.getMousePosition().getDrawX();
    this.y = VirtualMouse.INSTANCE.getMousePosition().getDrawY();

    setGlOptions();
    logRunnable.run();
    if (resource != null) {
      try {
        mc.renderEngine.bindTexture(resource);
        Gui.drawModalRectWithCustomSizedTexture(
            x - width / 2, y - height / 2, 0, 0, width, height, textureWidth, textureHeight);
      } catch (Exception ex) {
        ControllerMod.getLogger()
            .info(
                "Caught exception when rendering reticle. Defaulting to basic." + ex.getMessage());
        resource = null;
      }
    } else {
      Gui.drawRect(x - 4, y - 1, x + 4, y + 1, reticleColor);
      Gui.drawRect(x - 1, y - 4, x + 1, y + 4, reticleColor);
    }
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
}
