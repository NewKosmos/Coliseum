/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved
 *
 * This source file is part of New Kosmos
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package kosmos.uis;

import flounder.camera.*;
import flounder.devices.*;
import flounder.entities.*;
import flounder.fonts.*;
import flounder.guis.*;
import flounder.helpers.*;
import flounder.logger.*;
import flounder.maths.*;
import flounder.maths.vectors.*;
import flounder.resources.*;
import flounder.textures.*;
import flounder.visual.*;
import kosmos.*;
import kosmos.camera.*;
import kosmos.world.*;

import java.util.*;

public class OverlayUsernames extends ScreenObject {
	private Map<String, UsernameTag> tags;

	public OverlayUsernames(ScreenObject parent) {
		super(parent, new Vector2f(0.5f, 0.5f), new Vector2f(1.0f, 1.0f));
		super.setInScreenCoords(false);

		this.tags = new HashMap<>();
	}

	@Override
	public void updateObject() {
		KosmosWorld.get().getPlayers().keySet().forEach(s -> tags.putIfAbsent(s, new UsernameTag(this, s)));

		Iterator<String> it = tags.keySet().iterator();

		while (it.hasNext()) {
			String s = it.next();
			Entity e = KosmosWorld.get().getPlayers().get(s);

			if (e == null) {
				tags.get(s).deleteObject();
				it.remove();
			} else {
				tags.get(s).compute(e);
			}
		}
	}

	@Override
	public void deleteObject() {
	}

	public static class UsernameTag extends ScreenObject {
		private Vector3f screenspace;

		private TextObject text;
		private GuiObject gui;

		public UsernameTag(ScreenObject parent, String username) {
			super(parent, new Vector2f(0.5f, 0.5f), new Vector2f(1.0f, 1.0f));
			super.setInScreenCoords(false);

			this.screenspace = new Vector3f();

			this.text = new TextObject(this, new Vector2f(0.5f, 0.5f), username, 1.0f, FlounderFonts.CANDARA, 0.2f, GuiAlign.CENTRE);
			this.text.setInScreenCoords(false);
			this.text.setColour(new Colour(1.0f, 1.0f, 1.0f, 1.0f));
			this.text.setBorderColour(new Colour(0.0f, 0.0f, 0.0f));
			this.text.setBorder(new ConstantDriver(0.175f));
			this.text.setAlphaDriver(new ConstantDriver(0.75f));

			this.gui = new GuiObject(this, this.getPosition(), new Vector2f(), TextureFactory.newBuilder().setFile(new MyFile(FlounderGuis.GUIS_LOC, "username.png")).create(), 1);
			this.gui.setInScreenCoords(false);
			this.gui.setColourOffset(new Colour());
		}

		public void compute(Entity entity) {
			// Get 2D label space.
			screenspace.set(entity.getPosition());
			screenspace.y += KosmosPlayer.PLAYER_OFFSET_Y;
			float distance = Vector3f.getDistance(screenspace, FlounderCamera.get().getCamera().getPosition());
			boolean shouldRender = distance < 30.0f;
			Maths.worldToScreenSpace(screenspace, FlounderCamera.get().getCamera().getViewMatrix(), FlounderCamera.get().getCamera().getProjectionMatrix(), this.screenspace);
			// FlounderLogger.log(screenPosition.x + ", " + screenPosition.y);

			// Updates the alpha, hides if far away.
			if (text.getColour().a == 1.0f && !shouldRender) {
				text.setAlphaDriver(new SlideDriver(text.getAlpha(), 0.0f, KosmosGuis.SLIDE_TIME));
				gui.setAlphaDriver(new SlideDriver(text.getAlpha(), 0.0f, KosmosGuis.SLIDE_TIME));
				text.getColour().a = 0.0f;
			} else if (text.getColour().a == 0.0f && shouldRender) {
				text.setAlphaDriver(new SlideDriver(text.getAlpha(), 1.0f, KosmosGuis.SLIDE_TIME));
				gui.setAlphaDriver(new SlideDriver(text.getAlpha(), 1.0f, KosmosGuis.SLIDE_TIME));
				text.getColour().a = 1.0f;
			}

			// Updates the text positioning.
			text.getPosition().set(screenspace.x + (FlounderDisplay.get().getAspectRatio() / 2.0f), -screenspace.y);

			// Update background size.
			gui.getDimensions().set(text.getMeshSize());
			gui.getDimensions().y = 0.5f * (float) text.getFont().getMaxSizeY();
			Vector2f.multiply(text.getDimensions(), gui.getDimensions(), gui.getDimensions());
			gui.getDimensions().scale(2.0f * text.getScale());
			gui.getPositionOffsets().set(text.getPositionOffsets());
			gui.getPosition().set(text.getPosition());
		}

		@Override
		public void updateObject() {

		}

		@Override
		public void deleteObject() {

		}
	}
}
