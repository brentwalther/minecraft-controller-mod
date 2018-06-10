package net.brentwalther.controllermod.ui.screen;

import net.brentwalther.controllermod.ui.GuiOverlayWithExpiration;
import net.minecraft.client.gui.GuiScreen;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class ModScreen extends GuiScreen {
  private GuiScreen parentScreen;
  private List<GuiOverlayWithExpiration> overlays = new ArrayList<>();

  public GuiScreen getParent() {
    return parentScreen;
  }

  public void setParent(GuiScreen parentScreen) {
    this.parentScreen = parentScreen;
  }

  @Override
  public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    super.drawScreen(mouseX, mouseY, partialTicks);
    drawDefaultBackground();
  }

  public void addGuiOverlayWithExpiration(GuiOverlayWithExpiration renderOverlay) {
    this.overlays.add(renderOverlay);
  }

  public void drawOverlays() {
    Iterator<GuiOverlayWithExpiration> potentialOverlaysToRender = overlays.iterator();
    while (potentialOverlaysToRender.hasNext()) {
      GuiOverlayWithExpiration overlay = potentialOverlaysToRender.next();
      if (overlay.isExpired()) {
        potentialOverlaysToRender.remove();
      } else {
        overlay.drawOverlay();
      }
    }
  }
}
