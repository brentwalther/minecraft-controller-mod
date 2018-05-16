package net.brentwalther.controllermod;

import net.brentwalther.controllermod.config.Configuration;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.apache.logging.log4j.Logger;

@Mod(modid = ControllerMod.MODID, name = ControllerMod.NAME, version = ControllerMod.VERSION)
public class ControllerMod {
  public static final String MODID = "controllermod";
  public static final String NAME = "Brent Walther's Controller Mod";
  public static final String VERSION = "1.0";

  private static Logger logger;
  private static Configuration config;
  private static BindingApplierManager bindingApplierManager;

  private static boolean isEnabled;
  private static ControllerMod currentMod;

  public static void disableMod(String s) {
    getLogger().error(s);
    isEnabled = false;
    MinecraftForge.EVENT_BUS.unregister(currentMod);
    currentMod = null;
  }

  @EventHandler
  public void preInit(FMLPreInitializationEvent event) {
    isEnabled = true;
    currentMod = this;
    MinecraftForge.EVENT_BUS.register(currentMod);

    logger = event.getModLog();
    config = new Configuration(event.getSuggestedConfigurationFile());
    bindingApplierManager = new BindingApplierManager(config);
  }

  @SubscribeEvent
  public void handleTick(TickEvent event) {
    if (bindingApplierManager == null || !isEnabled || event.type != TickEvent.Type.RENDER) {
      return;
    }
    bindingApplierManager.tick();
  }

  @SubscribeEvent
  public void handlePostGuiRender(GuiScreenEvent.DrawScreenEvent.Post event) {
    bindingApplierManager.postGuiRender();
  }


  public static Logger getLogger() {
    return logger;
  }
}
