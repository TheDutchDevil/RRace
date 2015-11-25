package robotrace;

import com.jogamp.opengl.util.gl2.GLUT;
import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;
import jogamp.graph.curve.tess.HEdge;

/**
 * Represents a Robot, to be implemented according to the Assignments.
 *
 * All constant values in this class are specified in meters.
 */
class Robot {

    /**
     * Size of the robot in meters, all other constant size values of the robot
     * are based on this value.
     */
    private static final double SIZE = 2;

    /**
     * Radius of a joint of the stick figure skeleton.
     */
    private static final double SKELETON_JOINT_RADIUS = (0.05 * SIZE) / 2;

    /**
     * Radius of a limb of the stick figure skeleton.
     */
    private static final double SKELETON_LIMB_RADIUS = SKELETON_JOINT_RADIUS / 2;

    /**
     * The length of the bone in the lower leg.
     */
    private static final double SKELETON_LOWER_LEG_HEIGHT = .15 * SIZE;

    /**
     * The length of the bone in the upper leg.
     */
    private static final double SKELETON_UPPER_LEG_HEIGHT = SKELETON_LOWER_LEG_HEIGHT;

    /**
     * The length of the bone in the backbone.
     */
    private static final double SKELETON_BACKBONE_LENGTH = .25 * SIZE;

    /**
     * The length of the horizontal hipbone.
     */
    private static final double SKELETON_HORIZONTAL_HIPBONE_LENGTH = .2 * SIZE;

    /**
     * The length of the horizontal shoulder bone.
     */
    private static final double SKELETON_HORIZONTAL_SHOULDER_LENGTH = SKELETON_HORIZONTAL_HIPBONE_LENGTH;

    /**
     * Length of the upper arm bone.
     */
    private static final double SKELETON_UPPER_ARM_LENGTH = .15 * SIZE;

    /**
     * Length of the lower arm bone.
     */
    private static final double SKELETON_LOWER_ARM_LENGTH = .15 * SIZE;

    /**
     * Length of the lower part of the head (neck).
     */
    private static final double SKELETON_NECK_BONE_LENGTH = .1 * SIZE;
    
    /**
     * The distance, on the XY plane, between the spine and the jaw.
     */
    private static final double SKELETON_SPINE_TO_JAW_DISTANCE = 0.05 * SIZE;
    
    /**
     * The distance between the top of the neck joint and the jaw.
     */
    private static final double SKELETON_UPPER_NECK_TO_JAW_HEIGHT = 0.05*SIZE;
    
    /**
     * The length from the jaw to the hair joint. 
     */
    private static final double SKELETON_JAW_TO_HAIR_HEIGHT = .15*SIZE;

    /**
     * Height of the shoulder bone from the feet of the robot.
     */
    private static final double SHOULDER_JOINT_HEIGHT = .6 * SIZE;

    /**
     * Angle between the upper arm and head.
     */
    private static final double ANGLE_BETWEEN_Y_AND_UPPER_ARM = 135;

    /**
     * Angle between the upper arm and lower arm.
     */
    private static final double ANGLE_BETWEEN_X_AND_LOWER_ARM = -90;

    /**
     * Distance between a single leg and the origin.
     */
    private static final double DISTANCE_BETWEEN_LEG_AND_X_AXIS = 0.1 * SIZE;

    /**
     * Distance between the shoulder and backbone.
     */
    private static final double DISTANCE_BETWEEN_SHOUDLER_AND_X_AXIS = DISTANCE_BETWEEN_LEG_AND_X_AXIS;

    /**
     * Height of the shoe.
     */
    private static final double SHOE_HEIGHT = .05 * SIZE;

    /**
     * Height of the robot ankle
     */
    private static final double ANKLE_HEIGHT = .05 * SIZE;

    /**
     * The position of the robot.
     */
    public Vector position = new Vector(0, 0, 0);

