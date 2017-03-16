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
import flounder.framework.*;
import flounder.guis.*;
import flounder.logger.*;
import flounder.maths.*;
import flounder.maths.vectors.*;
import flounder.networking.*;
import flounder.profiling.*;
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
				String string = fullCommand.substring(3, fullCommand.length()).trim();

				if (!KosmosWorld.containsPlayer(string)) {
					String log = "Could not teleport to player " + string;
					FlounderLogger.log(log);
					((KosmosGuis) FlounderGuis.getGuiMaster()).getOverlayChat().addText(log, new Colour(0.8f, 0.1f, 0.0f));
					return;
				}

				Entity other = KosmosWorld.getPlayer(string);
				ComponentMultiplayer componentMultiplayer = (ComponentMultiplayer) other.getComponent(ComponentMultiplayer.ID);
				float chunkX = componentMultiplayer.getChunkX();
				float chunkZ = componentMultiplayer.getChunkZ();

				String log = "Teleporting to " + string + " in chunk [" + chunkX + ", " + chunkZ + "].";
				FlounderLogger.log(log);
				((KosmosGuis) FlounderGuis.getGuiMaster()).getOverlayChat().addText(log, new Colour(0.1f, 0.8f, 0.0f));

				FlounderCamera.getPlayer().getPosition().set(other.getPosition());
				other.setMoved();
				KosmosChunks.clear();
				KosmosChunks.setCurrent(new Chunk(KosmosChunks.getChunks(), new Vector3f(chunkX, 0.0f, chunkZ)));
			}
		}),
		TIME(new ICommand() {
			@Override
			public String commandPrefix() {
				return "time";
			}

			@Override
			public String commandDescription() {
				return "Changes the time offset of the framework (seconds).";
			}

			@Override
			public void runCommand(String fullCommand) {
				// Removes /time from the string.
				String string = fullCommand.substring(5, fullCommand.length()).trim();

				if (FlounderNetwork.getSocketClient() == null || string.isEmpty()) {
					String log = "Could not change the time offset of the framework.";
					FlounderLogger.log(log);
					((KosmosGuis) FlounderGuis.getGuiMaster()).getOverlayChat().addText(log, new Colour(0.8f, 0.1f, 0.0f));
					return;
				}

				float timeOffset = Float.parseFloat(string);

				String log = "Changing the time offset of the framework to: " + timeOffset;
				FlounderLogger.log(log);
				((KosmosGuis) FlounderGuis.getGuiMaster()).getOverlayChat().addText(log, new Colour(0.1f, 0.8f, 0.0f));

				Framework.setTimeOffset(timeOffset);
			}
		}),
		PROFILER(new ICommand() {
			@Override
			public String commandPrefix() {
				return "profiler";
			}

			@Override
			public String commandDescription() {
				return "Opens or closes the developer profiler.";
			}

			@Override
			public void runCommand(String fullCommand) {
				// Removes /profiler from the string.
				String string = fullCommand.substring(9, fullCommand.length()).trim();

				if (string.isEmpty()) {
					String log = "Could not open/close the developer profiler.";
					FlounderLogger.log(log);
					((KosmosGuis) FlounderGuis.getGuiMaster()).getOverlayChat().addText(log, new Colour(0.8f, 0.1f, 0.0f));
					return;
				}

				boolean profilerOpen = Boolean.parseBoolean(string);

				String log = "Changing the developer profiler to: " + profilerOpen;
				FlounderLogger.log(log);
				((KosmosGuis) FlounderGuis.getGuiMaster()).getOverlayChat().addText(log, new Colour(0.1f, 0.8f, 0.0f));

				FlounderProfiler.toggle(profilerOpen);
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
				FlounderLogger.log(log);
				((KosmosGuis) FlounderGuis.getGuiMaster()).getOverlayChat().addText(log, new Colour(0.1f, 0.8f, 0.0f));

				Framework.requestClose();
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
