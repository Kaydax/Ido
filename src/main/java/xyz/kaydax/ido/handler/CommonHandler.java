package xyz.kaydax.ido.handler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import xyz.kaydax.ido.util.handlers.ConfigHandler;

public class CommonHandler
{
  public static Method setSize = ObfuscationReflectionHelper.findMethod(Entity.class, "func_70105_a", void.class, float.class, float.class);
  
  //These I hope will allow other mod developers to detect if the player is swimming / crawling or sneaking if they need to fix something or add compatibility for our bad code lol.
  public static boolean IsSwimmingOrCrawling = false; 
  public static boolean IsSneaking = false;
  
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
  public void adjustSize(TickEvent.PlayerTickEvent event)
  {
    EntityPlayer player = event.player;
    AxisAlignedBB axisalignedbb = player.getEntityBoundingBox();
    AxisAlignedBB crawl = player.getEntityBoundingBox();
    axisalignedbb = new AxisAlignedBB(
        player.posX - player.width / 2.0D, axisalignedbb.minY, player.posZ - player.width / 2.0D, 
        player.posX + player.width / 2.0D, axisalignedbb.minY + player.height, player.posZ + player.width / 2.0D);
    crawl = new AxisAlignedBB(crawl.minX + 0.4, crawl.minY + 0.9, crawl.minZ + 0.4, crawl.minX + 0.6, crawl.minY + 1.5, crawl.minZ + 0.6);
    
    if(player.noClip) { return; }
    if(player.isOnLadder()) { return; }
    if(player.isRiding()) { player.eyeHeight = player.getDefaultEyeHeight(); return; }
    
    if(player.isInWater() && !underWater(player))
    {
      player.setSprinting(false);
    }
    
    if((player.isInWater() && player.isSprinting() && underWater(player) && ConfigHandler.SWIM_TOGGLE) || (!player.world.getCollisionBoxes(player, crawl).isEmpty() && ConfigHandler.CRAWL_TOGGLE))
    {
      player.height = 0.6f;
      player.width = 0.6f;
      player.eyeHeight = 0.45f;
      IsSwimmingOrCrawling = true;
    } else if(player.isSneaking() && !underWater(player) && ConfigHandler.SNEAK_TOGGLE) {
      player.height = 1.50f;
      player.width = 0.6f;
      player.eyeHeight = 1.35f;
      IsSneaking = true;
    } else {
      player.eyeHeight = player.getDefaultEyeHeight();
      IsSwimmingOrCrawling = false;
      IsSneaking = false;
    }
    
    try
    {
      setSize.invoke(player, player.width, player.height);
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    }
    
    player.setEntityBoundingBox(axisalignedbb);
  }
  
  @SubscribeEvent
  public void onLivingPlayer(LivingEvent.LivingUpdateEvent event)
  {
    EntityLivingBase entityLivingBase = event.getEntityLiving();

    if (entityLivingBase instanceof EntityPlayer)
    {
      EntityPlayer player = (EntityPlayer) event.getEntity();
      if(player.noClip)
      {
        return;
      }
      if (player.isInWater() && player.isSprinting() && ConfigHandler.SWIM_TOGGLE)
      {
        if (player.motionX < -0.4D)
        {
          player.motionX = -0.39F;
        }
        if (player.motionX > 0.4D)
        {
          player.motionX = 0.39F;
        }

        if (player.motionY < -0.4D)
        {
          player.motionY = -0.39F;
        }
        if (player.motionY > 0.4D)
        {
          player.motionY = 0.39F;
        }
        if (player.motionZ < -0.4D)
        {
          player.motionZ = -0.39F;
        }
        if (player.motionZ > 0.4D)
        {
          player.motionZ = 0.39F;
        }

        double d3 = player.getLookVec().y;
        double d4 = d3 < -0.2D ? 0.025D : 0.025D;

        if (d3 <= 0.0D || player.world.getBlockState(new BlockPos(player.posX, player.posY + 1.0D - 0.64D, player.posZ)).getMaterial() == Material.WATER && ConfigHandler.SWIM_TOGGLE)
        {
          player.motionY += (d3 - player.motionY) * d4;
        }

        // double d6 = player.posY; //Unused

        player.motionY += 0.018D;

        player.motionX *= 1.005F;
        player.motionZ *= 1.005F;

        player.move(MoverType.SELF, player.motionX, player.motionY, player.motionZ);
      }
    }
  }
}
