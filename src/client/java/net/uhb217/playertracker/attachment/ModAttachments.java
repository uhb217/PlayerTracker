package net.uhb217.playertracker.attachment;

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.util.Identifier;
import net.uhb217.playertracker.client.Global;

/**
 * Registers the mod's data attachments. Attachments are the modern Fabric
 * replacement for the old "mixin extra NBT onto the entity" pattern: they are
 * saved to disk automatically and copied when the player respawns.
 */
public final class ModAttachments implements Global {
    public static final AttachmentType<TrackerData> TRACKER = AttachmentRegistry.create(
            Identifier.of(modId, "tracker"),
            builder -> builder.initializer(TrackerData::empty)
                    .persistent(TrackerData.CODEC)
                    .copyOnDeath());

    private ModAttachments() {
    }

    public static void init() {
        // Touching this class runs the static initializers above and registers the attachment.
    }
}
