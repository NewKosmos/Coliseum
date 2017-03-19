package kosmos;

import flounder.devices.*;
import flounder.framework.*;
import flounder.maths.*;
import flounder.networking.*;
import flounder.parsing.*;
import flounder.physics.bounding.*;
import flounder.profiling.*;
import flounder.resources.*;
import flounder.textures.*;
import kosmos.shadows.*;
import kosmos.water.*;
import org.lwjgl.glfw.*;

/**
 * A class that contains a bunch of config references.
 */
public class KosmosConfigs {
	// Main configs.
	private static final Config CONFIG_MAIN = new Config(new MyFile(Framework.getRoamingFolder(), "configs", "settings.conf"));
	public static final ConfigData PROFILER_OPEN = CONFIG_MAIN.getData(ConfigSection.DEBUG, "profilerOpen", false, "If the framework developer profiler will be open", FlounderProfiler::isOpen);
	public static final ConfigData BOUNDINGS_RENDER = CONFIG_MAIN.getData(ConfigSection.DEBUG, "boundingsRender", false, "If bounding boxes will be drawn over object", FlounderBounding::renders);

	public static final ConfigData DISPLAY_WIDTH = CONFIG_MAIN.getData(ConfigSection.GRAPHICS, "displayWidth", 1080, "Sets the displays width", FlounderDisplay::getWindowWidth);
	public static final ConfigData DISPLAY_HEIGHT = CONFIG_MAIN.getData(ConfigSection.GRAPHICS, "displayHeight", 720, "Sets the displays height", FlounderDisplay::getWindowHeight);
	public static final ConfigData DISPLAY_VSYNC = CONFIG_MAIN.getData(ConfigSection.GRAPHICS, "displayVSync", false, "Enabled display vsync as another way to limit fps", FlounderDisplay::isVSync);
	public static final ConfigData DISPLAY_ANTIALIAS = CONFIG_MAIN.getData(ConfigSection.GRAPHICS, "displayAntialias", true, "Sets the rendered imaged to render to the display with antialias", FlounderDisplay::isAntialiasing);
	public static final ConfigData DISPLAY_FULLSCREEN = CONFIG_MAIN.getData(ConfigSection.GRAPHICS, "displayFullscreen", false, "Sets the display to fullscreen mode", FlounderDisplay::isFullscreen);
	public static final ConfigData FRAMEWORK_FPS_LIMIT = CONFIG_MAIN.getData(ConfigSection.GRAPHICS, "frameworkFpsLimit", -1, "Limits the framework of the display. -1=UNLIMITED", Framework::getFpsLimit);
	public static final ConfigData TEXTURES_ANISOTROPY_MAX = CONFIG_MAIN.getData(ConfigSection.GRAPHICS, "texturesAnisotropyMax", 8.0f, "How much default texture anisotropy is applied", FlounderTextures::getAnisotropyLevel);
	public static final ConfigData RENDERER_SCALE = CONFIG_MAIN.getData(ConfigSection.GRAPHICS, "rendererScale", 1.0f, "The scale of the rendered image compared to the display"); // Reference set in master renderer.
	public static final ConfigData WATER_REFLECTION_QUALITY = CONFIG_MAIN.getData(ConfigSection.GRAPHICS, "waterReflectionQuality", 0.3f, "Quality of reflections compared to display size", KosmosWater::getReflectionQuality);
	public static final ConfigData WATER_REFLECTION_ENABLED = CONFIG_MAIN.getData(ConfigSection.GRAPHICS, "waterReflectionEnabled", false, "If there will be reflections on the water", KosmosWater::reflectionsEnabled);
	public static final ConfigData SHADOWMAP_SIZE = CONFIG_MAIN.getData(ConfigSection.GRAPHICS, "shadowmapSize", 8192, "The size of the shadowmap. Larger looks better but costs more", KosmosShadows::getShadowSize);
	public static final ConfigData SHADOWMAP_PCF = CONFIG_MAIN.getData(ConfigSection.GRAPHICS, "shadowmapPcf", 0, "How much softening on shadows will be done. Larger is softer but costs more", KosmosShadows::getShadowPCF);
	public static final ConfigData SHADOWMAP_BIAS = CONFIG_MAIN.getData(ConfigSection.GRAPHICS, "shadowmapBias", 0.001f, "How much offset shadows will have. If grain appears increase", KosmosShadows::getShadowBias);
	public static final ConfigData SHADOWMAP_DARKNESS = CONFIG_MAIN.getData(ConfigSection.GRAPHICS, "shadowmapDarkness", 0.5f, "How much a shadow effects a surface. More darkens", KosmosShadows::getShadowDarkness);

