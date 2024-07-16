package net.uhb217.playertracker;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.uhb217.playertracker.gui.GUI;
import net.uhb217.playertracker.gui.TestScreen;
import net.uhb217.playertracker.utils.NBTConfigUtils;
import net.uhb217.playertracker.utils.NetworkUtils;
import org.lwjgl.glfw.GLFW;

import java.util.Objects;
import java.util.UUID;


import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class PlayerTrackerClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        /* registering commands */
        registerCommands();
        /* registering keybinds */
        registerKeyBindings();
    }

    private void registerCommands() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(ClientCommandManager.literal("set_target")
                .then(ClientCommandManager.argument("target", StringArgumentType.string())
                        .suggests(NetworkUtils.playerNameSuggestions)
                        .executes(context -> {
                            String targetName = StringArgumentType.getString(context, "target");
                            UUID targetUUID = NetworkUtils.getPlayerUuidFromName(targetName);
                            if (targetUUID != null) {
                                NBTConfigUtils playerData = (NBTConfigUtils) context.getSource().getClient().player;
                                Objects.requireNonNull(playerData).playerTracker$getPersistentData().putUuid("compass_target", targetUUID);
                            } else {
                                context.getSource().sendError(Text.literal("Player not found or not in the same dimension."));
                            }
                            return SINGLE_SUCCESS;
                        }))));
    }

    private void registerKeyBindings() {
        KeyBinding openLSScreenKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "Open the player tracker screen",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_R,
                "Player Tracker Keys"
        ));
//        this method will be executed every tick and open the gui when the "openLSScreenKey" is pressed ;)
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (openLSScreenKey.wasPressed() && client.world != null) {
                client.setScreen(new TestScreen(new GUI()));
            }
        });
    }
}
