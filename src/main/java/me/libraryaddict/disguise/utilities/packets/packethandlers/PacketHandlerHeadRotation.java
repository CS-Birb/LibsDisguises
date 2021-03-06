package me.libraryaddict.disguise.utilities.packets.packethandlers;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.StructureModifier;
import me.libraryaddict.disguise.disguisetypes.Disguise;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.utilities.DisguiseUtilities;
import me.libraryaddict.disguise.utilities.packets.IPacketHandler;
import me.libraryaddict.disguise.utilities.packets.LibsPackets;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

/**
 * Created by libraryaddict on 3/01/2019.
 */
public class PacketHandlerHeadRotation implements IPacketHandler {
    @Override
    public PacketType[] getHandledPackets() {
        return new PacketType[]{PacketType.Play.Server.ENTITY_HEAD_ROTATION};
    }

    @Override
    public void handle(Disguise disguise, PacketContainer sentPacket, LibsPackets packets, Player observer,
            Entity entity) {
        if (!disguise.getType().isPlayer() || entity.getType() == EntityType.PLAYER) {
            return;
        }

        Location loc = entity.getLocation();

        DisguiseType entityType = DisguiseType.getType(entity);

        byte pitch;
        byte yaw;

        switch (entityType) {
            case LLAMA_SPIT:
            case FIREBALL:
            case SMALL_FIREBALL:
            case DRAGON_FIREBALL:
            case FIREWORK:
            case SHULKER_BULLET:
            case ARROW:
            case TIPPED_ARROW:
            case SPECTRAL_ARROW:
            case EGG:
            case TRIDENT:
            case THROWN_EXP_BOTTLE:
            case EXPERIENCE_ORB:
            case SPLASH_POTION:
            case ENDER_CRYSTAL:
            case FALLING_BLOCK:
            case ITEM_FRAME:
            case ENDER_SIGNAL:
            case ENDER_PEARL:
            case DROPPED_ITEM:
            case EVOKER_FANGS:
            case SNOWBALL:
            case PAINTING:
            case PRIMED_TNT:
                if (sentPacket.getBytes().read(0) == 0 && entity.getVelocity().lengthSquared() > 0) {
                    loc.setDirection(entity.getVelocity());
                    pitch = DisguiseUtilities.getPitch(disguise.getType(), DisguiseType.PLAYER,
                            (byte) (int) (loc.getPitch() * 256.0F / 360.0F));
                    yaw = DisguiseUtilities.getYaw(disguise.getType(), DisguiseType.PLAYER,
                            (byte) (int) (loc.getYaw() * 256.0F / 360.0F));
                    break;
                }
            default:
                pitch = DisguiseUtilities.getPitch(disguise.getType(), entity.getType(),
                        (byte) (int) (loc.getPitch() * 256.0F / 360.0F));
                yaw = DisguiseUtilities
                        .getYaw(disguise.getType(), entity.getType(), (byte) (int) (loc.getYaw() * 256.0F / 360.0F));
                break;
        }

        PacketContainer rotation = new PacketContainer(PacketType.Play.Server.ENTITY_HEAD_ROTATION);

        StructureModifier<Object> mods = rotation.getModifier();

        mods.write(0, entity.getEntityId());
        mods.write(1, yaw);

        PacketContainer look = new PacketContainer(PacketType.Play.Server.REL_ENTITY_MOVE_LOOK);

        look.getIntegers().write(0, entity.getEntityId());
        look.getBytes().write(0, yaw);
        look.getBytes().write(1, pitch);

        packets.clear();

        packets.addPacket(look);
        packets.addPacket(rotation);
    }
}
