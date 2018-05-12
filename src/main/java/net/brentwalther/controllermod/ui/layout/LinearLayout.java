package net.brentwalther.controllermod.ui.layout;

import com.google.common.base.Preconditions;
import net.minecraft.client.gui.Gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class LinearLayout extends Gui implements Layout {

  private int boundingBoxWidth;
  private int boundingBoxHeight;
  private int y;
  private int x;
  private List<Layout> children;

  public enum Orientation {
    HORIZONTAL,
    VERTICAL,
  }

  private final Orientation orientation;

  public LinearLayout(Orientation orientation) {
    this.orientation = orientation;
    this.children = new ArrayList<>();
  }

  public void addChildren(Layout component, Layout... components) {
    this.children.add(component);
    this.children.addAll(Arrays.asList(components));
  }

  public void initGui() {
    int componentHeights[] = new int[children.size()];
    int componentWidths[] = new int[children.size()];

    switch (orientation) {
      case VERTICAL:
        Arrays.fill(componentWidths, boundingBoxWidth);
        break;
      case HORIZONTAL:
        Arrays.fill(componentHeights, boundingBoxHeight);
        break;
    }

    double totalWeight =
        children.stream().collect(Collectors.summingDouble(Layout::getRelativeWeight));
    int totalAvailableSpace =
        orientation == Orientation.HORIZONTAL ? boundingBoxWidth : boundingBoxHeight;

    // First, assign dimensions to all the children that don't care about how much relative
    // weight they hold in the layout. Since they don't care, we just user their minHeight or
    // minWidth attribute.
    for (int i = 0; i < children.size(); i++) {
      Layout component = children.get(i);
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

    // Now, we split the remaining space between the weighted children. If the relative weight
    // they specified would give them less than their minHeight or minWidth, we round up to their
    // minimum specified dimension.
    for (int i = 0; i < children.size(); i++) {
      Layout component = children.get(i);
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

    setBoundsOfChildren(componentWidths, componentHeights);

    for (Layout child : children) {
      child.initGui();
    }
  }

  @Override
  public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    for (Layout layout : children) {
      layout.drawScreen(mouseX, mouseY, partialTicks);
    }
  }

  @Override
  public boolean handleClick(int mouseX, int mouseY, int mouseButton) {
    for (Layout layout : children) {
      if (layout.handleClick(mouseX, mouseY, mouseButton)) {
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
    return 0;
  }

  @Override
  public int getMinHeight() {
    if (orientation == Orientation.HORIZONTAL) {
      Optional<Layout> tallestLayout =
          this.children.stream().max(Comparator.comparingInt(Layout::getMinHeight));
      if (tallestLayout.isPresent()) {
        return tallestLayout.get().getMinHeight();
      }
      return 0;
    } else {
      return this.children.stream().collect(Collectors.summingInt(Layout::getMinHeight));
    }
  }

  @Override
  public int getMinWidth() {
    if (orientation == Orientation.VERTICAL) {
      Optional<Layout> widestLayout =
          this.children.stream().max(Comparator.comparingInt(Layout::getMinWidth));
      if (widestLayout.isPresent()) {
        return widestLayout.get().getMinWidth();
      }
      return 0;
    } else {
      return this.children.stream().collect(Collectors.summingInt(Layout::getMinWidth));
    }
  }

  @Override
  public int getMaxHeight() {
    return this.children.stream().collect(Collectors.summingInt(Layout::getMaxHeight));
  }

  @Override
  public int getMaxWidth() {
    return this.children.stream().collect(Collectors.summingInt(Layout::getMaxWidth));
  }

  @Override
  public void setBounds(int x, int y, int width, int height) {
    this.x = x;
    this.y = y;
    this.boundingBoxWidth = width;
    this.boundingBoxHeight = height;
  }

  private void setBoundsOfChildren(int widths[], int heights[]) {
    Preconditions.checkState(widths.length == heights.length && children.size() == widths.length);
    int currX = this.x;
    int currY = this.y;

    for (int i = 0; i < children.size(); i++) {
      Layout component = children.get(i);
      int width = widths[i];
      int height = heights[i];
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
}
