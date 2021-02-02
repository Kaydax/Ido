package xyz.kaydax.ido;

import com.mrcrayfish.obfuscate.client.event.ModelPlayerEvent;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class EventHandler {
    public static final Method SET_SIZE_METHOD = ObfuscationReflectionHelper.findMethod(Entity.class, "func_70105_a", void.class, float.class, float.class);

    // These I hope will allow other mod developers to detect if the player is swimming / crawling or sneaking if they need to fix something or add compatibility for our bad code lol.
    public static boolean IsSwimmingOrCrawling = false;
    public static boolean IsSneaking = false;

    // Height of player when sneaking changes are enabled
    public static final float SNEAK_HEIGHT = 1.50f;

    public boolean underWater(Entity player) {
        // Check if player is under the water or not
        final World world = player.getEntityWorld();
        double eyeBlock = player.posY + (double) player.getEyeHeight() - 0.25;
        BlockPos blockPos = new BlockPos(player.posX, eyeBlock, player.posZ);

        // If the check passes, return true, else return false
        return world.getBlockState(blockPos).getMaterial() == Material.WATER && !(player.getRidingEntity() instanceof EntityBoat); //Clean this code up to be one line
    }

    private static boolean isSwimming(EntityPlayer playerIn) {
        return playerIn.isInWater() && playerIn.isSprinting();
    }

    @SubscribeEvent
    public void refreshConfig(ConfigChangedEvent event) {
        if (event.getModID().equals(Ido.ID)) {
            ConfigManager.sync(Ido.ID, Config.Type.INSTANCE);
        }
    }

    @SubscribeEvent
    public void adjustSize(TickEvent.PlayerTickEvent event) {
        EntityPlayer playerIn = event.player;

        // Skip when ladder or creative fly
        // Correct eye height if player is riding something
        if (playerIn.noClip || playerIn.isOnLadder()) return;
        if (playerIn.isRiding()) {
            playerIn.eyeHeight = playerIn.getDefaultEyeHeight();
            return;
        }

        AxisAlignedBB box = playerIn.getEntityBoundingBox();

        // recalculate size
        double d0 = playerIn.width / 2.0;
        AxisAlignedBB axisalignedbb = new AxisAlignedBB(playerIn.posX - d0, box.minY, playerIn.posZ - d0, playerIn.posX + d0, box.minY + playerIn.height, playerIn.posZ + d0);
        AxisAlignedBB crawl = new AxisAlignedBB(box.minX + 0.4, box.minY + 0.9, box.minZ + 0.4, box.minX + 0.6, box.minY + 1.5, box.minZ + 0.6);

        if (playerIn.isInWater() && !underWater(playerIn)) {
            playerIn.setSprinting(false);
        }

        float newHeight = playerIn.height;
        float newWidth = playerIn.width;

        // If we are swimming on the floor then stay swimming.
        if (IsSwimmingOrCrawling && underWater(playerIn) && Configuration.swimToggle && playerIn.rotationPitch >= 0.0) {
            newHeight = 0.6f;
            newWidth = 0.6f;
            playerIn.eyeHeight = 0.45f;
            IsSwimmingOrCrawling = true;
        } else if ((isSwimming(playerIn) && underWater(playerIn) && Configuration.swimToggle) || (!playerIn.world.getCollisionBoxes(playerIn, crawl).isEmpty() && Configuration.crawlToggle)) {
            newHeight = 0.6f;
            newWidth = 0.6f;
            playerIn.eyeHeight = 0.45f;
            IsSwimmingOrCrawling = true;
        } else if (playerIn.isSneaking() && !underWater(playerIn) && Configuration.sneakToggle) {
            newHeight = SNEAK_HEIGHT;
            newWidth = 0.6f;
            playerIn.eyeHeight = 1.35f;
            IsSneaking = true;
        } else {
            playerIn.eyeHeight = playerIn.getDefaultEyeHeight();
            IsSwimmingOrCrawling = false;
            IsSneaking = false;
        }

        // size is reset after pre tick so we must set it during post tick
        if (event.phase == TickEvent.Phase.END) {
            try {
                SET_SIZE_METHOD.invoke(playerIn, newWidth, newHeight);
            } catch (IllegalAccessException | InvocationTargetException | IllegalArgumentException e) {
                e.printStackTrace();
            }
        }

        playerIn.setEntityBoundingBox(axisalignedbb);
    }

    @SubscribeEvent
    public void onLivingPlayer(LivingEvent.LivingUpdateEvent event) {
        EntityLivingBase living = event.getEntityLiving();

        if (living instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getEntity();

            if (player.noClip) return;

            if (isSwimming(player) && Configuration.swimToggle) {
                if (player.motionX < -0.4D) {
                    player.motionX = -0.39F;
                }
                if (player.motionX > 0.4D) {
                    player.motionX = 0.39F;
                }

                if (player.motionY < -0.4D) {
                    player.motionY = -0.39F;
                }
                if (player.motionY > 0.4D) {
                    player.motionY = 0.39F;
                }
                if (player.motionZ < -0.4D) {
                    player.motionZ = -0.39F;
                }
                if (player.motionZ > 0.4D) {
                    player.motionZ = 0.39F;
                }

                double d3 = player.getLookVec().y;
                double d4 = d3 < -0.2D ? 0.025D : 0.025D;

                if (d3 <= 0.0D || player.world.getBlockState(new BlockPos(player.posX, player.posY + 1.0D - 0.64D, player.posZ)).getMaterial() == Material.WATER && Configuration.swimToggle) {
                    player.motionY += (d3 - player.motionY) * d4;
                }

                player.motionY += 0.018d;

                player.motionX *= 1.005F;
                player.motionZ *= 1.005F;

                player.move(MoverType.SELF, player.motionX, player.motionY, player.motionZ);
            }
        }
    }

    // New good code that makes animations easy thanks to Obfuscate!
    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void setupPlayerRotations(ModelPlayerEvent.SetupAngles.Post event) {
        EntityPlayer playerIn = event.getEntityPlayer();
        ModelPlayer model = event.getModelPlayer();

        if (playerIn.noClip) return;

        if ((isSwimming(playerIn) || playerIn.height == 0.6f) && !playerIn.isElytraFlying()) {
            // If the local player is in first person, hide the animations. If there is another client crawling, show them always (Yes this is very lazy)
            if (Minecraft.getMinecraft().gameSettings.thirdPersonView >= 1 || !event.getEntityPlayer().isUser()) {
                GlStateManager.rotate(45.0F, 1.0F, 0.0F, 0.0F);
                GlStateManager.translate(0.0F, 0.0F, -0.7F);

                float swing = playerIn.limbSwing / 3;

                // Head
                model.bipedHead.rotateAngleX = -0.95f;
                model.bipedHeadwear.rotateAngleX = -0.95f;

                // Make sure to rotate both the arm and the arm jacket texture
                model.bipedLeftArm.rotateAngleX = swing;
                model.bipedRightArm.rotateAngleX = swing;
                model.bipedLeftArm.rotateAngleY = swing;
                model.bipedRightArm.rotateAngleY = -swing;
                model.bipedLeftArmwear.rotateAngleX = swing;
                model.bipedRightArmwear.rotateAngleX = swing;
                model.bipedLeftArmwear.rotateAngleY = swing;
                model.bipedRightArmwear.rotateAngleY = -swing;
            }
        }
    }

    //TODO: Clean up the collision checking code, as its very hacky
    @SubscribeEvent
    public void InputHandler(InputUpdateEvent event) {
        EntityPlayer player = event.getEntityPlayer();
        AxisAlignedBB box = player.getEntityBoundingBox();
        AxisAlignedBB sneak = new AxisAlignedBB(box.minX + 0.4, box.minY + 0.9, box.minZ + 0.4, box.minX + 0.6, box.minY + 1.8, box.minZ + 0.6);
        AxisAlignedBB crawl = new AxisAlignedBB(box.minX + 0.4, box.minY + 0.9, box.minZ + 0.4, box.minX + 0.6, box.minY + 1.5, box.minZ + 0.6);

        if (player.noClip) return;

        if (!player.isSneaking() && !underWater(player) && (player.height == SNEAK_HEIGHT || player.height == 0.6F) && !player.world.getCollisionBoxes(player, sneak).isEmpty() && Configuration.sneakToggle) {
            event.getMovementInput().sneak = true;
            event.getMovementInput().moveStrafe = (float)((double)event.getMovementInput().moveStrafe * 0.3D);
            event.getMovementInput().moveForward = (float)((double)event.getMovementInput().moveForward * 0.3D);
        }

        if (player.height == 0.6f && !player.isInWater() && !player.world.getCollisionBoxes(player, crawl).isEmpty() && Configuration.crawlToggle) {
            event.getMovementInput().sneak = false;
            // This is to make sure crawling is still slow if sneaking is disabled
            if (!Configuration.sneakToggle) {
                event.getMovementInput().moveStrafe = (float)((double)event.getMovementInput().moveStrafe * 0.3D);
                event.getMovementInput().moveForward = (float)((double)event.getMovementInput().moveForward * 0.3D);
            }
        }
    }
}
