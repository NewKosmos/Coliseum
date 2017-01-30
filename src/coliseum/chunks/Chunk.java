package coliseum.chunks;

import flounder.events.*;
import flounder.helpers.*;
import flounder.inputs.*;
import flounder.logger.*;
import flounder.maths.vectors.*;
import flounder.physics.bounding.*;
import org.lwjgl.glfw.*;

import java.util.*;

/**
 * A hexagonal chunk.
 * http://www.redblobgames.com/grids/hexagons/#range
 * http://stackoverflow.com/questions/2459402/hexagonal-grid-coordinates-to-pixel-coordinates
 */
public class Chunk {
	private Vector2f position;
	private List<Tile> tiles;
	private ChunkMesh chunkMesh;
	//	private boolean tilesChanged;
	private float darkness;

	public Chunk(Vector2f position) {
		this.position = position;
		this.tiles = new ArrayList<>();
		this.chunkMesh = new ChunkMesh(this);
//		this.tilesChanged = true;
		this.darkness = 0.0f;

		FlounderLogger.log("Chunk[ " + position.x + ", " + position.y + " ]: Size = " + tiles.size());
		ChunkGenerator.generate(this);
		this.tiles = ArraySorting.quickSort(tiles);

		FlounderEvents.addEvent(new IEvent() {
			private KeyButton k = new KeyButton(GLFW.GLFW_KEY_R);

			@Override
			public boolean eventTriggered() {
				return k.wasDown();
			}

			@Override
			public void onEvent() {
				chunkMesh.rebuildAABB();
				chunkMesh.rebuildMesh();
			}
		});
	}

	public void update(Vector3f playerPosition) {
		/*if (tilesChanged) {
			chunkMesh.rebuildAABB();
			chunkMesh.rebuildMesh();
			tilesChanged = false;
		}*/

		if (playerPosition != null) {
			//	double distance = Math.sqrt(Math.pow(position.x - playerPosition.x, 2.0) + Math.pow(position.y - playerPosition.y, 2.0));
			//	if (distance >= 30.0) {
			//		darkness = 0.7f;
			//	} else {
			//		darkness = 0.0f;
			//	}
		}

		FlounderBounding.addShapeRender(chunkMesh.getAABB());
	}

	public Vector2f getPosition() {
		return position;
	}

	public List<Tile> getTiles() {
		return tiles;
	}

	public void addTile(Tile tile) {
		tiles.add(tile);
		//	tilesChanged = true;
	}

	public float getDarkness() {
		return darkness;
	}
}
