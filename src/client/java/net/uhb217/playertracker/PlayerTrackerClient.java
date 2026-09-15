package net.uhb217.playertracker;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.text.Text;
import net.uhb217.playertracker.attachment.ModAttachments;
import net.uhb217.playertracker.attachment.TrackerData;
import net.uhb217.playertracker.client.Global;
import net.uhb217.playertracker.gui.PlayerTrackerScreen;
import net.uhb217.playertracker.utils.NetworkUtils;
import org.lwjgl.glfw.GLFW;

import java.util.UUID;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class PlayerTrackerClient implements ClientModInitializer, Global {

    @Override
    public void onInitializeClient() {
        ModAttachments.init();
        /* registering commands */
        registerCommands();
        /* registering keybinds */
        registerKeyBindings();
    }

    private void registerCommands() {
        // TEMPORARY debug command - remove before release
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(ClientCommandManager.literal("ptdebug")
                .executes(context -> {
                    var src = context.getSource();
                    src.sendFeedback(Text.literal("nh=" + (mc.getNetworkHandler() != null)));
                    if (mc.getNetworkHandler() != null) {
                        for (var e : mc.getNetworkHandler().getPlayerList()) {
                            src.sendFeedback(Text.literal("tab: name=[" + e.getProfile().name() + "] uuid=" + e.getProfile().id()));
                        }
                    }
                    src.sendFeedback(Text.literal("world=" + (mc.world != null)));
                    if (mc.world != null) {
                        for (var p : mc.world.getPlayers()) {
                            src.sendFeedback(Text.literal("ent: name=[" + p.getName().getString() + "] uuid=" + p.getUuid()));
                        }
                    }
                    src.sendFeedback(Text.literal("lookup=" + NetworkUtils.getPlayerUuidFromName("Player235")));
                    return SINGLE_SUCCESS;
                })));
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(ClientCommandManager.literal("set_target")
                .then(ClientCommandManager.argument("target", StringArgumentType.string())
                        .suggests(NetworkUtils.playerNameSuggestions)
                        .executes(context -> {
                            String targetName = StringArgumentType.getString(context, "target");
                            UUID targetUUID = NetworkUtils.getPlayerUuidFromName(targetName);
                            ClientPlayerEntity player = context.getSource().getClient().player;
                            if (targetUUID != null && player != null) {
                                player.setAttached(ModAttachments.TRACKER, TrackerData.tracking(targetUUID));
                                context.getSource().sendFeedback(Text.literal(PREFIX + "§3The compass target set to: " + targetName));
                            } else {
                                context.getSource().sendError(Text.literal(PREFIX + "Compass target isn't in the game or in this dimension."));
                            }
                            return SINGLE_SUCCESS;
                        }))));
    }

    private void registerKeyBindings() {
        KeyBinding openScreenKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.playertracker.open_screen",
                GLFW.GLFW_KEY_R,
                KeyBinding.Category.MISC
        ));
//        this method will be executed every tick and open the gui when the "openScreenKey" is pressed ;)
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (openScreenKey.wasPressed() && client.world != null) {
                client.setScreen(new PlayerTrackerScreen(client.currentScreen));
            }
        });
    }
}
