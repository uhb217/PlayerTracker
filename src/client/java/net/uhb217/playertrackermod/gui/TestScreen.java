package net.uhb217.playertrackermod.gui;

import io.github.cottonmc.cotton.gui.GuiDescription;
import io.github.cottonmc.cotton.gui.client.CottonClientScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;

@Environment(EnvType.CLIENT)
public class TestScreen extends CottonClientScreen {
    private Screen parent;
    public TestScreen(GuiDescription description) {
        super(description);
    }
    public TestScreen setParent(Screen parent) {
        this.parent = parent;
        return this;
    }
    @Override
    public void close() {
        client.setScreen(parent);
    }
}
