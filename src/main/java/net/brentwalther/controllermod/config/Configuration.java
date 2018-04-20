package net.brentwalther.controllermod.config;

import net.brentwalther.controllermod.ControllerMod;
import net.brentwalther.controllermod.proto.ConfigurationProto;
import net.brentwalther.controllermod.proto.ConfigurationProto.GlobalConfig;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class Configuration {
  private final File file;
  private GlobalConfig config;
  private final Object configLock = new Object();

  public Configuration(File file) {
    this.file = file;
    this.config = GlobalConfig.getDefaultInstance();
    reloadConfiguration();
  }

  public void reloadConfiguration() {
    if (this.file == null) {
      return;
    }

    synchronized (configLock) {
      try {
        this.config = GlobalConfig.parseFrom(new FileInputStream(file));
      } catch (IOException e) {
        ControllerMod.getLogger().error("Failed to load configuration file: ", e);
      }
    }
  }

  public GlobalConfig get() {
    synchronized (this.configLock) {
      return this.config;
    }
  }

  /** A new configuration to commit both to memory (but not disk!). */
  public boolean commitToMemory(ConfigurationProto.GlobalConfig config) {
    synchronized (this.configLock) {
      this.config = config;
    }
    return true;
  }

  /** Commits the current configuration in memory to disk. */
  public boolean commitToDisk() {
    synchronized (this.configLock) {
      try {
        this.config.writeTo(new FileOutputStream(file));
      } catch (IOException e) {
        ControllerMod.getLogger().error("Could not commit configuration to file.", e);
        return false;
      }
      return true;
    }
  }
}
