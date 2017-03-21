package kosmos.uis;

import flounder.maths.*;

public class ConsoleDelay {
	private Timer delayTimer;
	private Timer repeatTimer;
	private boolean delayOver;

	protected ConsoleDelay() {
		this.delayTimer = new Timer(0.4);
		this.repeatTimer = new Timer(0.1);
		this.delayOver = false;
	}

	protected void update(boolean keyIsDown) {
		if (keyIsDown) {
			delayOver = delayTimer.isPassedTime();
		} else {
			delayOver = false;
			delayTimer.resetStartTime();
			repeatTimer.resetStartTime();
		}
	}

	protected boolean canInput() {
		if (delayOver && repeatTimer.isPassedTime()) {
			repeatTimer.resetStartTime();
			return true;
		}

		return false;
	}
}
