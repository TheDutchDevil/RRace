package robotrace;

import com.jogamp.opengl.util.gl2.GLUT;
import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

/**
 * Implementation of a race track that is made from Bezier segments.
 */
public class RaceTrack {

    /**
     * The width of one lane. The total width of the track is 4 * laneWidth.
     */
    private final static double LANE_WIDTH = 1.22d;

    private final static int NUMBER_OF_TRACK_SUBDIVISIONS = 70;

    /**
     * Array with 3N control points, where N is the number of segments.
     */
    private Vector[] controlPoints = null;

    /**
     * Constructor for the default track.
     */
    public RaceTrack() {
    }

    /**
     * Constructor for a spline track.
     */
    public RaceTrack(Vector[] controlPoints) {
        this.controlPoints = controlPoints;

        if ((controlPoints.length - 1) % 3 != 0 && controlPoints.length != 0) {
            throw new IllegalArgumentException("Invalid amount of control points");
        }
    }

    /**
     * Draws this track, based on the control points.
     */
    public void draw(GL2 gl, GLU glu, GLUT glut) {
        if (null == controlPoints) {
            this.drawRaceTrack(gl, glu, glut);
        } else {
            drawBezierRaceTrack(gl, glu, glut);
        }
    }

    /**
     * Returns the center of a lane at 0 <= t < 1. Use this method to find the
     * position of a robot on the track.>
     *
     * Based on the following formula. La = (p-u).norm * ((p-u).length +
     * (-2.5*Lane_Width + a*LANE_WIDTH))
     *
     * P is a point on the center of the track as a function of t. u is the up
     * vector.
     *
     * a is the lane, starting from 1 (innermost) to 4 (outermost).
     */
    public Vector getLanePoint(int lane, double t) {
        if (null == controlPoints) {
            return getPoint(t).subtract(Vector.Z).normalized().scale(getPoint(t).subtract(Vector.Z).length() + (-2.5 * LANE_WIDTH + lane * LANE_WIDTH)).add(Vector.Z);
        } else {
            return getCubicBezierPoint(t).subtract(Vector.Z).normalized().scale(getPoint(t).subtract(Vector.Z).length() + (-2.5 * LANE_WIDTH + lane * LANE_WIDTH)).add(Vector.Z);
        }
    }

    /**
     * Returns the tangent of a lane at 0 <= t < 1. Use this method to find the
     * orientation of a robot on the track.
     */
    public Vector getLaneTangent(int lane, double t) {
        if (null == controlPoints) {
            return getTangent(t);
        } else {
            return getCubicBezierTangent(t);
        }
    }

    /**
     * Returns a point on the test track at 0 <= t < 1.
     */
    private Vector getPoint(double t) {
        if (t < 0 || t > 1) {
            throw new IllegalArgumentException("T has to be >= 0 and <= 1");
        }
        return new Vector(10 * Math.cos(2 * Math.PI * t), 14 * Math.sin(2 * Math.PI * t), 1);
    }

    /**
     * Returns a tangent on the test track at 0 <= t < 1.
     */
    private Vector getTangent(double t) {
        if (t < 0 || t > 1) {
            throw new IllegalArgumentException("T has to be >= 0 and <= 1");
        }
        return new Vector(-20 * Math.PI * Math.sin(2 * Math.PI * t), 28 * Math.PI * Math.cos(2 * Math.PI * t), 0);
    }

    private Vector getCubicBezierPoint(double t) {
        int segments = (controlPoints.length - 1) / 3;
        int currSegment = 0;

        double segLength = 1d / segments;

        for (int i = 0; i < segments; i++) {
            if (t - segLength * (i + 1) < 0) {
                currSegment = i;
                break;
            }
        }

        double newT = ((t - (segLength * currSegment)) % segLength) * segments;

        return getCubicBezierPoint(newT, controlPoints[currSegment * 3],
                controlPoints[currSegment * 3 + 1],
                controlPoints[currSegment * 3 + 2],
                controlPoints[currSegment * 3 + 3]);
    }

