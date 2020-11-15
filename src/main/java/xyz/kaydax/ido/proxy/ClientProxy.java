package xyz.kaydax.ido.proxy;

import net.minecraftforge.common.MinecraftForge;
import xyz.kaydax.ido.handler.ClientHandler;
import xyz.kaydax.ido.handler.CommonHandler;
import xyz.kaydax.ido.init.ModKeys;

public class ClientProxy extends CommonProxy
{
  @Override
  public void init()
  {
	ModKeys.init();
	ModKeys.register();	  
    MinecraftForge.EVENT_BUS.register(new CommonHandler());
    MinecraftForge.EVENT_BUS.register(new ClientHandler());
  }
}
