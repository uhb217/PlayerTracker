package net.uhb217.playertracker.utils;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.uhb217.playertracker.client.Global;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;

public class NetworkUtils implements Global {
    public static UUID getPlayerUuidFromName(String name) {
        for (PlayerEntity player : Objects.requireNonNull(mc.world).getPlayers()) {
            if (player.getName().getString().equals(name))
                return player.getUuid();
        }
        return null;
    }

    public static ItemStack[] getPlayersHeads() {
        List<PlayerListEntry> players = mc.getNetworkHandler().getPlayerList().stream().toList();
        ItemStack[] skulls = new ItemStack[players.size()];

        for (int i = 0; i < skulls.length; i++) {
            PlayerListEntry player = players.get(i);
            if (player.getProfile().getName() != null && !player.getProfile().getName().isEmpty()) {
                skulls[i] = Items.PLAYER_HEAD.getDefaultStack();
                skulls[i].setNbt(getNbtFromProfile(player.getProfile()));
            }
        }
        return skulls;
    }

    public static String[] getPlayersNames() {
        List<PlayerListEntry> players = mc.getNetworkHandler().getPlayerList().stream().toList();
        String[] names = new String[players.size()];
        for (int i = 0; i < players.size(); i++) {
            PlayerListEntry player = players.get(i);
            String name = player.getProfile().getName();
            if (name != null && !name.isEmpty()) {
                names[i] = name;
            }
        }
        return names;
    }

    public static NbtCompound getNbtFromProfile(GameProfile profile) {
        NbtCompound nbtCompound = new NbtCompound();
        nbtCompound.put("SkullOwner", NbtHelper.writeGameProfile(new NbtCompound(), profile));
        NbtCompound displayTag = new NbtCompound();
        displayTag.putString("Name", profile.getName());
        nbtCompound.put("display", displayTag);
        return nbtCompound;
    }
    public static SuggestionProvider<FabricClientCommandSource> playerNameSuggestions = (context, builder) -> {
        String remaining = builder.getRemaining().toLowerCase();
        Stream.of(NetworkUtils.getPlayersNames())
                .filter(name -> name.toLowerCase().startsWith(remaining))
                .forEach(builder::suggest);
        return builder.buildFuture();
    };
}
