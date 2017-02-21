package deferred;

import flounder.framework.*;
import flounder.maths.*;
import flounder.maths.vectors.*;

/**
 * Created by matthew on 21/02/17.
 */
public class DeferredShading extends Framework {
	public static final Colour SKY_COLOUR_DAY = new Colour(0.0f, 0.498f, 1.0f);
	public static final Vector3f LIGHT_DIRECTION = new Vector3f(0.2f, -0.3f, -0.8f); // The starting light direction.

	public static void main(String[] args) {
		DeferredShading df = new DeferredShading();
		df.run();
		System.exit(0);
	}

	public DeferredShading() {
		super("Deferred Shading", -1, new DeferredInterface(), new DeferredPlayer(), new DeferredCamera(), new DeferredRenderer());
	}
}
