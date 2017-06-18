/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved.
 *
 * This source file is part of New Kosmos.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package kosmos.world.saving;

import flounder.framework.*;
import flounder.helpers.*;
import flounder.logger.*;
import flounder.maths.vectors.*;
import kosmos.*;

import java.io.*;
import java.util.*;

public class Save {
	private int seed;
	private Map<String, Pair<Vector3f, Vector3f>> players;
	private List<ChunkData> chunkData;

	public Save(int seed, Map<String, Pair<Vector3f, Vector3f>> players, List<ChunkData> chunkData) {
		this.seed = seed;
		this.players = players;
		this.chunkData = chunkData;
	}

	public int getSeed() {
		return seed;
	}

	public Vector3f getPlayerPosition(String username) {
		if (!players.containsKey(username)) {
			players.put(username, new Pair<>(new Vector3f(), new Vector3f()));
		}

		return players.get(username).getFirst();
	}

	public Vector3f getPlayerChunk(String username) {
		if (!players.containsKey(username)) {
			players.put(username, new Pair<>(new Vector3f(), new Vector3f()));
		}

		return players.get(username).getSecond();
	}

	public List<ChunkData> getChunkData() {
		return chunkData;
	}

	public void save(String path) {
		try {
			// The save file and the writers.
			File saveFile = new File(Framework.get().getRoamingFolder().getPath() + "/saves/" + path);
			saveFile.createNewFile();
			FileWriter fileWriter = new FileWriter(saveFile);
			FileWriterHelper fileWriterHelper = new FileWriterHelper(fileWriter);

			// Date and save info.
			String savedDate = Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + "." + (Calendar.getInstance().get(Calendar.MONTH) + 1) + "." + Calendar.getInstance().get(Calendar.YEAR) + " - " + Calendar.getInstance().get(Calendar.HOUR_OF_DAY) + ":" + Calendar.getInstance().get(Calendar.MINUTE);
			fileWriterHelper.addComment("Automatically generated save file.", "Date generated: " + savedDate);

			// Versioning and seed.
			fileWriterHelper.beginNewSegment("save");
			{
				fileWriterHelper.writeSegmentData("version = " + NewKosmos.VERSION + ";", true);
				fileWriterHelper.writeSegmentData("seed = " + seed + ";");
			}
			fileWriterHelper.endSegment(false);

			// Player data.
			fileWriterHelper.beginNewSegment("players");
			{
				for (String username : players.keySet()) {
					Pair<Vector3f, Vector3f> data = players.get(username);
					fileWriterHelper.writeSegmentData("\'" + username + "\', " + data.getFirst().x + ", " + data.getFirst().y + ", " + data.getFirst().z + ", ");
					fileWriterHelper.writeSegmentData(data.getSecond().x + ", " + data.getSecond().y + ", " + data.getSecond().z, true);
				}
			}
			fileWriterHelper.endSegment(false);

			// Chunk data.
			fileWriterHelper.beginNewSegment("chunks");
			{
				for (ChunkData c : chunkData) {
					fileWriterHelper.writeSegmentData(c.getSaveData(), true);
				}
			}
			fileWriterHelper.endSegment(true);

			// Closes the file for writing.
			fileWriter.close();
		} catch (IOException e) {
			FlounderLogger.get().exception(e);
		}
	}
}
