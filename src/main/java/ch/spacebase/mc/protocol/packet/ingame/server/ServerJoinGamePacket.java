package ch.spacebase.mc.protocol.packet.ingame.server;

import java.io.IOException;

import ch.spacebase.mc.protocol.data.game.values.MagicValues;
import ch.spacebase.mc.protocol.data.game.values.entity.player.GameMode;
import ch.spacebase.mc.protocol.data.game.values.setting.Difficulty;
import ch.spacebase.mc.protocol.data.game.values.world.WorldType;
import ch.spacebase.mc.util.NetUtil;
import ch.spacebase.packetlib.io.NetInput;
import ch.spacebase.packetlib.io.NetOutput;
import ch.spacebase.packetlib.packet.Packet;

public class ServerJoinGamePacket implements Packet {
	
	private int entityId;
	private boolean hardcore;
	private GameMode gamemode;
	private int dimension;
	private Difficulty difficulty;
	private int maxPlayers;
	private WorldType worldType;
	
	@SuppressWarnings("unused")
	private ServerJoinGamePacket() {
	}
	
	public ServerJoinGamePacket(int entityId, boolean hardcore, GameMode gamemode, int dimension, Difficulty difficulty, int maxPlayers, WorldType worldType) {
		this.entityId = entityId;
		this.hardcore = hardcore;
		this.gamemode = gamemode;
		this.dimension = dimension;
		this.difficulty = difficulty;
		this.maxPlayers = maxPlayers;
		this.worldType = worldType;
	}
	
	public int getEntityId() {
		return this.entityId;
	}
	
	public boolean getHardcore() {
		return this.hardcore;
	}
	
	public GameMode getGameMode() {
		return this.gamemode;
	}
	
	public int getDimension() {
		return this.dimension;
	}
	
	public Difficulty getDifficulty() {
		return this.difficulty;
	}
	
	public int getMaxPlayers() {
		return this.maxPlayers;
	}
	
	public WorldType getWorldType() {
		return this.worldType;
	}

	@Override
	public void read(NetInput in) throws IOException {
		this.entityId = in.readInt();
		int gamemode = in.readUnsignedByte();
		this.hardcore = (gamemode & 8) == 8;
        gamemode = gamemode & -9;
        this.gamemode = MagicValues.key(GameMode.class, gamemode);
        this.dimension = in.readByte();
        this.difficulty = MagicValues.key(Difficulty.class, in.readUnsignedByte());
        this.maxPlayers = in.readUnsignedByte();
        this.worldType = MagicValues.key(WorldType.class, in.readString());
        // Unfortunately this is needed to check whether to read skylight values in chunk data packets.
        NetUtil.hasSky = this.dimension != -1 && this.dimension != 1;
	}

	@Override
	public void write(NetOutput out) throws IOException {
		out.writeInt(this.entityId);
		int gamemode = MagicValues.value(Integer.class, this.gamemode);
		if(this.hardcore) {
			gamemode |= 8;
		}

		out.writeByte(gamemode);
		out.writeByte(this.dimension);
		out.writeByte(MagicValues.value(Integer.class, this.difficulty));
		out.writeByte(this.maxPlayers);
		out.writeString(MagicValues.value(String.class, this.worldType));
	}
	
	@Override
	public boolean isPriority() {
		return false;
	}

}
