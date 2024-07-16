package net.uhb217.playertracker.gui.widgets;

import io.github.cottonmc.cotton.gui.widget.WSprite;
import io.github.cottonmc.cotton.gui.widget.data.Texture;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;


public class Sprite extends WSprite {
    private Text toolTip = null;
    public Sprite(Texture texture) {
        super(texture);
    }

    public Sprite(Identifier image) {
        super(image);
    }

    public Sprite(Identifier image, float u1, float v1, float u2, float v2) {
        super(image, u1, v1, u2, v2);
    }

    public Sprite(int frameTime, Identifier... frames) {
        super(frameTime, frames);
    }

    public Sprite(int frameTime, Texture... frames) {
        super(frameTime, frames);
    }
    public Sprite setToolTip(Text toolTip){
        this.toolTip = toolTip;
        return this;
    }

    @Override
    public void paint(DrawContext context, int x, int y, int mouseX, int mouseY) {
        super.paint(context, x, y, mouseX, mouseY);
        SlotButton.drawToolTip(context,x,y,mouseX,mouseY,this,toolTip);
    }
}
