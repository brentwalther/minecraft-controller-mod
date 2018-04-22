package net.brentwalther.controllermod.ui.layout;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiLabel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class LinearLayout extends Gui implements Layout {

  private int boundingBoxWidth;
  private int boundingBoxHeight;
  private int y;
  private int x;
  private List<Layout> components;
  private List<GuiLabel> labelList;
  private List<GuiButton> buttonList;

  public enum Orientation {
    HORIZONTAL,
    VERTICAL,
  }

  private final Orientation orientation;

  public LinearLayout(Orientation orientation) {
    this.orientation = orientation;
    this.components = new ArrayList<>();
    this.labelList = new ArrayList<>();
    this.buttonList = new ArrayList<>();
  }

  public void addComponents(Layout component, Layout... components) {
    this.components.add(component);
    this.components.addAll(Arrays.asList(components));
  }

  public void initGui() {
    if (layoutIsOverconstrained()) {
      return;
    }

    int componentHeights[] = new int[components.size()];
    int componentWidths[] = new int[components.size()];

    switch (orientation) {
      case VERTICAL:
        Arrays.fill(componentWidths, boundingBoxWidth);
        break;
      case HORIZONTAL:
        Arrays.fill(componentHeights, boundingBoxHeight);
        break;
    }

    double totalWeight =
        components.stream().collect(Collectors.summingDouble(Layout::getRelativeWeight));
    int totalAvailableSpace =
        orientation == Orientation.HORIZONTAL ? boundingBoxWidth : boundingBoxHeight;

    // First, assign dimensions to all the components that don't care about how much relative
    // weight they hold in the layout. Since they don't care, we just user their minHeight or
    // minWidth attribute.
    for (int i = 0; i < components.size(); i++) {
      Layout component = components.get(i);
      if (component.getRelativeWeight() != 0f) {
        continue;
      }
      switch (orientation) {
        case VERTICAL:
          componentHeights[i] = component.getMinHeight();
          totalAvailableSpace -= component.getMinHeight();
          break;
        case HORIZONTAL:
          componentWidths[i] = component.getMinWidth();
          totalAvailableSpace -= component.getMinWidth();
          break;
      }
    }

    // Now, we split the remaining space between the weighted components. If the relative weight
    // they specified would give them less than their minHeight or minWidth, we round up to their
    // minimum specifeid dimension.
    for (int i = 0; i < components.size(); i++) {
      Layout component = components.get(i);
      if (component.getRelativeWeight() == 0f) {
        continue;
      }
      float weightRatio = (float) (component.getRelativeWeight() / totalWeight);
      switch (orientation) {
        case VERTICAL:
          int height =
              Math.max(component.getMinHeight(), Math.round(totalAvailableSpace * weightRatio));
          componentHeights[i] = height;
          totalAvailableSpace -= height;
          break;
        case HORIZONTAL:
          int width =
              Math.max(component.getMinWidth(), Math.round(totalAvailableSpace * weightRatio));
          componentWidths[i] = width;
          totalAvailableSpace -= width;
          break;
      }
      totalWeight -= component.getRelativeWeight();
    }

    this.buttonList.clear();
    this.labelList.clear();
    setBoundsOfChildren(componentWidths, componentHeights);

    for (Layout layout : components) {
      layout.initGui();
    }
  }

  @Override
  public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    for (Layout layout : components) {
      layout.drawScreen(mouseX, mouseY, partialTicks);
    }
//    for (GuiButton button : buttonList) {
//      button.drawButton(Minecraft.getMinecraft(), mouseX, mouseY, partialTicks);
//    }
//    for (GuiLabel label : labelList) {
//      label.drawLabel(Minecraft.getMinecraft(), mouseX, mouseY);
//    }
  }

  @Override
  public List<Layout> getClickedComponents(int mouseX, int mouseY, int mouseButton) {
    ImmutableList.Builder<Layout> clickedComponents = ImmutableList.builder();
    for (Layout layout : components) {
      clickedComponents.addAll(layout.getClickedComponents(mouseX, mouseY, mouseButton));
    }
    return clickedComponents.build();
  }

  @Override
  public int getId() {
    return 0;
  }

  @Override
  public float getRelativeWeight() {
    return 0;
  }

  @Override
  public int getMinHeight() {
    return this.components.stream().collect(Collectors.summingInt(Layout::getMinHeight));
  }

  @Override
  public int getMinWidth() {
    return this.components.stream().collect(Collectors.summingInt(Layout::getMinWidth));
  }

  @Override
  public void setBounds(int x, int y, int width, int height) {
    this.x = x;
    this.y = y;
    this.boundingBoxWidth = width;
    this.boundingBoxHeight = height;
  }

  private void setBoundsOfChildren(int widths[], int heights[]) {
    Preconditions.checkState(widths.length == heights.length && components.size() == widths.length);
    int currX = this.x;
    int currY = this.y;

    for (int i = 0; i < components.size(); i++) {
      Layout component = components.get(i);
      int width = widths[i];
      int height = heights[i];
      //      switch (component.getType()) {
      //        case LABEL:
      //          GuiLabel label =
      //              new GuiLabel(
      //                  Minecraft.getMinecraft().fontRenderer,
      //                  component.getId(),
      //                  x,
      //                  y,
      //                  width,
      //                  height,
      //                  0xffffff);
      //          label.addLine(compext());
      //          this.buttonList.add(button);
      //          button.isMouseOver();
      //          break;
      //      }ak;
      ////        case BUTTON:
      ////          GuiButton button =
      ////              new GuiButton(component.getId(), currX, currY, width, height,
      // component.getText());
      ////          this.buttonList.add(button);
      ////          button.isMouseOver();
      ////          break;
      ////      }
      component.setBounds(currX, currY, width, height);
      switch (orientation) {
        case HORIZONTAL:
          currX += width;
          break;
        case VERTICAL:
          currY += height;
          break;
      }
    }
  }

  /**
   * @return true if the layout cannot draw itself given it's current components and bounding box.
   */
  private boolean layoutIsOverconstrained() {
    switch (orientation) {
      case VERTICAL:
        for (Layout component : components) {
          if (component.getMinWidth() > boundingBoxWidth) {
            return true;
          }
        }
        if (components.stream().collect(Collectors.summingInt(Layout::getMinHeight))
            > boundingBoxHeight) {
          return true;
        }
        break;
      case HORIZONTAL:
        for (Layout component : components) {
          if (component.getMinHeight() > boundingBoxHeight) {
            return true;
          }
        }
        if (components.stream().collect(Collectors.summingInt(Layout::getMinWidth))
            > boundingBoxWidth) {
          return true;
        }
        break;
    }
    return false;
  }
}
