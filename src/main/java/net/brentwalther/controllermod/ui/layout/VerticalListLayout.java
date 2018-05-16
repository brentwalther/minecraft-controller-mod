package net.brentwalther.controllermod.ui.layout;

import net.brentwalther.controllermod.input.VirtualInputAction.PressState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraftforge.fml.client.GuiScrollingList;

import java.io.IOException;
import java.util.List;

public class VerticalListLayout extends AbstractLayoutImpl {

  private final List<Layout> children;
  private GuiScrollingList list;

  public VerticalListLayout(List<Layout> children) {
    this.children = children;
  }

  @Override
  public void initGui() {
    this.list =
        new GuiScrollingList(
            Minecraft.getMinecraft(),
            width,
            height,
            y,
            y + height,
            x,
            20,
            Minecraft.getMinecraft().displayWidth,
            Minecraft.getMinecraft().displayHeight) {
          @Override
          protected int getSize() {
            return children.size();
          }

          @Override
          protected void elementClicked(int index, boolean doubleClick) {}

          @Override
          protected boolean isSelected(int index) {
            return false;
          }

          @Override
          protected void drawBackground() {}

          @Override
          protected void drawSlot(
              int slotIdx, int entryRight, int slotTop, int slotBuffer, Tessellator tess) {
            drawChild(slotIdx, left, slotTop, entryRight - left, slotBuffer, mouseX, mouseY);
          }

          @Override
          public void drawScreen(int mouseX, int mouseY, float partialTicks) {
            resetChildrenBounds();

            try {
              handleMouseInput(mouseX, mouseY);
            } catch (IOException e) {
              /* Do nothing, but why can it throw? */
            }

            super.drawScreen(mouseX, mouseY, partialTicks);
          }
        };
  }

  @Override
  public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    this.list.drawScreen(mouseX, mouseY, partialTicks);
  }

  @Override
  public boolean handleClick(int mouseX, int mouseY, int mouseButton, PressState state) {
    for (Layout child : children) {
      if (child.handleClick(mouseX, mouseY, mouseButton, state)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public float getRelativeWeight() {
    return 1f;
  }

  @Override
  public int getMinHeight() {
    return height;
  }

  @Override
  public int getMinWidth() {
    return width;
  }

  @Override
  public int getMaxHeight() {
    return height;
  }

  @Override
  public int getMaxWidth() {
    return width;
  }

  private void drawChild(
      int index, int left, int top, int width, int height, int mouseX, int mouseY) {
    Layout row = children.get(index);
    row.setBounds(left, top, width, height);
    row.initGui();
    row.drawScreen(mouseX, mouseY, 0);
  }

  private void resetChildrenBounds() {
    for (Layout child : children) {
      child.setBounds(-1, -1, 0, 0);
      child.initGui();
    }
  }
}
