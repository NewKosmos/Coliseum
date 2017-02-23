package testing;

import flounder.devices.*;
import flounder.fonts.*;
import flounder.framework.*;
import flounder.maths.*;
import flounder.maths.vectors.*;
import flounder.resources.*;

public class Testing extends Framework {
	public static final Colour SKY_COLOUR_DAY = new Colour(0.0f, 0.498f, 1.0f);
	public static final Vector3f LIGHT_DIRECTION = new Vector3f(0.2f, -0.3f, -0.8f); // The starting light direction.

	public static void main(String[] args) {
		Testing testing = new Testing();
		testing.run();
		System.exit(0);
	}

	public Testing() {
		super("Deferred Shading", -1, new TestingInterface(), new TestingPlayer(), new TestingCamera(), new TestingGuis(), new TestingRenderer());
		FlounderDisplay.setup(1080, 720, "Deferred Shading", new MyFile[]{}, false, false, 0, false, false);
		TextBuilder.DEFAULT_TYPE = FlounderFonts.SEGO_UI;
	}
}
