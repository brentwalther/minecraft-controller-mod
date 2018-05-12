package net.brentwalther.controllermod.applier;

import com.google.common.collect.ImmutableMultimap;
import net.brentwalther.controllermod.binding.AxisBinding;
import net.brentwalther.controllermod.binding.ButtonBinding;
import net.brentwalther.controllermod.config.Configuration;
import net.brentwalther.controllermod.input.VirtualInputAction;
import net.brentwalther.controllermod.input.VirtualInputAction.PressState;
import net.brentwalther.controllermod.proto.ConfigurationProto.ScreenContext;
import net.brentwalther.controllermod.proto.ConfigurationProto.XInputAxis;
import net.brentwalther.controllermod.proto.ConfigurationProto.XInputButton;

import java.util.Iterator;
import java.util.List;

/**
 * An interface that can be implemented for various game contexts to handle controller inputs. For
 * example, there would be a different implementation for in-game and menu contexts.
 */
public interface BindingApplier {

  /** An immutable representation of a state change on a input device button. */
  class ButtonStateUpdate {
    public final XInputButton button;
    public final PressState state;

    public ButtonStateUpdate(XInputButton button, PressState state) {
      this.button = button;
      this.state = state;
    }
  }

  /** An immutable representation of a value change on a input device axis. */
  class AxisValueUpdate {
    public final XInputAxis axis;
    public final float value;

    public AxisValueUpdate(XInputAxis axis, float value) {
      this.axis = axis;
      this.value = value;
    }
  }

  /**
   * Called right after this control applier is loaded but before any handlers are called.
   *
   * @param config the configuration this applier should load its saved settings from.
   */
  void onLoad(Configuration config);

  /**
   * Called when this control applier method is about to be unloaded (to be replaced by another) and
   * should clean itself up.
   *
   * @param config the configuration this applier should save its updated settings to.
   */
  void onUnload(Configuration config);

  /** Called when this BindingApplier instance should update its bindings. */
  void setBindings(
      ImmutableMultimap<XInputButton, ButtonBinding> buttonBindings,
      ImmutableMultimap<XInputAxis, AxisBinding> axisBindings);

  /** @param updates the updates that have happened since the last call to this function */
  void processButtonUpdates(List<ButtonStateUpdate> updates);

  /** @param updates the updates that have happened since the last call to this function */
  void processAxisUpdates(List<AxisValueUpdate> updates);

  /**
   * An iterator to all of the virtual input actions that this applier has queued up for processing.
   */
  Iterator<VirtualInputAction> getInputActions();

  /**
   * Called to get the render runnable (code that should run post render). Calls to handlers may be
   * done on separate threads so the code belonging in this runnable should be synchronized with
   * internal state.
   */
  Runnable getRenderRunnable();

  /** @return the screen that this BindingApplier is associated with. */
  ScreenContext getScreenContext();
}
