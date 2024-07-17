package net.uhb217.playertracker.gui.widgets;

import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.cottonmc.cotton.gui.widget.WButton;
import io.github.cottonmc.cotton.gui.widget.WWidget;
import io.github.cottonmc.cotton.gui.widget.data.HorizontalAlignment;
import io.github.cottonmc.cotton.gui.widget.icon.Icon;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.uhb217.playertracker.client.Global;
import net.uhb217.playertracker.utils.NBTConfigUtils;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class SlotButton extends WButton implements Global {
    private static final int ICON_SPACING = 2;
    private Text toolTip;
    public SlotButton(){
    }
    public SlotButton(@Nullable Icon icon){
        this.setIcon(icon);
    }
    public SlotButton setToolTip(Text toolTip){
        this.toolTip = toolTip;
        return this;
    }
    public static void drawToolTip(DrawContext context, int x, int y, int mouseX, int mouseY, WWidget widget,Text toolTip){
        boolean shouldDrawToolTip = toolTip != null && mouseX >= 0 && mouseX <=widget.getWidth() && mouseY >= 0 && mouseY <= widget.getHeight();
        if (shouldDrawToolTip){
            var client = MinecraftClient.getInstance();
            context.drawTooltip(client.textRenderer,toolTip,x + mouseX,y + mouseY);
        }
    }
    @Override
    public void paint(DrawContext context, int x, int y, int mouseX, int mouseY) {
        Identifier t = new Identifier (modId,"textures/gui/slot_button_off.png");
        if (isWithinBounds(mouseX, mouseY) || isFocused())
            t = new Identifier(modId,"textures/gui/slot_button_on.png");
        context.drawTexture(t,x,y,0.0f,0.5f,getWidth(),getHeight(),getWidth(),getHeight());

        if (getIcon() != null) {
            setIconSize(19);
            getIcon().paint(context, x, y-1+(getHeight()-iconSize)/2, iconSize);
        }

        if (getLabel()!=null) {
            int color = 0xE0E0E0;

            int xOffset = (getIcon() != null && alignment == HorizontalAlignment.LEFT) ? ICON_SPACING+iconSize+ICON_SPACING : 0;
            ScreenDrawing.drawStringWithShadow(context, getLabel().asOrderedText(), alignment, x + xOffset, y + ((getHeight() - 8) / 2), width, color); //LibGuiClient.config.darkMode ? darkmodeColor : color);
        }
        drawToolTip(context,x,y,mouseX,mouseY,this,toolTip);
    }
    public static SlotButton defaultSlotButton(SlotButton button){
        NbtCompound nbt = ((NBTConfigUtils) Objects.requireNonNull(mc.player)).playerTracker$getPersistentData();
        if(button != null){
            button.setToolTip(null).setIcon(null).setOnClick(()-> {
                nbt.putBoolean("player_tracker",false);
                mc.currentScreen.close();
                mc.player.sendMessage(Text.literal(PREFIX + "No target selected ").formatted(Formatting.RED));
            });
            return button;
        }
        return (SlotButton) new SlotButton().setOnClick(()-> {
            nbt.putBoolean("player_tracker",false);
            mc.currentScreen.close();
            mc.player.sendMessage(Text.literal(PREFIX + "No target selected ").formatted(Formatting.RED));
        });
    }
}
