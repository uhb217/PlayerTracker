package net.uhb217.playertracker.utils;

import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.uhb217.playertracker.client.Global;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;

public final class NetworkUtils implements Global {

    public static UUID getPlayerUuidFromName(String name) {
        // The tab list is the primary source: it always contains every player
        // known to the client, including the local player.
        if (mc.getNetworkHandler() != null) {
            for (PlayerListEntry entry : mc.getNetworkHandler().getPlayerList()) {
                if (entry.getProfile() != null && name.equals(entry.getProfile().name())) {
                    return entry.getProfile().id();
                }
            }
        }
        // Fall back to the loaded entities (covers edge cases such as players
        // missing from the tab list).
        if (mc.world != null) {
            for (PlayerEntity player : mc.world.getPlayers()) {
                if (player.getName().getString().equals(name)) {
                    return player.getUuid();
                }
            }
        }
        return null;
    }

    public static ItemStack[] getPlayersHeads() {
        List<PlayerListEntry> players = getPlayerList();
        ItemStack[] skulls = new ItemStack[players.size()];

        for (int i = 0; i < skulls.length; i++) {
            PlayerListEntry player = players.get(i);
            if (player.getProfile().name() != null && !player.getProfile().name().isEmpty()) {
                skulls[i] = Items.PLAYER_HEAD.getDefaultStack();
                skulls[i].set(DataComponentTypes.PROFILE, ProfileComponent.ofStatic(player.getProfile()));
            }
        }
        return skulls;
    }

    public static String[] getPlayersNames() {
        List<PlayerListEntry> players = getPlayerList();
        String[] names = new String[players.size()];
        for (int i = 0; i < players.size(); i++) {
            String name = players.get(i).getProfile().name();
            if (name != null && !name.isEmpty()) {
                names[i] = name;
            }
        }
        return names;
    }

    private static List<PlayerListEntry> getPlayerList() {
        if (mc.getNetworkHandler() == null) {
            return List.of();
        }
        return mc.getNetworkHandler().getPlayerList().stream().toList();
    }

    public static SuggestionProvider<FabricClientCommandSource> playerNameSuggestions = (context, builder) -> {
        String remaining = builder.getRemaining().toLowerCase();
        Stream.of(NetworkUtils.getPlayersNames())
                .filter(Objects::nonNull)
                .filter(name -> name.toLowerCase().startsWith(remaining))
                .forEach(builder::suggest);
        return builder.buildFuture();
    };

    private NetworkUtils() {
    }
}
