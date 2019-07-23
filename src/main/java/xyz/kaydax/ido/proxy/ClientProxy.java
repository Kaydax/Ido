package xyz.kaydax.ido.proxy;

import net.minecraftforge.common.MinecraftForge;
import xyz.kaydax.ido.handler.ClientHandler;
import xyz.kaydax.ido.handler.CommonHandler;

public class ClientProxy extends CommonProxy
{
  @Override
  public void init()
  {
    MinecraftForge.EVENT_BUS.register(new CommonHandler());
    MinecraftForge.EVENT_BUS.register(new ClientHandler());
  }
}
