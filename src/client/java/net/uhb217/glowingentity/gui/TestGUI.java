package net.uhb217.glowingentity.gui;

import com.mojang.authlib.GameProfile;
import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.*;
import io.github.cottonmc.cotton.gui.widget.data.Insets;
import io.github.cottonmc.cotton.gui.widget.data.Texture;
import io.github.cottonmc.cotton.gui.widget.icon.ItemIcon;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.uhb217.glowingentity.GlowingEntity;
import net.uhb217.glowingentity.gui.widgeds.Button;
import net.uhb217.glowingentity.utils.IEntityDataSaver;

import java.util.List;

@Environment(EnvType.CLIENT)
public class TestGUI extends LightweightGuiDescription {
    private final Text OFF = Text.literal("Off").formatted(Formatting.RED);
    private final Text ON = Text.literal("On").formatted(Formatting.GREEN);
    public TestGUI(MinecraftClient client) {
        NbtCompound nbt = ((IEntityDataSaver) client.player).getPersistentData();
        WPlainPanel root = new WPlainPanel().setInsets(new Insets(0, 0, 0, 0));
        this.setRootPanel(root);
        root.setSize(144, 190);

        createGlowingEntityModule(nbt, root);
        createPlayerTrackerModule(nbt,root,client);
    }

    public void createGlowingEntityModule(NbtCompound nbt, WPlainPanel root) {
        WSprite glowingEntities = new WSprite(new Texture(new Identifier(GlowingEntity.MOD_ID, "textures/gui/glow.png")));
        root.add(glowingEntities, 5, 10);
        WToggleButton button = new WToggleButton(Text.literal("Off").formatted(Formatting.RED));
        if (nbt.contains("glow") && nbt.getInt("glow") > 0) {
            button.setToggle(true);
            button.setLabel(ON);
        }
        button.setOnToggle(aBoolean -> {
            if (aBoolean) {
                button.setLabel(ON);
                if (nbt.contains("last_glow"))
                    nbt.putInt("glow", nbt.getInt("last_glow"));
                else
                    nbt.putInt("glow", 15);
            } else {
                button.setLabel(OFF);
                if (nbt.contains("glow"))
                    nbt.putInt("last_glow", nbt.getInt("glow"));
                nbt.putInt("glow", -1);
            }
        });
        root.add(button, 28, 11);
    }
    public void createPlayerTrackerModule(NbtCompound nbt, WPlainPanel root,MinecraftClient mc){
        WSprite playerTracker = new WSprite(new Texture(new Identifier(GlowingEntity.MOD_ID, "textures/gui/player_tracker.png")));
        root.add(playerTracker, 5, 31);
        WToggleButton button = new WToggleButton(Text.literal("Off").formatted(Formatting.RED));
        if (nbt.contains("player_tracker") && nbt.getBoolean("player_tracker")){
            button.setToggle(true);
            button.setLabel(ON);
        }
        button.setOnToggle(aBoolean -> {
            if (aBoolean){
                button.setLabel(ON);
                nbt.putBoolean("player_tracker",true);
                mc.setScreen(new TestScreen(new PlayerTrackerSetTargetGUI(mc)));
            }else{
                button.setLabel(OFF);
                nbt.putBoolean("player_tracker",false);
            }});
        root.add(button,28,32);
    }
    public static class PlayerTrackerSetTargetGUI extends LightweightGuiDescription{
        public PlayerTrackerSetTargetGUI(MinecraftClient mc){
            WPlainPanel root = new WPlainPanel().setInsets(new Insets(2,2,2,2));
            this.setRootPanel(root);
            root.setSize(140, 190);


//            WItemSlot itemSlot = WItemSlot.of(new Inventory() {
//                @Override
//                public int size() {
//                    return 0;
//                }
//
//                @Override
//                public boolean isEmpty() {
//                    return false;
//                }
//
//                @Override
//                public ItemStack getStack(int slot) {
//                    return null;
//                }
//
//                @Override
//                public ItemStack removeStack(int slot, int amount) {
//                    return null;
//                }
//
//                @Override
//                public ItemStack removeStack(int slot) {
//                    return null;
//                }
//
//                @Override
//                public void setStack(int slot, ItemStack stack) {
//
//                }
//
//                @Override
//                public void markDirty() {
//
//                }
//
//                @Override
//                public boolean canPlayerUse(PlayerEntity player) {
//                    return false;
//                }
//
//                @Override
//                public void clear() {
//
//                }
//            }, 0, 7, 3).setIcon(new ItemIcon(getPLayersHeads(mc)[0]));
            Button button = new Button(new ItemIcon(getPLayersHeads(mc)[0]),Text.literal("ToolTip!").formatted(Formatting.GREEN));
//            WButton button = new WButton(new TextureIcon(new Texture(new Identifier(GlowingEntity.MOD_ID, "textures/gui/player_tracker.png"))));
            button.setOnClick(()-> mc.player.sendMessage(Text.literal("Clicked!"))).setIconSize(13);
            Button button1 = new Button(new ItemIcon(getPLayersHeads(mc)[0]),Text.literal("ToolTip 0.2!").formatted(Formatting.GREEN));
            button1.setOnClick(()-> mc.player.sendMessage(Text.literal("Clicked!"))).setIconSize(13);
            root.add(button,5,5);
//            root.add(button1,25,5);

        }

        public static ItemStack[] getPLayersHeads(MinecraftClient client) {
            List<AbstractClientPlayerEntity> players = client.world.getPlayers();
            ItemStack[] skulls = new ItemStack[players.size()];
            for (int i = 0; i < skulls.length; i++) {
                skulls[i] = Items.PLAYER_HEAD.getDefaultStack();
                skulls[i].setNbt(getNbtFromProfile(players.get(i).getGameProfile()));
            }
            return skulls;
        }
        public static NbtCompound getNbtFromProfile(GameProfile profile) {
            // apply player's skin to head
            NbtCompound nbtCompound = new NbtCompound();
            nbtCompound.put("SkullOwner", NbtHelper.writeGameProfile(new NbtCompound(), profile));

            // set name of item to player's name
            NbtCompound displayTag = new NbtCompound();
            displayTag.putString("Name", profile.getName());

            nbtCompound.put("display", displayTag);

            return nbtCompound;
        }
    }
}
