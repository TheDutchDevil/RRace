package robotrace;

import com.jogamp.opengl.util.gl2.GLUT;
import java.nio.FloatBuffer;
import java.util.Arrays;
import java.util.Random;
import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import static javax.media.opengl.GL2.*;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_AMBIENT;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_DIFFUSE;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_LIGHT0;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_SPECULAR;

/**
 * Handles all of the RobotRace graphics functionality, which should be extended
 * per the assignment.
 *
 * OpenGL functionality: - Basic commands are called via the gl object; -
 * Utility commands are called via the glu and glut objects;
 *
 * GlobalState: The gs object contains the GlobalState as described in the
 * assignment: - The camera viewpoint angles, phi and theta, are changed
 * interactively by holding the left mouse button and dragging; - The camera
 * view width, vWidth, is changed interactively by holding the right mouse
 * button and dragging upwards or downwards; - The center point can be moved up
 * and down by pressing the 'q' and 'z' keys, forwards and backwards with the
 * 'w' and 's' keys, and left and right with the 'a' and 'd' keys; - Other
 * settings are changed via the menus at the top of the screen.
 *
 * Textures: Place your "track.jpg", "brick.jpg", "head.jpg", and "torso.jpg"
 * files in the same folder as this file. These will then be loaded as the
 * texture objects track, bricks, head, and torso respectively. Be aware, these
 * objects are already defined and cannot be used for other purposes. The
 * texture objects can be used as follows:
 *
 * gl.glColor3f(1f, 1f, 1f); track.bind(gl); gl.glBegin(GL_QUADS);
 * gl.glTexCoord2d(0, 0); gl.glVertex3d(0, 0, 0); gl.glTexCoord2d(1, 0);
 * gl.glVertex3d(1, 0, 0); gl.glTexCoord2d(1, 1); gl.glVertex3d(1, 1, 0);
 * gl.glTexCoord2d(0, 1); gl.glVertex3d(0, 1, 0); gl.glEnd();
 *
 * Note that it is hard or impossible to texture objects drawn with GLUT. Either
 * define the primitives of the object yourself (as seen above) or add
 * additional textured primitives to the GLUT object.
 */
public class RobotRace extends Base {

    /**
     * Last time in milliseconds that the scene got updated, this time is used
     * to calculate the time that passed between scene updates so that the
     * animation and robot progress along the track can be calculated.
     */
    private long lastSceneUpdateTime = System.currentTimeMillis();

    /**
     * Random instance shared by the four different robots, used by the robots
     * to calculate random speed changes.
     */
    private final Random random;

    /**
     * Array of the four robots.
     */
    private final Robot[] robots;

    /**
     * Instance of the camera.
     */
    private final Camera camera;

    /**
     * Instance of the race track.
     */
    private final RaceTrack[] raceTracks;

    /**
     * Instance of the terrain.
     */
    private final Terrain terrain;

    /**
     * Constructs this robot race by initializing robots, camera, track, and
     * terrain.
     */
    public RobotRace() {
        
        random = new Random();

        // Create a new array of four robots
        robots = new Robot[4];

        // Initialize robot 0
        robots[0] = new Robot(Material.WOOD, new Vector(-2, 0, 0), 1, random);

        // Initialize robot 1
        robots[1] = new Robot(Material.SILVER, new Vector(-1, 0, 0), 2, random);

        // Initialize robot 2
        robots[2] = new Robot(Material.GOLD, new Vector(1, 0, 0), 3, random);

        // Initialize robot 3
        robots[3] = new Robot(Material.ORANGE, new Vector(2, 0, 0), 4, random);

        // Initialize the camera
        camera = new Camera();

        // Initialize the race tracks
        raceTracks = new RaceTrack[5];

        // Test track
        raceTracks[0] = new RaceTrack();

        /**
         * Control points were picked by mimicking an O in the spline tool. 
         * Divide the x and y coordinates of the controlpoints by 3 to find
         * the location of the control points on the grid of the spline application.
         * 
         * For the L track divide by 4, for the C track divide by 4 and for the
         * 8 track divide by 8.
         */
        raceTracks[1] = new RaceTrack(new Vector[]{
            new Vector(0, -15, 1),
            new Vector(12, -15, 1), new Vector(12, 15d, 1), new Vector(0, 15, 1),
            new Vector(-12, 15d, 1), new Vector(-12, -15d, 1), new Vector(0, -15, 1)
        });

        // L-track
        raceTracks[2] = new RaceTrack(new Vector[]{
            new Vector(-8, 8, 1),
            new Vector(-8, 2d, 1), new Vector(-8, -4, 1), new Vector(-4, -6, 1),
            new Vector(0, -8, 1), new Vector(2d, -8, 1), new Vector(8, -8, 1),
            new Vector(16, -8, 1), new Vector(16, 0, 1), new Vector(8, 0, 1),
            new Vector(2d, 0, 1), new Vector(0, 2d, 1), new Vector(0, 8, 1),
            new Vector(0, 16, 1), new Vector(-8, 16, 1), new Vector(-8, 8, 1)
        });

        // C-track
        raceTracks[3] = new RaceTrack(new Vector[]{
            new Vector(4, 12, 1),
            new Vector(-8, 12, 1), new Vector(-12, 8, 1), new Vector(-12, 0, 1),
            new Vector(-12, -8, 1), new Vector(-8, -12, 1), new Vector(4, -12, 1),
            new Vector(10, -12, 1), new Vector(10, -4, 1), new Vector(4, -4, 1),
            new Vector(-4, -4, 1), new Vector(-4, 4, 1), new Vector(4, 4, 1),
            new Vector(10, 4, 1), new Vector(10, 12, 1), new Vector(4, 12, 1)
        });

        // an 8-track
        raceTracks[4] = new RaceTrack(new Vector[]{
            new Vector(0, 0, 1),
            new Vector(-24, -8, 1), new Vector(8, -24, 1), new Vector(0, 0, 1),
            new Vector(-8, 24, 1), new Vector(24, 8, 1), new Vector(0, 0, 1)
        });

        // Initialize the terrain
        terrain = new Terrain();
    }