    /**
     * The direction in which the robot is running.
     */
    public Vector direction = new Vector(1, 0, 0);

    /**
     * The material from which this robot is built.
     */
    private final Material material;

    /**
     * Constructs the robot with initial parameters.
     */
    public Robot(Material material
    /* add other parameters that characterize this robot */) {
        this.material = material;

        // code goes here ...
    }

    /**
     * <p>
     * Draws this robot (as a {@code stickfigure} if specified). The first step
     * is translating the robot to its current position. If the boolean
     * stickFigure is true the drawStickFigure method is called. Otherwise the
     * robot proper is drawn by calling drawRobot.</p>
     */
    public void draw(GL2 gl, GLU glu, GLUT glut, boolean stickFigure, float tAnim) {
        gl.glPushMatrix();

        /**
         * Translate 'to' the position of the robot
         */
        gl.glTranslated(position.x, position.y, position.z);

        if (stickFigure) {
            drawStickFigure(gl, glu, glut);
        } else {
            drawRobot(gl, glu, glut);
        }

        gl.glColor3f(0, 0, 0);
        gl.glPopMatrix();
    }

    private void drawRobot(GL2 gl, GLU glu, GLUT glut) {

        drawRobotLeg(gl, glu, glut, true);
        drawRobotLeg(gl, glu, glut, false);

    }

    private void drawRobotLeg(GL2 gl, GLU glu, GLUT glut, boolean leftLeg) {
        gl.glPushMatrix();

        gl.glColor3f(1f, 0, 1f);

        double translationOverXAxis = leftLeg ? -1 * DISTANCE_BETWEEN_LEG_AND_X_AXIS
                : DISTANCE_BETWEEN_LEG_AND_X_AXIS;

        gl.glTranslated(translationOverXAxis, 0, 0);

        drawShoe(gl, glu, glut, SIZE * .025);

        drawAnkle(gl, glu, glut);

        gl.glTranslated(0, 0, SHOE_HEIGHT + ANKLE_HEIGHT);

        gl.glColor3f(1, 1, 0);

        glut.glutSolidCylinder(0.0125 * SIZE, 0.1 * SIZE, 32, 32);

        gl.glTranslated(0, 0, .1 * SIZE);

        glut.glutSolidCylinder(.0125 * SIZE, .15 * SIZE, 32, 32);

        gl.glPopMatrix();
    }

    /**
     * Draws the stick figure. Each method draws a specific version of the
     * stickfigure. Order of drawing the elements is not important.
     */
    private void drawStickFigure(GL2 gl, GLU glu, GLUT glut) {
        drawStickLeg(gl, glu, glut, true);
        drawStickLeg(gl, glu, glut, false);
        drawStickBody(gl, glu, glut);
        drawStickArm(gl, glu, glut, true);
        drawStickArm(gl, glu, glut, false);
        drawStickHead(gl, glu, glut);
    }

    /**
     * <p>
     * Draws either the right or left stick leg. It does this by pushing a new
     * matrix on the stack. Then it translates to the position of either the
     * left or right ankle. From there it draws the ankle joint, the lower leg
     * skeleton. Then on top of that it draws the knee joint and the upper leg
     * skeleton. </p>
     *
     * @param leftSide Whether this stick leg is the left leg of the stick
     * figure. If this value is false it assumed it is the right leg.
     */
    private void drawStickLeg(GL2 gl, GLU glu, GLUT glut, boolean leftLeg) {
        gl.glPushMatrix();

        double translationOverXAxis = leftLeg ? -1 * DISTANCE_BETWEEN_LEG_AND_X_AXIS
                : DISTANCE_BETWEEN_LEG_AND_X_AXIS;

        gl.glTranslated(translationOverXAxis, 0, SHOE_HEIGHT);

        glut.glutSolidSphere(SKELETON_JOINT_RADIUS, 32, 32);

        glut.glutSolidCylinder(SKELETON_LIMB_RADIUS, SKELETON_LOWER_LEG_HEIGHT, 32, 32);

        gl.glTranslated(0, 0, SKELETON_LOWER_LEG_HEIGHT);

        glut.glutSolidSphere(SKELETON_JOINT_RADIUS, 32, 32);

        glut.glutSolidCylinder(SKELETON_LIMB_RADIUS, SKELETON_UPPER_LEG_HEIGHT, 32, 32);

        gl.glTranslated(0, 0, SKELETON_UPPER_LEG_HEIGHT);

        glut.glutSolidSphere(SKELETON_JOINT_RADIUS, 32, 32);

        gl.glPopMatrix();
    }

