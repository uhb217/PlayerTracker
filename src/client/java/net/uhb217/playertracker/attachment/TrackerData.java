package net.uhb217.playertracker.attachment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Optional;
import java.util.UUID;

/**
 * Persistent per-player data for the compass tracker.
 *
 * @param tracking whether the compass should point at a player instead of behaving normally
 * @param target   the UUID of the tracked player, if any
 */
public record TrackerData(boolean tracking, Optional<UUID> target) {
    public static final Codec<TrackerData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.fieldOf("tracking").forGetter(TrackerData::tracking),
            Codec.STRING.optionalFieldOf("target").forGetter(data -> data.target().map(UUID::toString))
    ).apply(instance, (tracking, target) -> new TrackerData(tracking, target.map(UUID::fromString))));

    public static TrackerData empty() {
        return new TrackerData(false, Optional.empty());
    }

    public static TrackerData tracking(UUID target) {
        return new TrackerData(true, Optional.of(target));
    }
}
