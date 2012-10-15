package micdoodle8.mods.galacticraft.mars.client;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.util.EnumSet;
import java.util.Random;

import micdoodle8.mods.galacticraft.core.GCCoreUtil;
import micdoodle8.mods.galacticraft.core.GCCoreEntityArrow;
import micdoodle8.mods.galacticraft.core.GCCoreEntityCreeper;
import micdoodle8.mods.galacticraft.core.GCCoreEntitySkeleton;
import micdoodle8.mods.galacticraft.core.GCCoreEntitySpider;
import micdoodle8.mods.galacticraft.core.GCCoreEntityZombie;
import micdoodle8.mods.galacticraft.core.client.GCCoreRenderArrow;
import micdoodle8.mods.galacticraft.core.client.ClientProxyCore.GCKeyHandler;
import micdoodle8.mods.galacticraft.mars.CommonProxyMars;
import micdoodle8.mods.galacticraft.mars.GCMarsBlocks;
import micdoodle8.mods.galacticraft.mars.GCMarsConfigManager;
import micdoodle8.mods.galacticraft.mars.GCMarsEntityCreeperBoss;
import micdoodle8.mods.galacticraft.mars.GCMarsEntityProjectileTNT;
import micdoodle8.mods.galacticraft.mars.GCMarsEntitySludgeling;
import micdoodle8.mods.galacticraft.mars.GCMarsItemJetpack;
import micdoodle8.mods.galacticraft.mars.GCMarsItems;
import micdoodle8.mods.galacticraft.mars.GCMarsUtil;
import micdoodle8.mods.galacticraft.mars.GCMarsWorldProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.src.EntityClientPlayerMP;
import net.minecraft.src.EntityFX;
import net.minecraft.src.EntityHugeExplodeFX;
import net.minecraft.src.EntityLargeExplodeFX;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.Material;
import net.minecraft.src.NetworkManager;
import net.minecraft.src.Packet250CustomPayload;
import net.minecraft.src.PlayerAPI;
import net.minecraft.src.RenderLiving;
import net.minecraft.src.World;
import net.minecraft.src.WorldClient;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.KeyBindingRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.common.registry.TickRegistry;

/**
 * Copyright 2012, micdoodle8
 * 
 *  All rights reserved.
 *
 */
public class ClientProxyMars extends CommonProxyMars
{
	private static int fluidRenderID;
	public static long getFirstBootTime;
	public static long getCurrentTime;
	private Random rand = new Random();
	
	@Override
	public void preInit(FMLPreInitializationEvent event) 
	{
		MinecraftForge.EVENT_BUS.register(new GCMarsSounds());
		getFirstBootTime = System.currentTimeMillis();
				
		try
		{
			PlayerAPI.register("Galacticraft Mars", GCMarsPlayerBase.class);
		}
		catch(Exception e)
		{
			FMLLog.severe("PLAYER API NOT INSTALLED.");
			FMLLog.severe("Galacticraft Mars will now fail to load.");
			e.printStackTrace();
		}
	}

	@Override
	public void init(FMLInitializationEvent event) 
	{
		TickRegistry.registerTickHandler(new TickHandlerClient(), Side.CLIENT);
		KeyBindingRegistry.registerKeyBinding(new GCKeyHandler());
        NetworkRegistry.instance().registerChannel(new ClientPacketHandler(), "GalacticraftMars", Side.CLIENT);
        this.fluidRenderID = RenderingRegistry.getNextAvailableRenderId();
        RenderingRegistry.registerBlockHandler(new GCMarsBlockRendererBacterialSludge(this.fluidRenderID));
	}

	@Override
	public void postInit(FMLPostInitializationEvent event) 
	{
	}
	
	@Override
	public void registerRenderInformation() 
	{
        RenderingRegistry.registerEntityRenderingHandler(GCMarsEntityCreeperBoss.class, new GCMarsRenderCreeperBoss(new GCMarsModelCreeperBoss(), 10.0F));
        RenderingRegistry.registerEntityRenderingHandler(GCMarsEntitySludgeling.class, new GCMarsRenderSludgeling());
        RenderingRegistry.addNewArmourRendererPrefix("sensor");
        RenderingRegistry.addNewArmourRendererPrefix("sensorox");
        RenderingRegistry.addNewArmourRendererPrefix("quandrium");
        RenderingRegistry.addNewArmourRendererPrefix("quandriumox");
        RenderingRegistry.addNewArmourRendererPrefix("desh");
        RenderingRegistry.addNewArmourRendererPrefix("deshox");
        RenderingRegistry.addNewArmourRendererPrefix("heavy");
        RenderingRegistry.addNewArmourRendererPrefix("jetpack");
        RenderingRegistry.registerEntityRenderingHandler(GCCoreEntityArrow.class, new GCCoreRenderArrow());
		MinecraftForgeClient.preloadTexture("/micdoodle8/mods/galacticraft/mars/client/blocks/mars.png");
		MinecraftForgeClient.preloadTexture("/micdoodle8/mods/galacticraft/mars/client/items/mars.png");
	}

