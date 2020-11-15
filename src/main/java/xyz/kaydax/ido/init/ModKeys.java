package xyz.kaydax.ido.init;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class ModKeys 
{
	public static KeyBinding crawling;
	
	public static void init()
	{
		crawling = new KeyBinding("Crawl", Keyboard.KEY_C, "Ido Bekos Crawl");
	}
	
	public static void register()
	{
		ClientRegistry.registerKeyBinding(crawling);
	}
}
