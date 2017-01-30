package coliseum.uis.console;

import flounder.fonts.*;
import flounder.guis.*;
import flounder.maths.*;

import java.util.*;

public class ConsoleText extends GuiComponent {
	private List<Text> texts;
	private List<IConsoleCommand> commands;

	protected ConsoleText() {
		this.texts = new ArrayList<>();
		this.commands = new ArrayList<>();
	}

	@Override
	protected void updateSelf() {
	}

	@Override
	protected void getGuiTextures(List<GuiTexture> guiTextures) {
	}

	public void addText(String string, Colour colour) {
		Text text = Text.newText(" > " + string).textAlign(GuiAlign.LEFT).setFontSize(0.8f).setFont(FlounderFonts.SEGO_UI).create();
		text.setColour(colour);
		addText(text, 0.01f, 0.02f + (texts.size() * 0.03f), 1.0f);
		texts.add(text);

		if (string.contains("/") && string.contains(" ")) {
			String name = string.trim();
			name = name.substring(1, name.length()).trim();
			name = name.split(" ")[0];

			for (IConsoleCommand command : commands) {
				if (command.isCommandMatch(name)) {
					command.runCommand(string.trim());
				}
			}
		}
	}

	public void addCommand(IConsoleCommand command) {
		commands.add(command);
	}
}
