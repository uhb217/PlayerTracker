package net.uhb217.playertracker.mixin.client;

import net.minecraft.client.item.CompassAnglePredicateProvider;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.item.CompassItem;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.GlobalPos;
import net.uhb217.playertracker.client.Global;
import net.uhb217.playertracker.utils.NBTConfigUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ModelPredicateProviderRegistry.class)
public abstract class ModelPredicateProviderMixin implements Global {
    @Unique
    private static int sentError = 0;
    @Inject(method = "<clinit>",at = @At(value = "TAIL"))
    private static void injected(CallbackInfo ci){
        ModelPredicateProviderRegistry.register(Items.COMPASS, new Identifier("angle"), new CompassAnglePredicateProvider((world, stack, entity) -> {
                            NbtCompound data = ((NBTConfigUtils) entity).playerTracker$getPersistentData();
                            if(data.contains("player_tracker") && data.getBoolean("player_tracker")) {
                                if (entity.isPlayer() && data.contains("compass_target") && entity.getWorld().getPlayerByUuid(data.getUuid("compass_target")) != null && entity.getWorld().isClient())
                                        return GlobalPos.create(world.getRegistryKey(), entity.getWorld().getPlayerByUuid(data.getUuid("compass_target")).getBlockPos());
                                else if(entity.isPlayer() && sentError <= 0) {
                                    mc.player.sendMessage(Text.literal("Player Tracker: Compass target didn't exist in your dimension 😔").formatted(Formatting.RED));
                                    sentError = 100;
                                }
                                sentError--;
                            }
                            return CompassItem.hasLodestone(stack) ? CompassItem.createLodestonePos(stack.getOrCreateNbt()) : CompassItem.createSpawnPos(world);
                        }));
    }
}