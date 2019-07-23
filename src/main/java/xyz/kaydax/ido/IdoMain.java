package xyz.kaydax.ido;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import xyz.kaydax.ido.proxy.CommonProxy;
import xyz.kaydax.ido.util.Referance;

@Mod(modid = Referance.MODID, name = Referance.NAME, version = Referance.VERSION, acceptedMinecraftVersions = Referance.MC_VERSIONS)
public class IdoMain
{
  public static final Logger LOGGER = LogManager.getLogger(Referance.NAME);
  
  @SidedProxy(clientSide = Referance.CLIENT_PROXY_CLASS, serverSide = Referance.SERVER_PROXY_CLASS)
  public static CommonProxy proxy;
  
  @EventHandler
  public void init(FMLInitializationEvent event)
  {
    proxy.init();
  }
}
