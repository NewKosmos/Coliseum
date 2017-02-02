package coliseum.camera;

import flounder.camera.*;
import flounder.framework.*;
import flounder.inputs.*;
import flounder.maths.*;
import flounder.maths.vectors.*;
import org.lwjgl.glfw.*;

public class PlayerBasic extends IPlayer {
	private static final float RUN_SPEED = 10.0f;
	private static final float TURN_SPEED = 100.0f;

	private Vector3f position;
	private Vector3f rotation;

	private float currentSpeed;
	private float currentTurnSpeed;
	private IAxis inputForward;
	private IAxis inputTurn;

	public PlayerBasic() {
		super();
	}

	@Override
	public void init() {
		this.position = new Vector3f(0.0f, 0.0f, 0.0f);
		this.rotation = new Vector3f();

		IButton leftKeyButtons = new KeyButton(GLFW.GLFW_KEY_A, GLFW.GLFW_KEY_LEFT);
		IButton rightKeyButtons = new KeyButton(GLFW.GLFW_KEY_D, GLFW.GLFW_KEY_RIGHT);
		IButton upKeyButtons = new KeyButton(GLFW.GLFW_KEY_W, GLFW.GLFW_KEY_UP);
		IButton downKeyButtons = new KeyButton(GLFW.GLFW_KEY_S, GLFW.GLFW_KEY_DOWN);

		this.currentSpeed = 0.0f;
		this.currentTurnSpeed = 0.0f;
		this.inputForward = new CompoundAxis(new ButtonAxis(upKeyButtons, downKeyButtons), new JoystickAxis(0, 1));
		this.inputTurn = new CompoundAxis(new ButtonAxis(leftKeyButtons, rightKeyButtons), new JoystickAxis(0, 0));
	}

	@Override
	public void update() {
		currentSpeed = -RUN_SPEED * Maths.deadband(0.05f, inputForward.getAmount());
		currentTurnSpeed = -TURN_SPEED * Maths.deadband(0.05f, inputTurn.getAmount());
		float distance = currentSpeed * FlounderFramework.getDelta();
		float dx = (float) (distance * Math.sin(Math.toRadians(rotation.y)));
		float dz = (float) (distance * Math.cos(Math.toRadians(rotation.y)));
		float ry = currentTurnSpeed * FlounderFramework.getDelta();

		position.x += dx;
		position.z += dz;
		rotation.y += ry;
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