	public static final ConfigData HUD_COSSHAIR_TYPE = CONFIG_MAIN.getData(ConfigSection.CONTROLS, "hudCrosshairType", 1, "The type of crosshair to use in the hud"); // Reference set in overlay..
	public static final ConfigData CAMERA_REANGLE = CONFIG_MAIN.getData(ConfigSection.CONTROLS, "cameraReangle", GLFW.GLFW_MOUSE_BUTTON_RIGHT, "Mouse camera reangle. 1=LEFT | 2=RIGHT | 3=MIDDLE"); // Reference set in camera.

	// Server configs.
	private static final Config CONFIG_SERVER = new Config(new MyFile(Framework.getRoamingFolder(), "configs", "server.conf"));
	public static final ConfigData CLIENT_USERNAME = CONFIG_SERVER.getData(ConfigSection.GENERAL, "username", "USERNAME" + ((int) (Math.random() * 10000)), "Your username for this client", FlounderNetwork::getUsername);
	public static final ConfigData SERVER_PORT = CONFIG_SERVER.getData(ConfigSection.WORLD, "serverPort", FlounderNetwork.DEFAULT_PORT, "The port of the sever to connect to"); // Reference set in client interface.
	public static final ConfigData SERVER_IP = CONFIG_SERVER.getData(ConfigSection.GENERAL, "serverIP", "localhost", "The ip of the sever to connect to"); // Reference set in client interface.

	// Server configs.
	private static final Config CONFIG_HOST = new Config(new MyFile(Framework.getRoamingFolder(), "configs", "host.conf"));
	public static final ConfigData HOST_PORT = CONFIG_HOST.getData(ConfigSection.GENERAL, "hostPort", FlounderNetwork.getPort(), "The port to host the server off of"); // Reference set in server interface.
	public static final ConfigData HOST_SEED = CONFIG_HOST.getData(ConfigSection.GENERAL, "hostSeed", (int) Maths.randomInRange(1.0, 10000.0), "DO NOT CHANGE"); // Reference set in server interface.

	// Save configs.
	private static final Config CONFIG_SAVE0 = new Config(new MyFile(Framework.getRoamingFolder(), "saves", "save0.conf"));
	public static final ConfigData SAVE_SEED = CONFIG_SAVE0.getData(ConfigSection.WORLD, "saveSeed", (int) Maths.randomInRange(1.0, 10000.0), "DO NOT CHANGE"); // Reference set in world.
	public static final ConfigData SAVE_PLAYER_X = CONFIG_SAVE0.getData(ConfigSection.WORLD, "playerX", 0.0f, "DO NOT CHANGE"); // Reference set in world.
	public static final ConfigData SAVE_PLAYER_Y = CONFIG_SAVE0.getData(ConfigSection.WORLD, "playerY", 0.0f, "DO NOT CHANGE"); // Reference set in world.
	public static final ConfigData SAVE_PLAYER_Z = CONFIG_SAVE0.getData(ConfigSection.WORLD, "playerZ", 0.0f, "DO NOT CHANGE"); // Reference set in world.
	public static final ConfigData SAVE_CHUNK_X = CONFIG_SAVE0.getData(ConfigSection.WORLD, "chunkX", 0.0f, "DO NOT CHANGE"); // Reference set in chunks.
	public static final ConfigData SAVE_CHUNK_Z = CONFIG_SAVE0.getData(ConfigSection.WORLD, "chunkZ", 0.0f, "DO NOT CHANGE"); // Reference set in chunks.

	/**
	 * Saves the configs when closing the game.
	 */
	protected static void closeConfigs() {
		CONFIG_MAIN.save();
		CONFIG_SERVER.save();
		CONFIG_SAVE0.save();
	}
}
