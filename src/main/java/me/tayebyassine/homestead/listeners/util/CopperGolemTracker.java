package me.tayebyassine.homestead.listeners.util;

import me.tayebyassine.homestead.managers.ChunkManager;
import me.tayebyassine.homestead.models.Region;
import org.bukkit.Chunk;
import org.bukkit.entity.CopperGolem;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class CopperGolemTracker {

    private static final Map<UUID, Long> GOLEM_SPAWN_REGION = new ConcurrentHashMap<>();

    private CopperGolemTracker() {
        throw new AssertionError("Uninstantiable class");
    }

    /**
     * Records the region that owned the golem's spawn chunk. A golem without a claimed
     * spawn region is simply left absent from the map — {@link ConcurrentHashMap} does
     * not permit null values, and callers already treat an absent entry as "wild".
     */
    public static void recordSpawnRegion(CopperGolem golem) {
        Chunk spawnChunk = golem.getLocation().getChunk();

        Region region = null;
        if (ChunkManager.isChunkClaimed(spawnChunk)) {
            region = ChunkManager.getRegionOwnsTheChunk(spawnChunk);
        }

        if (region != null) {
            GOLEM_SPAWN_REGION.put(golem.getUniqueId(), region.getUniqueId());
        } else {
            GOLEM_SPAWN_REGION.remove(golem.getUniqueId());
        }
    }

    public static Long getSpawnRegionId(CopperGolem golem) {
        return GOLEM_SPAWN_REGION.get(golem.getUniqueId());
    }

    public static void forgetGolem(CopperGolem golem) {
        GOLEM_SPAWN_REGION.remove(golem.getUniqueId());
    }
}