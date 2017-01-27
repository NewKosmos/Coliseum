package coliseum.uis;

import coliseum.world.*;
import flounder.devices.*;
import flounder.guis.*;
import flounder.helpers.*;
import flounder.maths.*;
import flounder.maths.vectors.*;
import flounder.resources.*;
import flounder.textures.*;

import java.util.*;

public class ChunkTesting extends GuiComponent {
	private Map<Chunk, List<Pair<GuiTexture, Tile>>> chunks;

	public ChunkTesting() {
		this.chunks = new HashMap<>();

		for (int i = 0; i < 4; i ++) {
			for (int j = 0; j < 3; j ++) {
				float iOff = (j % 2 == 0) ? 0.5f : 0.0f;
				float x = (i + iOff) * (Chunk.CHUNK_RADIUS - 0.5f) * Tile.SIDE_LENGTH * 5.5f;
				float y = j * (Chunk.CHUNK_RADIUS - 0.5f) * Tile.SIDE_LENGTH * 1.5f;
				Chunk chunk = new Chunk(new Vector2f(x, y));
				List<Pair<GuiTexture, Tile>> textures = new ArrayList<>();

				for (Tile tile : chunk.getTiles()) {
					GuiTexture texture = new GuiTexture(Texture.newTexture(new MyFile(MyFile.RES_FOLDER, "hexagon.png")).create());
					textures.add(new Pair<>(texture, tile));
				}

				chunks.put(chunk, textures);
			}
		}
	}

	@Override
	protected void updateSelf() {
		for (Chunk chunk : chunks.keySet()) {
			for (Pair<GuiTexture, Tile> pair : chunks.get(chunk)) {
				float x = pair.getSecond().getPosition().x + (0.1f * FlounderDisplay.getAspectRatio());
				float y = pair.getSecond().getPosition().y + 0.1f;
				float t = 2.0f * Tile.SIDE_LENGTH;

				pair.getFirst().setColourOffset(new Colour(-x, (pair.equals(chunks.get(chunk).get(0))) ? -0.75f : -0.35f, -y));
				pair.getFirst().setPosition(x, y, t, t);
				pair.getFirst().update();
			}
		}
	}

	@Override
	protected void getGuiTextures(List<GuiTexture> guiTextures) {
		for (Chunk chunk : chunks.keySet()) {
			for (Pair<GuiTexture, Tile> pair : chunks.get(chunk)) {
				guiTextures.add(pair.getFirst());
			}
		}
	}
}
