package net.brentwalther.controllermod.ui.screen;

import com.google.common.collect.ImmutableList;
import net.brentwalther.controllermod.binding.BindingManager;
import net.brentwalther.controllermod.device.Control;
import net.brentwalther.controllermod.device.DeviceManager;
import net.brentwalther.controllermod.input.VirtualInputAction.PressState;
import net.brentwalther.controllermod.proto.ConfigurationProto.BindingType;
import net.brentwalther.controllermod.proto.ConfigurationProto.GlobalConfig.ControlBinding;
import net.brentwalther.controllermod.proto.ConfigurationProto.GlobalConfig.ControlBinding.ControlCase;
import net.brentwalther.controllermod.proto.ConfigurationProto.ScreenContext;
import net.brentwalther.controllermod.ui.GuiScreenUtil;
import net.brentwalther.controllermod.ui.layout.ButtonLayout;
import net.brentwalther.controllermod.ui.layout.LabelLayout;
import net.brentwalther.controllermod.ui.layout.Layout;
import net.brentwalther.controllermod.ui.layout.LinearLayout;
import net.brentwalther.controllermod.ui.layout.LinearLayout.Orientation;
import net.brentwalther.controllermod.ui.layout.SliderLayout;
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
    mainLayout.handleClick(mouseX, mouseY, mouseButton, PressState.IS_BECOMING_PRESSED);
    try {
      super.mouseClicked(mouseX, mouseY, mouseButton);
    } catch (IOException e) {
      // Do nothing, but why can it throw?
    }
  }

  @Override
  protected void mouseReleased(int mouseX, int mouseY, int state) {
    mainLayout.handleClick(mouseX, mouseY, 0, PressState.IS_BECOMING_UNPRESSED);
  }

  @Override
  public void initGui() {
    // Make a vertical layout that is the size of the entire parent (minus some left/right margin).
    mainLayout = new LinearLayout(Orientation.VERTICAL);
    mainLayout.setBounds(10, 0, width - 20, height);

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

    childrenList.add(
        listRow(
            new LabelLayout("Pointer sensitivity", 0xffffff, 1f, false),
            new SliderLayout(
                "Sensitivity",
                5,
                100,
                bindingManager.getPointerSensitivity(),
                (newValue) -> {
                  bindingManager.setPointerSensitivity(newValue);
                  GuiScreenUtil.refreshCurrentScreen();
                })));
    childrenList.add(
        listRow(
            new LabelLayout("In-game camera sensitivity", 0xffffff, 1f, false),
            new SliderLayout(
                "Sensitivity",
                5,
                100,
                bindingManager.getCameraSensitivity(),
                (newValue) -> {
                  bindingManager.setCameraSensitivity(newValue);
                  GuiScreenUtil.refreshCurrentScreen();
                })));

    List<ControlBinding> allBindings = bindingManager.getBindings();
    ScreenContext[] bindableContexts =
        new ScreenContext[] {ScreenContext.IN_GAME, ScreenContext.MENU, ScreenContext.MOD_SETTINGS};
    for (ScreenContext context : bindableContexts) {
      String categoryName = "Category: " + context.toString();
      childrenList.add(new LabelLayout(categoryName, 0xffffff, 0, true));
      for (ControlBinding binding : allBindings) {
        if (binding.getScreenContext() != context) {
          continue;
        }
        String controlName =
            (binding.getControlCase() == ControlCase.BUTTON
                ? binding.getButton().toString()
                : binding.getAxis().toString());
        childrenList.add(
            listRow(
                new LabelLayout(binding.getType().toString(), 0xffffff, 0.66f, false),
                new ButtonLayout(
                    controlName,
                    0.33f,
                    () ->
                        GuiScreenUtil.pushScreen(
                            new BindControlScreen(
                                (boundControl) -> {
                                  bindingManager
                                      .getNewControlBindingConsumer()
                                      .accept(
                                          makeControlBinding(
                                              boundControl,
                                              binding.getScreenContext(),
                                              binding.getType()));
                                  // Pop the "bind control" invisible screen.
                                  GuiScreenUtil.popScreen();
                                })))));
        if (binding.getControlCase() == ControlCase.AXIS) {
          childrenList.add(
              listRow(
                  new LabelLayout(
                      binding.getType().toString() + " Axis Deadzone", 0xffffff, 1f, false),
                  new SliderLayout(
                      "Deadzone",
                      0.05f,
                      0.95f,
                      binding.getAxisThreshold(),
                      (float newValue) -> {
                        bindingManager
                            .getNewControlBindingConsumer()
                            .accept(binding.toBuilder().setAxisThreshold(newValue).build());
                        GuiScreenUtil.refreshCurrentScreen();
                      })));
        }
      }
    }
    mainLayout.addChildren(new VerticalListLayout(childrenList.build()));

    mainLayout.initGui();
    super.initGui();
  }

  @Override
  public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    super.drawScreen(mouseX, mouseY, partialTicks);
    mainLayout.drawScreen(mouseX, mouseY, partialTicks);
  }

  private static Layout listRow(LabelLayout label, Layout control) {
    LinearLayout row = new LinearLayout(Orientation.HORIZONTAL);
    row.addChildren(DEFAULT_PADDING, label, DEFAULT_PADDING, control);
    return row;
  }

  private static ControlBinding makeControlBinding(
      Control control, ScreenContext bindingContext, BindingType bindingType) {
    ControlBinding.Builder bindingBuilder =
        ControlBinding.newBuilder().setScreenContext(bindingContext).setType(bindingType);
    switch (control.getType()) {
      case BUTTON:
        bindingBuilder.setButton(control.getButton());
        break;
      case AXIS:
        bindingBuilder.setAxis(control.getAxis());
        break;
    }
    return bindingBuilder.build();
  }
}
