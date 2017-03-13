/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved
 *
 * This source file is part of New Kosmos
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package kosmos.chunks;

import flounder.camera.*;
import flounder.entities.*;
import flounder.framework.*;
import flounder.maths.vectors.*;
import flounder.models.*;
import flounder.physics.*;
import flounder.physics.bounding.*;
import flounder.profiling.*;
import flounder.resources.*;
import flounder.space.*;
import flounder.textures.*;
import kosmos.*;
import kosmos.entities.instances.*;

import java.util.*;

public class KosmosChunks extends Module {
	private static final KosmosChunks INSTANCE = new KosmosChunks();
	public static final String PROFILE_TAB_NAME = "Kosmos Chunks";

	private ISpatialStructure<Entity> chunks;

	private Entity entityPlayer;
	private Entity entitySun;
	private Entity entityMoon;

	private Sphere chunkRange;

	private Vector3f lastPlayerPos;
	private Chunk currentChunk;

	private ModelObject modelHexagon;

	public KosmosChunks() {
		super(ModuleUpdate.UPDATE_PRE, PROFILE_TAB_NAME, FlounderBounding.class, FlounderTextures.class);
	}

	@Override
	public void init() {
		this.chunks = new StructureBasic<>();

		this.entityPlayer = new InstancePlayer(FlounderEntities.getEntities(), new Vector3f(), new Vector3f());
		this.entityMoon = new InstanceMoon(FlounderEntities.getEntities(), new Vector3f(200.0f, 200.0f, 200.0f), new Vector3f(0.0f, 0.0f, 0.0f));
		this.entitySun = new InstanceSun(FlounderEntities.getEntities(), new Vector3f(-200.0f, -200.0f, -200.0f), new Vector3f(0.0f, 0.0f, 0.0f));

		this.chunkRange = new Sphere(40.0f);

		this.lastPlayerPos = new Vector3f(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY);
		setCurrent(new Chunk(KosmosChunks.getChunks(), new Vector3f(
				KosmosConfigs.configSave.getFloatWithDefault("chunk_x", 0.0f, () -> KosmosChunks.getCurrent().getPosition().x),
				0.0f,
				KosmosConfigs.configSave.getFloatWithDefault("chunk_z", 0.0f, () -> KosmosChunks.getCurrent().getPosition().z)
		))); // The root chunk.

		this.modelHexagon = ModelFactory.newBuilder().setFile(new MyFile(MyFile.RES_FOLDER, "terrains", "hexagon.obj")).create();
	}

	@Override
	public void update() {
		if (FlounderCamera.getPlayer() != null) {
			Vector3f playerPos = FlounderCamera.getPlayer().getPosition();
			Chunk playerChunk = null;

			if (!playerPos.equals(lastPlayerPos)) {
				Sphere.recalculate(chunkRange, playerPos, 1.0f, chunkRange);
			}

			for (Entity entity : chunks.getAll()) {
				Chunk chunk = (Chunk) entity;

				if (chunk.isLoaded() && chunk.getBounding().inFrustum(FlounderCamera.getCamera().getViewFrustum())) {
					if (chunk.getBounding().contains(entityPlayer.getPosition())) {
						playerChunk = chunk;
					}
				}

				chunk.update();
			}

			if (playerChunk != currentChunk) {
				if (playerChunk != null) {
					playerChunk.createChunksAround();
					playerChunk.getChildrenChunks().forEach(Chunk::createChunksAround);

					Iterator it = chunks.getAll().iterator();

					while (it.hasNext()) {
						Chunk chunk = (Chunk) it.next();

						if (chunk != currentChunk && chunk.isLoaded()) {
							if (!chunk.getBounding().intersects(chunkRange).isIntersection() && !chunkRange.contains((Sphere) chunk.getBounding())) {
								chunk.delete();
								it.remove();
							}
						}
					}

					currentChunk = playerChunk;
				}
			}

			lastPlayerPos.set(playerPos);
			FlounderBounding.addShapeRender(chunkRange);
		}

		/*if (FlounderCamera.getCamera() != null && FlounderMouse.getMouse(0)) {
			Ray viewRay = FlounderCamera.getCamera().getViewRay();
			Entity entityInRay = null;
			float entityRayDistance = 0.0f;

			for (Entity entity : currentChunk.getEntities().getAll()) {
				IntersectData data = entity.getBounding().intersects(viewRay);

				if (entity.getBounding() != null && data.isIntersection()) {
					if (entityInRay == null || data.getDistance() < entityRayDistance) {
						entityInRay = entity;
						entityRayDistance = data.getDistance();
					}
				}
			}

			if (entityInRay != null) {
				entityInRay.forceRemove(true);
			}
		}*/
	}

	@Override
	public void profile() {
		FlounderProfiler.add(PROFILE_TAB_NAME, "Chunks Size", chunks.getSize());
		FlounderProfiler.add(PROFILE_TAB_NAME, "Chunks Current", currentChunk);
	}

	public static ISpatialStructure<Entity> getChunks() {
		return INSTANCE.chunks;
	}

	public static Entity getEntityPlayer() {
		return INSTANCE.entityPlayer;
	}

	public static Entity getEntitySun() {
		return INSTANCE.entitySun;
	}

	public static Entity getEntityMoon() {
		return INSTANCE.entityMoon;
	}

	public static Chunk getCurrent() {
		return INSTANCE.currentChunk;
	}

	public static void setCurrent(Chunk currentChunk) {
		currentChunk.createChunksAround();
		currentChunk.getChildrenChunks().forEach(Chunk::createChunksAround);
		INSTANCE.currentChunk = currentChunk;
	}

	public static void clear() {
		INSTANCE.chunks.getAll().forEach((Entity chunk) -> ((Chunk) chunk).delete());
		INSTANCE.chunks.clear();
		setCurrent(new Chunk(KosmosChunks.getChunks(), getCurrent().getPosition())); // The new root chunk.
	}

	public static ModelObject getModelHexagon() {
		return INSTANCE.modelHexagon;
	}

	@Override
	public Module getInstance() {
		return INSTANCE;
	}

	@Override
	public void dispose() {
		clear();
	}
}
