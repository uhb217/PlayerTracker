package net.uhb217.playertracker.gui;

import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WPlainPanel;
import io.github.cottonmc.cotton.gui.widget.data.Insets;
import io.github.cottonmc.cotton.gui.widget.icon.ItemIcon;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.uhb217.playertracker.client.Global;
import net.uhb217.playertracker.gui.widgets.Button;
import net.uhb217.playertracker.gui.widgets.SlotButton;
import net.uhb217.playertracker.utils.NBTConfigUtils;
import net.uhb217.playertracker.utils.NetworkUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Environment(EnvType.CLIENT)
public class GUI extends LightweightGuiDescription implements Global {
    private int page = 0;

    public GUI() {
        WPlainPanel root = new WPlainPanel().setInsets(new Insets(0, 0, 0, 0));
        this.setRootPanel(root);
        root.setSize(118, 70);

        ArrayList<SlotButton> buttons = new ArrayList<>();
        int index = 0;
        ItemStack[] playerHeads = NetworkUtils.getPlayersHeads();
        String[] playerNames = NetworkUtils.getPlayersNames();
        NbtCompound nbt = ((NBTConfigUtils) Objects.requireNonNull(mc.player)).playerTracker$getPersistentData();

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 3; j++) {
                buttons.add(new SlotButton());
                buttons.get(index).setSize(25, 25);

                if ((index < playerHeads.length) && playerNames.length > 1) {
                    String playerName = playerNames[index];
                    buttons.get(index).setToolTip(Text.literal(playerName).formatted(Formatting.BLUE)).setIcon(new ItemIcon(playerHeads[index]));
                    buttons.get(index).setOnClick(() -> {
                        UUID uuid = NetworkUtils.getPlayerUuidFromName(playerName);
                        if (uuid != null) {
                            nbt.putBoolean("player_tracker", true);
                            nbt.putUuid("compass_target", uuid);
                            Objects.requireNonNull(mc.currentScreen).close();
                            mc.player.sendMessage(Text.literal(PREFIX + "§3The compass target set to: " + playerName));
                        } else {
                            mc.player.sendMessage(Text.literal(PREFIX + "Compass target isn't in the game or in this dimension.").formatted(Formatting.RED));
                        }
                    });
                } else {
                    buttons.set(index, SlotButton.defaultSlotButton(null));
                }
                root.add(buttons.get(index), 15 + (20 * i), 5 + (20 * j));
                index++;
            }
        }

        Button left = new Button(new Identifier("textures/gui/sprites/transferable_list/unselect.png"))
                .setSelectedTexture(new Identifier("textures/gui/sprites/transferable_list/unselect_highlighted.png"));
        Button right = new Button(new Identifier("textures/gui/sprites/transferable_list/select.png"))
                .setSelectedTexture(new Identifier("textures/gui/sprites/transferable_list/select_highlighted.png"));
        Button normal = new Button(new Identifier("textures/item/compass_19.png"))
                .setToolTip(Text.literal("Returns the compass to normal").formatted(Formatting.DARK_AQUA));
        Button back = new Button(new Identifier("textures/gui/sprites/spectator/close.png"))
                .setToolTip(Text.literal("Back").formatted(Formatting.RED));

        left.setOnClick(() -> scrollLeft(buttons, playerHeads, playerNames));
        right.setOnClick(() -> scrollRight(buttons, playerHeads, playerNames));
        normal.setOnClick(() -> {
            nbt.putBoolean("player_tracker", false);
            mc.player.sendMessage(Text.literal(PREFIX + "§3The compass sets back to normal"));
        });
        back.setOnClick(() -> Objects.requireNonNull(mc.currentScreen).close());

        root.add(left, 3, 15, 20, 40);
        root.add(right, 109, 15, 20, 40);
        root.add(normal, 5, 53, 10, 10);
        root.add(back, 114, 53, 10, 10);
    }

    private void scrollLeft(List<SlotButton> buttons, ItemStack[] playerHeads, String[] playerNames) {
        if (this.page == 0)
            return;

        this.page--;
        updateButtons(buttons, playerHeads, playerNames);
    }

    private void scrollRight(List<SlotButton> buttons, ItemStack[] playerHeads, String[] playerNames) {
        this.page++;
        updateButtons(buttons, playerHeads, playerNames);
    }

    private void updateButtons(List<SlotButton> buttons, ItemStack[] playerHeads, String[] playerNames) {
        int counter = buttons.size() * page;

        for (SlotButton button : buttons) {
            if (counter < playerHeads.length && counter < playerNames.length && playerNames[counter] != null && !playerNames[counter].isEmpty()) {
                String playerName = playerNames[counter];
                NbtCompound nbt = ((NBTConfigUtils) Objects.requireNonNull(mc.player)).playerTracker$getPersistentData();

                button.setToolTip(Text.literal(playerNames[counter]).formatted(Formatting.BLUE)).setIcon(new ItemIcon(playerHeads[counter]));
                button.setOnClick(() -> {
                    UUID uuid = NetworkUtils.getPlayerUuidFromName(playerName);
                    if (uuid != null) {
                        nbt.putBoolean("player_tracker", true);
                        nbt.putUuid("compass_target", uuid);
                        mc.currentScreen.close();
                        mc.player.sendMessage(Text.literal(PREFIX + "§3The compass target set to: " + playerName));
                    } else {
                        mc.player.sendMessage(Text.literal(PREFIX + "Compass target not in the game or in this dimension.").formatted(Formatting.RED));
                    }
                });
            } else {
                button = SlotButton.defaultSlotButton(button);
            }
            counter++;
        }
    }
}