    /**
     * Called upon the start of the application. Primarily used to configure
     * OpenGL.
     */
    @Override
    public void initialize() {

        // Enable blending.
        gl.glEnable(GL_BLEND);
        gl.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        // Enable depth testing.
        gl.glEnable(GL_DEPTH_TEST);
        gl.glDepthFunc(GL_LESS);

        // Normalize normals.
        gl.glEnable(GL_NORMALIZE);

        /**
         * Enables lighting, by enabling a single light source and setting
         * diffuse, ambient and specular color values. Values less than 1 are
         * used to make the light less intense.
         */
        gl.glEnable(GL_LIGHTING);
        gl.glEnable(GL_LIGHT0);
        gl.glEnable(GL_COLOR_MATERIAL);

        gl.glLightfv(GL_LIGHT0, GL_AMBIENT, new float[]{.2f, .2f, .2f, 1f}, 0);
        gl.glLightfv(GL_LIGHT0, GL_DIFFUSE, new float[]{.7f, .7f, .7f, 1f}, 0);
        gl.glLightfv(GL_LIGHT0, GL_SPECULAR, new float[]{.7f, .7f, .7f}, 0);

        // Enable textures. 
        gl.glEnable(GL_TEXTURE_2D);
        gl.glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
        gl.glBindTexture(GL_TEXTURE_2D, 0);

        // Try to load four textures, add more if you like.
        track = loadTexture("track.jpg");
        brick = loadTexture("brick.jpg");
        head = loadTexture("head.jpg");
        torso = loadTexture("torso.jpg");

        /**
         * Sets the shade model to smooth. See
         * https://www.opengl.org/sdk/docs/man2/xhtml/glShadeModel.xml for the
         * effects of a smooth shade model.
         */
        gl.glShadeModel(GL_SMOOTH);

        /**
         * Initials values for theta, phi and vDist so that the eye is placed to
         * look directly into the scene.
         */
        gs.theta = (float) Math.PI / 4f;
        gs.phi = (float) Math.PI / 3f;

        gs.vDist = 21;
    }

    /**
     * Configures the viewing transform.
     */
    @Override
    public void setView() {
        // Select part of window.
        gl.glViewport(0, 0, gs.w, gs.h);

        // Set projection matrix.
        gl.glMatrixMode(GL_PROJECTION);
        gl.glLoadIdentity();

        /**
         * Specifies a viewing frustrum based on a viewing angle in the y
         * direction, aspect ratio and a near and far clipping plane. Values for
         * the near and far clipping plane (Last two parameters) are based on
         * the values mentioned in the assignment document.
         *
         * When a fixed camera mode is used, like the helicopter or motorcycle
         * modes the parameters for the near and far clipping plane are fixed.
         */
        if (gs.camMode < 1 || gs.camMode > 4) {
            glu.gluPerspective(40, (float) gs.w / (float) gs.h, 0.1 * gs.vDist, 10 * gs.vDist);
        } else {
            glu.gluPerspective(40, (float) gs.w / (float) gs.h, 0.1, 50);
        }
        // Set camera.
        gl.glMatrixMode(GL_MODELVIEW);
        gl.glLoadIdentity();

        // Update the view according to the camera mode and robot of interest.
        // For camera modes 1 to 4, determine which robot to focus on.
        camera.update(gs, Arrays.asList(robots));
        glu.gluLookAt(camera.eye.x(), camera.eye.y(), camera.eye.z(),
                camera.center.x(), camera.center.y(), camera.center.z(),
                camera.up.x(), camera.up.y(), camera.up.z());

        /**
         * Calculates the position of the point, using the theta and phi of the
         * camera and modifying them by ten degrees. Radius is arbritrary
         * because the light source is directional.
         */
        double tenDegreesInRad = (10d * Math.PI) / 180d;

        Vector lightPosition = sphericalToCoords(gs.theta - tenDegreesInRad, Math.PI / 2 - (gs.phi + tenDegreesInRad), 1);

        /**
         * Position of the light source, the last field is set to zero so that
         * the light source is directional (comes from infinity).
         */
        float[] lightPos = {(float) lightPosition.x, (float) lightPosition.y, (float) lightPosition.z, 0f};

        gl.glLightfv(GL_LIGHT0, GL_POSITION, lightPos, 0);
    }

