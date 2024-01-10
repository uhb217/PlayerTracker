package net.uhb217.glowingentity;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.uhb217.glowingentity.utils.IEntityDataSaver;
import net.uhb217.glowingentity.utils.KeyInputHandler;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import java.util.List;
import java.util.Objects;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class GlowingEntityClient implements ClientModInitializer {
    private Vec3d lastPos = new Vec3d(0, 0, 0);

    @Override
    public void onInitializeClient() {
        //register commands
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(literal("entity_glowing")
                .then(argument("glowing", IntegerArgumentType.integer())
                        .executes(context -> {
                            int value = IntegerArgumentType.getInteger(context, "glowing");
                            IEntityDataSaver playerData = (IEntityDataSaver) context.getSource().getPlayer();
                            playerData.getPersistentData().putInt("glow", value);
                            if (value > 15)
                                value = 15;
                            String glow = value < 0 ? "Normal" : String.valueOf(value);
                            context.getSource().sendFeedback(Text.literal("The Entity Glowing set to: " + glow).formatted(Formatting.DARK_AQUA));
                            return value;
                        }))));
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(literal("set_target")
                .then(argument("target", StringArgumentType.string())
                        .executes(context -> {
                            List<AbstractClientPlayerEntity> targets = context.getSource().getWorld().getPlayers();
                            PlayerEntity target = null;
                            for (AbstractClientPlayerEntity t : targets) {
                                if (Objects.equals(t.getName().getString(), StringArgumentType.getString(context, "target")))
                                    target = t;
                            }
                            if (target != null) {
                                IEntityDataSaver playerData = (IEntityDataSaver) context.getSource().getClient().player;
                                playerData.getPersistentData().putUuid("compass_target", target.getUuid());
                            }
                            return 0;
                        }))));
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(literal("setLine")
                .then(argument("x", IntegerArgumentType.integer()).then(argument("y", IntegerArgumentType.integer())
                        .then(argument("z", IntegerArgumentType.integer())
                                .executes(context -> {
                                    NbtCompound data = ((IEntityDataSaver) context.getSource().getPlayer()).getPersistentData();
                                    data.put("po1", NbtHelper.fromBlockPos(context.getSource().getPlayer().getBlockPos()));
                                    BlockPos pos2 = new BlockPos(IntegerArgumentType.getInteger(context, "x"), IntegerArgumentType.getInteger(context, "y"), IntegerArgumentType.getInteger(context, "z"));
                                    data.put("pos2", NbtHelper.fromBlockPos(pos2));
                                    return 0;
                                }))))));

        WorldRenderEvents.END.register(context -> {
            Camera camera = context.camera();
            Vec3d targetPosition = new Vec3d(0, 100, 0);
            Vec3d transformedPosition = targetPosition.subtract(camera.getPos());

            MatrixStack matrixStack = new MatrixStack();
            matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));
            matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(camera.getYaw() + 180.0F));
            matrixStack.translate(transformedPosition.x, transformedPosition.y, transformedPosition.z);

            Matrix4f positionMatrix = matrixStack.peek().getPositionMatrix();
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder buffer = tessellator.getBuffer();

            buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR_TEXTURE);
            buffer.vertex(positionMatrix, 0, 1, 0).color(1f, 1f, 1f, 1f).texture(0f, 0f).next();
            buffer.vertex(positionMatrix, 0, 0, 0).color(1f, 0f, 0f, 1f).texture(0f, 1f).next();
            buffer.vertex(positionMatrix, 1, 0, 0).color(0f, 1f, 0f, 1f).texture(1f, 1f).next();
            buffer.vertex(positionMatrix, 1, 1, 0).color(0f, 0f, 1f, 1f).texture(1f, 0f).next();

            RenderSystem.setShader(GameRenderer::getPositionColorTexProgram);
            RenderSystem.setShaderTexture(0, new Identifier(GlowingEntity.MOD_ID, "glow.png"));
            RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
            RenderSystem.disableCull();
            RenderSystem.depthFunc(GL11.GL_ALWAYS);

            tessellator.draw();

            RenderSystem.depthFunc(GL11.GL_LEQUAL);
            RenderSystem.enableCull();
        });

        WorldRenderEvents.END.register(context -> {
            Camera camera = context.camera();
            if (lastPos != context.gameRenderer().getClient().player.getPos())//check if it should switch poד
                lastPos = context.gameRenderer().getClient().player.getPos();
            NbtCompound data = ((IEntityDataSaver) context.gameRenderer().getClient().player).getPersistentData();
            Vec3d targetPosition = new Vec3d(0, 90, 0);
            if (data.contains("pos2")) {
                BlockPos pos = NbtHelper.toBlockPos(data.getCompound("pos2"));
                targetPosition = new Vec3d(pos.getX(), pos.getY(), pos.getZ());
            }
            Vec3d transformedPosition = targetPosition.subtract(camera.getPos());
            Vec3d pos = lastPos.subtract(targetPosition);

            MatrixStack matrixStack = new MatrixStack();
            matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(camera.getPitch()));
            matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(camera.getYaw() + 180.0F));
            matrixStack.translate(transformedPosition.x, transformedPosition.y, transformedPosition.z);

            Matrix4f positionMatrix = matrixStack.peek().getPositionMatrix();
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder buffer = tessellator.getBuffer();

            buffer.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
            buffer.vertex(positionMatrix, -1, 1, 1).color(0x088CB7F9).next();
            buffer.vertex(positionMatrix, (int) pos.x, (int) pos.y, (int) pos.z).color(0x088CB7F9).next();

            RenderSystem.lineWidth(3.0f);
            RenderSystem.setShader(GameRenderer::getPositionColorProgram);
            RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
            RenderSystem.disableCull();
            RenderSystem.depthFunc(GL11.GL_ALWAYS);

            tessellator.draw();

            RenderSystem.depthFunc(GL11.GL_LEQUAL);
            RenderSystem.enableCull();
        });
        //register KeyBiding
        KeyInputHandler.register();
    }
}