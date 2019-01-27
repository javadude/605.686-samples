package com.javadude.grapheditor;

/**
 * Created by scott on 5/8/2016.
 */
public class Line {
	private Thing end1;
	private Thing end2;

	public Line(Thing end1, Thing end2) {
		this.end1 = end1;
		this.end2 = end2;
	}

	public Thing getEnd1() {
		return end1;
	}

	public Thing getEnd2() {
		return end2;
	}
}
