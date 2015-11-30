package robotrace;

import com.jogamp.opengl.util.gl2.GLUT;
import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.fixedfunc.GLLightingFunc;
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
     * Skin like color (between red and yellow) for the limbs and ears.
     */
    private static final float[] LIMB_COLOR = {254f / 255f, 179f / 255f, 129f / 255f};

    /**
     * Dark black like color used in the soles of the shoes.
     */
    private static final float[] SOLE_COLOR = {50f / 255f, 50f / 255f, 50f / 255f};

    /**
     * Metallic like color used in the head of the robot.
     */
    private static final float[] ROBOT_HEAD_COLOR = {160f / 255f, 160f / 255f, 160f / 255f};

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
    private static final double SKELETON_BACKBONE_LENGTH = .3 * SIZE;

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
    private static final double SKELETON_NECK_BONE_LENGTH = .05 * SIZE;

    /**
     * The distance, on the XY plane, between the spine and the jaw.
     */
    private static final double SKELETON_SPINE_TO_JAW_DISTANCE = 0.05 * SIZE;

    /**
     * The distance between the top of the neck joint and the jaw.
     */
    private static final double SKELETON_UPPER_NECK_TO_JAW_HEIGHT = 0.05 * SIZE;

    /**
     * The length from the jaw to the hair joint.
     */
    private static final double SKELETON_JAW_TO_HAIR_HEIGHT = .15 * SIZE;

    /**
     * Height of the shoulder bone from the feet of the robot.
     */
    private static final double SHOULDER_JOINT_HEIGHT = .65 * SIZE;

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
     * Width of the robot ankle.
     */
    private static final double ANKLE_WIDTH = .0175 * SIZE;

    /**
     * Height of the torso. That is from the shoulders to the hip.
     */
    private static final double TORSO_HEIGHT = .35 * SIZE;

    /**
     * Height of the top of the robot shoulders, which is slightly above the
     * shoulder joints.
     */
    private static final double SHOULDER_HEIGHT = SHOULDER_JOINT_HEIGHT + .025 * SIZE;

    /**
     * Width at the top of the torso.
     */
    private static final double UPPER_TORSO_WIDTH = 0.1 * SIZE;

    /**
     * Width at the bottom of the torso.
     */
    private static final double LOWER_TORSO_WIDTH = 0.2 * SIZE;

    /**
     * Length of the robot neck.
     */
    private static final double NECK_LENGTH = .05 * SIZE;

    /**
     * Used for both legs and arms.
     */
    private static final double ROBOT_LIMB_WIDTH = .0125 * SIZE;

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
    public Robot(Material material, Vector pos) {
        this.material = material;
        this.position = pos;
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
            /**
             * Sets the color to green for the stick figure.
             */
            gl.glColor3ub((byte) 0, (byte) 255, (byte) 0);
            drawStickFigure(gl, glu, glut);
        } else {
            drawRobot(gl, glu, glut);
        }

        gl.glColor3f(0, 0, 0);
        unsetSpecularMaterialValues(gl);
        gl.glPopMatrix();
    }

    /**
     * Draws the robot proper by drawing the geometry of the several robot
     * parts. These methods can be called in any order since they all expect the
     * reference frame to be at the origin of the robot.
     */
    private void drawRobot(GL2 gl, GLU glu, GLUT glut) {
        drawRobotLeg(gl, glu, glut, true);
        drawRobotLeg(gl, glu, glut, false);
        drawUpperBody(gl, glu, glut);
        drawRobotArm(gl, glu, glut, true);
        drawRobotArm(gl, glu, glut, false);
        drawRobotHead(gl, glu, glut);
    }

    /**
     * Draws the entire leg (including the shoe) of the robot. Starts from the
     * bottom then moves up drawing the shoe, ankle, lower leg and upper leg.
     * Modifies the reference frame up after each item of geometry. At the end
     * it clears the transformed matrix to return the reference to where it was
     * before calling the method.
     *
     * For the shoe and ankle seperate methods are called that draw those items
     * in more detail, the lower and upper leg consist out of a cylinder.
     *
     * @param leftLeg Draws the left leg when true, otherwise it draws the right
     * leg.
     */
    private void drawRobotLeg(GL2 gl, GLU glu, GLUT glut, boolean leftLeg) {
        gl.glPushMatrix();

        setRobotMaterialColor(gl);

        double translationOverXAxis = leftLeg ? -1 * DISTANCE_BETWEEN_LEG_AND_X_AXIS
                : DISTANCE_BETWEEN_LEG_AND_X_AXIS;

        gl.glTranslated(translationOverXAxis, 0, 0);

        drawShoe(gl, glu, glut, SIZE * .025);

        setRobotMaterialColor(gl);

        drawAnkle(gl, glu, glut);

        unsetSpecularMaterialValues(gl);

        gl.glTranslated(0, 0, SHOE_HEIGHT + ANKLE_HEIGHT);

        gl.glColor3fv(LIMB_COLOR, 0);

        glut.glutSolidCylinder(ROBOT_LIMB_WIDTH, 0.1 * SIZE, 32, 32);

        gl.glTranslated(0, 0, .1 * SIZE);

        glut.glutSolidCylinder(ROBOT_LIMB_WIDTH, .15 * SIZE, 32, 32);

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
     * Draws the joints that are present in the head. The head consists out of a
     * neck, a move-able jaw and move-able hair. It first draws the neck bone,
     * and on top of that a joint is drawn that represents the head movement. A
     * bit above the neck joint a jaw is attached. On the end of this jaw a
     * joint is attached that represents the jaw movement. </p>
     *
     * <p>
     * On top of the head/neck joint a long bone is attached on top of which the
     * hair joint is attached. The hair joint will represent the movement of the
     * hair.
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

    /**
     * Draws the ankle of the robot using the current matrix. The ankle consists
     * out of a cylinder drawn from the base of the foot to the height of the
     * ankle.
     */
    private void drawAnkle(GL2 gl, GLU glu, GLUT glut) {
        glut.glutSolidCylinder(ANKLE_WIDTH, SHOE_HEIGHT + ANKLE_HEIGHT, 32, 32);
    }

    /**
     * <p>
     * Draws the shoe / foot of the robot. The shoe consists out of several
     * cylinders stacked on top of eachother. Each cylinder with a smaller
     * radius than the one below it. This method starts by drawing the top
     * cylinder, and then works its way down. Before this method is called the
     * matrix stack should be manipulated so that it's at the bottom of the
     * feet.</p>
     *
     * <p>
     * First of all it translates up the height of the shoe scales up the y
     * axis, then it calculates how high each cylinder should be and how much
     * wider every cylinder should be. In a loop it moves down the height of one
     * cylinder then draws a cylinder, calculating the radius using a square
     * root function. So that there's a recognizable shape to the shoe.
     * Afterwards it pops the current matrix of the stack.</p>
     * 
     * <p>The bottom two cylinders of the shoe are drawn in a 
     * different color, without any specular reflection. This represents the
     * sole of the shoe. </p>
     */
    private void drawShoe(GL2 gl, GLU glu, GLUT glut, double maxRadius) {

        gl.glPushMatrix();

        gl.glTranslated(0, 0, SHOE_HEIGHT);

        gl.glScaled(1, 2, 1);

        final int nrDivisions = 12;
        final double subdivionHeight = SHOE_HEIGHT * (1d / nrDivisions);
        final double step = maxRadius / Math.sqrt(nrDivisions);

        for (int i = 1; i <= nrDivisions; i++) {

            if (i == nrDivisions - 1) {
                gl.glColor3fv(SOLE_COLOR, 0);
                unsetSpecularMaterialValues(gl);
            }

            gl.glTranslated(0, 0, -subdivionHeight);

            glut.glutSolidCylinder(step * Math.sqrt(i), subdivionHeight, 32, 32);
        }

        gl.glPopMatrix();
    }

    /**
     * Moves up to the height of the neck the it draws the upper body as a
     * series of increasingly wider cylinders. To draw the torso it uses the
     * drawSolidCup method. To ensure a torso shape the torso is scaled .666 in
     * the y axis. To make it wider than that the body is thick.
     */
    private void drawUpperBody(GL2 gl, GLU glu, GLUT glut) {
        gl.glPushMatrix();

        setRobotMaterialColor(gl);

        gl.glTranslated(0, 0, SHOULDER_HEIGHT);

        gl.glScaled(1, .666, 1);

        drawSolidCup(gl, glu, glut, LOWER_TORSO_WIDTH, UPPER_TORSO_WIDTH, TORSO_HEIGHT);

        unsetSpecularMaterialValues(gl);

        gl.glPopMatrix();
    }

    /**
     * <p>
     * Draws a stack of cups on top of each other, each subsequent cup below and
     * wider than the previous cup. Based on a set number of cylinders in which
     * the cup is divided a step value is calculated. Based on an index the
     * radius of the next cup is calculated. After drawing the cup it pops the
     * latest matrix of the stack to ensure that the matrix is reset to what it
     * was when the method was called.</p>
     *
     * @param bottomRadius The bottom (and generally wider) radius of the cup.
     * @param upperRadius The upper (and generally lower) radius of the cup.
     * @param height Total height of the cup in meters.
     */
    private void drawSolidCup(GL2 gl, GLU glu, GLUT glut, double bottomRadius, double upperRadius, double height) {
        gl.glPushMatrix();

        final int nrDivisions = 20;
        final double subdivisionHeight = height / nrDivisions;
        final double step = (bottomRadius - upperRadius) / nrDivisions;

        for (int i = 0; i < nrDivisions; i++) {
            gl.glTranslated(0, 0, -subdivisionHeight);
            glut.glutSolidCylinder(upperRadius + step * i, subdivisionHeight, 32, 32);
        }

        gl.glPopMatrix();
    }

    /**
     * <p>
     * Draws a single arm of the robot. Based on whether it's the left or right
     * arm position and rotation of the arm are different. The arm consists out
     * of a cup connecting the upper arm to the torso, an upper arm and a lower
     * arm. </p>
     *
     * @param leftArm Whether the left arm should be drawn. If this is false the
     * right arm is drawn.
     */
    private void drawRobotArm(GL2 gl, GLU glu, GLUT glut, boolean leftArm) {
        gl.glPushMatrix();

        double xTranslation = leftArm ? -DISTANCE_BETWEEN_SHOUDLER_AND_X_AXIS : DISTANCE_BETWEEN_SHOUDLER_AND_X_AXIS;
        double armRotation = leftArm ? 45 : -45;

        gl.glTranslated(xTranslation, 0, SHOULDER_JOINT_HEIGHT);

        gl.glRotated(armRotation, 0, 1, 0);

        setRobotMaterialColor(gl);

        drawSolidCup(gl, glu, glut, ROBOT_LIMB_WIDTH * 2, ROBOT_LIMB_WIDTH, SKELETON_UPPER_ARM_LENGTH / 2);

        unsetSpecularMaterialValues(gl);
        gl.glColor3fv(LIMB_COLOR, 0);

        gl.glTranslated(0, 0, -.15 * SIZE);

        glut.glutSolidCylinder(ROBOT_LIMB_WIDTH, SKELETON_UPPER_ARM_LENGTH / 2, 32, 32);

        gl.glRotated(ANGLE_BETWEEN_X_AND_LOWER_ARM, 1, 0, 0);

        glut.glutSolidCylinder(ROBOT_LIMB_WIDTH, SKELETON_LOWER_ARM_LENGTH, 32, 32);

        gl.glPopMatrix();
    }

    /**
     *
     * Draws the head and neck of the robot. The head consists out of three
     * cubes, two forming the head shape and one red cube representing the
     * mouth. The head also consists out of two ears and two eyes.
     *
     * The neck is drawn as a cylinder starting from the shoulders. The first
     * part of the head that's drawn is the back part, which spawns the entire
     * width and height. in front of that the block representing the face is
     * drawn and below that the red part representing the mouth is drawn.
     *
     */
    private void drawRobotHead(GL2 gl, GLU glu, GLUT glut) {
        gl.glPushMatrix();

        gl.glTranslated(0, 0, SHOULDER_HEIGHT);

        glut.glutSolidCylinder(ROBOT_LIMB_WIDTH, NECK_LENGTH, 32, 32);

        gl.glColor3fv(ROBOT_HEAD_COLOR, 0);

        gl.glTranslated(0, 0, .15 * SIZE);

        /**
         * Pushes a new matrix, moves to the center of the back part of the head
         * and draws it. Afterwards pop the matrix so that the position is
         * returned to the center of the head.
         */
        gl.glPushMatrix();
        {
            gl.glTranslated(0, -.025 * SIZE, 0);
            gl.glScaled(2, 1.5, 2);

            glut.glutSolidCube((float) (.1 * SIZE));
        }
        gl.glPopMatrix();

        /**
         * Draws the upper frontal part of the head. Clears the matrix stack
         * after finishing to move the everything back to the center of the
         * head.
         */
        gl.glPushMatrix();
        {
            gl.glTranslated(0, .075 * SIZE, 0.025 * SIZE);
            gl.glScaled(4, 1, 3);
            glut.glutSolidCube((float) (.05 * SIZE));
        }
        gl.glPopMatrix();

        /**
         * Draws the mouth (lower frontal part) of the head. Changes the color
         * to a shade of red.
         */
        gl.glPushMatrix();
        {
            gl.glTranslated(0, .075 * SIZE, -.075 * SIZE);
            gl.glColor3ub((byte) 230, (byte) 46, (byte) 0);
            gl.glScaled(4, 1, 1);
            glut.glutSolidCube((float) (.05 * SIZE));
        }
        gl.glPopMatrix();

        drawEar(gl, glu, glut, true);
        drawEar(gl, glu, glut, false);

        drawEye(gl, glu, glut, true);
        drawEye(gl, glu, glut, false);

        gl.glPopMatrix();
    }

    /**
     * Draws either the right or left ear of the robot. An ear consists out of a
     * wide but short cup attached to the head. Based on whether it's the right
     * or left ear the reference frame is rotated.
     *
     * @param leftEar Draws the leftear if true, otherwise draws the right ear.
     */
    private void drawEar(GL2 gl, GLU glu, GLUT glut, boolean leftEar) {
        double translationXAxis = leftEar ? -0.125 * SIZE : .125 * SIZE;
        double rotationYAxis = leftEar ? -90 : 90;

        gl.glColor3fv(LIMB_COLOR, 0);

        gl.glPushMatrix();

        gl.glTranslated(translationXAxis, 0, 0);
        gl.glRotated(rotationYAxis, 0, 1, 0);

        drawSolidCup(gl, glu, glut, .05 * SIZE, .025 * SIZE, .025 * SIZE);

        gl.glPopMatrix();

    }

    /**
     * Draws either the left or right eye. The eye consists out of a stretched
     * white cylinder and a small black sphere. The eyes are drawn to the right
     * or left of the center of the head. The reference frame is first
     * translated to the position of the eye, and then rotated to ensure the
     * eyes are put on on the correct position.
     *
     * The pupil is placed halfway into the white cylinder to make it look like
     * a half sphere.
     *
     * @param leftEye Draws the left eye when true, otherwise it draws the right
     * eye.
     */
    private void drawEye(GL2 gl, GLU glu, GLUT glut, boolean leftEye) {
        double translationXAxis = leftEye ? -.04 * SIZE : .04 * SIZE;
        double rotationXAxis = -90;

        gl.glPushMatrix();

        gl.glTranslated(translationXAxis, .1 * SIZE, .025 * SIZE);

        gl.glRotated(rotationXAxis, 1, 0, 0);

        gl.glColor3f(1, 1, 1);

        gl.glPushMatrix();
        {
            gl.glScaled(1, 2, 1);
            glut.glutSolidCylinder(0.018 * SIZE, 0.0125 * SIZE, 32, 32);
        }
        gl.glPopMatrix();

        gl.glColor3f(0, 0, 0);

        gl.glTranslated(0, .01 * SIZE, .0125 * SIZE);

        glut.glutSolidSphere(.01 * SIZE, 32, 32);

        gl.glPopMatrix();
    }

    /**
     * Sets the color, specular color and specular intensity of the robot's own
     * color.
     */
    private void setRobotMaterialColor(GL2 gl) {
        gl.glColor3f(material.diffuse[0], material.diffuse[1], material.diffuse[2]);
        gl.glMaterialfv(GL.GL_FRONT, GLLightingFunc.GL_SPECULAR, material.specular, 0);
        gl.glMaterialf(GL.GL_FRONT_AND_BACK, GLLightingFunc.GL_SHININESS, material.shininess);
    }

    /**
     * Unsets the specular values to zero so that the next object can be drawn
     * without any specular reflection. 
     * @param gl 
     */
    private void unsetSpecularMaterialValues(GL2 gl) {
        gl.glMaterialfv(GL.GL_FRONT, GLLightingFunc.GL_SPECULAR, new float[]{0, 0, 0}, 0);
        gl.glMaterialf(GL.GL_FRONT_AND_BACK, GLLightingFunc.GL_SHININESS, 0);
    }

}
