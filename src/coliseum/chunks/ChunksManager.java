package coliseum.chunks;

import coliseum.entities.instances.*;
import flounder.camera.*;
import flounder.entities.*;
import flounder.maths.*;
import flounder.maths.vectors.*;

import java.util.*;

public class ChunksManager {
	private List<Chunk> chunks;

	public ChunksManager() {
		this.chunks = new ArrayList<>();
		generate();
	}

	public void generate() {
		// new InstanceDerpWalk(FlounderEntities.getEntities(), new Vector3f(0.0f, 1.0f, 0.0f), new Vector3f());
		new InstanceCowboy(FlounderEntities.getEntities(), new Vector3f(0.0f, 1.5f, 0.0f), new Vector3f());
		// new InstanceRobit(FlounderEntities.getEntities(), new Vector3f(0.0f, 1.0f, 0.0f), new Vector3f());

		for (int x = -3; x < 7; x++) {
			for (int y = -3; y < 7; y++) {
				new InstanceCloud(FlounderEntities.getEntities(), new Vector3f(
						(x * 50.0f) + Maths.randomInRange(-15.0f, 15.0f),
						24.0f,
						(y * 50.0f) + Maths.randomInRange(-15.0f, 15.0f)
				), new Vector3f(
						0.0f,
						Maths.randomInRange(0.0f, 360.0f),
						Maths.randomInRange(0.0f, 180.0f)
				), Maths.randomInRange(1.5f, 4.0f));
			}
		}

		new InstanceMoon(FlounderEntities.getEntities(), new Vector3f(200.0f, 200.0f, 200.0f), new Vector3f(0.0f, 0.0f, 0.0f));
		new InstanceSun(FlounderEntities.getEntities(), new Vector3f(-200.0f, -200.0f, -200.0f), new Vector3f(0.0f, 0.0f, 0.0f));

		for (int i = 0; i < 1; i++) {
			int shapesOnEdge = i;
			float r = 0;
			float g = -i;
			float b = i;
			chunks.add(new Chunk(FlounderEntities.getEntities(), ChunkMaths.calculateXY(new Vector3f(r, g, b), ChunkGenerator.HEXAGON_SIDE_LENGTH * ChunkGenerator.CHUNK_RADIUS, null)));

			for (int j = 0; j < ChunkGenerator.HEXAGON_SIDE_COUNT; j++) {
				if (j == ChunkGenerator.HEXAGON_SIDE_COUNT - 1) {
					shapesOnEdge = i - 1;
				}

				for (int w = 0; w < shapesOnEdge; w++) {
					// r + g + b = 0
					r = r + ChunkGenerator.GENERATE_DELTAS[j][0];
					g = g + ChunkGenerator.GENERATE_DELTAS[j][1];
					b = b + ChunkGenerator.GENERATE_DELTAS[j][2];
					chunks.add(new Chunk(FlounderEntities.getEntities(), ChunkMaths.calculateXY(new Vector3f(r, g, b), ChunkGenerator.HEXAGON_SIDE_LENGTH * ChunkGenerator.CHUNK_RADIUS, null)));
				}
			}
		}

		//chunks.add(new Chunk(new Vector2f(0.0f, 0.0f)));
		//chunks.add(new Chunk(FlounderEntities.getEntities(), new Vector3f(10.392304f, 0.0f, 18.0f)));
		//chunks.add(new Chunk(new Vector2f(20.784609f, 0.0f)));
		//chunks.add(new Chunk(new Vector2f(10.392304f, -18.0f)));
		//chunks.add(new Chunk(new Vector2f(-10.392304f, -18.0f)));
		//chunks.add(new Chunk(new Vector2f(-20.784609f, 0.0f)));
		//chunks.add(new Chunk(new Vector2f(-10.392304f, 18.0f)));

		//	chunks.add(new Chunk(FlounderEntities.getEntities(), new Vector3f(15.f, 0.0f, 30.0f)));
	}

	public void update() {
		for (Chunk chunk : chunks) {
			if (FlounderCamera.getPlayer() != null) {
				chunk.update(FlounderCamera.getPlayer().getPosition());
			} else {
				chunk.update(null);
			}
		}
	}

	public List<Chunk> getChunks() {
		return chunks;
	}

	public void dispose() {

	}
}
