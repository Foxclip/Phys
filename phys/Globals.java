package phys;

public final class Globals {
    
    static boolean collisionsEnabled = true;
    static boolean gravityRadialEnabled = false;
    static boolean gravityVerticalEnabled = false;
    static boolean backgroundFrictionEnabled = false;
    static boolean springsEnabled = false;
    
    static double gravityVerticalForce = 0.1;
    static double gravityRadialForce = 0.1;
    static double springForce = 1;
    static double springDamping = 0.5;
    static double springDistance = 20;
    static double springInitialDistance = 0;
    static double springMaxDistance = 25;
    static double springMaxConnections = 600000;
    static double backgroundFrictionForce = 1;
    static double cubucPixelMass = 0.001;
    final static double DAMPING = 1;
    
    static boolean variableTimestep = true;
    static double simulationSpeedExponent = 0;
    static boolean pause = true;
    final static int TARGET_FPS = 100;
    final static int MAX_FRAME_TIME = (int)(0.1 * 1000000000);
    
}
