package xyz.kaydax.ido.util.handlers;

import java.io.File;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import xyz.kaydax.ido.IdoMain;
import xyz.kaydax.ido.util.Referance;

public class ConfigHandler
{
  public static Configuration config;
  
  public static boolean SWIM_TOGGLE = true;
  public static boolean SNEAK_TOGGLE = true;
  public static boolean CRAWL_TOGGLE = true;
  
  public static void init(File file)
  {
    config = new Configuration(file);
    String category;
    
    category = "Mod Features";
    config.addCustomCategoryComment(category, "Allows you to enable or disable parts of Ido");
    SWIM_TOGGLE = config.getBoolean("Toggle Swimming", category, true, "This allows you enable / disable swimming");
    SNEAK_TOGGLE = config.getBoolean("Toggle Sneaking Change", category, true, "This allows you enable / disable the changes to sneaking");
    CRAWL_TOGGLE = config.getBoolean("Toggle Crawling", category, true, "This allows you enable / disable crawling (Note: This breaks being able to crawl when swimming into one block tall spaces with no water if disabled)");
  
    config.save();
  }
  
  public static void regConfig(FMLPreInitializationEvent event)
  {
    IdoMain.config = new File(event.getModConfigurationDirectory() + "/" + Referance.MODID);
    IdoMain.config.mkdirs();
    init(new File(IdoMain.config.getPath(), Referance.MODID + ".cfg"));
  }
}
