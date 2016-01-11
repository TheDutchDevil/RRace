package robotrace;

import com.jogamp.opengl.util.gl2.GLUT;
import java.awt.Color;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Random;
import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

/**
 * Implementation of the terrain.
 */
class Terrain {

    /**
     * The amount of horizontal and vertical steps in which the terrain is
     * drawn.
     */
    private static final int TERRAIN_STEPS = 50;

    /**
     * Color array used for the 1D texture mapping of the terrain. The amount of
     * colors in this array is a power of two. The first two colors are blue,
     * the third one is a sand like color and the fourth color is green.
     */
    private static final Color[] TEXTURE_COLORS = new Color[]{
        new Color(44, 170, 211, 255), new Color(44, 170, 211, 255),
        new Color(194, 178, 128, 255), new Color(51, 181, 32, 255)
    };

    /**
     * Draws the terrain. Sets up 1D texturemapping, then starts with drawing
     * the terrain. The terrain itself is drawn as a collection of triangles,
     * where a strip of triangles is drawn iteration. Incrementing the step
     * variable in the u direction and modifying the v variable each iteration,
     * removing or adding one step value so that a strip in the u direction is
     * drawn as a series of triangles.
     *
     * After each strip u is reset to zero and v is incremented by one so that
     * the next strip is drawn. Finally it draws a transparent polygon
     * representing the water.
     */
    public void draw(GL2 gl, GLU glu, GLUT glut) {

        double step = 1d / TERRAIN_STEPS;

        double u = 0, v = 0;

        gl.glDisable(GL2.GL_TEXTURE_2D);
        gl.glEnable(GL2.GL_TEXTURE_1D);

        create1DTexture(gl, TEXTURE_COLORS);

        gl.glBegin(GL2.GL_TRIANGLE_STRIP);

        boolean vAheadOfU = false;

        double texCord = 0;

        gl.glColor3d(1, 1, 1);

        do {

            Vector normal = tangentInU(u, v).cross(tangentInV(u, v)).normalized();

            Vector position = pointAt(u, v);

            if (position.z <= 0) {
                texCord = .33d;
            } else if (position.z <= .5d) {
                texCord = .66d;
            } else {
                texCord = 1d;
            }

            gl.glNormal3d(normal.x, normal.y, normal.z);

            gl.glTexCoord1d(texCord);

            gl.glVertex3d(position.x, position.y, position.z);

            if (!vAheadOfU) {
                v += step;
                vAheadOfU = true;
            } else {
                v -= step;
                u += step;

                vAheadOfU = false;
            }

            if (u > 1) {
                u = 0;
                v += step;
                gl.glEnd();

                gl.glBegin(GL2.GL_TRIANGLE_STRIP);
            }

        } while (u <= 1 && v <= 1);

        gl.glEnd();

        gl.glDisable(GL2.GL_TEXTURE_1D);
        gl.glEnable(GL2.GL_TEXTURE_2D);

        drawPinetree(gl, glu, glut, -15.3, -18, heightAt(-15.3, -18), 0.8);
        drawPinetree(gl, glu, glut, -18, 1, heightAt(-18, 1), 1.0);
        drawPinetree(gl, glu, glut, 16, -17, heightAt(16, -17), 1.5);
        drawRoundTree(gl, glu, glut, 16, 4, heightAt(16, 4), 0.5);
        drawRoundTree(gl, glu, glut, 15.1, 16, heightAt(15.1, 16), 1.3);
        drawRoundTree(gl, glu, glut, -16, 18, heightAt(-16, 18), 0.9);

        drawTransparentPolygon(gl, glu, glut);
    }

