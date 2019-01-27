package com.javadude.grapheditor;

import android.graphics.Rect;
import android.graphics.RectF;

/**
 * Created by scott on 5/8/2016.
 */
public class Thing {
	public enum Type {
		Square, Circle, Triangle;
	}
	private Type type;
	private Rect bounds;

	public Thing(Type type, Rect bounds) {
		this.type = type;
		this.bounds = bounds;
	}

	public void setBounds(Rect bounds) {
		this.bounds = bounds;
	}

	public Rect getBounds() {
		return bounds;
	}

	public Type getType() {
		return type;
	}
}
