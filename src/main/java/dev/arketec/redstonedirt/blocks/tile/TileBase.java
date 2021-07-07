
/*
 * This class is derived from code from the Botania Mod by Vazkii and all credit to him for this great tile base class.
 * Get the Original Source Code in github:
 * https://github.com/Vazkii/Botania/blob/master/src/main/java/vazkii/botania/common/block/tile/TileMod.java
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package dev.arketec.redstonedirt.blocks.tile;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

import javax.annotation.Nonnull;

public class TileBase extends TileEntity {
    public TileBase(TileEntityType<?> type) {
        super(type);
    }
    @Nonnull
    @Override
    public CompoundNBT save(CompoundNBT tag) {
        CompoundNBT ret = super.save(tag);
        writePacketNBT(ret);
        return ret;
    }

    @Nonnull
    @Override
    public final CompoundNBT getUpdateTag() {
        return save(new CompoundNBT());
    }

    @Override
    public void load(BlockState state, CompoundNBT tag) {
        super.load(state, tag);
        readPacketNBT(tag);
    }

    public void writePacketNBT(CompoundNBT cmp) {}

    public void readPacketNBT(CompoundNBT cmp) {}

    @Override
    public final SUpdateTileEntityPacket getUpdatePacket() {
        CompoundNBT tag = new CompoundNBT();
        writePacketNBT(tag);
        return new SUpdateTileEntityPacket(worldPosition, -999, tag);
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket packet) {
        super.onDataPacket(net, packet);
        readPacketNBT(packet.getTag());
    }
}
