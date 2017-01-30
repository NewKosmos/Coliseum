package coliseum.uis.console;

public interface IConsoleCommand {
	boolean isCommandMatch(String name);

	void runCommand(String fullCommand);
}
