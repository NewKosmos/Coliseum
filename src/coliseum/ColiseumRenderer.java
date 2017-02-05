package coliseum;

import coliseum.entities.*;
import coliseum.particles.*;
import coliseum.shadows.*;
import coliseum.skybox.*;
import coliseum.water.*;
import coliseum.world.*;
import flounder.camera.*;
import flounder.devices.*;
import flounder.events.*;
import flounder.fbos.*;
import flounder.fonts.*;
import flounder.guis.*;
import flounder.helpers.*;
import flounder.inputs.*;
import flounder.logger.*;
import flounder.maths.*;
import flounder.maths.vectors.*;
import flounder.physics.bounding.*;
import flounder.post.filters.*;
import flounder.profiling.*;
import flounder.renderer.*;
import org.lwjgl.glfw.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

public class ColiseumRenderer extends IRendererMaster {
	private static final Vector4f POSITIVE_INFINITY = new Vector4f(0.0f, 1.0f, 0.0f, Float.POSITIVE_INFINITY);
	private static final Colour CLEAR_COLOUR = new Colour(0.0f, 0.0f, 0.0f);

	private ShadowRenderer shadowRenderer;
	private SkyboxRenderer skyboxRenderer;
	private EntitiesRenderer entitiesRenderer;
	private ParticleRenderer particleRenderer;
	private WaterRenderer waterRenderer;
	private BoundingRenderer boundingRenderer;
	private GuisRenderer guisRenderer;
	private FontRenderer fontRenderer;

	private FBO rendererFBO;

	private FilterFXAA filterFXAA;
	private FilterPixel filterPixel;
	private FilterCRT filterCRT;
	private FilterLensFlare filterLensFlare;
	private FilterTiltShift filterTiltShift;
	private int effect;

	public ColiseumRenderer() {
		super(FlounderLogger.class, FlounderProfiler.class, FlounderDisplay.class);
	}

	@Override
	public void init() {
		this.shadowRenderer = new ShadowRenderer();
		this.skyboxRenderer = new SkyboxRenderer();
		this.entitiesRenderer = new EntitiesRenderer();
		this.particleRenderer = new ParticleRenderer();
		this.waterRenderer = new WaterRenderer();
		this.boundingRenderer = new BoundingRenderer();
		this.guisRenderer = new GuisRenderer();
		this.fontRenderer = new FontRenderer();

		this.rendererFBO = FBO.newFBO(1.0f).depthBuffer(DepthBufferType.TEXTURE).create();

		this.filterFXAA = new FilterFXAA();
		this.filterPixel = new FilterPixel(4.0f);
		this.filterCRT = new FilterCRT(new Colour(0.5f, 1.0f, 0.5f), 0.175f, 0.175f, 1024.0f, 0.05f);
		this.filterLensFlare = new FilterLensFlare();
		this.filterTiltShift = new FilterTiltShift(0.75f, 1.1f, 0.004f, 3.0f);
		this.effect = 2;

		FlounderEvents.addEvent(new IEvent() {
			private KeyButton c = new KeyButton(GLFW.GLFW_KEY_C);

			@Override
			public boolean eventTriggered() {
				return c.wasDown();
			}

			@Override
			public void onEvent() {
				effect++;

				if (effect > 3) {
					effect = 0;
				}
			}
		});
	}

	@Override
	public void render() {
		/* Water Reflection & Refraction */
		if (waterRenderer.reflectionsEnabled()) {
			FlounderCamera.getCamera().reflect(waterRenderer.getWater().getPosition().y);
			shadowRenderer.render(POSITIVE_INFINITY, FlounderCamera.getCamera());

			glEnable(GL_CLIP_DISTANCE0);
			{
				waterRenderer.getReflectionFBO().bindFrameBuffer();
				renderScene(new Vector4f(0.0f, 1.0f, 0.0f, -waterRenderer.getWater().getPosition().y), CLEAR_COLOUR, true);
				waterRenderer.getReflectionFBO().unbindFrameBuffer();
			}
			glDisable(GL_CLIP_DISTANCE0);

			FlounderCamera.getCamera().reflect(waterRenderer.getWater().getPosition().y);
		}

		/* Shadow rendering. */
		shadowRenderer.render(POSITIVE_INFINITY, FlounderCamera.getCamera());

		/* Binds the relevant FBO. */
		bindRelevantFBO();

		/* Scene rendering. */
		renderScene(POSITIVE_INFINITY, CLEAR_COLOUR, false);

		/* Post rendering. */
		renderPost(FlounderGuis.getGuiMaster().isGamePaused(), FlounderGuis.getGuiMaster().getBlurFactor());

		/* Scene independents. */
		// renderIndependents();

		/* Unbinds the FBO. */
		unbindRelevantFBO();
	}

	private void bindRelevantFBO() {
		rendererFBO.bindFrameBuffer();
	}

	private void unbindRelevantFBO() {
		rendererFBO.unbindFrameBuffer();
	}

	public ShadowRenderer getShadowRenderer() {
		return shadowRenderer;
	}

	private void renderScene(Vector4f clipPlane, Colour clearColour, boolean waterPass) {
		/* Clear and update. */
		ICamera camera = FlounderCamera.getCamera();
		OpenGlUtils.prepareNewRenderParse(clearColour);

		skyboxRenderer.render(clipPlane, camera);
		entitiesRenderer.render(clipPlane, camera);
		particleRenderer.render(clipPlane, camera);

		if (!waterPass) {
			waterRenderer.render(clipPlane, camera);
			boundingRenderer.render(clipPlane, camera);
		}
	}

	private void renderIndependents() {
		guisRenderer.render(POSITIVE_INFINITY, FlounderCamera.getCamera());
		fontRenderer.render(POSITIVE_INFINITY, FlounderCamera.getCamera());
	}

	private void renderPost(boolean isPaused, float blurFactor) {
		boolean independentsRendered = false;
		FBO output = rendererFBO;

		switch (effect) {
			case 0:
				break;
			case 1:
				filterTiltShift.applyFilter(output.getColourTexture(0));
				output = filterTiltShift.fbo;
				break;
			case 2:
				filterLensFlare.setSunPositon(ColiseumWorld.getEntitySun().getPosition());
				filterLensFlare.applyFilter(output.getColourTexture(0));
				output = filterLensFlare.fbo;
				break;
			case 3:
				/* Scene independents. */
				renderIndependents();
				independentsRendered = true;
				filterPixel.applyFilter(output.getColourTexture(0));
				output = filterPixel.fbo;
				filterCRT.applyFilter(output.getColourTexture(0));
				output = filterCRT.fbo;
				break;
		}

		if (FlounderDisplay.isAntialiasing()) {
			filterFXAA.applyFilter(output.getColourTexture(0));
			output = filterFXAA.fbo;
		}

		output.blitToScreen();

		if (!independentsRendered) {
			/* Scene independents. */
			renderIndependents();
		}
	}

	@Override
	public void profile() {
	}

	@Override
	public void dispose() {
		shadowRenderer.dispose();
		skyboxRenderer.dispose();
		particleRenderer.dispose();
		entitiesRenderer.dispose();
		waterRenderer.dispose();
		boundingRenderer.dispose();
		guisRenderer.dispose();
		fontRenderer.dispose();

		rendererFBO.delete();

		filterFXAA.dispose();
		filterPixel.dispose();
		filterCRT.dispose();
		filterLensFlare.dispose();
		filterTiltShift.dispose();
	}

	@Override
	public boolean isActive() {
		return true;
	}
}