    /**
     * Returns a point on a bezier segment with control points P0, P1, P2, P3 at
     * 0 <= t < 1.
     */
    private Vector getCubicBezierPoint(double t, Vector P0, Vector P1,
            Vector P2, Vector P3) {

        Vector wP0, wP1, wP2, wP3;

        wP0 = P0.scale(Math.pow(1d - t, 3));
        wP1 = P1.scale(3d * Math.pow(1d - t, 2) * t);
        wP2 = P2.scale(3d * (1d - t) * Math.pow(t, 2));
        wP3 = P3.scale(Math.pow(t, 3));

        return wP0.add(wP1).add(wP2).add(wP3);
    }

    private Vector getCubicBezierTangent(double t) {
        int segments = (controlPoints.length - 1) / 3;
        int currSegment = 0;

        double segLength = 1d / segments;

        for (int i = 0; i < segments; i++) {
            if (t - segLength * (i + 1) < 0) {
                currSegment = i;
                break;
            }
        }

        double newT = ((t - (segLength * currSegment)) % segLength) * segments;

        return getCubicBezierTangent(newT, controlPoints[currSegment * 3],
                controlPoints[currSegment * 3 + 1],
                controlPoints[currSegment * 3 + 2],
                controlPoints[currSegment * 3 + 3]);
    }

    /**
     * Returns a tangent on a bezier segment with control points P0, P1, P2, P3
     * at 0 <= t < 1.
     */
    private Vector getCubicBezierTangent(double t, Vector P0, Vector P1,
            Vector P2, Vector P3) {

        Vector firstTerm = P1.subtract(P0).scale(3d * Math.pow(1d - t, 2));
        Vector secondTerm = P2.subtract(P1).scale(6d * t * (1d - t));
        Vector thirdTerm = P3.subtract(P2).scale(3 * Math.pow(t, 2));

        return firstTerm.add(secondTerm).add(thirdTerm);
    }

    private void drawRaceTrack(GL2 gl, GLU glu, GLUT glut) {

        gl.glColor3f(.5f, .5f, .5f);

        double step = 1d / NUMBER_OF_TRACK_SUBDIVISIONS;

        gl.glBegin(GL.GL_TRIANGLE_STRIP);

        gl.glNormal3d(0, 0, 1);

        for (int i = 0; i <= NUMBER_OF_TRACK_SUBDIVISIONS; i++) {
            double position = step * i;
            Vector nodePosition = getPoint(position);
            Vector normalForNodePosition = getTangent(position).cross(Vector.Z).normalized();

            Vector laneRadius = normalForNodePosition.scale(2 * LANE_WIDTH);

            Vector innerPosition = nodePosition.subtract(laneRadius);
            Vector outerPosition = nodePosition.add(laneRadius);

            gl.glVertex3d(innerPosition.x, innerPosition.y, innerPosition.z);
            gl.glVertex3d(outerPosition.x, outerPosition.y, outerPosition.z);

        }
        gl.glEnd();

        gl.glColor3f(.3f, .3f, .3f);

        gl.glBegin(GL2.GL_QUAD_STRIP);
        for (int i = 0; i <= NUMBER_OF_TRACK_SUBDIVISIONS; i++) {
            double position = step * i;
            Vector nodePosition = getPoint(position);
            Vector normalForNodePosition = getTangent(position).cross(Vector.Z).normalized();

            Vector laneRadius = normalForNodePosition.scale(2 * LANE_WIDTH);

            Vector upperPosition = nodePosition.add(laneRadius);
            Vector lowerPosition = upperPosition.subtract(new Vector(0, 0, 2));

            gl.glNormal3d(normalForNodePosition.x, normalForNodePosition.y, normalForNodePosition.z);

            gl.glVertex3d(upperPosition.x, upperPosition.y, upperPosition.z);
            gl.glVertex3d(lowerPosition.x, lowerPosition.y, lowerPosition.z);

        }
        gl.glEnd();

        gl.glBegin(GL2.GL_QUAD_STRIP);
        for (int i = 0; i <= NUMBER_OF_TRACK_SUBDIVISIONS; i++) {
            double position = step * i;
            Vector nodePosition = getPoint(position);
            Vector normalForNodePosition = getTangent(position).cross(Vector.Z).normalized();

            Vector laneRadius = normalForNodePosition.scale(2 * LANE_WIDTH);

            Vector upperPosition = nodePosition.subtract(laneRadius);
            Vector lowerPosition = upperPosition.subtract(new Vector(0, 0, 2));

            gl.glNormal3d(-normalForNodePosition.x, -normalForNodePosition.y, -normalForNodePosition.z);

            gl.glVertex3d(upperPosition.x, upperPosition.y, upperPosition.z);
            gl.glVertex3d(lowerPosition.x, lowerPosition.y, lowerPosition.z);

        }
        gl.glEnd();
    }

