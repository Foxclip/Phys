package phys;

import java.awt.Color;
import static java.lang.Math.*;

public class Utils {
    
    public static double randomBetween(double min, double max) {
        return random() * (max - min) + min;
    }
    
    public static double nonLinearRandom(double min, double max, Function func) {
        return func.f(random()) * (max - min) + min;
    }
    
    public static Color randomColor() {
        return new Color((float)random(), (float)random(), (float)random());
    }
    
    public static double distance(double x1, double x2, double y1, double y2) {
        return sqrt(pow(x1 - x2, 2) + pow(y1 - y2, 2));
    }
    
}

abstract class Function {
    public abstract double f(double arg);
}
