package xyz.kaydax.ido;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.*;

@Config(modid = Ido.ID, name = Ido.ID + "/" + Ido.ID)
public final class Configuration {
    @Name("Toggle Crawling")
    @Comment("This allows you enable / disable crawling (Note: This breaks being able to crawl when swimming into one block tall spaces with no water if disabled)")
    public static boolean crawlToggle = true;

    @Name("Toggle Sneaking Change")
    @Comment("This allows you enable / disable the changes to sneaking")
    public static boolean sneakToggle = true;

    @Name("Toggle Swimming")
    @Comment("This allows you enable / disable swimming")
    public static boolean swimToggle = true;
}
