package net.uhb217.glowingentity.mixin.client;

import net.minecraft.client.item.CompassAnglePredicateProvider;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ModelPredicateProviderRegistry.class)
public abstract class ModelPredicateProviderMixin {


    @Inject(method = "<clinit>",at = @At(value = "TAIL"))
    private static void injected(CallbackInfo ci){
        ModelPredicateProviderRegistry.register(
                Items.COMPASS,
                new Identifier("angle"),
                new CompassAnglePredicateProvider(
                        (world, stack, entity) -> GlobalPos.create(world.getRegistryKey(),new BlockPos(100,100,100))
        ));
    }
}