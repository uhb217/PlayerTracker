package net.uhb217.glowingentity.gui.widgeds;

import io.github.cottonmc.cotton.gui.widget.WButton;
import io.github.cottonmc.cotton.gui.widget.WWidget;
import io.github.cottonmc.cotton.gui.widget.icon.Icon;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public class Button extends WButton {
    private Text toolTip;
    public Button(@Nullable Icon icon,Text toolTip){
        this.setIcon(icon);
        this.toolTip = toolTip;
    }
    public void setToolTip(Text toolTip){
        this.toolTip = toolTip;
    }
    public void drawToolTip(DrawContext context, int x, int y, int mouseX, int mouseY, WWidget widget){
        boolean shouldDrawToolTip = mouseX >= 0 && mouseX <=widget.getWidth() && mouseY >= 0 && mouseY <= widget.getHeight();
        if (shouldDrawToolTip){
            var client = MinecraftClient.getInstance();
            context.drawTooltip(client.textRenderer,toolTip,x + mouseX,y + mouseY);
        }else
            System.out.println("X: "+mouseX+"  Y: "+mouseY);
    }
    @Override
    public void paint(DrawContext context, int x, int y, int mouseX, int mouseY) {
        super.paint(context, x, y, mouseX, mouseY);
        drawToolTip(context,x,y,mouseX,mouseY,this);
    }
}
