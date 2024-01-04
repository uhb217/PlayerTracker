package net.uhb217.glowingentity.gui;

import io.github.cottonmc.cotton.gui.GuiDescription;
import io.github.cottonmc.cotton.gui.client.CottonClientScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class TestScreen extends CottonClientScreen {
    public TestScreen(GuiDescription description) {
        super(description);
    }
}
