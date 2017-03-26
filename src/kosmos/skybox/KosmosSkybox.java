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
			new MyFile(SKYBOX_FOLDER, "starsRight.png"),
			new MyFile(SKYBOX_FOLDER, "starsLeft.png"),
			new MyFile(SKYBOX_FOLDER, "starsTop.png"),
			new MyFile(SKYBOX_FOLDER, "starsBottom.png"),
			new MyFile(SKYBOX_FOLDER, "starsBack.png"),
			new MyFile(SKYBOX_FOLDER, "starsFront.png")
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
		this.cubemap.delete();
	}
}