    private void drawBezierRaceTrack(GL2 gl, GLU glu, GLUT glut) {

        gl.glColor3f(.5f, .5f, .5f);

        double step = 1d / NUMBER_OF_TRACK_SUBDIVISIONS;

        gl.glBegin(GL.GL_TRIANGLE_STRIP);

        gl.glNormal3d(0, 0, 1);

        for (int i = 0; i <= NUMBER_OF_TRACK_SUBDIVISIONS; i++) {
            double position = step * i;
            Vector nodePosition = getCubicBezierPoint(position);
            Vector normalForNodePosition = getCubicBezierTangent(position).cross(Vector.Z).normalized();

            Vector laneRadius = normalForNodePosition.scale(2 * LANE_WIDTH);

            Vector innerPosition = nodePosition.subtract(laneRadius);
            Vector outerPosition = nodePosition.add(laneRadius);

            gl.glVertex3d(innerPosition.x, innerPosition.y, innerPosition.z);
            gl.glVertex3d(outerPosition.x, outerPosition.y, outerPosition.z);

        }
        gl.glEnd();

        gl.glColor3f(.3f, .3f, .3f);

        gl.glBegin(GL2.GL_QUAD_STRIP);
        for (int i = 0; i <= NUMBER_OF_TRACK_SUBDIVISIONS; i++) {
            double position = step * i;
            Vector nodePosition = getCubicBezierPoint(position);
            Vector normalForNodePosition = getCubicBezierTangent(position).cross(Vector.Z).normalized();

            Vector laneRadius = normalForNodePosition.scale(2 * LANE_WIDTH);

            Vector upperPosition = nodePosition.add(laneRadius);
            Vector lowerPosition = upperPosition.subtract(new Vector(0, 0, 2));

            gl.glNormal3d(normalForNodePosition.x, normalForNodePosition.y, normalForNodePosition.z);

            gl.glVertex3d(upperPosition.x, upperPosition.y, upperPosition.z);
            gl.glVertex3d(lowerPosition.x, lowerPosition.y, lowerPosition.z);
        }
        gl.glEnd();

        gl.glBegin(GL2.GL_QUAD_STRIP);
        for (int i = 0; i <= NUMBER_OF_TRACK_SUBDIVISIONS; i++) {
            double position = step * i;
            Vector nodePosition = getCubicBezierPoint(position);
            Vector normalForNodePosition = getCubicBezierTangent(position).cross(Vector.Z).normalized();

            Vector laneRadius = normalForNodePosition.scale(2 * LANE_WIDTH);

            Vector upperPosition = nodePosition.subtract(laneRadius);
            Vector lowerPosition = upperPosition.subtract(new Vector(0, 0, 2));

            gl.glNormal3d(-normalForNodePosition.x, -normalForNodePosition.y, -normalForNodePosition.z);

            gl.glVertex3d(upperPosition.x, upperPosition.y, upperPosition.z);
            gl.glVertex3d(lowerPosition.x, lowerPosition.y, lowerPosition.z);

        }
        gl.glEnd();
    }
}
