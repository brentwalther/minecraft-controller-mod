package net.brentwalther.controllermod.ui.screen;

import com.google.common.collect.ImmutableList;
import net.brentwalther.controllermod.binding.BindingManager;
import net.brentwalther.controllermod.device.DeviceManager;
import net.brentwalther.controllermod.proto.ConfigurationProto.GlobalConfig.ControlBinding;
import net.brentwalther.controllermod.proto.ConfigurationProto.GlobalConfig.ControlBinding.ControlCase;
import net.brentwalther.controllermod.proto.ConfigurationProto.ScreenContext;
import net.brentwalther.controllermod.ui.GuiScreenUtil;
import net.brentwalther.controllermod.ui.layout.ButtonLayout;
import net.brentwalther.controllermod.ui.layout.LabelLayout;
import net.brentwalther.controllermod.ui.layout.Layout;
import net.brentwalther.controllermod.ui.layout.LinearLayout;
import net.brentwalther.controllermod.ui.layout.LinearLayout.Orientation;
import net.brentwalther.controllermod.ui.layout.SpaceLayout;
import net.brentwalther.controllermod.ui.layout.VerticalListLayout;

import java.io.IOException;
import java.util.List;

public class ControllerSettingsScreen extends ModScreen {
  private static final Runnable NO_OP = () -> {};
  private static final Layout DEFAULT_PADDING = new SpaceLayout(10, 10);

  private final DeviceManager deviceManager;
  private final BindingManager bindingManager;

  private LinearLayout mainLayout;

  public ControllerSettingsScreen(DeviceManager deviceManager, BindingManager bindingManager) {
    this.deviceManager = deviceManager;
    this.bindingManager = bindingManager;
  }

  @Override
  protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
    mainLayout.handleClick(mouseX, mouseY, mouseButton);
    try {
      super.mouseClicked(mouseX, mouseY, mouseButton);
    } catch (IOException e) {
      // Do nothing, but why can it throw?
    }
  }

  @Override
  public void initGui() {
    // Make a vertical layout that is the size of the entire parent (minus some left/right margin).
    mainLayout = new LinearLayout(Orientation.VERTICAL);
    mainLayout.setBounds(10, 0, getParent().width - 20, getParent().height);

    mainLayout.addChildren(new LabelLayout("Controller Settings", 0xffffffff, 0, true));

    LinearLayout controllerSwitcher = new LinearLayout(Orientation.HORIZONTAL);
    controllerSwitcher.addChildren(
        new ButtonLayout(
            "Prev Controller",
            0,
            () -> {
              deviceManager.previousDevice();
              GuiScreenUtil.refreshCurrentScreen();
            }),
        DEFAULT_PADDING,
        new ButtonLayout(deviceManager.getCurrentDeviceName(), 1, NO_OP),
        DEFAULT_PADDING,
        new ButtonLayout(
            "Next Controller",
            0,
            () -> {
              deviceManager.nextDevice();
              GuiScreenUtil.refreshCurrentScreen();
            }));
    mainLayout.addChildren(controllerSwitcher, DEFAULT_PADDING);

    ImmutableList.Builder<Layout> childrenList = ImmutableList.builder();
    List<ControlBinding> allBindings = bindingManager.getBindings();
    ScreenContext[] bindableContexts =
        new ScreenContext[] {ScreenContext.IN_GAME, ScreenContext.MENU, ScreenContext.MOD_SETTINGS};
    for (ScreenContext context : bindableContexts) {
      childrenList.add(new LabelLayout(context.toString(), 0xffffff, 0, true));
      for (ControlBinding binding : allBindings) {
        if (binding.getScreenContext() != context) {
          continue;
        }
        String boundControl =
            (binding.getControlCase() == ControlCase.BUTTON
                ? binding.getButton().toString()
                : binding.getAxis().toString());
        LinearLayout row = new LinearLayout(Orientation.HORIZONTAL);
        row.addChildren(
            DEFAULT_PADDING,
            new LabelLayout(binding.getType().toString(), 0xffffff, 0.66f, false),
            DEFAULT_PADDING,
            new ButtonLayout(
                boundControl,
                0.33f,
                () -> {
                  GuiScreenUtil.pushScreen(
                      new BindControlScreen(binding.getScreenContext(), binding.getType()));
                }));
        childrenList.add(row);
      }
    }
    mainLayout.addChildren(new VerticalListLayout(childrenList.build()));

    mainLayout.initGui();
    super.initGui();
  }

  @Override
  public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    super.drawScreen(mouseX, mouseY, partialTicks);
    drawDefaultBackground();
    mainLayout.drawScreen(mouseX, mouseY, partialTicks);
  }
}
