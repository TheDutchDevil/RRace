package robotrace;

import java.util.List;
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

    /**
     * Which mode the auto camera mode selected.
     */
    private int autoCameraMode;

    /**
     * Instance of a random object used for determining the next auto camera
     * mode.
     */
    private final Random random;

    /**
     * Used to ensure that a certain time passed before changing auto camera
     * mode.
     */
    private long msSinceLastCameraSwitch;

    /**
     * Time in ms since the auto camera method was called.
     */
    private long timeOfLastMethodCall;

    public Camera() {
        this.random = new Random();
        this.autoCameraMode = 1;
        this.msSinceLastCameraSwitch = 0;
    }

    /**
     * Updates the camera viewpoint and direction based on the selected camera
     * mode.
     */
    public void update(GlobalState gs, List<Robot> focus) {

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
     * should focus on the robot. Focuses on the robot from above. Uses the
     * tangent of the robot to ensure the camera rotates together with the
     * robots.
     */
    private void setHelicopterMode(GlobalState gs, List<Robot> focus) {
        this.center = focus.get(0).position;
        this.eye = focus.get(0).position.add(new Vector(0, 0, 15));
        this.up = focus.get(0).direction;
    }

    /**
     * Computes eye, center, and up, based on the motorcycle mode. The camera
     * should focus on the robot. Takes a position perpendicular to the focused
     * robot and focuses on the torso of the robot.
     */
    private void setMotorCycleMode(GlobalState gs, List<Robot> focus) {
        
        Robot mostTravelled = null;

        for (Robot rob : focus) {
            if (mostTravelled == null || mostTravelled.getTotalDistanceTravelled() < rob.getTotalDistanceTravelled()) {
                mostTravelled = rob;
            }
        }
        
        mostTravelled = focus.get(0);

        Vector perpToRobot = mostTravelled.direction.cross(Vector.Z).normalized();

        this.eye = mostTravelled.position.add(perpToRobot.scale(8 + focus.indexOf(mostTravelled)*-1.75d));
        this.eye.z = mostTravelled.position.z + 1.5d;

        this.center = mostTravelled.position.add(new Vector(0, 0, 1.1d));
        this.up = Vector.Z;
    }

    /**
     * Computes eye, center, and up, based on the first person mode. The camera
     * should view from the perspective of the robot. Takes a position slightly
     * in front of the focus robot.
     */
    private void setFirstPersonMode(GlobalState gs, List<Robot> focus) {
        Robot leastTravelled = null;

        for (Robot rob : focus) {
            if (leastTravelled == null || leastTravelled.getTotalDistanceTravelled() > rob.getTotalDistanceTravelled()) {
                leastTravelled = rob;
            }
        }

        this.center = leastTravelled.direction.normalized().add(leastTravelled.position).add(new Vector(0, 0, 1.75));
        this.up = Vector.Z;
        this.eye = leastTravelled.position;
        this.eye.z += 1.75;

        this.eye = this.eye.add(leastTravelled.direction.normalized().scale(.5d));
    }

    /**
     * Computes eye, center, and up, based on the auto mode. The above modes are
     * alternated. Changes camera mode randomly every 3 seconds.
     */
    private void setAutoMode(GlobalState gs, List<Robot> focus) {
        msSinceLastCameraSwitch += System.currentTimeMillis() - timeOfLastMethodCall;

        if (msSinceLastCameraSwitch > 3000) {
            this.autoCameraMode = random.nextInt(3) + 1;
            msSinceLastCameraSwitch = 0;
        }

        switch (this.autoCameraMode) {
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
