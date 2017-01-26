package coliseum.uis;

import coliseum.world.*;
import flounder.guis.*;
import flounder.helpers.*;
import flounder.maths.*;
import flounder.maths.vectors.*;
import flounder.resources.*;
import flounder.textures.*;

import java.util.*;

public class ChunkTesting extends GuiComponent {
	private Chunk chunk;
	private List<Pair<GuiTexture, Tile>> textures;

	private float minX, maxX, minY, maxY;

	public ChunkTesting() {
		this.chunk = new Chunk(new Vector2f());
		this.textures = new ArrayList<>();

		for (Tile tile : chunk.getTiles()) {
			GuiTexture texture = new GuiTexture(Texture.newTexture(new MyFile(MyFile.RES_FOLDER, "hexagon.png")).create());
			textures.add(new Pair<>(texture, tile));

			if (tile.getPosition().x < minX) {
				minX = tile.getPosition().x;
			} else if (tile.getPosition().x > maxX) {
				maxX = tile.getPosition().x;
			}

			if (tile.getPosition().y < minY) {
				minY = tile.getPosition().y;
			} else if (tile.getPosition().y > maxY) {
				maxY = tile.getPosition().y;
			}
		}

		textures.get(0).getFirst().setColourOffset(new Colour(0.2f, 0.1f, 0.1f));
	}

	@Override
	protected void updateSelf() {
		for (Pair<GuiTexture, Tile> pair : textures) {
			float x = pair.getSecond().getPosition().x + (maxX - minX);
			float y = pair.getSecond().getPosition().y + (maxY - minY);

			float w = 0.1f;
			float h = 0.1f;

			pair.getFirst().setPosition(x, y, w, h);
			pair.getFirst().update();
		}
	}

	@Override
	protected void getGuiTextures(List<GuiTexture> guiTextures) {
		for (Pair<GuiTexture, Tile> pair : textures) {
			guiTextures.add(pair.getFirst());
		}
	}
}