    /**
     * Draws a transparent polygon where the water is supposed to be. Draws the
     * polygon from -20,-20,-1 to 20,20,0. Is drawn as the last part of the
     * terrain because of the blending. To ensure normals are correctly defined
     * the polygon is drawn as a loose collection of GL_QUADS.
     */
    private void drawTransparentPolygon(GL2 gl, GLU glu, GLUT glut) {

        gl.glColor4d(.4, .4, .4, .2);

        gl.glBegin(GL2.GL_QUADS);

        gl.glNormal3d(0, -1, 0);

        gl.glVertex3d(20, -20, 0);
        gl.glVertex3d(-20, -20, 0);
        gl.glVertex3d(-20, -20, -1);
        gl.glVertex3d(20, -20, -1);

        gl.glNormal3d(-1, 0, 0);

        gl.glVertex3d(-20, 20, 0);
        gl.glVertex3d(-20, 20, -1);
        gl.glVertex3d(-20, -20, -1);
        gl.glVertex3d(-20, -20, 0);

        gl.glNormal3d(0, 1, 0);

        gl.glVertex3d(-20, 20, 0);
        gl.glVertex3d(20, 20, 0);
        gl.glVertex3d(20, 20, -1);
        gl.glVertex3d(-20, 20, -1);

        gl.glNormal3d(1, 0, 0);

        gl.glVertex3d(20, 20, 0);
        gl.glVertex3d(20, -20, 0);
        gl.glVertex3d(20, -20, -1);
        gl.glVertex3d(20, 20, -1);

        gl.glNormal3d(0, 0, 1);

        gl.glVertex3d(20, 20, 0);
        gl.glVertex3d(-20, 20, 0);
        gl.glVertex3d(-20, -20, 0);
        gl.glVertex3d(20, -20, 0);

        gl.glEnd();
    }

    /**
     * Computes the elevation of the terrain at (x, y). Formula taken from the
     * assignments document.
     */
    public double heightAt(double x, double y) {
        return 0.6d * Math.cos(0.3d * x + 0.2d * y) + 0.4d * Math.cos(x - 0.5d * y);
    }

    /**
     * <p>
     * Computes a point on the terrain using u and v. Where u and v are limited
     * to 0 <= u <= 1 and 0 <= v <= 1 </p>
     *
     * <p>
     * First maps u and v to a value between -20 and 20, then uses the heightAt
     * function to compute that height at that point of the terrain.
     * </p>
     *
     * @param u A value between and including 0 and 1
     * @param v A value between and including 0 and 1
     * @return A point on the terrain at the specified u and v coordinates.
     */
    private Vector pointAt(double u, double v) {

        double x, y, z;

        x = u * 40 - 20;
        y = v * 40 - 20;
        z = heightAt(x, y);

        return new Vector(x, y, z);
    }

    /**
     * Tangent w.r.t. to u of the heightAt function. u and v are input
     * variables, ranging from 0 to 1. In the function both u and v are
     * multiplied with 40 and then 20 is subtracted so that the range of x and y
     * is -20 to 20.
     *
     * @return
     */
    private Vector tangentInU(double u, double v) {

        double x, y, z;

        x = 40;
        y = 0;
        z = -7.2d * Math.sin(0.3d * (-20d + 40d * u) + 0.2d * (-20d + 40d * v)) + 16 * Math.sin(20d - 40d * u + 0.5d * (-20d + 40 * v));

        return new Vector(x, y, z);
    }

    /**
     * Tangent w.r.t. to v of the heightAt function. u and v are input
     * variables, ranging from 0 to 1. In the function both u and v are
     * multiplied with 40 and then 20 is subtracted so that the range of x and y
     * is -20 to 20.
     *
     * @param u
     * @param v
     * @return
     */
    private Vector tangentInV(double u, double v) {
        double x, y, z;

        x = 0;
        y = 40;
        z = -4.8d * Math.sin(0.3d * (-20d + 40d * u) + 0.2d * (-20d + 40d * v)) - 8 * Math.sin(20d - 40d * u + 0.5d * (-20d + 40d * v));

        return new Vector(x, y, z);
    }

