package net.uhb217.playertracker.mixin.client;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.uhb217.playertracker.utils.NBTConfigUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class ModEntityDataSaverMixin implements NBTConfigUtils {
    @Unique
    private NbtCompound persistentData;

    @Override
    public NbtCompound playerTracker$getPersistentData() {
        if(this.persistentData == null) {
            this.persistentData = new NbtCompound();
        }
        return persistentData;
    }

    @Inject(method = "writeNbt", at = @At("HEAD"))
    protected void injectWriteMethod(NbtCompound nbt, CallbackInfoReturnable<NbtCompound> cir) {
        if(persistentData != null) {
            nbt.put("player_tracker.uhb_data", persistentData);
        }
    }

    @Inject(method = "readNbt", at = @At("HEAD"))
    protected void injectReadMethod(NbtCompound nbt, CallbackInfo info) {
        if (nbt.contains("player_tracker.uhb_data", 10)) {
            persistentData = nbt.getCompound("player_tracker.uhb_data");
        }
    }
}
