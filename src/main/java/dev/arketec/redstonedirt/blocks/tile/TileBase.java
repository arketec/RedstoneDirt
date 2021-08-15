
/*
 * This class is derived from code from the Botania Mod by Vazkii and all credit to him for this great tile base class.
 * Get the Original Source Code in github:
 * https://github.com/Vazkii/Botania/blob/master/src/main/java/vazkii/botania/common/block/tile/TileMod.java
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package dev.arketec.redstonedirt.blocks.tile;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import javax.annotation.Nonnull;

public class TileBase extends BlockEntity {
    public TileBase(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }
    @Nonnull
    @Override
    public CompoundTag save(CompoundTag tag) {
        CompoundTag ret = super.save(tag);
        writePacketNBT(ret);
        return ret;
    }

    @Nonnull
    @Override
    public final CompoundTag getUpdateTag() {
        return save(new CompoundTag());
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        readPacketNBT(tag);
    }

    public void writePacketNBT(CompoundTag cmp) {}

    public void readPacketNBT(CompoundTag cmp) {}

    @Override
    public final ClientboundBlockEntityDataPacket getUpdatePacket() {
        CompoundTag tag = new CompoundTag();
        writePacketNBT(tag);
        return new ClientboundBlockEntityDataPacket(worldPosition, -999, tag);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket packet) {
        super.onDataPacket(net, packet);
        readPacketNBT(packet.getTag());
    }
}
