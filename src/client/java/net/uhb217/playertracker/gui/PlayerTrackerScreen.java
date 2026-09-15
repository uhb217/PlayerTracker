package net.uhb217.playertracker.gui;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.uhb217.playertracker.attachment.ModAttachments;
import net.uhb217.playertracker.attachment.TrackerData;
import net.uhb217.playertracker.client.Global;
import net.uhb217.playertracker.utils.NetworkUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * The player selection screen: a paginated grid of player heads (15 per page).
 * Clicking a head makes the compass track that player.
 */
public class PlayerTrackerScreen extends Screen implements Global {
    private static final int COLUMNS = 5;
    private static final int ROWS = 3;
    private static final int SLOTS_PER_PAGE = COLUMNS * ROWS;
    private static final int PITCH = TrackerSlotWidget.SIZE + 2;

    private final Screen parent;
    private final List<TrackerSlotWidget> slots = new ArrayList<>();
    private ButtonWidget leftButton;
    private ButtonWidget rightButton;
    private int page = 0;

    public PlayerTrackerScreen(Screen parent) {
        super(Text.translatable("screen.playertracker.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        this.slots.clear();

        int gridWidth = COLUMNS * PITCH - 2;
        int gridHeight = ROWS * PITCH - 2;
        int startX = (this.width - gridWidth) / 2;
        int startY = (this.height - gridHeight) / 2 - 12;

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLUMNS; col++) {
                TrackerSlotWidget slot = new TrackerSlotWidget(startX + col * PITCH, startY + row * PITCH);
                this.slots.add(slot);
                this.addDrawableChild(slot);
            }
        }

        int navY = startY + gridHeight + 6;
        this.leftButton = ButtonWidget.builder(Text.literal("<"), button -> {
                    if (this.page > 0) {
                        this.page--;
                        this.refreshSlots();
                    }
                })
                .dimensions(startX, navY, 20, 20)
                .build();
        this.rightButton = ButtonWidget.builder(Text.literal(">"), button -> {
                    this.page++;
                    this.refreshSlots();
                })
                .dimensions(startX + gridWidth - 20, navY, 20, 20)
                .build();
        ButtonWidget resetButton = ButtonWidget.builder(Text.translatable("screen.playertracker.reset"), button -> {
                    if (mc.player != null) {
                        mc.player.setAttached(ModAttachments.TRACKER, TrackerData.empty());
                        mc.player.sendMessage(Text.literal(PREFIX + "§3The compass sets back to normal"), false);
                    }
                    this.close();
                })
                .dimensions(startX + 22, navY, 44, 20)
                .build();
        ButtonWidget closeButton = ButtonWidget.builder(Text.translatable("screen.playertracker.close"), button -> this.close())
                .dimensions(startX + gridWidth - 66, navY, 44, 20)
                .build();

        this.addDrawableChild(this.leftButton);
        this.addDrawableChild(this.rightButton);
        this.addDrawableChild(resetButton);
        this.addDrawableChild(closeButton);

        this.refreshSlots();
    }

    private void refreshSlots() {
        ItemStack[] heads = NetworkUtils.getPlayersHeads();
        String[] names = NetworkUtils.getPlayersNames();

        int index = this.page * SLOTS_PER_PAGE;
        for (TrackerSlotWidget slot : this.slots) {
            if (index < heads.length && index < names.length && names[index] != null && !names[index].isEmpty()
                    && heads[index] != null && !heads[index].isEmpty()) {
                String playerName = names[index];
                ItemStack head = heads[index];
                slot.setPlayer(head, playerName, () -> this.trackPlayer(playerName));
            } else {
                slot.clear();
            }
            index++;
        }

        this.leftButton.active = this.page > 0;
        this.rightButton.active = (this.page + 1) * SLOTS_PER_PAGE < Math.max(heads.length, names.length);
    }

    private void trackPlayer(String playerName) {
        UUID uuid = NetworkUtils.getPlayerUuidFromName(playerName);
        if (uuid != null && mc.player != null) {
            mc.player.setAttached(ModAttachments.TRACKER, TrackerData.tracking(uuid));
            this.close();
            mc.player.sendMessage(Text.literal(PREFIX + "§3The compass target set to: " + playerName), false);
        } else if (mc.player != null) {
            mc.player.sendMessage(Text.literal(PREFIX + "Compass target isn't in the game or in this dimension.").formatted(Formatting.RED), false);
        }
    }

    @Override
    public void close() {
        mc.setScreen(this.parent);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
