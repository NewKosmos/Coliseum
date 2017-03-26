package kosmos.skybox;

import flounder.camera.*;
import flounder.framework.*;
import flounder.loaders.*;
import flounder.maths.matrices.*;
import flounder.physics.bounding.*;
import flounder.resources.*;
import flounder.shaders.*;
import flounder.textures.*;
import kosmos.world.*;

public class KosmosSkybox extends Module {
	private static final KosmosSkybox INSTANCE = new KosmosSkybox();
	public static final String PROFILE_TAB_NAME = "Kosmos Skybox";

	public static final MyFile SKYBOX_FOLDER = new MyFile(MyFile.RES_FOLDER, "skybox");

	private static MyFile[] TEXTURE_FILES = {
			new MyFile(SKYBOX_FOLDER, "starsRight.png"),
			new MyFile(SKYBOX_FOLDER, "starsLeft.png"),
			new MyFile(SKYBOX_FOLDER, "starsTop.png"),
			new MyFile(SKYBOX_FOLDER, "starsBottom.png"),
			new MyFile(SKYBOX_FOLDER, "starsBack.png"),
			new MyFile(SKYBOX_FOLDER, "starsFront.png")
	};

	private TextureObject cubemap;
	private Matrix4f modelMatrix;

	public KosmosSkybox() {
		super(ModuleUpdate.UPDATE_PRE, PROFILE_TAB_NAME, FlounderBounding.class, FlounderLoader.class, FlounderTextures.class, FlounderShaders.class);
	}

	@Override
	public void init() {
		this.cubemap = TextureFactory.newBuilder().setCubemap(TEXTURE_FILES).create();
		this.modelMatrix = new Matrix4f();
	}

	@Override
	public void update() {
		if (FlounderCamera.getCamera() != null) {
			Matrix4f.transformationMatrix(FlounderCamera.getCamera().getPosition(), KosmosWorld.getLightRotation(), 1.0f, modelMatrix);
		}
	}

	@Override
	public void profile() {
	}

	public static TextureObject getCubemap() {
		return INSTANCE.cubemap;
	}

	public static Matrix4f getModelMatrix() {
		return INSTANCE.modelMatrix;
	}

	@Override
	public Module getInstance() {
		return INSTANCE;
	}

	@Override
	public void dispose() {
		this.cubemap.delete();
	}
}
