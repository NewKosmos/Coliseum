/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved
 *
 * This source file is part of New Kosmos
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package kosmos.uis.console;

import flounder.maths.*;

public class ConsoleDelay {
	private Timer delayTimer;
	private Timer repeatTimer;
	private boolean delayOver;

	protected ConsoleDelay() {
		this.delayTimer = new Timer(0.35);
		this.repeatTimer = new Timer(0.05);
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