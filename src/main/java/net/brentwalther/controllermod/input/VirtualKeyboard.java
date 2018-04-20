package net.brentwalther.controllermod.input;

import net.brentwalther.controllermod.ControllerMod;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;

public class VirtualKeyboard {

  public static VirtualKeyboard INSTANCE = new VirtualKeyboard();

  private Field keyDownField;
  private Field keyBufferField;
  private int keypressCount = 0;
  boolean[] pressedKeys = new boolean[Keyboard.getKeyCount()];

  private VirtualKeyboard() {
    ControllerMod.getLogger().info("Creating VirtualKeyboard");
    try {
      keyBufferField = Keyboard.class.getDeclaredField("readBuffer");
      keyDownField = Keyboard.class.getDeclaredField("keyDownBuffer");
    } catch (NoSuchFieldException e) {
    }
    keyDownField.setAccessible(true);
    keyBufferField.setAccessible(true);
  }

  // send a press key event to the keyboard buffer
  public void pressKey(int keycode) {
    if (!isValidKey(keycode)) {
      ControllerMod.getLogger().error("Trying to press an invalid key: " + keycode);
      return;
    }
    ControllerMod.getLogger().info("Pressing key " + Keyboard.getKeyName(keycode));
    writeKeyEventToBuffer(keycode, true);
    setKeyState(keycode, true);
    pressedKeys[keycode] = true;
  }

  // send a release key event to the keyboard buffer
  // give option to only send the event if a pressKey event was recorded prior
  public void releaseKey(int keycode) {
    if (!isValidKey(keycode)) {
      ControllerMod.getLogger().error("Trying to release an invalid key: " + keycode);
      return;
    }
    ControllerMod.getLogger().info("Releasing key " + Keyboard.getKeyName(keycode));
    writeKeyEventToBuffer(keycode, false);
    setKeyState(keycode, false);
    pressedKeys[keycode] = false;
  }

  public void resetKeyState() {
    for (int i = 0; i < pressedKeys.length; i++) {
      if (pressedKeys[i]) {
        releaseKey(i);
      }
    }
  }

  private void setKeyState(int keycode, boolean down) {
    if (keyDownField == null) {
      ControllerMod.getLogger().error("Keydown field is uninitialized.");
      return;
    }
    try {
      ((ByteBuffer) keyDownField.get(null)).put(keycode, (byte) (down ? 1 : 0));
    } catch (Exception ex) {
      ControllerMod.getLogger().error("Failed putting value in key buffer" + ex.toString());
    }
  }

  private static boolean isValidKey(int keycode) {
    return (keycode >= 0 || keycode < Keyboard.KEYBOARD_SIZE);
  }

  private void writeKeyEventToBuffer(int keycode, boolean down) {
    if (keyBufferField == null) {
      ControllerMod.getLogger().error("The keybuffer field is uninitialized.");
      return;
    }
    try {
      ((ByteBuffer) keyBufferField.get(null)).compact();
      ((ByteBuffer) keyBufferField.get(null)).putInt(keycode); // key
      ((ByteBuffer) keyBufferField.get(null)).put((byte) (down ? 1 : 0)); // state
      ((ByteBuffer) keyBufferField.get(null)).putInt(keycode); // character
      ((ByteBuffer) keyBufferField.get(null)).putLong(Minecraft.getSystemTime() * 1000 + (keypressCount++ % 1000)); // nanos
      ((ByteBuffer) keyBufferField.get(null)).put((byte) 0); // repeat
      ((ByteBuffer) keyBufferField.get(null)).flip();
    } catch (Exception ex) {
      ControllerMod.getLogger().error("Failed putting value in keyBufferField " + ex.toString());
    }
  }
}