    /**
     * <p>
     * Draws the torso of the stick figure. First it draws the hipbone that
     * connects the two hip joints with eachother. Then it draws the center
     * spine. On top of the center spine it draws the horizontal shoulder bone.
     * Attached to the shoulder bone are the two shoulder joints.</p>
     *
     * <p>
     * Because of the rotation needed for drawing the horizontal shoulder and
     * hip bone several pop and push matrix calls are used. These calls are
     * wrapped in curly braces to make it clear which rotation and translation
     * are used for which skeleton parts.</p>
     */
    private void drawStickBody(GL2 gl, GLU glu, GLUT glut) {
        gl.glPushMatrix();
        {
            gl.glTranslated(0, 0, .35 * SIZE);

            gl.glPushMatrix();
            {

                gl.glTranslated(-1 * DISTANCE_BETWEEN_LEG_AND_X_AXIS, 0, 0);

                gl.glRotated(90, 0, 1, 0);

                glut.glutSolidCylinder(SKELETON_LIMB_RADIUS, SKELETON_HORIZONTAL_HIPBONE_LENGTH, 32, 32);

            }
            gl.glPopMatrix();

            glut.glutSolidCylinder(SKELETON_LIMB_RADIUS, SKELETON_BACKBONE_LENGTH, 32, 32);

            gl.glTranslated(0, 0, SKELETON_BACKBONE_LENGTH);

            gl.glPushMatrix();
            {

                gl.glTranslated(-1 * DISTANCE_BETWEEN_SHOUDLER_AND_X_AXIS, 0, 0);

                glut.glutSolidSphere(SKELETON_JOINT_RADIUS, 32, 32);

                gl.glRotated(90, 0, 1, 0);

                glut.glutSolidCylinder(SKELETON_LIMB_RADIUS, SKELETON_HORIZONTAL_SHOULDER_LENGTH, 32, 32);

                gl.glTranslated(0, 0, .2 * SIZE);

                glut.glutSolidSphere(SKELETON_JOINT_RADIUS, 32, 32);
            }
            gl.glPopMatrix();
        }
        gl.glPopMatrix();
    }

    /**
     * <p>
     * Draws the joint and bones of the stick arms. Starts from either the left
     * or right shoulder joint and then works downward. Drawing and rotating to
     * draw the two arm skeletons and elbow and wrist joint. </p>
     *
     * @param leftArm Based on the value of this variable either the left or
     * right arm is drawn.
     */
    private void drawStickArm(GL2 gl, GLU glu, GLUT glut, boolean leftArm) {
        gl.glPushMatrix();

        double xAxisTranslation = leftArm ? -DISTANCE_BETWEEN_SHOUDLER_AND_X_AXIS : DISTANCE_BETWEEN_SHOUDLER_AND_X_AXIS;

        gl.glTranslated(xAxisTranslation, 0, SHOULDER_JOINT_HEIGHT);

        double upperArmRotation = leftArm ? -ANGLE_BETWEEN_Y_AND_UPPER_ARM : ANGLE_BETWEEN_Y_AND_UPPER_ARM;

        gl.glRotated(upperArmRotation, 0, 1, 0);

        glut.glutSolidCylinder(SKELETON_LIMB_RADIUS, SKELETON_UPPER_ARM_LENGTH, 32, 32);

        gl.glTranslated(0, 0, SKELETON_UPPER_ARM_LENGTH);

        glut.glutSolidSphere(SKELETON_JOINT_RADIUS, 32, 32);

        gl.glRotated(ANGLE_BETWEEN_X_AND_LOWER_ARM, 1, 0, 0);

        glut.glutSolidCylinder(SKELETON_LIMB_RADIUS, SKELETON_LOWER_ARM_LENGTH, 32, 32);

        gl.glTranslated(0, 0, SKELETON_LOWER_ARM_LENGTH);

        glut.glutSolidSphere(SKELETON_JOINT_RADIUS, 32, 32);

        gl.glPopMatrix();
    }

