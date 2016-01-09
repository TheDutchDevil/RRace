package robotrace;

import java.util.Random;

/**
 * Implementation of a camera with a position and orientation.
 */
class Camera {

    /**
     * The position of the camera.
     */
    public Vector eye = new Vector(3f, 6f, 5f);

    /**
     * The point to which the camera is looking.
     */
    public Vector center = Vector.O;

    /**
     * The up vector.
     */
    public Vector up = Vector.Z;
    
    private int autoCameraMode;
    private Random random; 
    private long msSinceLastCameraSwitch;
    private long timeOfLastMethodCall;
        
    public Camera () {
        this.random = new Random();
        this.autoCameraMode = 1;
        this.msSinceLastCameraSwitch = 0;
    }

    /**
     * Updates the camera viewpoint and direction based on the selected camera
     * mode.
     */
    public void update(GlobalState gs, Robot focus) {

        switch (gs.camMode) {

            // Helicopter mode
            case 1:
                setHelicopterMode(gs, focus);
                break;

            // Motor cycle mode    
            case 2:
                setMotorCycleMode(gs, focus);
                break;

            // First person mode    
            case 3:
                setFirstPersonMode(gs, focus);
                break;

            // Auto mode    
            case 4:
                setAutoMode(gs, focus);
                break;

            // Default mode    
            default:
                setDefaultMode(gs);
        }
    }

    /**
     * <p>
     * Computes eye, center, and up, based on the camera's default mode. From
     * the assignment document it has been assumed that for the defaultmode the
     * x,y and z coordinates are calculated by using a spherical coordinates
     * system. Input for calculating the x, y and z coordinates is gs.theta,
     * gs.phi and gs.vDist (Which is the distance between the eye point and the
     * center point: gs.cnt).</p>
     *
     * <p>
     * the x is calculated by the formula: r*cos(theta)*sin(invertedPhi) the y
     * is calculated by y = r*sin(theta)*cos(invertedPhi) The z is calculated by
     * z = r*cos(invertedPhi). This calculating is implemented in
     * RobotRace.sphericalToCoords</p>
     *
     * @param gs instance of the GlobalState object which provides the theta,
     * phi and vDist variables.
     */
    private void setDefaultMode(GlobalState gs) {
        double invertedPhi = Math.PI / 2 - gs.phi;

        this.eye = RobotRace.sphericalToCoords(gs.theta, invertedPhi, gs.vDist);

        this.center = gs.cnt;
        this.up = Vector.Z;
    }

    /**
     * Computes eye, center, and up, based on the helicopter mode. The camera
     * should focus on the robot. Focuses on the robot from above. 
     */
    private void setHelicopterMode(GlobalState gs, Robot focus) {
        this.center = focus.position;
        this.eye = focus.position.add(new Vector(0, 0, 15));
        this.up = focus.direction;
    }

    /**
     * Computes eye, center, and up, based on the motorcycle mode. The camera
     * should focus on the robot.
     */
    private void setMotorCycleMode(GlobalState gs, Robot focus) {
        
        
        Vector perpToRobot = focus.direction.cross(Vector.Z).normalized();
        
        this.eye = focus.position.add(perpToRobot.scale(2.1d));
        this.eye.z = focus.position.z + 1.5d;
        
        this.center = Vector.O;
        this.up = Vector.Z;
    }

    /**
     * Computes eye, center, and up, based on the first person mode. The camera
     * should view from the perspective of the robot.
     */
    private void setFirstPersonMode(GlobalState gs, Robot focus) {
        this.center = focus.direction;
        this.up = Vector.Z;
        this.eye = focus.position;
        this.eye.z += 1.75;
        
        this.eye = this.eye.add(focus.direction.normalized().scale(.5d));
    }

    /**
     * Computes eye, center, and up, based on the auto mode. The above modes are
     * alternated.
     */
    private void setAutoMode(GlobalState gs, Robot focus) {
        msSinceLastCameraSwitch += System.currentTimeMillis() - timeOfLastMethodCall;
        
        if(msSinceLastCameraSwitch > 3000) {
            this.autoCameraMode = random.nextInt(3) + 1;
            msSinceLastCameraSwitch = 0;            
        }        
        
        switch(this.autoCameraMode) {
            case 1:
                this.setHelicopterMode(gs, focus);
                break;
            case 2:
                this.setMotorCycleMode(gs, focus);
                               break;
            case 3:
                this.setFirstPersonMode(gs, focus);
                break;
        }
        
        timeOfLastMethodCall = System.currentTimeMillis();
    }

}
