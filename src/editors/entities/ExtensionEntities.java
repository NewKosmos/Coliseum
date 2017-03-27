/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved
 *
 * This source file is part of New Kosmos
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package editors.entities;

import editors.editor.*;
import flounder.camera.*;
import flounder.devices.*;
import flounder.entities.*;
import flounder.entities.components.*;
import flounder.framework.*;
import flounder.helpers.*;
import flounder.maths.vectors.*;
import flounder.models.*;
import flounder.physics.bounding.*;
import flounder.resources.*;
import flounder.textures.*;
import kosmos.entities.components.*;
import kosmos.particles.*;
import kosmos.shadows.*;
import kosmos.skybox.*;
import kosmos.world.*;

import javax.swing.*;

public class ExtensionEntities extends IEditorType {
	private static boolean ACTIVE = false;

	public boolean entityRotate;
	public boolean polygonMode;
	public String entityName;
	public String loadFromEntity;

	public Entity focusEntity;

	public ExtensionEntities() {
		super(FlounderDisplay.class, FlounderBounding.class, KosmosShadows.class, KosmosParticles.class, KosmosSkybox.class, KosmosWorld.class);
		ACTIVE = true;
	}

	@Override
	public void init() {
		// Sets the engine up for the editor.
		// FlounderProfiler.toggle(true);
		FlounderMouse.setCursorHidden(false);
		OpenGlUtils.goWireframe(false);
		FlounderBounding.toggle(true);

		// Sets the world to constant fog and a sun.
		//	EbonWorld.addFog(new Fog(new Colour(1.0f, 1.0f, 1.0f), 0.003f, 2.0f, 0.0f, 50.0f));
		//	EbonWorld.addSun(new Light(new Colour(1.0f, 1.0f, 1.0f), new Vector3f(0.0f, 2000.0f, 2000.0f)));

		// Default editor values.
		entityRotate = false;
		polygonMode = false;
		entityName = "unnamed";
		loadDefaultEntity();
	}

	public void loadDefaultEntity() {
		if (focusEntity != null) {
			focusEntity.forceRemove(true);
		}

		focusEntity = new Entity(FlounderEntities.getEntities(), new Vector3f(), new Vector3f());
		//focusEntity = FlounderEntities.load("dragon").createEntity(FlounderEntities.getEntities(), new Vector3f(), new Vector3f());
		new ComponentModel(
				focusEntity, 1.0f,
				ModelFactory.newBuilder().setFile(new MyFile(FlounderEntities.ENTITIES_FOLDER, "cactus1", "cactus1.obj")).create(),
				TextureFactory.newBuilder().setFile(new MyFile(FlounderEntities.ENTITIES_FOLDER, "cactus1", "cactus1Diffuse.png")).create(),
				1
		);
		new ComponentSway(focusEntity, TextureFactory.newBuilder().setFile(new MyFile(FlounderEntities.ENTITIES_FOLDER, "cactus1", "cactus1Sway.png")).create());
		new ComponentSurface(focusEntity, 1.0f, 0.0f, false, false);
		new ComponentCollider(focusEntity);
		new ComponentCollision(focusEntity);
		forceAddComponents();
	}

	@Override
	public void update() {
		// Updates wireframe modes.
		OpenGlUtils.goWireframe(polygonMode);

		// Used to load a entity from a .entity file.
		if (loadFromEntity != null) {
			if (focusEntity != null) {
				focusEntity.forceRemove(true);
			}

			//focusEntity = FlounderEntities.load(loadFromEntity).createEntity(FlounderEntities.getEntities(), new Vector3f(), new Vector3f()); // TODO
			forceAddComponents();

			FrameEntities.nameField.setText(loadFromEntity);
			loadFromEntity = null;
		}

		// Updates the model to be focused, and rotated.
		if (focusEntity != null) {
			ComponentModel componentModel = (ComponentModel) focusEntity.getComponent(ComponentModel.ID);

			if (componentModel != null && componentModel.getModel() != null && componentModel.getModel().getAABB() != null) {
				double height = componentModel.getModel().getAABB().getCentreY() * componentModel.getScale();

				if (entityRotate) {
					FlounderCamera.getPlayer().getRotation().y = 20.0f * Framework.getDelta();
				}

				focusEntity.move(componentModel.getEntity().getPosition().set(0.0f, (float) height / -2.0f, 0.0f), FlounderCamera.getPlayer().getRotation());
			}
		}
	}

	protected void setEntity(Entity entity) {
		if (this.focusEntity != null) {
			this.focusEntity.forceRemove(true);
			FrameEntities.clearSideTab();
		}

		this.focusEntity = entity;
		forceAddComponents();
	}

	private void forceAddComponents() {
		for (IComponentEntity component : focusEntity.getComponents()) {
			IComponentEditor editorComponent = (IComponentEditor) component;
			FrameEntities.editorComponents.add(editorComponent);

			if (editorComponent != null) {
				JPanel panel = IComponentEditor.makeTextPanel();
				editorComponent.addToPanel(panel);
				FrameEntities.componentAddRemove(panel, editorComponent);
				FrameEntities.addSideTab(IComponentEditor.getTabName(editorComponent), panel);
			}
		}
	}

	@Override
	public void profile() {
	}

	@Override
	public void dispose() {
		ACTIVE = false;
	}

	@Override
	public boolean isActive() {
		return ACTIVE;
	}
}
