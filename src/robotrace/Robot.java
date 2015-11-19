package robotrace;

import com.jogamp.opengl.util.gl2.GLUT;
import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

/**
* Represents a Robot, to be implemented according to the Assignments.
*/
class Robot {
    /**
     * Size of the robot in meters
     */
    private static final double SIZE = 2;
    private static final double SKELETON_JOINT_RADIUS= (0.05*SIZE) / 2;
    private static final double SKELETON_LIMB_RADIUS = SKELETON_JOINT_RADIUS /2;
    
    /** The position of the robot. */
    public Vector position = new Vector(0, 0, 0);
    
    /** The direction in which the robot is running. */
    public Vector direction = new Vector(1, 0, 0);

    /** The material from which this robot is built. */
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
     * Draws this robot (as a {@code stickfigure} if specified).
     */
    public void draw(GL2 gl, GLU glu, GLUT glut, boolean stickFigure, float tAnim) {
        gl.glPushMatrix();
        
        /**
         * Translate 'to' the position of the robot 
         */
        gl.glTranslated(position.x, position.y, position.z);
        
        if(stickFigure) {
           drawStickFigure(gl, glu, glut);
        } else {
            
        }               
        
        gl.glPopMatrix();
    }

    private void drawStickFigure(GL2 gl, GLU glu, GLUT glut) {
        drawStickLeg(gl, glu, glut, true);
        drawStickLeg(gl, glu, glut, false);
        drawStickBody(gl, glu, glut);
        drawStickArm(gl, glu, glut, true);
        drawStickArm(gl, glu, glut, false);
        drawStickHead(gl, glu, glut);
    }

    /**
     * 
     * @param leftSide Whether this stick leg is the left leg of the stick 
     * figure. If this value is false it assumed it is the right leg. 
     */
    private void drawStickLeg(GL2 gl, GLU glu, GLUT glut, boolean leftLeg) {
        gl.glPushMatrix();
        
        double footHeight = 0.05*SIZE;
        
        double translationOverXAxis = leftLeg ? -0.1*SIZE : 0.1*SIZE;
        
        gl.glTranslated(translationOverXAxis, 0, footHeight);
        
        glut.glutSolidSphere(SKELETON_JOINT_RADIUS, 32,32);
        
        glut.glutSolidCylinder(SKELETON_LIMB_RADIUS, .15*SIZE, 32, 32);
        
        gl.glTranslated(0, 0, .15*SIZE);
        
        glut.glutSolidSphere(SKELETON_JOINT_RADIUS, 32,32);
        
        glut.glutSolidCylinder(SKELETON_LIMB_RADIUS, .15*SIZE, 32, 32);
        
        gl.glTranslated(0, 0, .15*SIZE);
        
        glut.glutSolidSphere(SKELETON_JOINT_RADIUS, 32,32);
        
        gl.glPopMatrix();
    }

    private void drawStickBody(GL2 gl, GLU glu, GLUT glut) {
       gl.glPushMatrix();
       
       gl.glTranslated(0,0, .35*SIZE);
       
       gl.glPushMatrix();
       
       gl.glTranslated(-.1*SIZE, 0, 0);
       
       gl.glRotated(90, 0, 1, 0);
       
       glut.glutSolidCylinder(SKELETON_LIMB_RADIUS, .2*SIZE, 32, 32);
       
       gl.glPopMatrix();
       
       glut.glutSolidCylinder(SKELETON_LIMB_RADIUS, .25*SIZE, 32, 32);
       
       gl.glTranslated(0, 0, .25*SIZE);
       
       gl.glPushMatrix();
       
       gl.glTranslated(-.1*SIZE, 0, 0);
       
       glut.glutSolidSphere(SKELETON_JOINT_RADIUS,32,32);
       
       gl.glRotated(90, 0, 1, 0);
       
       glut.glutSolidCylinder(SKELETON_LIMB_RADIUS, .2*SIZE, 32, 32);
       
       gl.glTranslated(0, 0, .2*SIZE);
       
       glut.glutSolidSphere(SKELETON_JOINT_RADIUS, 32, 32);
       
       gl.glPopMatrix();
       
       
       gl.glPopMatrix();
    }

    private void drawStickArm(GL2 gl, GLU glu, GLUT glut, boolean leftArm) {
        gl.glPushMatrix();
        
        double xAxisTranslation = leftArm ? -.1 * SIZE : .1*SIZE;
        
        gl.glTranslated(xAxisTranslation, 0, .6*SIZE);
        
        double upperArmRotation = leftArm ? -135 : 135;
        
        gl.glRotated(upperArmRotation, 0, 1, 0);
        
        glut.glutSolidCylinder(SKELETON_LIMB_RADIUS, .15*SIZE, 32, 32);
        
        gl.glTranslated(0, 0, .15*SIZE);
        
        glut.glutSolidSphere(SKELETON_JOINT_RADIUS, 32, 32);
        
        gl.glRotated(-90, 1, 0, 0);
        
        glut.glutSolidCylinder(SKELETON_LIMB_RADIUS, .15*SIZE, 32, 32);
        
        gl.glTranslated(0, 0, .15*SIZE);
        
        glut.glutSolidSphere(SKELETON_JOINT_RADIUS, 32, 32);
        
        gl.glPopMatrix();
    }

    private void drawStickHead(GL2 gl, GLU glu, GLUT glut) {
        gl.glPushMatrix();
        
        gl.glTranslated(0,0, .6*SIZE);
        
        glut.glutSolidCylinder(SKELETON_LIMB_RADIUS, .1*SIZE, 32, 32);
        
        gl.glTranslated(0, 0, .1*SIZE);
        
        glut.glutSolidSphere(SKELETON_JOINT_RADIUS, 32, 32);
        
        glut.glutSolidCylinder(SKELETON_LIMB_RADIUS, .05*SIZE, 32, 32);
        
        gl.glTranslated(0, 0, .05*SIZE);
        
        gl.glPushMatrix();
        
        gl.glRotated(-90, 1, 0, 0);
        
        glut.glutSolidCylinder(SKELETON_LIMB_RADIUS, 0.05*SIZE, 32, 32);
        
        gl.glTranslated(0, 0, .05*SIZE);
        
        glut.glutSolidSphere(SKELETON_JOINT_RADIUS, 32, 32);
        
        gl.glPopMatrix();
        
        glut.glutSolidCylinder(SKELETON_LIMB_RADIUS, .15*SIZE, 32, 32);
        
        gl.glTranslated(0, 0, .15*SIZE);
        
        glut.glutSolidSphere(SKELETON_JOINT_RADIUS, 32, 32);
        
        gl.glPopMatrix();
    }
    
}
