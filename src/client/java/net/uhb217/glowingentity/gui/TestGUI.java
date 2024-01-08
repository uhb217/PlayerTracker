package net.uhb217.glowingentity.gui;

import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.TooltipBuilder;
import io.github.cottonmc.cotton.gui.widget.WPlainPanel;
import io.github.cottonmc.cotton.gui.widget.WSprite;
import io.github.cottonmc.cotton.gui.widget.WToggleButton;
import io.github.cottonmc.cotton.gui.widget.data.Insets;
import io.github.cottonmc.cotton.gui.widget.data.Texture;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.uhb217.glowingentity.GlowingEntity;
import net.uhb217.glowingentity.utils.IEntityDataSaver;

import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
public class TestGUI extends LightweightGuiDescription {
    public TestGUI(MinecraftClient client) {
        IEntityDataSaver playerData = (IEntityDataSaver) client.player;
        NbtCompound nbt = playerData.getPersistentData();
        WPlainPanel root = new WPlainPanel();
        setRootPanel(root);
        root.setSize(144,190);
        root.setInsets(new Insets(0,0,0,0));

        WSprite glowingEntities = new WSprite(new Texture(new Identifier(GlowingEntity.MOD_ID,"textures/gui/glow.png")));
        glowingEntities.addTooltip(new TooltipBuilder().add(Text.literal("Hello")));
        root.add(glowingEntities,5,10);
        WToggleButton button = new WToggleButton(Text.literal("Off").formatted(Formatting.RED));
        if (nbt.contains("glow") && nbt.getInt("glow") > 0){
            button.setToggle(true);
            button.setLabel(Text.literal("On").formatted(Formatting.GREEN));
        }
        button.setOnToggle(aBoolean -> {
            if (aBoolean){
                button.setLabel(Text.literal("On").formatted(Formatting.GREEN));
                if (nbt.contains("last_glow"))
                    nbt.putInt("glow", nbt.getInt("last_glow"));
                else
                    nbt.putInt("glow", 15);
            }
            else{
                button.setLabel(Text.literal("Off").formatted(Formatting.RED));
                if (nbt.contains("glow"))
                    nbt.putInt("last_glow",nbt.getInt("glow"));
                nbt.putInt("glow",-1);
            }
        });
        root.add(button,28,11);
    }
}
