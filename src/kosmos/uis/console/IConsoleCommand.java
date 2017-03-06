/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved
 *
 * This source file is part of New Kosmos
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package kosmos.uis.console;

import flounder.camera.*;
import flounder.entities.*;
import flounder.logger.*;
import flounder.maths.vectors.*;
import kosmos.chunks.*;
import kosmos.entities.components.*;
import kosmos.world.*;

public interface IConsoleCommand {
	String commandPrefix();

	void runCommand(String fullCommand);

	public enum ConsoleCommands {
		TP(new IConsoleCommand() {
			@Override
			public String commandPrefix() {
				return "tp";
			}

			@Override
			public void runCommand(String fullCommand) {
				String player = fullCommand.substring(2, fullCommand.length()).trim();

				if (!KosmosWorld.containsPlayer(player)) {
					FlounderLogger.log("Could not teleport to player " + player);
					return;
				}

				Entity other = KosmosWorld.getPlayer(player);
				ComponentMultiplayer componentMultiplayer = (ComponentMultiplayer) other.getComponent(ComponentMultiplayer.ID);
				float chunkX = componentMultiplayer.getChunkX();
				float chunkZ = componentMultiplayer.getChunkZ();

				FlounderLogger.log("Teleporting to " + player + " in chunk [" + chunkX + ", " + chunkZ + "].");

				FlounderCamera.getPlayer().getPosition().set(other.getPosition());
				other.setMoved();
				KosmosChunks.clear();
				KosmosChunks.setCurrent(new Chunk(KosmosChunks.getChunks(), new Vector3f(chunkX, 0.0f, chunkZ)));
			}
		}),
		MEME(new IConsoleCommand() {
			@Override
			public String commandPrefix() {
				return "meme";
			}

			@Override
			public void runCommand(String fullCommand) {
				String meme = fullCommand.substring(4, fullCommand.length()).trim();
				FlounderLogger.log("Memeing " + meme);
			}
		});

		private IConsoleCommand command;

		ConsoleCommands(IConsoleCommand command) {
			this.command = command;
		}

		public IConsoleCommand getCommand() {
			return command;
		}

		public static void runCommand(String command) {
			String[] data = command.split("\\s+");

			for (ConsoleCommands console : values()) {
				if (console.command.commandPrefix().equals(data[0])) {
					console.command.runCommand(command);
					return;
				}
			}
		}
	}
}