    /**
     * Creates a new 1D - texture. Taken from the hints and tricks document.
     *
     * @param gl
     * @param colors
     * @return the texture ID for the generated texture.
     */
    private int create1DTexture(GL2 gl, Color[] colors) {
        int[] texid = new int[]{-1};
        gl.glGenTextures(1, texid, 0);
        ByteBuffer bb = ByteBuffer.allocateDirect(colors.length * 4).order(ByteOrder.nativeOrder());
        for (Color color : colors) {
            int pixel = color.getRGB();
            bb.put((byte) ((pixel >> 16) & 0xFF)); // Red component
            bb.put((byte) ((pixel >> 8) & 0xFF));  // Green component
            bb.put((byte) (pixel & 0xFF));         // Blue component
            bb.put((byte) ((pixel >> 24) & 0xFF)); // Alpha component
        }
        bb.flip();
        gl.glBindTexture(GL2.GL_TEXTURE_1D, texid[0]);
        gl.glTexImage1D(GL2.GL_TEXTURE_1D, 0, GL2.GL_RGBA8, colors.length, 0, GL2.GL_RGBA, GL2.GL_UNSIGNED_BYTE, bb);
        gl.glTexParameteri(GL2.GL_TEXTURE_1D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR);
        gl.glTexParameteri(GL2.GL_TEXTURE_1D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR);
        return texid[0];
    }

    /**
     * this methods draws a tree. It first draws a tree base. This is drawn in
     * brown, after that it draws a cone on top of the base. At 6/7 height of
     * the cone a new cone is drawn.
     *
     * @param treeBaseHeight this is a fixed number between 0.5 and 1.5
     * @param xPosition this is the x-coordinate of the tree
     * @param yPosition this is the y-coordinate of the tree
     * @param zPosition this is the z-coordinate of the tree, It should be equal
     * to the height of the terrain at the position of the tree.
     */
    private void drawPinetree(GL2 gl, GLU glu, GLUT glut, double xPosition, double yPosition, double zPosition, double treeBaseHeight) {
        double treeBaseRadius = 0.2 * treeBaseHeight;
        double treeLowerLeavesRadius = 3.0 * treeBaseRadius;
        double treeUpperLeavesRadius = 2.0 * treeBaseRadius;
        double treeLowerLeaveHeight = 0.7 * treeBaseHeight;
        double treeUpperLeaveHeight = 0.4 * treeBaseHeight;
        gl.glPushMatrix();

        gl.glTranslated(xPosition, yPosition, zPosition);

        gl.glColor3f(112f / 255f, 3f / 255f, 3f / 255f);

        glut.glutSolidCylinder(treeBaseRadius, treeBaseHeight, 16, 16);

        gl.glTranslated(0, 0, treeBaseHeight);

        gl.glColor3f(0f / 255f, 153f / 255f, 0f / 255f);

        glut.glutSolidCone(treeLowerLeavesRadius, treeLowerLeaveHeight, 16, 16);

        gl.glTranslated(0, 0, 0.6 * treeBaseHeight);

        glut.glutSolidCone(treeUpperLeavesRadius, treeUpperLeaveHeight, 16, 16);

        gl.glPopMatrix();
    }

    /**
     * Draws a tree as a brown cylinder with two green spheres stacked on top of
     * it.
     *
     * @param treeBaseHeight this is a fixed number between 0.5 and 1.5
     * @param xPosition this is the x-coordinate of the tree
     * @param yPosition this is the y-coordinate of the tree
     * @param zPosition this is the z-coordinate of the tree, It should be equal
     * to the height of the terrain at the position of the tree.
     */
    private void drawRoundTree(GL2 gl, GLU glu, GLUT glut, double xPosition, double yPosition, double zPosition, double treeBaseHeight) {
        double treeBaseRadius = 0.2 * treeBaseHeight;
        double treeLowerLeavesRadius = 3.0 * treeBaseRadius;
        double treeUpperLeavesRadius = 2.0 * treeBaseRadius;
        gl.glPushMatrix();

        gl.glTranslated(xPosition, yPosition, zPosition);

        gl.glColor3f(112f / 255f, 3f / 255f, 3f / 255f);

        glut.glutSolidCylinder(treeBaseRadius, treeBaseHeight, 16, 16);

        gl.glTranslated(0, 0, treeBaseHeight + 0.7 * treeLowerLeavesRadius);

        gl.glColor3f(0f / 255f, 153f / 255f, 0f / 255f);

        glut.glutSolidSphere(treeLowerLeavesRadius, 16, 16);

        gl.glTranslated(0, 0, 1.5 * treeLowerLeavesRadius);

        glut.glutSolidSphere(treeUpperLeavesRadius, 16, 16);

        gl.glPopMatrix();
    }

}
