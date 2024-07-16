package net.uhb217.playertrackermod;

import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.uhb217.playertrackermod.utils.IEntityDataSaver;
import net.uhb217.playertrackermod.utils.KeyInputHandler;

import java.util.List;
import java.util.Objects;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class PlayerTrackerModClient implements ClientModInitializer {


    @Override
    public void onInitializeClient() {
        //register commands
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(literal("set_target")
                .then(argument("target", StringArgumentType.string())
                        .executes(context -> {
                            List<AbstractClientPlayerEntity> targets = context.getSource().getWorld().getPlayers();
                            PlayerEntity target = null;
                            for (AbstractClientPlayerEntity t : targets) {
                                if (Objects.equals(t.getName().getString(), StringArgumentType.getString(context, "target")))
                                    target = t;
                            }
                            if (target != null) {
                                IEntityDataSaver playerData = (IEntityDataSaver) context.getSource().getClient().player;
                                playerData.getPersistentData().putUuid("compass_target", target.getUuid());
                            }
                            return 0;
                        }))));
        //register KeyBiding
        KeyInputHandler.register();
    }
}