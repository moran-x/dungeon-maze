package com.timvisee.dungeonmaze.populator.maze;

import java.util.Random;

import com.timvisee.dungeonmaze.world.dungeon.chunk.DungeonChunk;
import org.bukkit.Chunk;
import org.bukkit.World;

import com.timvisee.dungeonmaze.populator.ChunkBlockPopulator;
import com.timvisee.dungeonmaze.populator.ChunkBlockPopulatorArgs;

public abstract class MazeLayerBlockPopulator extends ChunkBlockPopulator {

	private static final int MIN_LAYER = 1;
	private static final int MAX_LAYER = 7;
	private static final int LAYER_COUNT = 7;
	
	@Override
	public void populateChunk(ChunkBlockPopulatorArgs args) {
		final World world = args.getWorld();
		final Random rand = args.getRandom();
		final Chunk chunk = args.getSourceChunk();
        final DungeonChunk dungeonChunk = args.getDungeonChunk();

        // Make sure the dungeon chunk isn't custom
        if(dungeonChunk.isCustomChunk())
            return;

		// Get the minimum and maximum layer count
		final int layerMin = Math.max(getMinimumLayer(), 1);
        final int layerMax =  Math.min(getMaximumLayer(), LAYER_COUNT);

		// The layers
		for(int l = layerMin; l <= layerMax; l++) {
            // Check whether this this layer should be populated based on it's chance
            if(rand.nextFloat() >= getLayerChance())
                continue;

            // Iterate through this layer
            final int iterations = getLayerIterations();
            final int iterationsMax = getLayerIterationsMax();
            int iterationCount = 0;
            for(int i = 0; i < iterations; i++) {
                // Make sure we didn't iterate too many times
                if(iterationCount >= iterationsMax && iterationsMax >= 0)
                    break;

                // Check whether this this layer should be populated in this iteration based on it's iteration chance
                if(rand.nextFloat() >= getLayerIterationsChance())
                    continue;

                // Increase the iterations counter
                iterationCount++;

                // Make sure the dungeon chunk isn't custom
                if(dungeonChunk.isCustomChunk())
                    return;

                // Calculate the Y coordinate based on the current layer
                int y = 30 + ((l - 1) * 6);

                // Construct the MazePopulatorArgs to use the the populateMaze method
                MazeLayerBlockPopulatorArgs newArgs = new MazeLayerBlockPopulatorArgs(world, rand, chunk, dungeonChunk, l, y);

                // Populate the maze
                populateLayer(newArgs);
            }
		}
	}
	
	/**
	 * Population method.
     *
	 * @param args Populator arguments.
	 */
	public abstract void populateLayer(MazeLayerBlockPopulatorArgs args);
	
	/**
	 * Get the minimum layer.
     *
	 * @return Minimum layer.
	 */
	public int getMinimumLayer() {
		return MIN_LAYER;
	}
	
	/**
	 * Get the maximum layer.
     *
	 * @return Maximum layer.
	 */
	public int getMaximumLayer() {
		return MAX_LAYER;
	}

    /**
     * Get the layer population chance. This value is between 0.0 and 1.0.
     *
     * @return The population chance of the room.
     */
    public float getLayerChance() {
        return 1.0f;
    }

    /**
     * Get the number of times to iterate through each layer.
     *
     * @return The number of iterations.
     */
    public int getLayerIterations() {
        return 1;
    }

    /**
     * Get the layer population chance for each iteration. This value is between 0.0 and 1.0.
     *
     * @return The population chance of the room.
     */
    public float getLayerIterationsChance() {
        return 1.0f;
    }

    /**
     * Get the maximum number of times to iterate based on the chance and iteration count.
     *
     * @return The maximum number of times to iterate. Return a negative number of ignore the maximum.
     */
    public int getLayerIterationsMax() {
        return -1;
    }
}