	@Override
	public int getGCFluidRenderID()
	{
		return this.fluidRenderID;
	}

	@Override
    public void spawnParticle(String var1, double var2, double var4, double var6, double var8, double var10, double var12, boolean b)
    {
        Minecraft var14 = FMLClientHandler.instance().getClient();

        if (var14 != null && var14.renderViewEntity != null && var14.effectRenderer != null)
        {
            double var15 = var14.renderViewEntity.posX - var2;
            double var17 = var14.renderViewEntity.posY - var4;
            double var19 = var14.renderViewEntity.posZ - var6;
            Object var21 = null;
            double var22 = 64.0D;

            if (var15 * var15 + var17 * var17 + var19 * var19 < var22 * var22)
            {
            	if (var1.equals("sludgeDrip"))
            	{
            		var21 = new GCMarsEntityDropParticleFX(var14.theWorld, var2, var4, var6, GCMarsBlocks.bacterialSludge);
            	}
            }
            
            if (var21 != null)
            {
                ((EntityFX)var21).prevPosX = ((EntityFX)var21).posX;
                ((EntityFX)var21).prevPosY = ((EntityFX)var21).posY;
                ((EntityFX)var21).prevPosZ = ((EntityFX)var21).posZ;
                var14.effectRenderer.addEffect((EntityFX)var21);
            }
        }
    }
	
    public class ClientPacketHandler implements IPacketHandler
    {
        @Override
        public void onPacketData(NetworkManager manager, Packet250CustomPayload packet, Player p)
        {
            DataInputStream data = new DataInputStream(new ByteArrayInputStream(packet.data));
            int packetType = GCCoreUtil.readPacketID(data);
            EntityPlayer player = (EntityPlayer)p;
            
            if (packetType == 0)
            {
            	
            }
        }
    }
    
    public static boolean handleBacterialMovement(EntityPlayer player)
    {
        return player.worldObj.isMaterialInBB(player.boundingBox.expand(-0.10000000149011612D, -0.4000000059604645D, -0.10000000149011612D), GCMarsBlocks.bacterialSludge);
    }
    
    public static boolean handleLavaMovement(EntityPlayer player)
    {
        return player.worldObj.isMaterialInBB(player.boundingBox.expand(-0.10000000149011612D, -0.4000000059604645D, -0.10000000149011612D), Material.lava);
    }
    
    public static boolean handleWaterMovement(EntityPlayer player)
    {
        return player.worldObj.isMaterialInBB(player.boundingBox.expand(-0.10000000149011612D, -0.4000000059604645D, -0.10000000149011612D), Material.water);
    }
    
    public static boolean handleLiquidMovement(EntityPlayer player)
    {
    	return (handleBacterialMovement(player) || handleLavaMovement(player) || handleWaterMovement(player));
    }
    
    public static class TickHandlerClient implements ITickHandler
    {
    	@Override
    	public void tickStart(EnumSet<TickType> type, Object... tickData)
        {
    		ClientProxyMars.getCurrentTime = System.currentTimeMillis();
    		
    		Minecraft minecraft = FMLClientHandler.instance().getClient();
    		
            WorldClient world = minecraft.theWorld;
            
            EntityClientPlayerMP player = minecraft.thePlayer;
    		
    		if (type.equals(EnumSet.of(TickType.CLIENT)))
            {
        		if (player != null && world != null && player.inventory.armorItemInSlot(2) != null && player.inventory.armorItemInSlot(2).getItem().shiftedIndex == GCMarsItems.jetpack.shiftedIndex && FMLClientHandler.instance().getClient().gameSettings.keyBindJump.pressed && player.posY < 125)
        		{
        			((GCMarsItemJetpack)player.inventory.armorItemInSlot(2).getItem()).setActive();
        			player.motionY += (0.05 + ((player.rotationPitch * 2) / 180) * 0.05);
        			player.fallDistance = 0;
            		world.spawnParticle("largesmoke", player.posX, player.posY - 1D, player.posZ, 0, -0.1, 0);
        		}
    			
    			if (world != null && world.provider instanceof GCMarsWorldProvider)
    			{
    				if (world.provider.getSkyProvider() == null)
                    {
    					world.provider.setSkyProvider(new GCMarsSkyProvider());
                    }
    			}
            }
        }

    	@Override
    	public void tickEnd(EnumSet<TickType> type, Object... tickData) 
    	{
    	}
    	
        public String getLabel()
        {
            return "Galacticraft Mars Client";
        }

    	@Override
    	public EnumSet<TickType> ticks() 
    	{
    		return EnumSet.of(TickType.CLIENT);
    	}
    }
}