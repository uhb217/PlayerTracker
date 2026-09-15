package net.uhb217.playertracker.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.uhb217.playertracker.client.Global;
import org.jetbrains.annotations.Nullable;

/**
 * A single player slot in the tracker screen: draws the mod's slot background
 * texture with the player's head on top, and runs an action when clicked.
 */
public class TrackerSlotWidget extends ClickableWidget implements Global {
    public static final int SIZE = 25;
    private static final Identifier SLOT_OFF = Identifier.of(modId, "textures/gui/slot_button_off.png");
    private static final Identifier SLOT_ON = Identifier.of(modId, "textures/gui/slot_button_on.png");

    // ClickableWidget's own x/y fields are private in this version, so the
    // position is kept here as well for rendering.
    private final int slotX;
    private final int slotY;
    private ItemStack head = ItemStack.EMPTY;
    @Nullable
    private Text toolTip;
    private Runnable onPress = () -> {
    };

    public TrackerSlotWidget(int x, int y) {
        super(x, y, SIZE, SIZE, Text.empty());
        this.slotX = x;
        this.slotY = y;
    }

    /** Shows a player in this slot. */
    public void setPlayer(ItemStack head, String name, Runnable onPress) {
        this.head = head;
        this.toolTip = Text.literal(name).formatted(Formatting.BLUE);
        this.onPress = onPress;
        this.active = true;
    }

    /** Empties the slot (used for padding and pages without enough players). */
    public void clear() {
        this.head = ItemStack.EMPTY;
        this.toolTip = null;
        this.onPress = () -> {
        };
        this.active = false;
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        context.drawTexture(RenderPipelines.GUI_TEXTURED, this.isHovered() && this.active ? SLOT_ON : SLOT_OFF,
                this.slotX, this.slotY, 0, 0, this.width, this.height, this.width, this.height);
        if (!this.head.isEmpty()) {
            context.drawItem(this.head, this.slotX + 4, this.slotY + 4);
        }
        if (this.toolTip != null && this.isHovered()) {
            context.drawTooltip(MinecraftClient.getInstance().textRenderer, this.toolTip, mouseX, mouseY);
        }
    }

    @Override
    public void onClick(Click click, boolean doubled) {
        if (this.active) {
            this.onPress.run();
        }
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
        this.appendDefaultNarrations(builder);
    }
}
