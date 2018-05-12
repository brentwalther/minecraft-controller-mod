package net.brentwalther.controllermod.input;

import net.brentwalther.controllermod.ControllerMod;
import net.brentwalther.controllermod.input.VirtualInputAction.PressState;
import net.brentwalther.controllermod.util.PositionOnScreen;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Mouse;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;

public class VirtualMouse {

  public static final VirtualMouse INSTANCE = new VirtualMouse();

  private final Field xField;
  private final Field yField;
  private final Field dxField;
  private final Field dyField;
  private final Field buttonField;
  private final Field readBufferField;
  private final Field isGrabbedField;

  private int lastX = 0;
  private int lastY = 0;
  private int unflushedDy = 0;
  private int unflushedDx = 0;
  private int mouseEventCount = 0;

  private VirtualMouse() {
    ControllerMod.getLogger().info("Creating VirtualMouse");
    xField = getMouseField("x");
    yField = getMouseField("y");
    dxField = getMouseField("dx");
    dyField = getMouseField("dy");
    buttonField = getMouseField("buttons");
    readBufferField = getMouseField("readBuffer");
    isGrabbedField = getMouseField("isGrabbed");

    setMousePosition(PositionOnScreen.middle());
  }

  public void unpressAllButtons() {
    for (int i = 0; i < Mouse.getButtonCount(); i++) {
      setMouseButton(ButtonPressInputAction.create(i, PressState.IS_BECOMING_UNPRESSED));
    }
  }

  public void setMousePosition(PositionOnScreen screen) {
    ControllerMod.getLogger().info("Settings mouse position to " + screen);
    lastX = screen.getMouseX();
    lastY = screen.getMouseY();
    unflushedDx = 0;
    unflushedDy = 0;
  }

  public PositionOnScreen getMousePosition() {
    return PositionOnScreen.fromMouseCoords(lastX, lastY);
  }

  public void moveMouse(int dx, int dy) {
    if (Mouse.isGrabbed()) {
      unflushedDx += dx;
      unflushedDy += dy;
    } else {
      lastX += dx;
      lastY += dy;
    }
  }

  public void scrollWheel(int event_dwheel) {
    ControllerMod.getLogger().info("Setting scroll wheel: " + event_dwheel);
    addMouseEvent((byte) -1, (byte) 0, 0, 0, event_dwheel);
  }

  public void flushMouseMovements() {
    if (Mouse.isGrabbed()) {
      addMouseEvent((byte) -1, (byte) 0, unflushedDx, unflushedDy, 0);
      try {
        dxField.setInt(null, unflushedDx);
        dyField.setInt(null, unflushedDy);
        unflushedDx = 0;
        unflushedDy = 0;
      } catch (Exception ex) {
        ControllerMod.getLogger().warn("Failed setting dx/dy value of mouse. " + ex.toString());
      }
    } else {
      try {
        xField.setInt(null, lastX);
        yField.setInt(null, lastY);
      } catch (Exception ex) {
        ControllerMod.getLogger().warn("Failed setting x/y value of mouse. " + ex.toString());
      }
    }
  }

  public void setMouseButton(ButtonPressInputAction event) {
    if (!isButtonSupported(event.mouseButton())) {
      ControllerMod.getLogger()
          .error("Trying to set an unsupported mouse button: " + event.mouseButton());
      return;
    }
    int eventX = Mouse.isGrabbed() ? 0 : lastX;
    int eventY = Mouse.isGrabbed() ? 0 : lastY;
    ControllerMod.getLogger()
        .info("Setting mouse button " + event.mouseButton() + " to " + event.state());
    byte isPressedByte = (byte) (event.state().equals(PressState.IS_BECOMING_PRESSED) ? 1 : 0);
    try {
      ((ByteBuffer) buttonField.get(null)).put(event.mouseButton(), isPressedByte);
    } catch (Exception ex) {
      ControllerMod.getLogger().warn("Failed setting mouse button field: " + ex.toString());
    }
    addMouseEvent((byte) event.mouseButton(), isPressedByte, eventX, eventY, 0);
  }

  private void addMouseEvent(
      byte eventButton, byte eventState, int eventDx, int eventDy, int eventDwheel) {
    try {
      long eventNanos = Minecraft.getSystemTime() * 1000 + (mouseEventCount++ % 1000);
      // Byte buffer ordering found here:
      // https://github.com/LWJGL/lwjgl/blob/lwjgl2.9.3/src/java/org/lwjgl/input/Mouse.java
      // To add a new event, we first compact that current buffer, add all the values that Mouse
      // expects, and then call flip() which advances our cursor to the current position.
      ByteBuffer mouseEventBuffer = (ByteBuffer) readBufferField.get(null);
      mouseEventBuffer.compact();
      mouseEventBuffer.put(eventButton);
      mouseEventBuffer.put(eventState);
      mouseEventBuffer.putInt(eventDx);
      mouseEventBuffer.putInt(eventDy);
      mouseEventBuffer.putInt(eventDwheel);
      mouseEventBuffer.putLong(eventNanos);
      mouseEventBuffer.flip();
    } catch (Exception ex) {
      ControllerMod.getLogger().warn("Failed putting value in readBufferField " + ex.toString());
    }
  }

  private static boolean isButtonSupported(int button) {
    return button >= 0 && button < Mouse.getButtonCount();
  }

  private static Field getMouseField(String fieldName) {
    try {
      Field f = Mouse.class.getDeclaredField(fieldName);
      f.setAccessible(true);
      return f;
    } catch (NoSuchFieldException | SecurityException e) {
      ControllerMod.disableMod(
          "Mod is disabled because VirtualMouse could not be initialized: " + e);
      return null;
    }
  }
}
