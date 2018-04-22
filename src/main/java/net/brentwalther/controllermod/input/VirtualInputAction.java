package net.brentwalther.controllermod.input;

public interface VirtualInputAction {
  /** Whether or not a button is becoming pressed or unpressed. */
  enum PressState {
    UNKNOWN,
    IS_BECOMING_PRESSED,
    IS_BECOMING_UNPRESSED,
  }

  /** The axis that a mouse movement input constants should be performed on */
  enum Axis {
    X,
    Y
  }

  /** Performs this virtual input constants when called. */
  void perform();
}
