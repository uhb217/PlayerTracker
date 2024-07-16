package net.uhb217.playertrackermod.utils;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.uhb217.playertrackermod.gui.GUI;
import net.uhb217.playertrackermod.gui.TestScreen;
import org.lwjgl.glfw.GLFW;

public class KeyInputHandler {
    public static final String KEY_CATEGORY_LS = "Player Tracker Keys";
    public static final String KEY_OPEN_LS_SCREEN = "Open the player tracker screen";
    public static KeyBinding openLSScreenKey;

    public static void registerKeyInputs() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (openLSScreenKey.wasPressed() && client.world.isClient()) {
                client.setScreen(new TestScreen(new GUI(client)));
            }
        });
    }

    public static void register() {
        openLSScreenKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                KEY_OPEN_LS_SCREEN,
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_R,
                KEY_CATEGORY_LS
        ));
        registerKeyInputs();
    }
}
