package net.uhb217.playertracker.client;

import net.minecraft.client.MinecraftClient;

public interface Global {
    MinecraftClient mc = MinecraftClient.getInstance();
    String modId = "playertracker";
    String PREFIX = "§6Player Tracker:§r ";
}
