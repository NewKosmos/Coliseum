/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved
 *
 * This source file is part of New Kosmos
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package kosmos.entities.components;

import flounder.camera.*;
import flounder.entities.*;
import flounder.entities.components.*;
import flounder.maths.*;
import flounder.maths.vectors.*;
import flounder.networking.*;
import flounder.physics.*;
import kosmos.network.*;

public class ComponentPlayer extends IComponentEntity {
	public static final int ID = EntityIDAssigner.getId();

	private Vector3f lastPosition;
	private Vector3f lastRotation;
	private int lastPlayerCount;
	private boolean needSendData;

	private Timer timer;

	public ComponentPlayer(Entity entity) {
		super(entity, ID);
		this.lastPosition = new Vector3f();
		this.lastRotation = new Vector3f();
		this.lastPlayerCount = 0;
		this.needSendData = true;

		this.timer = new Timer(0.05); // 20 ticks per second.
	}

	@Override
	public void update() {
		if (ComponentMultiplayer.players.size() != lastPlayerCount) {
			lastPlayerCount = ComponentMultiplayer.players.size();
			sendData();
		}

		if (FlounderCamera.getPlayer() == null) {
			return;
		}

		getEntity().getPosition().set(FlounderCamera.getPlayer().getPosition());
		getEntity().getRotation().set(FlounderCamera.getPlayer().getRotation());

		if (!getEntity().getPosition().equals(lastPosition) || !getEntity().getRotation().equals(lastRotation)) {
			getEntity().setMoved();

			if (!needSendData) {
				needSendData = true;
				timer.resetStartTime();
			}

			lastPosition.set(getEntity().getPosition());
			lastRotation.set(getEntity().getRotation());
		}

		if (needSendData && timer.isPassedTime()) {
			needSendData = false;
			sendData();
		}
	}

	private void sendData() {
		new PacketMove(FlounderNetwork.getUsername(), getEntity().getPosition(), getEntity().getRotation()).writeData(FlounderNetwork.getSocketClient());
	}

	@Override
	public IBounding getBounding() {
		return null;
	}

	@Override
	public void dispose() {

	}
}