    /**
     * Draws the entire scene.
     */
    @Override
    public void drawScene() {

        // Background color.
        gl.glClearColor(1f, 1f, 1f, 0f);

        // Clear background.
        gl.glClear(GL_COLOR_BUFFER_BIT);

        // Clear depth buffer.
        gl.glClear(GL_DEPTH_BUFFER_BIT);

        // Set color to black.
        gl.glColor3f(0f, 0f, 0f);

        gl.glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);

        // Draw the axis frame.
        if (gs.showAxes) {
            drawAxisFrame();
        }

        /**
         * Used to calculate the time in ms between each draw cycle. 
         * timeSinceLastSceneUpdate is used in the Robot class to update the
         * animation and tracklocation variables.
         */
        int timeSinceLastSceneUpdate;
        timeSinceLastSceneUpdate = Math.toIntExact(System.currentTimeMillis() - this.lastSceneUpdateTime);
        lastSceneUpdateTime = System.currentTimeMillis();

        for (int i = 1; i <= 4; i++) {
            Robot rob = robots[i - 1];

            rob.calculateNewPosOnTrack(timeSinceLastSceneUpdate);
            rob.calculateNewAnimValue(timeSinceLastSceneUpdate);
            rob.determineNewAnimationVariables(timeSinceLastSceneUpdate);

            rob.position = raceTracks[gs.trackNr].getLanePoint(i, rob.getPosOnTrack());
            rob.direction = raceTracks[gs.trackNr].getLaneTangent(i, rob.getPosOnTrack());
            rob.draw(gl, glu, glut, gs.showStick, rob.getTAnim());

        }

        // Draw the race track.
        raceTracks[gs.trackNr].draw(gl, glu, glut, track, brick);

        // Draw the terrain.
        terrain.draw(gl, glu, glut);
    }

    /**
     * Draws the x-axis (red), y-axis (green), z-axis (blue), and origin
     * (yellow).
     */
    public void drawAxisFrame() {
        // set color to yellow
        gl.glColor3f(1f, 1f, 0f);
        glut.glutSolidSphere(0.1, 30, 30);
        // red arrow on x as
        // set color to red
        gl.glPushMatrix();
        {
            gl.glColor3f(1f, 0f, 0f);
            // rotat to x-as
            gl.glRotatef(90f, 0f, 1f, 0f);
            gl.glTranslatef(0f, 0f, 0.25f);
            gl.glScalef(0.1f, 0.1f, 1f);
            glut.glutSolidCube(0.5f);
            gl.glTranslatef(0f, 0f, 0.25f);
            glut.glutSolidCone(0.6, 0.5, 30, 30);
        }
        gl.glPopMatrix();

        // green arrow on the y-as
        // set color to green
        gl.glColor3f(0f, 1f, 0f);
        // rotat to y-as
        gl.glPushMatrix();
        {
            gl.glRotatef(-90f, 1f, 0f, 0f);
            gl.glTranslatef(0f, 0f, 0.25f);
            gl.glScalef(0.1f, 0.1f, 1f);
            glut.glutSolidCube(0.5f);
            gl.glTranslatef(0f, 0f, 0.25f);
            glut.glutSolidCone(0.6, 0.5, 30, 30);
        }
        gl.glPopMatrix();
        // gl.glLoadIdentity();
        // bluw arrow on the z-as
        // set color to blue
        gl.glColor3f(0f, 0f, 1f);
        // rotat to y-as
        gl.glPushMatrix();
        {
            //gl.glRotatef(90f, 1f, 0f, 0f);
            gl.glTranslatef(0f, 0f, 0.25f);
            gl.glScalef(0.1f, 0.1f, 1f);
            glut.glutSolidCube(0.5f);
            gl.glTranslatef(0f, 0f, 0.25f);
            glut.glutSolidCone(0.6, 0.5, 30, 30);
        }
        gl.glPopMatrix();

    }

    /**
     * Main program execution body, delegates to an instance of the RobotRace
     * implementation.
     */
    public static void main(String args[]) {
        RobotRace robotRace = new RobotRace();
        robotRace.run();
    }

    /**
     * Converts coordinates from a spherical coordinate system to xyz
     * coordinates.
     *
     * @param theta The angle between the point projected on the XY plane and
     * positive X-axis.
     * @param phi The angle between the point and the positive Z-axis.
     * @param radius The distance of the origin to the point.
     * @return Returns a vector containing the xyz coordinates.
     */
    public static Vector sphericalToCoords(double theta, double phi, double radius) {
        Vector ret = new Vector(0, 0, 0);

        ret.x = radius * (float) Math.cos(theta)
                * (float) Math.sin(phi);

        ret.y = radius * (float) Math.sin(theta)
                * (float) Math.sin(phi);

        ret.z = radius * (float) Math.cos(phi);

        return ret;
    }
}
