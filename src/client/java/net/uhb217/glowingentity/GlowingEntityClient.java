package net.uhb217.glowingentity;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.advancement.AdvancementsScreen;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.uhb217.glowingentity.gui.TestGUI;
import net.uhb217.glowingentity.gui.TestScreen;
import net.uhb217.glowingentity.utils.KeyInputHandler;

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
		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(literal("my_screen")
				.executes(context -> {
					if (context.getSource().getWorld().isClient()){
						context.getSource().getClient().setScreen(new TestScreen(new TestGUI()));
					}
					return 1;
				})));
		//register KeyBiding
		KeyInputHandler.register();
	}
}