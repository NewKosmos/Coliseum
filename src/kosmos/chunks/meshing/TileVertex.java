/*
 * Copyright (C) 2017, Equilibrium Games - All Rights Reserved
 *
 * This source file is part of New Kosmos
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package kosmos.chunks.meshing;

public class TileVertex implements Comparable<TileVertex> {
	protected int index;
	protected float vertex0;
	protected float vertex1;
	protected float vertex2;
	protected float texture0;
	protected float texture1;
	protected float normal0;
	protected float normal1;
	protected float normal2;
	protected float tangent0;
	protected float tangent1;
	protected float tangent2;

	protected TileVertex(int index, float vertex0, float vertex1, float vertex2, float texture0, float texture1, float normal0, float normal1, float normal2, float tangent0, float tangent1, float tangent2) {
		this.index = index;
		this.vertex0 = vertex0;
		this.vertex1 = vertex1;
		this.vertex2 = vertex2;
		this.texture0 = texture0;
		this.texture1 = texture1;
		this.normal0 = normal0;
		this.normal1 = normal1;
		this.normal2 = normal2;
		this.tangent0 = tangent0;
		this.tangent1 = tangent1;
		this.tangent2 = tangent2;
	}

	@Override
	public int compareTo(TileVertex tileVertex) {
		return ((Integer) index).compareTo(tileVertex.index);
	}
}
