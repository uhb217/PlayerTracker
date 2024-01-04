package net.uhb217.glowingentity.gui;

import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.cottonmc.cotton.gui.impl.LibGuiCommon;
import io.github.cottonmc.cotton.gui.impl.client.NarrationMessages;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WLabel;
import io.github.cottonmc.cotton.gui.widget.WToggleButton;
import io.github.cottonmc.cotton.gui.widget.WWidget;
import io.github.cottonmc.cotton.gui.widget.data.InputResult;
import io.github.cottonmc.cotton.gui.widget.data.Texture;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class TestGUI extends LightweightGuiDescription {
    public TestGUI() {
        WGridPanel root = new WGridPanel();
        setRootPanel(root);
        root.setSize(300, 200);
        WToggleButton button = new WToggleButton(Text.literal("Off"));
        button.setOnToggle(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) {
                if (aBoolean)
                    button.setLabel(Text.literal("On"));
                else
                    button.setLabel(Text.literal("Off"));
            }
        });
        root.add(button,1,0);
        WToggleButton button1 = new WToggleButton(Text.literal("Off"));
        button1.setOnToggle(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) {
                if (aBoolean)
                    button1.setLabel(Text.literal("On"));
                else
                    button1.setLabel(Text.literal("Off"));
            }
        });
        root.add(button1,1,1);
    }
}
