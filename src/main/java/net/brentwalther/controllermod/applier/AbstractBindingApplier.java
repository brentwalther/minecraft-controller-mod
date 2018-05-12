package net.brentwalther.controllermod.applier;

import com.google.common.collect.ImmutableMultimap;
import net.brentwalther.controllermod.binding.AxisBinding;
import net.brentwalther.controllermod.binding.ButtonBinding;
import net.brentwalther.controllermod.config.Configuration;
import net.brentwalther.controllermod.input.VirtualInputAction;
import net.brentwalther.controllermod.input.VirtualInputAction.PressState;
import net.brentwalther.controllermod.proto.ConfigurationProto.XInputAxis;
import net.brentwalther.controllermod.proto.ConfigurationProto.XInputButton;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * The most basic implementation of a {@link BindingApplier}. It receives bindings through {@link
 * #setBindings} and then applies all matchings ones during the button and axis updates.
 */
public abstract class AbstractBindingApplier implements BindingApplier {

  private final Queue<VirtualInputAction> inputActions = new ConcurrentLinkedQueue<>();
  private ImmutableMultimap<XInputButton, ButtonBinding> buttonBindings = ImmutableMultimap.of();
  private ImmutableMultimap<XInputAxis, AxisBinding> axisBindings = ImmutableMultimap.of();
  private Set<ButtonBinding> pressedButtonToUnpressOnUnload = new HashSet<>();

  @Override
  public void setBindings(
      ImmutableMultimap<XInputButton, ButtonBinding> buttonBindings,
      ImmutableMultimap<XInputAxis, AxisBinding> axisBindings) {
    this.buttonBindings = buttonBindings;
    this.axisBindings = axisBindings;
  }

  @Override
  public void processButtonUpdates(List<ButtonStateUpdate> updates) {
    // Process each of the button updates
    for (ButtonStateUpdate update : updates) {
      // If the button has any bindings, we grab them and apply the update to each.
      if (buttonBindings.containsKey(update.button)) {
        Iterable<ButtonBinding> bindings = buttonBindings.get(update.button);
        for (ButtonBinding binding : bindings) {
          // If the update is a button down press, we need to stash the binding because the binding
          // could cause the applier to change and unload this one. If that happens, we want to make
          // sure that the binding has a chance to perform it's reciprocal button up action.
          if (update.state == PressState.IS_BECOMING_PRESSED) {
            pressedButtonToUnpressOnUnload.add(binding);
          } else if (update.state == PressState.IS_BECOMING_UNPRESSED) {
            if (!pressedButtonToUnpressOnUnload.contains(binding)) {
              // Since this button was not already recorded as pressed, we apply the updated state
              // to the binding. This commonly happens when the applier switches and a button is
              // released in the new applier but was pressed in the old applier.
              break;
            }
            pressedButtonToUnpressOnUnload.remove(binding);
          }
          for (VirtualInputAction action : binding.update(update.state)) {
            inputActions.offer(action);
          }
        }
      }
    }
  }

  @Override
  public void processAxisUpdates(List<AxisValueUpdate> updates) {
    for (AxisValueUpdate update : updates) {
      if (axisBindings.containsKey(update.axis)) {
        Iterable<AxisBinding> bindings = axisBindings.get(update.axis);
        for (AxisBinding binding : bindings) {
          for (VirtualInputAction action : binding.update(update.value)) {
            inputActions.offer(action);
          }
        }
      }
    }
  }

  @Override
  public Iterator<VirtualInputAction> getInputActions() {
    return new Iterator<VirtualInputAction>() {
      @Override
      public boolean hasNext() {
        return !inputActions.isEmpty();
      }

      @Override
      public VirtualInputAction next() {
        return inputActions.poll();
      }
    };
  }

  @Override
  public void onUnload(Configuration config) {
    // Before we unload and switch binding appliers, we want to go ahead and unpress buttons that
    // were pressed in this applier but didn't have a chance to become unpressed while still in
    // this applier.
    for (ButtonBinding bindingToUnpress : pressedButtonToUnpressOnUnload) {
      bindingToUnpress.update(PressState.IS_BECOMING_UNPRESSED);
    }
    pressedButtonToUnpressOnUnload.clear();
  }
}
