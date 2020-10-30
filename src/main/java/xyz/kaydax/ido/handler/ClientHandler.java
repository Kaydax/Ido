package xyz.kaydax.ido.handler;

import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import xyz.kaydax.ido.util.handlers.ConfigHandler;
import com.mrcrayfish.obfuscate.client.event.ModelPlayerEvent;

public class ClientHandler
{
  public boolean underWater(Entity player)
  {
    // Check if player is under the water or not
    final World world = player.getEntityWorld();
    double eyeBlock = player.posY + (double) player.getEyeHeight() - 0.25;
    BlockPos blockPos = new BlockPos(player.posX, eyeBlock, player.posZ);
    
    //If the check passes, return true, else return false
    return (world.getBlockState(blockPos).getMaterial() == Material.WATER && !(player.getRidingEntity() instanceof EntityBoat)) ? true : false; //Clean this code up to be one line
  }

  //Really old and bad code... don't use this anymore, its really really bad with many incompatibility's
  /*@SubscribeEvent
  public void onLivingRender(RenderPlayerEvent.Pre event)
  {
    EntityPlayer player = event.getEntityPlayer();

    if(player.noClip) return; //If the player is creative flying, we don't need to do anything

    boolean type = false;
    if(player.isInWater() && player.isSprinting() || player.height == 0.6F)
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
  }*/
  
  //New good code that makes animations easy thanks to Obfuscate!
  @SubscribeEvent
  public void setupPlayerRotations(ModelPlayerEvent.SetupAngles.Post event)
  {
      EntityPlayer player = event.getEntityPlayer();
      ModelPlayer pm = event.getModelPlayer();
    
      if(player.noClip) return;
      
      //
      if((player.isInWater() && player.isSprinting() || player.height == 0.6f) && !player.isElytraFlying())
      {
        //If the local player is in first person, hide the animations. If there is another client crawling, show them always (Yes this is very lazy)
        if(Minecraft.getMinecraft().gameSettings.thirdPersonView >= 1 || !event.getEntityPlayer().isUser())
        {
          GlStateManager.rotate(45.0F, 1.0F, 0.0F, 0.0F);
          GlStateManager.translate(0.0F, 0.0F, -0.7F);
          
          float swing = player.limbSwing / 3;
          
          pm.bipedHead.rotateAngleX = -0.95f; //Head
          pm.bipedHeadwear.rotateAngleX = -0.95f; //Hat texture
          
          //Make sure to rotate both the arm and the arm jacket texture
          pm.bipedLeftArm.rotateAngleX = swing;
          pm.bipedRightArm.rotateAngleX = swing;
          pm.bipedLeftArm.rotateAngleY = swing;
          pm.bipedRightArm.rotateAngleY = -swing;
          pm.bipedLeftArmwear.rotateAngleX = swing;
          pm.bipedRightArmwear.rotateAngleX = swing;
          pm.bipedLeftArmwear.rotateAngleY = swing;
          pm.bipedRightArmwear.rotateAngleY = -swing;
        }
      }
  }

  //TODO: Clean up the collision checking code, as its very hacky
  @SubscribeEvent
  public void InputHandler(InputUpdateEvent event)
  {
    EntityPlayer player = event.getEntityPlayer();
    AxisAlignedBB sneak = player.getEntityBoundingBox();
    AxisAlignedBB crawl = player.getEntityBoundingBox();
    sneak = new AxisAlignedBB(sneak.minX + 0.4, sneak.minY + 0.9, sneak.minZ + 0.4, sneak.minX + 0.6, sneak.minY + 1.8, sneak.minZ + 0.6);
    crawl = new AxisAlignedBB(crawl.minX + 0.4, crawl.minY + 0.9, crawl.minZ + 0.4, crawl.minX + 0.6, crawl.minY + 1.5, crawl.minZ + 0.6);

    if(player.noClip) return;

    if(!player.isSneaking() && !underWater(player) && (player.height == 1.50F || player.height == 0.6F) && !player.world.getCollisionBoxes(player, sneak).isEmpty() && ConfigHandler.SNEAK_TOGGLE)
    {
      event.getMovementInput().sneak = true;
      event.getMovementInput().moveStrafe = (float)((double)event.getMovementInput().moveStrafe * 0.3D);
      event.getMovementInput().moveForward = (float)((double)event.getMovementInput().moveForward * 0.3D);
    }

    if(player.height == 0.6f && !player.isInWater() && !player.world.getCollisionBoxes(player, crawl).isEmpty() && ConfigHandler.CRAWL_TOGGLE)
    {
      event.getMovementInput().sneak = false;
      if(!ConfigHandler.SNEAK_TOGGLE) //This is to make sure crawling is still slow if sneaking is disabled
      {
        event.getMovementInput().moveStrafe = (float)((double)event.getMovementInput().moveStrafe * 0.3D);
        event.getMovementInput().moveForward = (float)((double)event.getMovementInput().moveForward * 0.3D);
      }
    }
  }
}
