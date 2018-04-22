package net.brentwalther.controllermod.ui;

import com.google.common.collect.ImmutableMap;
import net.brentwalther.controllermod.binding.BindingManager;
import net.brentwalther.controllermod.device.DeviceManager;
import net.brentwalther.controllermod.ui.constants.GuiButtonId;
import net.brentwalther.controllermod.ui.layout.Button;
import net.brentwalther.controllermod.ui.layout.Layout;
import net.brentwalther.controllermod.ui.layout.LinearLayout;
import net.brentwalther.controllermod.ui.layout.LinearLayout.Orientation;
import net.minecraft.client.gui.GuiScreen;

import java.util.ArrayList;
import java.util.List;

public class SettingsMenu extends ModScreen {
  private static final Runnable NO_OP = () -> {};

  private final DeviceManager deviceManager;
  private final List<Layout> layouts;
  private final ImmutableMap<Integer, Runnable> actionMap;
  private final BindingManager bindingManager;

  public SettingsMenu(GuiScreen parent, DeviceManager deviceManager, BindingManager bindingManager) {
    super(parent);
    this.deviceManager = deviceManager;
    this.bindingManager = bindingManager;
    this.layouts = new ArrayList<>();
    this.actionMap = buildActions();
  }

  private ImmutableMap<Integer, Runnable> buildActions() {
    ImmutableMap.Builder<Integer, Runnable> actionMapBuilder = ImmutableMap.builder();
    actionMapBuilder.put(GuiButtonId.NAVIGATE_BACK.ordinal(), () -> GuiScreenUtil.popScreen());
    actionMapBuilder.put(
        GuiButtonId.DECREMENT_CONTROLLER_NUMBER.ordinal(),
        () -> {
          deviceManager.previousDevice();
          GuiScreenUtil.refreshCurrentScreen();
        });
    actionMapBuilder.put(
        GuiButtonId.INCREMENT_CONTROLLER_NUMBER.ordinal(),
        () -> {
          deviceManager.nextDevice();
          GuiScreenUtil.refreshCurrentScreen();
        });
    return actionMapBuilder.build();
  }

  @Override
  protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
    for (Layout layout : layouts) {
      for (Layout clickedLayout :  layout.getClickedComponents(mouseX, mouseY, mouseButton)) {
        actionMap.getOrDefault(clickedLayout.getId(), NO_OP).run();
      }
    }
  }

  @Override
  public void initGui() {
    this.layouts.clear();

    LinearLayout controllerSwitcher = new LinearLayout(Orientation.HORIZONTAL);
    controllerSwitcher.setBounds(10, 10, getParent().width - 10, 20);
    controllerSwitcher.addComponents(
        new Button(GuiButtonId.DECREMENT_CONTROLLER_NUMBER.ordinal(), "Prev Controller", 0),
        new Button(GuiButtonId.NAVIGATE_BACK.ordinal(), deviceManager.getCurrentDeviceName(), 1),
        new Button(GuiButtonId.INCREMENT_CONTROLLER_NUMBER.ordinal(), "Next Controller", 0));
    layouts.add(controllerSwitcher);

    for (Layout layout : layouts) {
      layout.initGui();
    }
  }

  @Override
  public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    drawDefaultBackground();
    this.drawCenteredString(mc.fontRenderer, "Testing, 456...", getParent().width / 2, 0, 0xAAAAAA);
    for (Layout layout : layouts) {
      layout.drawScreen(mouseX, mouseY, partialTicks);
    }
    super.drawScreen(mouseX, mouseY, partialTicks);
  }
}
