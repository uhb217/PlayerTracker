package net.uhb217.glowingentity;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.uhb217.glowingentity.gui.TestGUI;
import net.uhb217.glowingentity.gui.TestScreen;
import net.uhb217.glowingentity.utils.IEntityDataSaver;
import net.uhb217.glowingentity.utils.KeyInputHandler;

import java.util.List;
import java.util.Objects;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class GlowingEntityClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		//register commands
		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(literal("entity_glowing")
				.then(argument("glowing", IntegerArgumentType.integer())
						.executes(context -> {
							int value = IntegerArgumentType.getInteger(context, "glowing");
							IEntityDataSaver playerData = (IEntityDataSaver) context.getSource().getPlayer();
							playerData.getPersistentData().putInt("glow",value);
							if (value > 15)
								value = 15;
							String glow = value < 0 ? "Normal" : String.valueOf(value);
							context.getSource().sendFeedback(Text.literal("The Entity Glowing set to: " + glow).formatted(Formatting.DARK_AQUA));
							return value;
						}))));
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