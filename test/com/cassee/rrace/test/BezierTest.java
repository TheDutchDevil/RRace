/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cassee.rrace.test;

import org.junit.Assert;
import org.junit.Test;
import static org.junit.Assert.*;

import robotrace.*;

/**
 *
 * @author Dutch
 */
public class BezierTest {

    private final static double LANE_WIDTH = 1.22d;

    public BezierTest() {
    }

    @Test
    public void testLane1StraightCurve() {
        RaceTrack track = new RaceTrack(new Vector[]{
            new Vector(0, 0, 0), new Vector(0, 2.5d, 0),
            new Vector(0, 5, 0), new Vector(0, 7.5d, 0)});

        Vector lane1 = track.getLanePoint(1, .5);

        Assert.assertEquals("Wrong lane", new Vector(-1.5 * LANE_WIDTH, 7.5d / 2, 0), lane1);
    }

    @Test
    public void testTangentStraightCurve() {
        RaceTrack track = new RaceTrack(new Vector[]{
            new Vector(0, 0, 0), new Vector(0, 2.5d, 0),
            new Vector(0, 5, 0), new Vector(0, 7.5d, 0)});

        Vector tangent = track.getLaneTangent(1, 1).normalized();

        Assert.assertEquals(new Vector(0, 1, 0).y(), tangent.y(), 0.000001);
    }
}
