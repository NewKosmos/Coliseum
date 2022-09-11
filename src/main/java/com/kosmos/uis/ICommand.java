/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved.
 *
 * This source file is part of New Kosmos.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 * Proprietary and confidential.
 */

package com.kosmos.uis;

import com.flounder.camera.*;
import com.flounder.entities.*;
import com.flounder.framework.*;
import com.flounder.logger.*;
import com.flounder.maths.*;
import com.flounder.maths.vectors.*;
import com.flounder.networking.*;
import com.kosmos.camera.*;
import com.kosmos.entities.components.*;
import com.kosmos.world.*;
import com.kosmos.world.chunks.*;

public interface ICommand {
	String commandPrefix();

	String commandDescription();

	void runCommand(String fullCommand);

	enum ConsoleCommands {
		HELP(new ICommand() {
			@Override
			public String commandPrefix() {
				return "help";
			}

			@Override
			public String commandDescription() {
				return "Provides descriptions to commands that can be run.";
			}

			@Override
			public void runCommand(String fullCommand) {
				for (ConsoleCommands commands : ConsoleCommands.values()) {
					String log = "    [" + commands.getCommand().commandPrefix() + "]: " + commands.getCommand().commandDescription();
					OverlayChat.addText(log, new Colour(0.81f, 0.81f, 0.81f));
				}
			}
		}),
		PLAYERS(new ICommand() {
			@Override
			public String commandPrefix() {
				return "players";
			}

			@Override
			public String commandDescription() {
				return "Lists all the players connected to the server.";
			}

			@Override
			public void runCommand(String fullCommand) {
				for (String username : KosmosWorld.get().getPlayers().keySet()) {
					String log = "    [" + username + "]: " + KosmosWorld.get().getPlayers().get(username).toString();
					OverlayChat.addText(log, new Colour(0.81f, 0.81f, 0.81f));
				}

				if (FlounderNetwork.get().getSocketClient() == null) {
					String log = "    You are in single player! No other players connected.";
					OverlayChat.addText(log, new Colour(0.81f, 0.81f, 0.81f));
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
				String string = fullCommand.substring(3, fullCommand.length()).trim();

				if (!KosmosWorld.get().containsPlayer(string)) {
					String log = "Could not teleport to player " + string;
					FlounderLogger.get().log(log);
					OverlayChat.addText(log, new Colour(0.8f, 0.1f, 0.0f));
					return;
				}

				Entity other = KosmosWorld.get().getPlayer(string);
				ComponentMultiplayer componentMultiplayer = (ComponentMultiplayer) other.getComponent(ComponentMultiplayer.class);
				float chunkX = componentMultiplayer.getChunkX();
				float chunkZ = componentMultiplayer.getChunkZ();

				String log = "Teleporting to " + string + " in chunk [" + chunkX + ", " + chunkZ + "].";
				FlounderLogger.get().log(log);
				OverlayChat.addText(log, new Colour(0.1f, 0.8f, 0.0f));

				KosmosWorld.get().getEntityPlayer().getPosition().set(other.getPosition());
				KosmosChunks.get().clear(true);
				KosmosChunks.get().setCurrent(new Chunk(FlounderEntities.get().getEntities(), new Vector3f(chunkX, 0.0f, chunkZ)));
			}
		}),
		TIME(new ICommand() {
			@Override
			public String commandPrefix() {
				return "time";
			}

			@Override
			public String commandDescription() {
				return "Adds to the time offset of the framework (seconds).";
			}

			@Override
			public void runCommand(String fullCommand) {
				// Removes /time from the string.
				String string = fullCommand.substring(5, fullCommand.length()).trim();

				if (FlounderNetwork.get().getSocketClient() != null || string.isEmpty()) {
					String log = "Could not change the time offset of the framework.";
					FlounderLogger.get().log(log);
					OverlayChat.addText(log, new Colour(0.8f, 0.1f, 0.0f));
					return;
				}

				float timeOffset = Math.abs(Float.parseFloat(string));
				timeOffset = Maths.clamp(timeOffset, 0.0f, 14449.0f);

				String log = "Adding " + timeOffset + " to the time offset of the framework.";
				FlounderLogger.get().log(log);
				OverlayChat.addText(log, new Colour(0.1f, 0.8f, 0.0f));

				Framework.get().setTimeOffset(Framework.get().getTimeOffset() + timeOffset);
			}
		}),
		NOCLIP(new ICommand() {
			@Override
			public String commandPrefix() {
				return "noclip";
			}

			@Override
			public String commandDescription() {
				return "Enables collisionless noclip mode.";
			}

			@Override
			public void runCommand(String fullCommand) {
				boolean enable = !((KosmosPlayer) FlounderCamera.get().getPlayer()).isNoclipEnabled();
				((KosmosPlayer) FlounderCamera.get().getPlayer()).setNoclipEnabled(enable);

				String log = "Setting noclip mode to: " + enable;
				FlounderLogger.get().log(log);
				OverlayChat.addText(log, new Colour(0.1f, 0.8f, 0.0f));
			}
		}),
		EXIT(new ICommand() {
			@Override
			public String commandPrefix() {
				return "exit";
			}

			@Override
			public String commandDescription() {
				return "Quits the game to the desktop.";
			}

			@Override
			public void runCommand(String fullCommand) {
				String log = "Requesting to exit the game to desktop.";
				FlounderLogger.get().log(log);
				OverlayChat.addText(log, new Colour(0.1f, 0.8f, 0.0f));

				Framework.get().requestClose(false);
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
