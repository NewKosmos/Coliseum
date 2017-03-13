/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved
 *
 * This source file is part of New Kosmos
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package kosmos.uis;

import flounder.camera.*;
import flounder.entities.*;
import flounder.guis.*;
import flounder.logger.*;
import flounder.maths.*;
import flounder.maths.vectors.*;
import kosmos.*;
import kosmos.chunks.*;
import kosmos.entities.components.*;
import kosmos.world.*;

public interface ICommand {
	String commandPrefix();

	String commandDescription();

	void runCommand(String fullCommand);

	public enum ConsoleCommands {
		H(new ICommand() {
			@Override
			public String commandPrefix() {
				return "h";
			}

			@Override
			public String commandDescription() {
				return "Provides descriptions to commands that can be run.";
			}

			@Override
			public void runCommand(String fullCommand) {
				//	((KosmosGuis) FlounderGuis.getGuiMaster()).getOverlayChat().addText("Type in plain text to create a message, hit enter to send, escape for discarding or editing.", new Colour(0.81f, 0.37f, 0.24f));
				//	((KosmosGuis) FlounderGuis.getGuiMaster()).getOverlayChat().addText("To find command type '/h', to enter commands enter '/command params'.", new Colour(0.81f, 0.37f, 0.24f));

				for (ConsoleCommands commands : ConsoleCommands.values()) {
					((KosmosGuis) FlounderGuis.getGuiMaster()).getOverlayChat().addText("    [" + commands.getCommand().commandPrefix() + "]: " + commands.getCommand().commandDescription(), new Colour(0.81f, 0.81f, 0.81f));
				}
			}
		}),
		TP(new ICommand() {
			@Override
			public String commandPrefix() {
				return "tp";
			}

			@Override
			public String commandDescription() {
				return "Teleports you to the player username given.";
			}

			@Override
			public void runCommand(String fullCommand) {
				// Removes /tp from the string.
				String player = fullCommand.substring(3, fullCommand.length()).trim();

				if (!KosmosWorld.containsPlayer(player)) {
					String log = "Could not teleport to player " + player;
					FlounderLogger.log(log);
					((KosmosGuis) FlounderGuis.getGuiMaster()).getOverlayChat().addText(log, new Colour(0.8f, 0.1f, 0.0f));
					return;
				}

				Entity other = KosmosWorld.getPlayer(player);
				ComponentMultiplayer componentMultiplayer = (ComponentMultiplayer) other.getComponent(ComponentMultiplayer.ID);
				float chunkX = componentMultiplayer.getChunkX();
				float chunkZ = componentMultiplayer.getChunkZ();

				String log = "Teleporting to " + player + " in chunk [" + chunkX + ", " + chunkZ + "].";
				FlounderLogger.log(log);
				((KosmosGuis) FlounderGuis.getGuiMaster()).getOverlayChat().addText(log, new Colour(0.1f, 0.8f, 0.0f));

				FlounderCamera.getPlayer().getPosition().set(other.getPosition());
				other.setMoved();
				KosmosChunks.clear();
				KosmosChunks.setCurrent(new Chunk(KosmosChunks.getChunks(), new Vector3f(chunkX, 0.0f, chunkZ)));
			}
		});

		private ICommand command;

		ConsoleCommands(ICommand command) {
			this.command = command;
		}

		public ICommand getCommand() {
			return command;
		}
	}
}
