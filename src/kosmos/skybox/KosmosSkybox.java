package kosmos.skybox;

import flounder.framework.*;
import flounder.physics.bounding.*;
import flounder.resources.*;
import flounder.textures.*;

public class KosmosSkybox extends Module {
	private static final KosmosSkybox INSTANCE = new KosmosSkybox();
	public static final String PROFILE_TAB_NAME = "Kosmos Skybox";

	public static final MyFile SKYBOX_FOLDER = new MyFile(MyFile.RES_FOLDER, "skybox");

	private static MyFile[] TEXTURE_FILES = {
			new MyFile(SKYBOX_FOLDER, "nightRight.png"),
			new MyFile(SKYBOX_FOLDER, "nightLeft.png"),
			new MyFile(SKYBOX_FOLDER, "nightTop.png"),
			new MyFile(SKYBOX_FOLDER, "nightBottom.png"),
			new MyFile(SKYBOX_FOLDER, "nightBack.png"),
			new MyFile(SKYBOX_FOLDER, "nightFront.png")
	};

	private TextureObject cubemap;

	public KosmosSkybox() {
		super(ModuleUpdate.UPDATE_PRE, PROFILE_TAB_NAME, FlounderBounding.class);
	}

	@Override
	public void init() {
		this.cubemap = TextureFactory.newBuilder().setCubemap(TEXTURE_FILES).create();
	}

	@Override
	public void update() {
	}

	@Override
	public void profile() {
	}

	public static TextureObject getCubemap() {
		return INSTANCE.cubemap;
	}

	@Override
	public Module getInstance() {
		return INSTANCE;
	}

	@Override
	public void dispose() {
	}
}
