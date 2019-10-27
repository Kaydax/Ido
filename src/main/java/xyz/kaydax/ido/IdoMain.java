package xyz.kaydax.ido;

import org.apache.logging.log4j.Logger;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import xyz.kaydax.ido.proxy.CommonProxy;
import xyz.kaydax.ido.util.Referance;

@Mod(modid = Referance.MODID, name = Referance.NAME, version = Referance.VERSION, acceptedMinecraftVersions = Referance.MC_VERSIONS, useMetadata = true, dependencies = "required:forge@[14.23.5.2811,);")
public class IdoMain
{ 
  public static Logger LOGGER;
  
  @SidedProxy(clientSide = Referance.CLIENT_PROXY_CLASS, serverSide = Referance.SERVER_PROXY_CLASS)
  public static CommonProxy proxy;
  
  @EventHandler
  public void preInit(FMLPreInitializationEvent event) 
  {
    LOGGER = event.getModLog();
  }
  
  @EventHandler
  public void init(FMLInitializationEvent event)
  {
    proxy.init();
  }
}
