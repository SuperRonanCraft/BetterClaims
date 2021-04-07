package me.RonanCraft.Pueblos.resources.tools.packet;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.ChunkCoordIntPair;
import com.comphenix.protocol.wrappers.MultiBlockChangeInfo;

public class WrapperPlayServerMultiBlockChange extends AbstractPacket {
    public static final PacketType TYPE =
            PacketType.Play.Server.MULTI_BLOCK_CHANGE;

    public WrapperPlayServerMultiBlockChange() {
        super(new PacketContainer(TYPE), TYPE);
        handle.getModifier().writeDefaults();
    }

    public WrapperPlayServerMultiBlockChange(PacketContainer packet) {
        super(packet, TYPE);
    }

    /**
     * Retrieve the chunk that has been altered.
     *
     * @return The current chunk
     */
    public ChunkCoordIntPair getChunk() {
        return handle.getChunkCoordIntPairs().read(0);
    }

    /**
     * Set the chunk that has been altered.
     *
     * @param value - new value
     */
    public void setChunk(ChunkCoordIntPair value) {
        handle.getChunkCoordIntPairs().write(0, value);
    }

    /**
     * Retrieve a copy of the record data as a block change array.
     *
     * @return The copied block change array.
     */
    public MultiBlockChangeInfo[] getRecords() {
        return handle.getMultiBlockChangeInfoArrays().read(0);
    }

    /**
     * Set the record data using the given helper array.
     *
     * @param value - new value
     */
    public void setRecords(MultiBlockChangeInfo[] value) {
        handle.getMultiBlockChangeInfoArrays().write(0, value);
    }
}