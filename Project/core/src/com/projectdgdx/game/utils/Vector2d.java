package com.projectdgdx.game.utils;

/**
 * Data structure handling 2D vectors
 *
 * Created by konglobemeralt on 2017-04-27.
 */

public class Vector2d {

    public float x, z;

    public Vector2d(float x, float z) {
        this.x = x;
        this.z = z;
    }

    public Vector2d add(Vector2d vector) {
        x += vector.x;
        z += vector.z;
        return this;
    }

    public float getAngle() {
        return (float)Math.toDegrees(Math.atan2(z, x));
    }

    public float getLength() {
        return (float)Math.sqrt(x*x + z*z);
    }

    public String toString() {
        return "[" + x + "," + z + "]";
    }

	/**
	 * Created by Eddie on 2017-04-28.
	 *
	 * Class containing various settings variables for the game.
	 *
	 */

}