package coliseum.camera;

import flounder.camera.*;
import flounder.maths.vectors.*;

public class PlayerBasic extends IPlayer {
	private Vector3f position;
	private Vector3f rotation;

	public PlayerBasic() {
		super();
	}

	@Override
	public void init() {
		this.position = new Vector3f(0,5,0);
		this.rotation = new Vector3f();
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
