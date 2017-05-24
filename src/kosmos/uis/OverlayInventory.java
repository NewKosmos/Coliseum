/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved
 *
 * This source file is part of New Kosmos
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package kosmos.uis;

import flounder.guis.*;
import flounder.maths.vectors.*;

public class OverlayInventory extends ScreenObject {
	public OverlayInventory(ScreenObject parent) {
		super(parent, new Vector2f(0.5f, 0.5f), new Vector2f(1.0f, 1.0f));
		super.setInScreenCoords(false);
	}

	@Override
	public void updateObject() {
	}

	@Override
	public void deleteObject() {
	}
}
