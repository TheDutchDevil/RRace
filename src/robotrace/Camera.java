package robotrace;

/**
 * Implementation of a camera with a position and orientation. 
 */
class Camera {

    /** The position of the camera. */
    public Vector eye = new Vector(3f, 6f, 5f);

    /** The point to which the camera is looking. */
    public Vector center = Vector.O;

    /** The up vector. */
    public Vector up = Vector.Z;

    /**
     * Updates the camera viewpoint and direction based on the
     * selected camera mode.
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
     * <p>Computes eye, center, and up, based on the camera's default mode. From 
     * the assignment document it has been assumed that for the defaultmode the
     * x,y and z coordinates are calculated by using a spherical coordinates 
     * system. Input for calculating the x, y and z coordinates is gs.theta,
     * gs.phi and gs.vDist (Which is the distance between the eye point and
     * the center point: gs.cnt).</p>
     * 
     * <p>Where gs.vDist is the radius (r). Let V be a vector between the center
     * point and the eye point. theta is the angle between V projected on the 
     * XY-plane and the positive X-axis. Phi is the angle between V and the 
     * XY-plane. To use phi in the calculation for the coordinates of the eye 
     * point it has to be inverted, so that is the angle between V and the Z
     * axis.</p>
     * 
     * <p> the x is calculated by the formula: r*cos(theta)*sin(invertedPhi)
     * the y is calculated by y = r*sin(theta)*cos(invertedPhi)
     * The z is calculated by z = r*cos(invertedPhi)</p>
     * 
     * @param gs instance of the GlobalState object which provides the theta,
     * phi and vDist variables. 
     */
    private void setDefaultMode(GlobalState gs) { 
        double invertedPhi = Math.PI /2 - gs.phi;
        
        this.eye.x = gs.vDist * (float)Math.cos(gs.theta) * 
                (float)Math.sin(invertedPhi);
        
        this.eye.y = gs.vDist * (float)Math.sin(gs.theta) * 
                (float)Math.sin(invertedPhi);
        
        this.eye.z = gs.vDist * (float)Math.cos(invertedPhi);
        
        this.center = gs.cnt;
    }

    /**
     * Computes eye, center, and up, based on the helicopter mode.
     * The camera should focus on the robot.
     */
    private void setHelicopterMode(GlobalState gs, Robot focus) {
        // code goes here ...
    }

    /**
     * Computes eye, center, and up, based on the motorcycle mode.
     * The camera should focus on the robot.
     */
    private void setMotorCycleMode(GlobalState gs, Robot focus) {
        // code goes here ...
    }

    /**
     * Computes eye, center, and up, based on the first person mode.
     * The camera should view from the perspective of the robot.
     */
    private void setFirstPersonMode(GlobalState gs, Robot focus) {
        // code goes here ...
    }
    
    /**
     * Computes eye, center, and up, based on the auto mode.
     * The above modes are alternated.
     */
    private void setAutoMode(GlobalState gs, Robot focus) {
        // code goes here ...
    }

}
