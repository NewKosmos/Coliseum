package testing;

import flounder.camera.*;
import flounder.maths.vectors.*;

public class TestingPlayer extends Player {
	private Vector3f position;
	private Vector3f rotation;

	public TestingPlayer() {
		super();
	}

	@Override
	public void init() {
		position = new Vector3f();
		rotation = new Vector3f();
	}

	@Override
	public void update() {

	}

	@Override
	public Vector3f getPosition() {
		return position;
	}

	@Override
	public Vector3f getRotation() {
		return rotation;
	}

	@Override
	public boolean isActive() {
		return true;
	}
}
