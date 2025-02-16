/*
 * Copyright © 2025 Tanvir Ahamed
 * All rights reserved.
 *
 * This software is developed and maintained by Tanvir Ahamed, CEO and Founder of LevelPixela.
 * Unauthorized copying, modification, distribution, or use of this software in any medium is strictly prohibited.
 *
 * For inquiries, permissions, or contributions, contact LevelPixel at [https://www.levelpixel.net].
 */

package com.levelpixel.minimalcharts;

/**
 * Helper class for managing the properties and animations of a clock-style pie chart.
 */
public class ClockPie {

    private static final int DEFAULT_VELOCITY = 5; // Default velocity for updates

    private final int velocity; // Velocity for animating changes
    private float start; // Current start angle
    private float end; // Current end angle
    private float targetStart; // Target start angle
    private float targetEnd; // Target end angle

    /**
     * Constructor to initialize ClockPie with a specific start and end angle,
     * and set targets from another ClockPie.
     */
    public ClockPie(float startDegree, float endDegree, ClockPie targetPie) {
        this.velocity = DEFAULT_VELOCITY;
        this.start = startDegree;
        this.end = endDegree;
        this.targetStart = targetPie.getStart();
        this.targetEnd = targetPie.getEnd();
    }

    /**
     * Constructor to initialize ClockPie using start and end times (hours and minutes).
     */
    public ClockPie(int startHour, int startMin, int endHour, int endMin) {
        this.velocity = DEFAULT_VELOCITY;
        this.start = calculateAngle(startHour, startMin, 0);
        this.end = calculateAngle(endHour, endMin, 0);
        normalizeEndAngle();
    }

    /**
     * Constructor to initialize ClockPie using start and end times (hours, minutes, and seconds).
     */
    public ClockPie(int startHour, int startMin, int startSec, int endHour, int endMin, int endSec) {
        this.velocity = DEFAULT_VELOCITY;
        this.start = calculateAngle(startHour, startMin, startSec);
        this.end = calculateAngle(endHour, endMin, endSec);
        normalizeEndAngle();
    }

    /**
     * Sets the target start and end angles for animation.
     *
     * @param targetStart Target start angle.
     * @param targetEnd   Target end angle.
     * @return This instance for method chaining.
     */
    public ClockPie setTarget(float targetStart, float targetEnd) {
        this.targetStart = targetStart;
        this.targetEnd = targetEnd;
        return this;
    }

    /**
     * Sets the target angles from another ClockPie.
     *
     * @param targetPie Another ClockPie to copy target angles from.
     * @return This instance for method chaining.
     */
    public ClockPie setTarget(ClockPie targetPie) {
        this.targetStart = targetPie.getStart();
        this.targetEnd = targetPie.getEnd();
        return this;
    }

    /**
     * Checks if the current angles have reached their target values.
     *
     * @return True if both start and end angles match their targets, false otherwise.
     */
    public boolean isAtRest() {
        return (start == targetStart) && (end == targetEnd);
    }

    /**
     * Updates the current start and end angles to move closer to their targets.
     */
    public void update() {
        start = updateAngle(start, targetStart, velocity);
        end = updateAngle(end, targetEnd, velocity);
    }

    /**
     * Gets the sweep angle of the pie (difference between end and start angles).
     *
     * @return The sweep angle.
     */
    public float getSweep() {
        return end - start;
    }

    /**
     * Gets the current start angle.
     *
     * @return The start angle.
     */
    public float getStart() {
        return start;
    }

    /**
     * Gets the current end angle.
     *
     * @return The end angle.
     */
    public float getEnd() {
        return end;
    }

    /**
     * Calculates the angle based on hour, minute, and second values.
     *
     * @param hour   The hour component.
     * @param minute The minute component.
     * @param second The second component.
     * @return The calculated angle.
     */
    private float calculateAngle(int hour, int minute, int second) {
        return 270 + hour * 15 + minute * 15 / 60f + second * 15 / 3600f;
    }

    /**
     * Ensures the end angle is greater than or equal to the start angle.
     */
    private void normalizeEndAngle() {
        while (end < start) {
            end += 360;
        }
    }

    /**
     * Updates the current angle towards the target angle by the specified velocity.
     *
     * @param current  The current angle.
     * @param target   The target angle.
     * @param velocity The velocity of the change.
     * @return The updated angle.
     */
    private float updateAngle(float current, float target, int velocity) {
        if (current < target) {
            current += velocity;
        } else if (current > target) {
            current -= velocity;
        }
        if (Math.abs(target - current) < velocity) {
            current = target;
        }
        return current;
    }
}
