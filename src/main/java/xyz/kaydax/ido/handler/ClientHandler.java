package xyz.kaydax.ido.handler;

import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import xyz.kaydax.ido.legacy.RenderPlayerSwiming;

public class ClientHandler
{
  public boolean underWater(Entity player)
  {
    // Check if player is under the water or not
    final World world = player.getEntityWorld();
    double eyeBlock = player.posY + (double) player.getEyeHeight() - 0.25;
    BlockPos blockPos = new BlockPos(player.posX, eyeBlock, player.posZ);

    if (world.getBlockState(blockPos).getMaterial() == Material.WATER && !(player.getRidingEntity() instanceof EntityBoat))
    {
      return true;
    } else
    {
      return false;
    }
  }

  @SubscribeEvent
  public void onLivingRender(RenderPlayerEvent.Pre event)
  {
    EntityPlayer player = event.getEntityPlayer();

    if(player.noClip)
    {
      return;
    }

    boolean type = false;
    if (player.isInWater() && player.isSprinting() || player.height == 0.6F)
    {
      event.setCanceled(true);
      
      if (Minecraft.getMinecraft().getRenderViewEntity() instanceof AbstractClientPlayer)
      {
        AbstractClientPlayer client = ((AbstractClientPlayer) Minecraft.getMinecraft().getRenderViewEntity());
        type = client.getSkinType().equals("slim");
      }

      RenderPlayerSwiming sp = new RenderPlayerSwiming(event.getRenderer().getRenderManager(), type);
      sp.doRender(((AbstractClientPlayer) event.getEntity()), event.getX(), event.getY(), event.getZ(),
          ((AbstractClientPlayer) event.getEntity()).rotationYaw, event.getPartialRenderTick());
    }
  }

  @SubscribeEvent
  public void InputHandler(InputUpdateEvent event)
  {
    EntityPlayer player = event.getEntityPlayer();
    AxisAlignedBB sneak = player.getEntityBoundingBox();
    AxisAlignedBB crawl = player.getEntityBoundingBox();
    sneak = new AxisAlignedBB(sneak.minX + 0.4, sneak.minY + 0.9, sneak.minZ + 0.4, sneak.minX + 0.6, sneak.minY + 1.8, sneak.minZ + 0.6);
    crawl = new AxisAlignedBB(crawl.minX + 0.4, crawl.minY + 0.9, crawl.minZ + 0.4, crawl.minX + 0.6, crawl.minY + 1.5, crawl.minZ + 0.6);

    if(player.noClip)
    {
      return;
    }

    if(!player.isSneaking() && !underWater(player) && (player.height == 1.50F || player.height == 0.6F) && !player.world.getCollisionBoxes(player, sneak).isEmpty())
    {
      event.getMovementInput().sneak = true;
      event.getMovementInput().moveStrafe = (float)((double)event.getMovementInput().moveStrafe * 0.3D);
      event.getMovementInput().moveForward = (float)((double)event.getMovementInput().moveForward * 0.3D);
    }

    if(player.height == 0.6f && !player.isInWater() && !player.world.getCollisionBoxes(player, crawl).isEmpty())
    {
      event.getMovementInput().sneak = false;
    }
  }
}
