package net.uhb217.playertracker.mixin.client;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.HeldItemContext;
import net.minecraft.util.math.GlobalPos;
import net.uhb217.playertracker.attachment.ModAttachments;
import net.uhb217.playertracker.attachment.TrackerData;
import net.uhb217.playertracker.client.Global;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Makes compasses point at the tracked player.
 *
 * <p>Since 1.21.4 compasses are rendered through data-driven item models and the
 * needle angle comes from {@code CompassState.Target#getPosition}. That method is
 * abstract on the {@code Target} enum itself and implemented separately by each
 * target (none, lodestone, spawn, recovery), so this mixin targets all four
 * implementations. Overriding them replaces the old model-predicate hack.
 */
@Mixin(targets = {
        "net.minecraft.client.render.item.property.numeric.CompassState$Target$1",
        "net.minecraft.client.render.item.property.numeric.CompassState$Target$2",
        "net.minecraft.client.render.item.property.numeric.CompassState$Target$3",
        "net.minecraft.client.render.item.property.numeric.CompassState$Target$4"
})
public abstract class CompassTargetMixin implements Global {
    @Unique
    private static int playerTracker$muteTicks = 0;

    @Inject(method = "getPosition", at = @At("HEAD"), cancellable = true)
    private void playerTracker$pointAtTrackedPlayer(ClientWorld world, ItemStack stack, HeldItemContext context,
                                                    CallbackInfoReturnable<GlobalPos> cir) {
        if (context.getEntity() == null) {
            return;
        }

        TrackerData data = context.getEntity().getAttached(ModAttachments.TRACKER);
        if (data == null || !data.tracking() || data.target().isEmpty()) {
            return;
        }

        PlayerEntity target = world.getPlayerByUuid(data.target().get());
        if (target == null && data.target().get().equals(context.getEntity().getUuid())
                && context.getEntity() instanceof PlayerEntity holder) {
            // The tracked player is the compass holder themselves; the world
            // lookup may not include them, so use their position directly.
            target = holder;
        }
        if (target != null) {
            cir.setReturnValue(GlobalPos.create(world.getRegistryKey(), target.getBlockPos()));
        } else if (context.getEntity() == mc.player && mc.player != null) {
            // The target is offline or in another dimension: warn, but throttled so we
            // don't spam chat (this runs every frame for every rendered compass).
            if (playerTracker$muteTicks <= 0) {
                mc.player.sendMessage(Text.literal(PREFIX + "Compass target isn't in the game or in this dimension.").formatted(Formatting.RED), false);
                playerTracker$muteTicks = 200;
            }
            playerTracker$muteTicks--;
        }
    }
}
