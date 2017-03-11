/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved
 *
 * This source file is part of New Kosmos
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package kosmos.chunks.biomes;

import flounder.maths.vectors.*;
import kosmos.chunks.*;
import kosmos.chunks.tiles.*;
import kosmos.entities.instances.*;
import kosmos.particles.*;
import kosmos.particles.loading.*;
import kosmos.world.*;

public class BiomeGrass implements IBiome {
	@Override
	public String getBiomeName() {
		return "grass";
	}

	@Override
	public Tile getMainTile() {
		return Tile.TILE_GRASS;
	}

	@Override
	public void generateEntity(Chunk chunk, Vector2f worldPos, Vector2f tilePosition, int height) {
		float rotation = KosmosWorld.getNoise().noise1((worldPos.x - worldPos.y) / 50.0f) * 3600.0f;
		float spawn = Math.abs(KosmosWorld.getNoise().noise1((worldPos.y - worldPos.x) / 10.0f) * 250.0f);

		switch ((int) spawn) {
			case 1:
				new InstanceTree1(chunk.getEntities(),
						new Vector3f(
								chunk.getPosition().x + (float) (tilePosition.x * 0.5),
								(float) ((2.0 * 0.25) + (height * Math.sqrt(2.0)) * 0.5),
								chunk.getPosition().z + (float) (tilePosition.y * 0.5)
						),
						new Vector3f(0.0f, rotation, 0.0f)
				);
				break;
			case 2:
				new InstanceTree3(chunk.getEntities(),
						new Vector3f(
								chunk.getPosition().x + (float) (tilePosition.x * 0.5),
								(float) ((2.5 * 0.25) + (height * Math.sqrt(2.0)) * 0.5),
								chunk.getPosition().z + (float) (tilePosition.y * 0.5)
						),
						new Vector3f(0.0f, rotation, 0.0f)
				);
				break;
			case 3:
				if (spawn - 3 < 0.123f) {
					new InstancePod(chunk.getEntities(),
							new Vector3f(
									chunk.getPosition().x + (float) (tilePosition.x * 0.5),
									(float) ((4.20 * 0.25) + (height * Math.sqrt(2.0)) * 0.5),
									chunk.getPosition().z + (float) (tilePosition.y * 0.5)
							),
							new Vector3f(0.0f, rotation, 0.0f)
					);
				}
				break;
			case 4:
				new InstanceBush(chunk.getEntities(),
						new Vector3f(
								chunk.getPosition().x + (float) (tilePosition.x * 0.5),
								(float) ((2.5 * 0.25) + (height * Math.sqrt(2.0)) * 0.5),
								chunk.getPosition().z + (float) (tilePosition.y * 0.5)
						),
						new Vector3f(0.0f, rotation, 0.0f)
				);
				break;
			case 5:
			case 6:
			case 7:
				new InstanceFlowerpatch1(chunk.getEntities(),
						new Vector3f(
								chunk.getPosition().x + (float) (tilePosition.x * 0.5),
								(float) ((1.5 * 0.25) + (height * Math.sqrt(2.0)) * 0.5),
								chunk.getPosition().z + (float) (tilePosition.y * 0.5)
						),
						new Vector3f()
				);
				break;
			case 8:
			case 9:
			case 10:
				new InstanceTallGrass(chunk.getEntities(),
						new Vector3f(
								chunk.getPosition().x + (float) (tilePosition.x * 0.5),
								(float) ((1.0 * 0.25) + (height * Math.sqrt(2.0)) * 0.5),
								chunk.getPosition().z + (float) (tilePosition.y * 0.5)
						),
						new Vector3f()
				);
				break;
			case 11:
				new InstanceTreeBlossom(chunk.getEntities(),
						new Vector3f(
								chunk.getPosition().x + (float) (tilePosition.x * 0.5),
								(float) ((1.0 * 0.25) + (height * Math.sqrt(2.0)) * 0.5),
								chunk.getPosition().z + (float) (tilePosition.y * 0.5)
						),
						new Vector3f()
				);
				break;
			default:
				break;
		}
	}

	@Override
	public ParticleTemplate getWeatherParticle() {
		return KosmosParticles.load("rain");
	}

	@Override
	public float getTempDay() {
		return 20.9f;
	}

	@Override
	public float getTempNight() {
		return 12.4f;
	}

	@Override
	public float getHumidity() {
		return 61.0f;
	}

	@Override
	public float getWindSpeed() {
		return 0.17f;
	}
}
