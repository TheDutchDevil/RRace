package robotrace;

import com.jogamp.opengl.util.gl2.GLUT;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
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

    private static final double INITIAL_TRACK_ROUND_STEP = 1d/40000;
    
    private static final int INITIAL_ANIMATION_MODIFIER = 18;
    
    private static final double DESIRED_ANIMATION_RATIO = INITIAL_TRACK_ROUND_STEP / INITIAL_ANIMATION_MODIFIER;
    
    /**
     * Size of the robot in meters, all other constant size values of the robot
     * are based on this value.
     */
    private static final double SIZE = 2;

    /**
     * Skin like color (between red and yellow) for the limbs and ears.
     */
    private static final float[] LIMB_COLOR
            = {
                254f / 255f, 179f / 255f, 129f / 255f
            };

    /**
     * Dark black like color used in the soles of the shoes.
     */
    private static final float[] SOLE_COLOR
            = {
                50f / 255f, 50f / 255f, 50f / 255f
            };

    /**
     * Metallic like color used in the head of the robot.
     */
    private static final float[] ROBOT_HEAD_COLOR
            = {
                160f / 255f, 160f / 255f, 160f / 255f
            };

    /**
     * Color of the hair, dark red.
     */
    private static final float[] ROBOT_HAIR_COLOR
            = {
                154f / 255f, 54f / 255f, 5f / 255f
            };

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
     * Radius of the robot ankle.
     */
    private static final double ANKLE_RADIUS = .0175 * SIZE;

    /**
     * Height of the torso. That is from the shoulders to the hip.
     */
    private static final double TORSO_HEIGHT = .35 * SIZE;

    /**
     * Radius at the top of the torso.
     */
    private static final double UPPER_TORSO_RADIUS = 0.1 * SIZE;

    /**
     * Radius at the bottom of the torso.
     */
    private static final double LOWER_TORSO_RADIUS = 0.2 * SIZE;

    /**
     * Length of the robot neck.
     */
    private static final double NECK_LENGTH = .05 * SIZE;

    /**
     * Used for both legs and arms.
     */
    private static final double ROBOT_LIMB_RADIUS = .0125 * SIZE;

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

    private final int robotNr;

    /**
     * Angle between the left lower leg and upper leg, used in the calculation
     * for the animation.
     */
    private double alpha;

    /**
     * Angle between the right lower leg and upper leg.
     */
    private double beta;

    /**
     * Angle between the z-as and upper arm.
     */
    private double gamma;

    /**
     * Leading leg is the leg that is on the ground during the walking
     * animation.
     */
    private boolean leftLegIsLeading;

    /**
     * If this is false the middle leg is the front leg.
     */
    private boolean leftLegIsFrontLeg;

    /**
     * Height of the hip, this value is calculated each time that the draw
     * method is called because it depends on the tAnim value.
     */
    private double hipheight;

    /**
     * Height of the shoulder joint based on the value of tAnim after draw is
     * called.
     */
    private double shoulderJointHeight;

    /**
     * Height of the top of the robot shoulders, which is slightly above the
     * shoulder joints. Based on the value of tAnim.
     */
    private double shoulderHeight;
    
    private double trackRoundStep = INITIAL_TRACK_ROUND_STEP;
    
    private double posOnTrack;
    
    private double tAnim; 
    
    private double tAnimModifier = INITIAL_ANIMATION_MODIFIER; 
    
    private long msUntilNextRandomValueUpdate;
    
    private double totalDistanceTravelled;
    
    private final Random random;

    /**
     * Constructs the robot with initial parameters.
     */
    public Robot(Material material, Vector position, int robotNr, Random random) {
        this.material = material;
        this.position = position;
        this.robotNr = robotNr;
        this.random = random;
        
        msUntilNextRandomValueUpdate = random.nextInt(5000) + 3000;
    }
    
    public void determineNewAnimationVariables(int msElapsed) {
        msUntilNextRandomValueUpdate -= msElapsed;
        
        if(msUntilNextRandomValueUpdate < 0) {
            int trackRoundInMs = 32500 + random.nextInt(15000);
            trackRoundStep = 1d/trackRoundInMs;
            tAnimModifier = (1d/trackRoundInMs)/DESIRED_ANIMATION_RATIO;
            
            msUntilNextRandomValueUpdate = random.nextInt(5000) + 3000;
        }
    }
    
    public void calculateNewPosOnTrack(int msElapsed) {
        posOnTrack += (trackRoundStep * msElapsed);
        totalDistanceTravelled += (trackRoundStep * msElapsed);
        while(posOnTrack > 1) {
            posOnTrack -= 1;
        }
    }
    
    public void calculateNewAnimValue(int msElapsed) {
        tAnim += (msElapsed /1000d) * tAnimModifier;
        while(tAnim >= 10) {
            tAnim -= 10;
        }
    }
    
    public double getTotalDistanceTravelled() {
        return totalDistanceTravelled;
    }
    
    public double getTAnim() {
        return this.tAnim;
    }
    
    public double getPosOnTrack() {
        return this.posOnTrack;
    }

    /**
     * Calculates several values used in the animation that are dependent on
     * time during the animation. The main part of the calculation are the
     * angles alpha and beta, these are the angles between the upper and lower
     * leg of both legs.
     *
     * gamma is also calculate this is the angle between the z-as and the upper
     * arm. This will go linearly form 0 to 45.
     *
     * The animation is divided into four parts, half of it is when the right
     * leg makes a step forward and half of it for when the left leg makes a
     * step forward. Those two parts are further subdivided into two parts each
     * where either the left leg is in front of the right leg or the right leg
     * is in front of the left leg.
     *
     * Based on the values calculated for alpha and beta a new hip and shoulder
     * height is calculated. So that the upper body moves up and down based on
     * the position of the legs.
     *
     * @param tAnim Value from 0 to 100 signifying how far along the animation
     * is. 0 is the start, 100 is the end.
     */
    private void calculateAnimValues(double tAnim) {
        if (tAnim <= 25) {
            alpha = 180d - (45d * tAnim) / 25d;
            beta = 135 + 0.075d * Math.pow(tAnim - 25d, 2);
            gamma = (45d * tAnim) / 25d;
            leftLegIsLeading = true;
            leftLegIsFrontLeg = false;
        } else if (tAnim <= 50) {
            alpha = 135 + 0.075d * Math.pow(tAnim - 25d, 2);
            beta = 135 + (45 / 25d) * (tAnim - 25d);
            gamma = 45-((45d * (tAnim-25)) / 25d);
            leftLegIsLeading = false;
            leftLegIsFrontLeg = false;
        } else if (tAnim <= 75) {
            alpha = 135 + 0.075d * Math.pow(tAnim - 75d, 2);
            beta = 180d - (45d * (tAnim - 50)) / 25d;
            gamma = (45d * (tAnim - 50)) / 25d;
            leftLegIsLeading = false;
            leftLegIsFrontLeg = true;
        } else {
            alpha = 135 + (45 / 25d) * (tAnim - 75d);
            beta = 135 + 0.075d * Math.pow(tAnim - 75d, 2);
            gamma = 45-((45d * (tAnim - 75)) / 25d);
            leftLegIsLeading = true;
            leftLegIsFrontLeg = true;
        }

        if (leftLegIsLeading) {
            hipheight = SKELETON_LOWER_LEG_HEIGHT + Math.cos(Math.toRadians(180 - alpha)) * SKELETON_UPPER_LEG_HEIGHT + ANKLE_HEIGHT;
        } else {
            hipheight = SKELETON_LOWER_LEG_HEIGHT + Math.cos(Math.toRadians(180 - beta)) * SKELETON_UPPER_LEG_HEIGHT + ANKLE_HEIGHT;
        }

        shoulderJointHeight = hipheight + SKELETON_BACKBONE_LENGTH;
        shoulderHeight = shoulderJointHeight + .025 * SIZE;

    }

    /**
     * <p>
     * Draws this robot (as a {@code stickfigure} if specified). The first step
     * is translating the robot to its current position. If the boolean
     * stickFigure is true the drawStickFigure method is called. Otherwise the
     * robot proper is drawn by calling drawRobot.</p>
     */
    public void draw(GL2 gl, GLU glu, GLUT glut, boolean stickFigure, double tAnim) {
        calculateAnimValues(tAnim);

        gl.glPushMatrix();

        /**
         * Translate 'to' the position of the robot
         */
        gl.glTranslated(position.x, position.y, position.z);

        int additonalAngle = direction.y < 0 ? 180 : 0;

        gl.glRotated((-Math.toDegrees(Math.atan(direction.x / direction.y))) + additonalAngle, 0, 0, 1); //TODO

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
     * Draws the entire leg (including the shoe) of the robot. Starts at the hip.
     * Hip height is dependent on the animation value. After moving the reference
     * frame to the height of the hip the reference frame is flipped 180 degrees
     * over the x axis.
     * 
     * Then the reference frame is rotated again based on the animation values
     * and in which state the animation is. So that the upper leg is drawn
     * at the correct angle w.r.t. to the torso. 
     * 
     * At the knee it again rotates the reference frame based on the animation
     * values, then it draws the lower leg. 
     * 
     * For the shoe and ankle seperate methods are called that draw those items
     * in more detail, the lower and upper leg consist out of a cylinder.
     *
     * @param leftLeg Draws the left leg when true, otherwise it draws the right
     * leg.
     */
    private void drawRobotLeg(GL2 gl, GLU glu, GLUT glut, boolean leftLeg) {
        gl.glPushMatrix();

        double translationOverXAxis = leftLeg ? -1 * DISTANCE_BETWEEN_LEG_AND_X_AXIS
                : DISTANCE_BETWEEN_LEG_AND_X_AXIS;

        gl.glTranslated(translationOverXAxis, 0, hipheight);

        gl.glRotated(180, 0, 1, 0);

        unsetSpecularMaterialValues(gl);

        gl.glColor3fv(LIMB_COLOR, 0);

        if (!leftLegIsFrontLeg) {
            if (leftLegIsLeading) {
                if (!leftLeg) {
                    gl.glRotated(180 + beta, 1, 0, 0);
                }
            } else if (!leftLeg) {
                gl.glRotated(180 + beta, 1, 0, 0);
            }
        } else if (leftLegIsLeading) {
            if (leftLeg) {
                gl.glRotated(180 + alpha, 1, 0, 0);
            }
        } else if (leftLeg) {
            gl.glRotated(180 + alpha, 1, 0, 0);
        }

        glut.glutSolidCylinder(ROBOT_LIMB_RADIUS, SKELETON_UPPER_LEG_HEIGHT, 16, 16);

        gl.glTranslated(0, 0, SKELETON_UPPER_LEG_HEIGHT);

        if (!leftLegIsFrontLeg) {
            if (leftLegIsLeading) {
                if (leftLeg) {
                    gl.glRotated(180 - alpha, 1, 0, 0);
                } else {
                    gl.glRotated(-beta + 180, 1, 0, 0);
                }
            } else if (leftLeg) {
                gl.glRotated(180 - alpha, 1, 0, 0);
            } else {
                gl.glRotated(-beta + 180, 1, 0, 0);
            }
        } else if (leftLegIsLeading) {
            if (!leftLeg) {
                gl.glRotated(180 - beta, 1, 0, 0);
            } else {
                gl.glRotated(-alpha + 180, 1, 0, 0);
            }
        } else if (!leftLeg) {
            gl.glRotated(180 - beta, 1, 0, 0);
        } else {
            gl.glRotated(-alpha + 180, 1, 0, 0);
        }

        glut.glutSolidCylinder(ROBOT_LIMB_RADIUS, 0.1 * SIZE, 16, 16);

        gl.glTranslated(0, 0, .1 * SIZE);

        if (!leftLegIsFrontLeg) {
            if (leftLegIsLeading) {
                if (leftLeg) {
                    gl.glRotated(alpha, 1, 0, 0);
                }
            } else if (leftLeg) {
                gl.glRotated(alpha, 1, 0, 0);
            }
        } else if (leftLegIsLeading) {
            if (!leftLeg) {
                gl.glRotated(beta, 1, 0, 0);
            }
        } else if (!leftLeg) {
            gl.glRotated(beta, 1, 0, 0);
        }

        setRobotMaterialColor(gl);

        if (leftLeg && leftLegIsFrontLeg || !leftLeg && !leftLegIsFrontLeg) {
            gl.glRotated(180, 0, 1, 0);
        } else {

        }

        gl.glTranslated(0, 0, -(SHOE_HEIGHT + ANKLE_HEIGHT));

        drawShoe(gl, glu, glut, SIZE * .025);

        drawAnkle(gl, glu, glut);

        unsetSpecularMaterialValues(gl);

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
     * Draws either the right or left stick leg. First the reference frame is
     * moved to the height of the hips. Based on the height of the hips
     * calculated at the start of the draw function.</p>
     * 
     * <p>Then it rotates the reference frame 180 degrees over the y axis. After
     * doing that it rotates the reference frame with the angle between the
     * upper leg, for this there are several cases based on which leg is leading,
     * which leg is in front and which leg is being drawn. Then it draws the 
     * upper leg.</p>
     * 
     * <p>At the knee it again rotates the reference with a angle depending on
     * the animation values and alpha or beta values. after which it rotates the
     * reference frame again so that the lower leg and foot joint can be
     * correctly drawn based on the animation values. </p>
     *
     * @param leftSide Whether this stick leg is the left leg of the stick
     * figure. If this value is false it assumed it is the right leg.
     */
    private void drawStickLeg(GL2 gl, GLU glu, GLUT glut, boolean leftLeg) {
        gl.glPushMatrix();

        double translationOverXAxis = leftLeg ? -1 * DISTANCE_BETWEEN_LEG_AND_X_AXIS
                : DISTANCE_BETWEEN_LEG_AND_X_AXIS;

        gl.glTranslated(translationOverXAxis, 0, hipheight);

        gl.glRotated(180, 0, 1, 0);

        glut.glutSolidSphere(SKELETON_JOINT_RADIUS, 16, 16);

        if (!leftLegIsFrontLeg) {
            if (leftLegIsLeading) {
                if (!leftLeg) {
                    gl.glRotated(180 + beta, 1, 0, 0);
                }
            } else if (!leftLeg) {
                gl.glRotated(180 + beta, 1, 0, 0);
            }
        } else if (leftLegIsLeading) {
            if (leftLeg) {
                gl.glRotated(180 + alpha, 1, 0, 0);
            }
        } else if (leftLeg) {
            gl.glRotated(180 + alpha, 1, 0, 0);
        }

        glut.glutSolidCylinder(SKELETON_LIMB_RADIUS, SKELETON_UPPER_LEG_HEIGHT, 16, 16);

        gl.glTranslated(0, 0, SKELETON_UPPER_LEG_HEIGHT);

        if (!leftLegIsFrontLeg) {
            if (leftLegIsLeading) {
                if (leftLeg) {
                    gl.glRotated(180 - alpha, 1, 0, 0);
                } else {
                    gl.glRotated(-beta + 180, 1, 0, 0);
                }
            } else if (leftLeg) {
                gl.glRotated(180 - alpha, 1, 0, 0);
            } else {
                gl.glRotated(-beta + 180, 1, 0, 0);
            }
        } else if (leftLegIsLeading) {
            if (!leftLeg) {
                gl.glRotated(180 - beta, 1, 0, 0);
            } else {
                gl.glRotated(-alpha + 180, 1, 0, 0);
            }
        } else if (!leftLeg) {
            gl.glRotated(180 - beta, 1, 0, 0);
        } else {
            gl.glRotated(-alpha + 180, 1, 0, 0);
        }

        glut.glutSolidSphere(SKELETON_JOINT_RADIUS, 16, 16);

        glut.glutSolidCylinder(SKELETON_LIMB_RADIUS, SKELETON_LOWER_LEG_HEIGHT, 16, 16);

        gl.glTranslated(0, 0, SKELETON_LOWER_LEG_HEIGHT);

        if (!leftLegIsFrontLeg) {
            if (leftLegIsLeading) {
                if (leftLeg) {
                    gl.glRotated(alpha, 1, 0, 0);
                }
            } else if (leftLeg) {
                gl.glRotated(alpha, 1, 0, 0);
            }
        } else if (leftLegIsLeading) {
            if (!leftLeg) {
                gl.glRotated(beta, 1, 0, 0);
            }
        } else if (!leftLeg) {
            gl.glRotated(beta, 1, 0, 0);
        }

        glut.glutSolidSphere(SKELETON_JOINT_RADIUS, 16, 16);

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

            gl.glTranslated(0, 0, hipheight);

            gl.glPushMatrix();
            {

                gl.glTranslated(-1 * DISTANCE_BETWEEN_LEG_AND_X_AXIS, 0, 0);

                gl.glRotated(90, 0, 1, 0);

                glut.glutSolidCylinder(SKELETON_LIMB_RADIUS, SKELETON_HORIZONTAL_HIPBONE_LENGTH, 16, 16);

            }
            gl.glPopMatrix();

            glut.glutSolidCylinder(SKELETON_LIMB_RADIUS, SKELETON_BACKBONE_LENGTH, 16, 16);

            gl.glTranslated(0, 0, SKELETON_BACKBONE_LENGTH);

            gl.glPushMatrix();
            {

                gl.glTranslated(-1 * DISTANCE_BETWEEN_SHOUDLER_AND_X_AXIS, 0, 0);

                glut.glutSolidSphere(SKELETON_JOINT_RADIUS, 16, 16);

                gl.glRotated(90, 0, 1, 0);

                glut.glutSolidCylinder(SKELETON_LIMB_RADIUS, SKELETON_HORIZONTAL_SHOULDER_LENGTH, 16, 16);

                gl.glTranslated(0, 0, .2 * SIZE);

                glut.glutSolidSphere(SKELETON_JOINT_RADIUS, 16, 16);
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
     * First we assign armRotation with a value that makes an angel between the
     * body and the arms. After we made that angle we assign a new value to
     * armRotation. This is turn the arm the the front or the back. If the left
     * is in front the right arm is in fort and the other way around.
     *
     * @param leftArm Based on the value of this variable either the left or
     * right arm is drawn.
     */
    private void drawStickArm(GL2 gl, GLU glu, GLUT glut, boolean leftArm) {
        gl.glPushMatrix();

        double xAxisTranslation = leftArm ? -DISTANCE_BETWEEN_SHOUDLER_AND_X_AXIS : DISTANCE_BETWEEN_SHOUDLER_AND_X_AXIS;

        gl.glTranslated(xAxisTranslation, 0, shoulderJointHeight);

        double upperArmRotation = leftArm ? -ANGLE_BETWEEN_Y_AND_UPPER_ARM : ANGLE_BETWEEN_Y_AND_UPPER_ARM;

        gl.glRotated(upperArmRotation, 0, 1, 0);

        if (leftArm) {
            if (!leftLegIsFrontLeg) {
                upperArmRotation = gamma;
            } else {
                upperArmRotation = -gamma;
            }
        } else if (!leftLegIsFrontLeg) {
            upperArmRotation = -gamma;
        } else {
            upperArmRotation = gamma;
        }

        gl.glRotated(upperArmRotation, 1, 0, 0);

        glut.glutSolidCylinder(SKELETON_LIMB_RADIUS, SKELETON_UPPER_ARM_LENGTH, 16, 16);

        gl.glTranslated(0, 0, SKELETON_UPPER_ARM_LENGTH);

        glut.glutSolidSphere(SKELETON_JOINT_RADIUS, 16, 16);

        gl.glRotated(ANGLE_BETWEEN_X_AND_LOWER_ARM, 1, 0, 0);

        glut.glutSolidCylinder(SKELETON_LIMB_RADIUS, SKELETON_LOWER_ARM_LENGTH, 16, 16);

        gl.glTranslated(0, 0, SKELETON_LOWER_ARM_LENGTH);

        glut.glutSolidSphere(SKELETON_JOINT_RADIUS, 16, 16);

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

        gl.glTranslated(0, 0, shoulderJointHeight);

        glut.glutSolidCylinder(SKELETON_LIMB_RADIUS, SKELETON_NECK_BONE_LENGTH, 16, 16);

        gl.glTranslated(0, 0, SKELETON_NECK_BONE_LENGTH);

        glut.glutSolidSphere(SKELETON_JOINT_RADIUS, 32, 32);

        glut.glutSolidCylinder(SKELETON_LIMB_RADIUS, SKELETON_UPPER_NECK_TO_JAW_HEIGHT, 16, 16);

        gl.glTranslated(0, 0, SKELETON_UPPER_NECK_TO_JAW_HEIGHT);

        gl.glPushMatrix();
        {
            gl.glRotated(-90, 1, 0, 0);

            glut.glutSolidCylinder(SKELETON_LIMB_RADIUS, SKELETON_SPINE_TO_JAW_DISTANCE, 16, 16);

            gl.glTranslated(0, 0, SKELETON_SPINE_TO_JAW_DISTANCE);

            glut.glutSolidSphere(SKELETON_JOINT_RADIUS, 16, 16);
        }
        gl.glPopMatrix();

        glut.glutSolidCylinder(SKELETON_LIMB_RADIUS, SKELETON_JAW_TO_HAIR_HEIGHT, 16, 16);

        gl.glTranslated(0, 0, SKELETON_JAW_TO_HAIR_HEIGHT);

        glut.glutSolidSphere(SKELETON_JOINT_RADIUS, 16, 16);

        gl.glPopMatrix();
    }

    /**
     * Draws the ankle of the robot using the current matrix. The ankle consists
     * out of a cylinder drawn from the base of the foot to the height of the
     * ankle.
     */
    private void drawAnkle(GL2 gl, GLU glu, GLUT glut) {
        glut.glutSolidCylinder(ANKLE_RADIUS, SHOE_HEIGHT + ANKLE_HEIGHT, 16, 16);
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
     * <p>
     * The bottom two cylinders of the shoe are drawn in a different color,
     * without any specular reflection. This represents the sole of the shoe.
     * </p>
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

            glut.glutSolidCylinder(step * Math.sqrt(i), subdivionHeight, 16, 16);
        }

        gl.glPopMatrix();
    }

    /**
     * Moves up to the height of the hips, binds the torso texture, then draws
     * three surfaces. First it draws the outside of the body (a cylinder) for
     * that it uses a parametric definition defined in torsoPoint(u, v).
     * 
     * While drawing the outside it also maps the torso texture onto the body.
     * The torso texture is a strip of four textures, each texture for a different
     * robot. Based on the robotNumber (1 to 4) 1/4 of the texture is mapped
     * onto both the front and back of the torso.
     * 
     * After that the top and bottom of the torso are drawn to close the torso.
     */
    private void drawUpperBody(GL2 gl, GLU glu, GLUT glut) {
        gl.glPushMatrix();

        gl.glTranslated(0, 0, hipheight);

        RobotRace.torso.bind(gl);

        setRobotMaterialColor(gl);

        gl.glBegin(GL2.GL_QUAD_STRIP);

        double steps = 1 / 20d;

        for (int i = 0; i <= 20; i++) {
            Vector lower = torsoPoint(i * steps, 0);
            Vector upper = torsoPoint(i * steps, 1);

            Vector lowerNormal = torsoTangentInUDirection(i * steps, 0).cross(torsoTangentInVDirection(i * steps, 0)).normalized();
            Vector upperNormal = torsoTangentInUDirection(i * steps, 1).cross(torsoTangentInVDirection(i * steps, 1)).normalized();

            double t = i;

            if (t > 10) {
                t -= 10;
            }

            gl.glNormal3d(lowerNormal.x, lowerNormal.y, lowerNormal.z);

            gl.glTexCoord2d(((t / 10d) / 4d) + ((robotNr - 1d)/4), 0);

            gl.glVertex3d(lower.x, lower.y, lower.z);

            gl.glNormal3d(upperNormal.x, upperNormal.y, upperNormal.z);

            gl.glTexCoord2d(((t / 10d) / 4d) + ((robotNr - 1d)/4), 1);

            gl.glVertex3d(upper.x, upper.y, upper.z);
        }

        gl.glEnd();
        
        /**
         * Bind a neutral texture to prevent the rest of the robot from being 
         * impacted. 
         */
        gl.glBindTexture(GL2.GL_TEXTURE_2D, 0);

        gl.glBegin(GL2.GL_TRIANGLE_STRIP);

        gl.glNormal3d(0, 0, 1);

        for (int i = 0; i <= 20; i++) {
            Vector edgePoint = torsoPoint(i * steps, 1);

            gl.glVertex3d(edgePoint.x, edgePoint.y, edgePoint.z);

            gl.glVertex3d(0, 0, TORSO_HEIGHT);
        }

        gl.glEnd();

        gl.glBegin(GL2.GL_TRIANGLE_STRIP);
        gl.glNormal3d(0, 0, -1);

        for (int i = 0; i <= 20; i++) {
            Vector edgePoint = torsoPoint(i * steps, 0);

            gl.glVertex3d(edgePoint.x, edgePoint.y, edgePoint.z);

            gl.glVertex3d(0, 0, 0);
        }

        gl.glEnd();

        unsetSpecularMaterialValues(gl);

        gl.glPopMatrix();
    }

    /**
     * Calculates a point on the cylinder surface of the torso of the robot. 
     * A standard parametric cylinder function is used. However, the radius
     * of the drawn eclipse is decreased as v increases. This way, the effect
     * of a body that is wider at the hips is mimicked. 
     * 
     * A is the maximum radius on the X axis at V is 0. D is how much A decreases
     * as V increases. Until at V is 1 the radius of the torso on the X axis 
     * is LOWER_TORSO_RADIUS. 
     * 
     * @param u A value from 0 to 1
     * @param v A value from 0 to 1
     * @return A point on the torso.
     */
    private Vector torsoPoint(double u, double v) {
        double a = LOWER_TORSO_RADIUS;

        double d = LOWER_TORSO_RADIUS - UPPER_TORSO_RADIUS;

        return new Vector((a - d * v) * Math.cos(2 * Math.PI * u), .6666 * (a - d * v) * Math.sin(2 * Math.PI * u), TORSO_HEIGHT * v);
    }

    /**
     * Derivative of the torsoPoint function w.r.t to the U parameter.
     * @param u
     * @param v
     * @return 
     */
    private Vector torsoTangentInUDirection(double u, double v) {
        double a = LOWER_TORSO_RADIUS;
        double d = LOWER_TORSO_RADIUS - UPPER_TORSO_RADIUS;

        return new Vector(-2 * Math.PI * (a - d * v) * Math.sin(2 * Math.PI * u), .666 * 2 * Math.PI * (a - d * v) * Math.cos(2 * Math.PI * u), 0);
    }

    /**
     * Derivative of the torsoPoint function w.r.t. to the V parameter.
     * @param u
     * @param v
     * @return 
     */
    private Vector torsoTangentInVDirection(double u, double v) {
        double a = LOWER_TORSO_RADIUS;
        double d = LOWER_TORSO_RADIUS - UPPER_TORSO_RADIUS;

        return new Vector(-d * Math.cos(2 * Math.PI * u), -.666 * d * Math.sin(2 * Math.PI * u), TORSO_HEIGHT);
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

        final int nrDivisions = 10;
        final double subdivisionHeight = height / nrDivisions;
        final double step = (bottomRadius - upperRadius) / nrDivisions;

        for (int i = 0; i < nrDivisions; i++) {
            gl.glTranslated(0, 0, -subdivisionHeight);
            glut.glutSolidCylinder(upperRadius + step * i, subdivisionHeight, 16, 16);
        }

        gl.glPopMatrix();
    }

    /**
     * <p>
     * Draws three strands of hair originating from a half sphere on top of the
     * robot head. The method starts by translating up to the top of the head,
     * draws a small sphere. With the center of the sphere lying on top of the
     * robot head. Before the start of this method the local reference frame
     * should be at the center of the robot head.</p>
     *
     * <p>
     * The hairs strands that are drawn are stored in a list of vectors, where
     * the x component is the rotation over the x-axis, the y component is the
     * rotation on the y axis and the z component is the length of the hair
     * strands.</p>
     *
     * <p>
     * From the center of the sphere it draws three cones with a different
     * rotation and different length.</p>
     */
    private void drawRobotHair(GL2 gl, GLU glu, GLUT glut) {
        gl.glPushMatrix();

        gl.glColor3fv(ROBOT_HAIR_COLOR, 0);

        gl.glTranslated(0, 0, 0.1 * SIZE);

        glut.glutSolidSphere(0.025 * SIZE, 16, 16);

        List<Vector> hairStrands = new ArrayList<>();

        hairStrands.add(new Vector(0, 0, 0.1 * SIZE));
        hairStrands.add(new Vector(30, 15, .08 * SIZE));
        hairStrands.add(new Vector(20, -35, 0.1 * SIZE));

        for (Vector hair : hairStrands) {
            gl.glPushMatrix();

            gl.glRotated(hair.x, 1, 0, 0);
            gl.glRotated(hair.y, 0, 1, 0);

            glut.glutSolidCylinder(0.005 * SIZE, hair.z, 8, 8);

            gl.glPopMatrix();
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
     * First we assign armRotation with a value that makes an angel between the
     * body and the arms. After we made that angle we assign a new value to
     * armRotation. This is turn the arm the the front or the back. If the left
     * is in front the right arm is in fort and the other way around.
     *
     * @param leftArm Whether the left arm should be drawn. If this is false the
     * right arm is drawn.
     */
    private void drawRobotArm(GL2 gl, GLU glu, GLUT glut, boolean leftArm) {
        gl.glPushMatrix();

        double xTranslation = leftArm ? -DISTANCE_BETWEEN_SHOUDLER_AND_X_AXIS : DISTANCE_BETWEEN_SHOUDLER_AND_X_AXIS;
        double armRotation = leftArm ? 45 : -45;

        gl.glTranslated(xTranslation, 0, shoulderJointHeight);

        gl.glRotated(armRotation, 0, 1, 0);

        if (leftArm) {
            if (!leftLegIsFrontLeg) {
                armRotation = gamma;
            } else {
                armRotation = -gamma;
            }
        } else if (!leftLegIsFrontLeg) {
            armRotation = -gamma;
        } else {
            armRotation = gamma;
        }

        gl.glRotated(armRotation, 1, 0, 0);

        setRobotMaterialColor(gl);

        drawSolidCup(gl, glu, glut, ROBOT_LIMB_RADIUS * 2, ROBOT_LIMB_RADIUS, SKELETON_UPPER_ARM_LENGTH / 2);

        unsetSpecularMaterialValues(gl);
        gl.glColor3fv(LIMB_COLOR, 0);

        gl.glTranslated(0, 0, -.15 * SIZE);

        glut.glutSolidCylinder(ROBOT_LIMB_RADIUS, SKELETON_UPPER_ARM_LENGTH / 2, 16, 16);

        gl.glRotated(ANGLE_BETWEEN_X_AND_LOWER_ARM, 1, 0, 0);

        glut.glutSolidCylinder(ROBOT_LIMB_RADIUS, SKELETON_LOWER_ARM_LENGTH, 16, 16);

        gl.glTranslated(0, 0, SKELETON_LOWER_ARM_LENGTH);

        glut.glutSolidSphere(ROBOT_LIMB_RADIUS * 2, 16, 16);

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

        gl.glTranslated(0, 0, shoulderHeight);

        glut.glutSolidCylinder(ROBOT_LIMB_RADIUS, NECK_LENGTH, 16, 16);

        gl.glTranslated(0, 0, .15 * SIZE);
        
        gl.glColor3f(1, 1, 1);
        
        RobotRace.head.bind(gl);
        
        gl.glBegin(GL2.GL_QUADS);
        
        gl.glNormal3d(0,1,0);
        
        gl.glTexCoord2d(1,1);
        gl.glVertex3d(-.1*SIZE, .1*SIZE, .1*SIZE);
        gl.glTexCoord2d(0,1);
        gl.glVertex3d(.1*SIZE, .1*SIZE, .1*SIZE);
        gl.glTexCoord2d(0,0);
        gl.glVertex3d(.1*SIZE, .1*SIZE, -.1*SIZE);
        gl.glTexCoord2d(1,0);
        gl.glVertex3d(-.1*SIZE, .1*SIZE, -.1*SIZE);
        
        gl.glNormal3d(1,0,0);
        
        gl.glTexCoord2d(1,1);
        gl.glVertex3d(.1*SIZE, .1*SIZE, .1*SIZE);
        gl.glTexCoord2d(0,1);
        gl.glVertex3d(.1*SIZE, -.1*SIZE, .1*SIZE);
        gl.glTexCoord2d(0,0);
        gl.glVertex3d(.1*SIZE, -.1*SIZE, -.1*SIZE);
        gl.glTexCoord2d(1,0);
        gl.glVertex3d(.1*SIZE, .1*SIZE, -.1*SIZE);
        
        gl.glNormal3d(0,-1,0);
        
        gl.glTexCoord2d(1,1);
        gl.glVertex3d(-.1*SIZE, -.1*SIZE, .1*SIZE);
        gl.glTexCoord2d(0,1);
        gl.glVertex3d(.1*SIZE, -.1*SIZE, .1*SIZE);
        gl.glTexCoord2d(0,0);
        gl.glVertex3d(.1*SIZE, -.1*SIZE, -.1*SIZE);
        gl.glTexCoord2d(1,0);
        gl.glVertex3d(-.1*SIZE, -.1*SIZE, -.1*SIZE);
        
        gl.glNormal3d(-1,0,0);
        
        gl.glTexCoord2d(1,1);
         gl.glVertex3d(-.1*SIZE, .1*SIZE, .1*SIZE);
         gl.glTexCoord2d(0,1);
        gl.glVertex3d(-.1*SIZE, -.1*SIZE, .1*SIZE);
        gl.glTexCoord2d(0,0);
        gl.glVertex3d(-.1*SIZE, -.1*SIZE, -.1*SIZE);
        gl.glTexCoord2d(1,0);
        gl.glVertex3d(-.1*SIZE, .1*SIZE, -.1*SIZE);
        
        gl.glNormal3d(0,0,1);
        
        gl.glTexCoord2d(1,1);
        gl.glVertex3d(-.1*SIZE, .1*SIZE, .1*SIZE);
        gl.glTexCoord2d(0,1);
        gl.glVertex3d(-.1*SIZE, -.1*SIZE, .1*SIZE);
        gl.glTexCoord2d(0,0);
        gl.glVertex3d(.1*SIZE, -.1*SIZE, .1*SIZE);
        gl.glTexCoord2d(1,0);
        gl.glVertex3d(.1*SIZE, .1*SIZE, .1*SIZE);
        
        gl.glNormal3d(0,0,-1);
        
        gl.glTexCoord2d(1,1);
        gl.glVertex3d(-.1*SIZE, .1*SIZE, -.1*SIZE);
        gl.glTexCoord2d(0,1);
        gl.glVertex3d(-.1*SIZE, -.1*SIZE, -.1*SIZE);
        gl.glTexCoord2d(0,0);
        gl.glVertex3d(.1*SIZE, -.1*SIZE, -.1*SIZE);
        gl.glTexCoord2d(1,0);
        gl.glVertex3d(.1*SIZE, .1*SIZE, -.1*SIZE);
        
        gl.glEnd();
        
        gl.glBindTexture(GL2.GL_TEXTURE_2D, 0);

        gl.glColor3fv(ROBOT_HEAD_COLOR, 0);

        /**
         * Pushes a new matrix, moves to the center of the back part of the head
         * and draws it. Afterwards pop the matrix so that the position is
         * returned to the center of the head.
         *
        
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
         *
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
         *
        gl.glPushMatrix();
        {
            gl.glTranslated(0, .075 * SIZE, -.075 * SIZE);
            gl.glColor3ub((byte) 230, (byte) 46, (byte) 0);
            gl.glScaled(4, 1, 1);
            glut.glutSolidCube((float) (.05 * SIZE));
        }
        gl.glPopMatrix();
        * */

        drawEar(gl, glu, glut, true);
        drawEar(gl, glu, glut, false);

        drawEye(gl, glu, glut, true);
        drawEye(gl, glu, glut, false);

        drawRobotHair(gl, glu, glut);

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
            glut.glutSolidCylinder(0.018 * SIZE, 0.0125 * SIZE, 16, 16);
        }
        gl.glPopMatrix();

        gl.glColor3f(0, 0, 0);

        gl.glTranslated(0, .01 * SIZE, .0125 * SIZE);

        glut.glutSolidSphere(.01 * SIZE, 16, 16);

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
     *
     * @param gl
     */
    private void unsetSpecularMaterialValues(GL2 gl) {
        gl.glMaterialfv(GL.GL_FRONT, GLLightingFunc.GL_SPECULAR, new float[]{
            0, 0, 0
        }, 0);
        gl.glMaterialf(GL.GL_FRONT_AND_BACK, GLLightingFunc.GL_SHININESS, 0);
    }

}