    /**
     * <p>
     * Draws the joints that are present in the head. The head consists out of 
     * a neck, a move-able jaw and move-able hair. It first draws the neck bone,
     * and on top of that a joint is drawn that represents the head movement.
     * A bit above the neck joint a jaw is attached. On the end of this jaw a 
     * joint is attached that represents the jaw movement. </p>
     * 
     * <p>
     * On top of the head/neck joint a long bone is attached on top of which the 
     * hair joint is attached. The hair joint will represent the movement of 
     * the hair. 
     * </p>
     * 
     */
    private void drawStickHead(GL2 gl, GLU glu, GLUT glut) {
        gl.glPushMatrix();

        gl.glTranslated(0, 0, SHOULDER_JOINT_HEIGHT);

        glut.glutSolidCylinder(SKELETON_LIMB_RADIUS, SKELETON_NECK_BONE_LENGTH, 32, 32);

        gl.glTranslated(0, 0, SKELETON_NECK_BONE_LENGTH);

        glut.glutSolidSphere(SKELETON_JOINT_RADIUS, 32, 32);

        glut.glutSolidCylinder(SKELETON_LIMB_RADIUS, SKELETON_UPPER_NECK_TO_JAW_HEIGHT, 32, 32);

        gl.glTranslated(0, 0, SKELETON_UPPER_NECK_TO_JAW_HEIGHT);

        gl.glPushMatrix();
        {
            gl.glRotated(-90, 1, 0, 0);

            glut.glutSolidCylinder(SKELETON_LIMB_RADIUS, SKELETON_SPINE_TO_JAW_DISTANCE, 32, 32);

            gl.glTranslated(0, 0, SKELETON_SPINE_TO_JAW_DISTANCE);

            glut.glutSolidSphere(SKELETON_JOINT_RADIUS, 32, 32);
        }
        gl.glPopMatrix();

        glut.glutSolidCylinder(SKELETON_LIMB_RADIUS, SKELETON_JAW_TO_HAIR_HEIGHT, 32, 32);

        gl.glTranslated(0, 0, SKELETON_JAW_TO_HAIR_HEIGHT);

        glut.glutSolidSphere(SKELETON_JOINT_RADIUS, 32, 32);

        gl.glPopMatrix();
    }

    private void drawAnkle(GL2 gl, GLU glu, GLUT glut) {
        double height = .05 * SIZE;

        glut.glutSolidCylinder(.0175 * SIZE, SHOE_HEIGHT + height, 32, 32);
    }

    private void drawShoe(GL2 gl, GLU glu, GLUT glut, double maxRadius) {

        gl.glPushMatrix();

        gl.glTranslated(0, 0, SHOE_HEIGHT);

        gl.glScaled(1, 2, 1);

        final int nrDivisions = 25;
        final double subdivionHeight = SHOE_HEIGHT * (1d / nrDivisions);
        final double step = maxRadius / Math.sqrt(nrDivisions);

        for (int i = 1; i <= nrDivisions; i++) {

            gl.glTranslated(0, 0, -subdivionHeight);

            glut.glutSolidCylinder(step * Math.sqrt(i), subdivionHeight, 32, 32);
        }

        gl.glPopMatrix();
    }

}
