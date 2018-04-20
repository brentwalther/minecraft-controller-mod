package net.brentwalther.controllermod.util;

import net.minecraft.client.Minecraft;

/** A runnable that will only run if not called in the last N milliseconds. */
public class ThrottleRunnable implements Runnable {

  private final long msTimeBetweenCalls;
  private final Runnable runnable;
  private long lastTickTime;

  public ThrottleRunnable(Runnable runnable, long msTimeBetweenCalls) {
    this.runnable = runnable;
    this.msTimeBetweenCalls = msTimeBetweenCalls;
    this.lastTickTime = Minecraft.getSystemTime();
  }

  @Override
  public void run() {
    long timeNow = Minecraft.getSystemTime();
    long msSinceLastTick = timeNow - lastTickTime;

    if (msSinceLastTick < msTimeBetweenCalls) {
      return;
    }
    lastTickTime = timeNow;
    runnable.run();
  }
}
