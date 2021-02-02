package xyz.kaydax.ido;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;

@Mod(modid = Ido.ID, name = Ido.NAME, version = Ido.VERSION, acceptedMinecraftVersions = Ido.MC_VERSIONS, useMetadata = true, dependencies = Ido.DEPENDENCIES)
public final class Ido {
    public static final String ID = "ido";
    public static final String NAME = "Ido";
    public static final String VERSION = "2.0.1-B";
    public static final String MC_VERSIONS = "[1.12.2]";
    public static final String DEPENDENCIES = "required:forge@[14.23.5.2811,);required:obfuscate@[0.4.0,);";

    public Ido() {
        MinecraftForge.EVENT_BUS.register(new EventHandler());
    }
}
