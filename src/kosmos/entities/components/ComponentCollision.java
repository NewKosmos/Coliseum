/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved
 *
 * This source file is part of New Kosmos
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package kosmos.entities.components;

import flounder.entities.*;
import flounder.entities.components.*;
import flounder.helpers.*;
import flounder.maths.vectors.*;
import flounder.physics.*;

import javax.swing.*;

/**
 * Component that detects collision between two engine.entities.
 * <p>
 * Note: this component requires that both engine.entities have a ComponentCollider. Should one entity not have a ComponentCollider, then no collisions will be detected, because there is no collider to detect collisions against.
 */
public class ComponentCollision extends IComponentEntity implements IComponentMove, IComponentEditor {
	/**
	 * Creates a new ComponentCollision.
	 *
	 * @param entity The entity this component is attached to.
	 */
	public ComponentCollision(Entity entity) {
		super(entity);
	}

	@Override
	public void update() {
	}

	/**
	 * Resolves collisions with any other collision components encountered.
	 *
	 * @param amount The amount attempting to be moved.
	 *
	 * @return A new move vector that will not cause collisions after movement.
	 */
	public Vector3f resolveCollisions(Vector3f amount) {
		// Sets the resulting resolved collisions.
		Vector3f result = new Vector3f(amount);

		// Gets this entities collider.
		Collider collider1 = getEntity().getCollider();

		// Verifies that this entities main collider will work.
		if (collider1 == null) {
			return result;
		}

		// Gets a collider that may contain more colliders.
		ComponentCollider componentCollider1 = (ComponentCollider) getEntity().getComponent(ComponentCollider.class);

		// Calculates the range in where there can be collisions.
		final AABB collisionRange = AABB.stretch((AABB) collider1, null, amount);

		// Goes though all entities in the collision range.
		getEntity().visitInRange(ComponentCollision.class, collisionRange, (Entity entity, IComponentEntity component) -> {
			// Ignores the original entity.
			if (entity.equals(getEntity())) {
				return;
			}

			// Gets the checked entities collider.
			Collider collider2 = entity.getCollider();

			// Verifies that the checked entities main collider will work.
			if (collider2 == null) {
				return;
			}

			// Gets a collider that may contain more colliders.
			ComponentCollider componentCollider2 = (ComponentCollider) entity.getComponent(ComponentCollider.class);

			// If the main collider intersects or if a collision type is contained in the other.
			if (collider2.intersects(collisionRange).isIntersection() || collider2.contains(collider1) || collider1.contains(collider2)) {
				// If the main colliders are the only ones use them.
				if (componentCollider2 == null || componentCollider2.getColliders().isEmpty()) {
					collider1.resolveCollision(collider2, result, result);
				}
			}
		});

		// The final resulting move amount.
		return result;
	}

	@Override
	public void verifyMove(Entity entity, Vector3f moveAmount, Vector3f rotateAmount) {
		moveAmount.set(resolveCollisions(moveAmount));
		// rotateAmount = rotateAmount; // TODO: Stop some rotations?
	}

	@Override
	public void addToPanel(JPanel panel) {
	}

	@Override
	public void editorUpdate() {
	}

	@Override
	public Pair<String[], String[]> getSaveValues(String entityName) {
		return new Pair<>(
				new String[]{}, // Static variables
				new String[]{} // Class constructor
		);
	}

	@Override
	public void dispose() {
	}
}
