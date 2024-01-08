package net.uhb217.glowingentity.utils;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.uhb217.glowingentity.gui.TestGUI;
import net.uhb217.glowingentity.gui.TestScreen;
import org.lwjgl.glfw.GLFW;

public class KeyInputHandler {
    public static final String KEY_CATEGORY_LS = "Lightning Strike Keys";
    public static final String KEY_OPEN_LS_SCREEN = "Open Lightning Strike screen";
    public static KeyBinding openLSScreenKey;
    public static void registerKeyInputs(){
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (openLSScreenKey.wasPressed() && client.world.isClient()){
//                client.setScreen(new TestScreen(new TestGUI(client)));
                IEntityDataSaver data = (IEntityDataSaver) client.player;
                if (data.getPersistentData().contains("compass_target"))
                    client.player.sendMessage(Text.literal(data.getPersistentData().getUuid("compass_target").toString()));
                else client.player.sendMessage(Text.literal("No target found"));
            }

        });
    }
    public static void register(){
        openLSScreenKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                KEY_OPEN_LS_SCREEN,
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_R,
                KEY_CATEGORY_LS
        ));
        registerKeyInputs();
    }
}
