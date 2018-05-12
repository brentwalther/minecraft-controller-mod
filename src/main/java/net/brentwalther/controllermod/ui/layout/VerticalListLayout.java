package net.brentwalther.controllermod.ui.layout;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraftforge.fml.client.GuiScrollingList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class VerticalListLayout implements Layout {

  private final List<Layout> children;
  private final List<Layout> activeRows;
  private GuiScrollingList list;
  private int x;
  private int y;
  private int width;
  private int height;

  public VerticalListLayout(List<Layout> children) {
    this.children = children;
    this.activeRows = new ArrayList<>();
  }

  @Override
  public void setBounds(int x, int y, int width, int height) {
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
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
  public boolean handleClick(int mouseX, int mouseY, int mouseButton) {
    for (Layout child : children) {
      if (child.handleClick(mouseX, mouseY, mouseButton)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public int getId() {
    return 0;
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
