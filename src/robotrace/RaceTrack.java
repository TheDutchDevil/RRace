package robotrace;

import com.jogamp.opengl.util.gl2.GLUT;
import com.jogamp.opengl.util.texture.Texture;
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
     * This means that one full texture will mapped onto five subdivisions.
     */
    private final static int SUBDIVISIONS_PER_TRACK_TEXTURE = 5;

    private final static int HORIZONTAL_SUBDIVISIONS_PER_BRICK_TEXTURE = 1;

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
        } else if(controlPoints[0].x != controlPoints[controlPoints.length - 1].x ||
                controlPoints[0].y != controlPoints[controlPoints.length - 1].y ||
                controlPoints[0].z != controlPoints[controlPoints.length - 1].z) {
            throw new IllegalArgumentException("Last control point should be equal to the first control point");
        }
    }

    /**
     * Draws this track, based on the control points.
     */
    public void draw(GL2 gl, GLU glu, GLUT glut, Texture track, Texture brick) {
            this.drawRaceTrack(gl, glu, glut, track, brick);
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
<<<<<<< HEAD

        Vector point = getPoint(t);
        Vector tangent = getTangent(t);

        Vector perpendicular = tangent.cross(Vector.Z).normalized();

        return point.add(perpendicular.scale(-2.5 * LANE_WIDTH + lane * LANE_WIDTH));
=======
        Vector point, tangent;
        
        if (null == controlPoints) {
            point = getPoint(t);
            tangent = getTangent(t);
        } else {
            point = getCubicBezierPoint(t);
            tangent = getCubicBezierTangent(t);
        }
        
        Vector perpendicular = tangent.cross(Vector.Z).normalized();
        
        
        
        return point.add(perpendicular.scale(-2.5 * LANE_WIDTH + lane*LANE_WIDTH));
>>>>>>> 96bd2540c64193148016d907c90f0b42f9c37c09
    }

    /**
     * Returns the tangent of a lane at 0 <= t < 1. Use this method to find the
     * orientation of a robot on the track.
     */
    public Vector getLaneTangent(int lane, double t) {
        return getTangent(t);
    }

    private Vector getPoint(double t) {
        if (controlPoints != null) {
            return getCubicBezierPoint(t);
        } else {
            return getTestPoint(t);
        }
    }

    private Vector getTangent(double t) {
        if (controlPoints != null) {
            return getCubicBezierTangent(t);
        } else {
            return getTestTangent(t);
        }
    }

    /**
     * Returns a point on the test track at 0 <= t < 1.
     */
    private Vector getTestPoint(double t) {
        if (t < 0 || t > 1) {
            throw new IllegalArgumentException("T has to be >= 0 and <= 1");
        }
        return new Vector(10 * Math.cos(2 * Math.PI * t), 14 * Math.sin(2 * Math.PI * t), 1);
    }

    private Vector getCubicBezierPoint(double t) {
        int segments = (controlPoints.length - 1) / 3;
        int currSegment = 0;

        double segLength = 1d / segments;

        double newT;

        if (t == 1) {
            currSegment = segments - 1;
            newT = 1;
        } else {
            for (int i = 0; i < segments; i++) {
                if (t - segLength * (i + 1) < 0) {
                    currSegment = i;
                    break;
                }
            }

            newT = ((t - (segLength * currSegment)) % segLength) * segments;
        }

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

    /**
     * Returns a tangent on the test track at 0 <= t < 1.
     */
    private Vector getTestTangent(double t) {
        if (t < 0 || t > 1) {
            throw new IllegalArgumentException("T has to be >= 0 and <= 1");
        }
        return new Vector(-20 * Math.PI * Math.sin(2 * Math.PI * t), 28 * Math.PI * Math.cos(2 * Math.PI * t), 0);
    }

    private Vector getCubicBezierTangent(double t) {
        int segments = (controlPoints.length - 1) / 3;
        int currSegment = 0;

        double segLength = 1d / segments;

        double newT;

        if (t == 1) {
            newT = 1;
            currSegment = segments - 1;
        } else {
            for (int i = 0; i < segments; i++) {
                if (t - segLength * (i + 1) < 0) {
                    currSegment = i;
                    break;
                }
            }

            newT = ((t - (segLength * currSegment)) % segLength) * segments;
        }

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

    private void drawRaceTrack(GL2 gl, GLU glu, GLUT glut, Texture track, Texture brick) {

        double step = 1d / NUMBER_OF_TRACK_SUBDIVISIONS;

        gl.glColor3f(1f, 1f, 1f);

        track.bind(gl);

        gl.glBegin(GL.GL_TRIANGLE_STRIP);

        gl.glNormal3d(0, 0, 1);

        double t = 0;
        double tStep = 1d / SUBDIVISIONS_PER_TRACK_TEXTURE;

        for (int i = 0; i <= NUMBER_OF_TRACK_SUBDIVISIONS; i++) {
            double position = step * i;
            Vector nodePosition = getPoint(position);
            Vector normalForNodePosition = getTangent(position).cross(Vector.Z).normalized();

            Vector laneRadius = normalForNodePosition.scale(2 * LANE_WIDTH);

            Vector innerPosition = nodePosition.subtract(laneRadius);
            Vector outerPosition = nodePosition.add(laneRadius);

            gl.glTexCoord2d(0, t);

            gl.glVertex3d(innerPosition.x, innerPosition.y, innerPosition.z);

            gl.glTexCoord2d(1, t);

            gl.glVertex3d(outerPosition.x, outerPosition.y, outerPosition.z);

            t += tStep;

            if (t > 1) {
                t = 0;
            }
        }
        gl.glEnd();

        brick.bind(gl);

        gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S, GL2.GL_REPEAT);
        gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T, GL2.GL_REPEAT);

        gl.glBegin(GL2.GL_QUAD_STRIP);

        double s = 0;
        double sStep = 1d / HORIZONTAL_SUBDIVISIONS_PER_BRICK_TEXTURE;

        for (int i = 0; i <= NUMBER_OF_TRACK_SUBDIVISIONS; i++) {
            double position = step * i;
            Vector nodePosition = getPoint(position);
            Vector normalForNodePosition = getTangent(position).cross(Vector.Z).normalized();

            Vector laneRadius = normalForNodePosition.scale(2 * LANE_WIDTH);

            Vector upperPosition = nodePosition.add(laneRadius);
            Vector lowerPosition = upperPosition.subtract(new Vector(0, 0, 2));

            gl.glNormal3d(normalForNodePosition.x, normalForNodePosition.y, normalForNodePosition.z);

            gl.glTexCoord2d(s, 4);

            gl.glVertex3d(upperPosition.x, upperPosition.y, upperPosition.z);

            gl.glTexCoord2d(s, 0);

            gl.glVertex3d(lowerPosition.x, lowerPosition.y, lowerPosition.z);

            s += sStep;

            if (s > 1) {
                s = 0;
            }

        }
        gl.glEnd();

        gl.glBegin(GL2.GL_QUAD_STRIP);

        s = 0;

        for (int i = 0; i <= NUMBER_OF_TRACK_SUBDIVISIONS; i++) {
            double position = step * i;
            Vector nodePosition = getPoint(position);
            Vector normalForNodePosition = getTangent(position).cross(Vector.Z).normalized();

            Vector laneRadius = normalForNodePosition.scale(2 * LANE_WIDTH);

            Vector upperPosition = nodePosition.subtract(laneRadius);
            Vector lowerPosition = upperPosition.subtract(new Vector(0, 0, 2));

            gl.glNormal3d(-normalForNodePosition.x, -normalForNodePosition.y, -normalForNodePosition.z);

            gl.glTexCoord2d(s, 4);

            gl.glVertex3d(upperPosition.x, upperPosition.y, upperPosition.z);

            gl.glTexCoord2d(s, 0);

            gl.glVertex3d(lowerPosition.x, lowerPosition.y, lowerPosition.z);

            s += sStep;

            if (s > 1) {
                s = 0;
            }

        }
        gl.glEnd();
    }

}
