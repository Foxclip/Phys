package phys;

import java.awt.Color;
import java.awt.Graphics;
import static java.lang.Math.*;
import java.util.ArrayList;
import static phys.Globals.*;

public abstract class PhysObject {
    
    double x, y;
    double speedX, speedY;
    double damping;
    Color color;
    protected double mass;
    public ArrayList<PhysObject> springConnections;
    int incomingSpringConnections = 0;
    
    public PhysObject(double x, double y, double speedX, double speedY, Color color) {
        this.x = x;
        this.y = y;
        this.speedX = speedX;
        this.speedY = speedY;
        this.color = color;
        damping = DAMPING;
        springConnections = new ArrayList();
    }
    
    public abstract void recalculateMass();
    public abstract void draw(Graphics g);
    
    public double getMass() {
        return mass;
    }
    
    public void move(double minX, double minY, double maxX, double maxY, double delta) {
        x += speedX * delta;
        y += speedY * delta;
    }
    
    public void calculateBackgroudFriction(double delta) {
        double speed = sqrt(pow(speedX, 2) + pow(speedY, 2));
        if(speed == 0)
            return;
        double forceX = -speedX / speed * backgroundFrictionForce;
        double forceY = -speedY / speed * backgroundFrictionForce;
        double oldSpeedX = speedX;
        double oldSpeedY = speedY;
        speedX += forceX / getMass() * delta;
        speedY += forceY / getMass() * delta;
        if(speedX * oldSpeedX < 0)
            speedX = 0;
        if(speedY * oldSpeedY < 0)
            speedY = 0;
    }
    
    public static void collide(PhysObject obj1, PhysObject obj2) {
        if(obj1.getClass() == Ball.class && obj2.getClass() == Ball.class) {
            Ball.collideBalls((Ball)obj1, (Ball)obj2);
        }
    }
    
    public void calculateVerticalGravity(double delta) {
        speedY += gravityVerticalForce * delta;
    }
    
    public void calculateGravity(PhysObject anotherObject, double delta) {
        
        double distance = Utils.distance(x, anotherObject.x, y, anotherObject.y);
        if(distance == 0)
            return;
        double force = gravityRadialForce * getMass() * anotherObject.getMass() / pow(distance, 2);
        double forceX = (anotherObject.x - x) / distance * force;
        double forceY = (anotherObject.y - y) / distance * force;
        speedX += forceX / getMass() * delta;
        speedY += forceY / getMass() * delta;
        
    }
    
    public void calculateSprings(PhysObject anotherObject, double delta) {
        double distance = Utils.distance(x, anotherObject.x, y, anotherObject.y);
        if(distance == 0) return;
        if(distance > springMaxDistance) {
            springConnections.remove(anotherObject);
            anotherObject.incomingSpringConnections--;
            return;
        }
        double offset = distance - springDistance;
        double relativeSpeedX = anotherObject.speedX - speedX;
        double relativeSpeedY = anotherObject.speedY - speedY;
        double relativeSpeed = sqrt(pow(relativeSpeedX, 2) + pow(relativeSpeedY, 2));
        double dampingForce = relativeSpeed * springDamping;
        double dampingForceX, dampingForceY;
        if(relativeSpeed != 0) {
            dampingForceX = relativeSpeedX / relativeSpeed * dampingForce;
            dampingForceY = relativeSpeedY / relativeSpeed * dampingForce;
        } else {
            dampingForceX = 0;
            dampingForceY = 0;
        }
        double force;
        force = offset * springForce - dampingForce;
        double forceX = (anotherObject.x - x) / distance * force + dampingForceX;
        double forceY = (anotherObject.y - y) / distance * force + dampingForceY;
        speedX += forceX / getMass() * delta;
        speedY += forceY / getMass() * delta;
    }
    
}

class Box extends PhysObject {
    
    private double width, height;
    
    public Box(double x, double y, double width, double height, double speedX, double speedY, Color color) {
        super(x, y, speedX, speedY, color);
        setSize(width, height);
    }
    
    private void setSize(double width, double height) {
        this.width = width;
        this.height = height;
        recalculateMass();
    }
    
    @Override
    public void draw(Graphics g) {
        g.setColor(color);
        g.fillRect((int)x, (int)y, (int)width, (int)height);
    }

    @Override
    public void move(double minX, double minY, double maxX, double maxY, double delta) {
        
        super.move(minX, minY, maxX, maxY, delta);
        
        if(x < 0) {
            x = 0;
            speedX = -speedX;
        }
        if(x > maxX - width) {
            x = maxX - width;
            speedX = -speedX;
        }
        if(y < 0) {
            y = 0;
            speedY = -speedY;
        }
        if(y > maxY - height) {
            y = maxY - height;
            speedY = -speedY;
        }
        
    }

    @Override
    public void recalculateMass() {
        double bigSide = max(width, height);
        double smallSide = min(width, height);
        mass = pow(bigSide, 2)*smallSide*cubucPixelMass;
    }
    
}

class Ball extends PhysObject {
    
    private double radius;
    
    public Ball(double x, double y, double radius, double speedX, double speedY, Color color) {
        super(x, y, speedX, speedY, color);
        setRadius(radius);
    }
    
    private void setRadius(double radius) {
        this.radius = radius;
        recalculateMass();
    }
    
    @Override
    public void draw(Graphics g) {
        g.setColor(color);
        g.fillOval((int)x - (int)radius, (int)y - (int)radius, (int)radius*2, (int)radius*2);
    }

    @Override
    public void move(double minX, double minY, double maxX, double maxY, double delta) { 
        
        super.move(minX, minY, maxX, maxY, delta);
        
        if(x < radius) {
            x = radius;
            speedX = -speedX * damping;
        }
        if(x > maxX - radius) {
            x = maxX - radius;
            speedX = -speedX * damping;
        }
        if(y < radius) {
            y = radius;
            speedY = -speedY * damping;
        }
        if(y > maxY - radius) {
            y = maxY - radius;
            speedY = -speedY * damping;
        }
        
    }
    
    public static void collideBalls(Ball b1, Ball b2) {
        
        double distance = Utils.distance(b1.x, b2.x, b1.y, b2.y);
        if(distance == 0)
            return;
        double overlap = b1.radius + b2.radius - distance;
        
        if(overlap <= 0)
            return;

        double b1Speed = sqrt(pow(b1.speedX, 2) + pow(b1.speedY, 2));
        double b2Speed = sqrt(pow(b2.speedX, 2) + pow(b2.speedY, 2));
        double collisionAngle = atan2(b2.x - b1.x, b1.y - b2.y) - PI/2;
        double b1SpeedAngle = atan2(b1.speedX, -b1.speedY) - PI/2;
        double b2SpeedAngle = atan2(b2.speedX, -b2.speedY) + PI/2;
        double b1SpeedXRot = b1Speed * cos(b1SpeedAngle - collisionAngle);
        double b1SpeedYRot = b1Speed * sin(b1SpeedAngle - collisionAngle);
        double b2SpeedXRot = -b2Speed * cos(b2SpeedAngle - collisionAngle);
        double b2SpeedYRot = -b2Speed * sin(b2SpeedAngle - collisionAngle);
        double b1NewSpeedXRot = partiallyElasticCollision(b1SpeedXRot,
                b2SpeedXRot, b1.getMass(), b2.getMass(), b1.damping*b2.damping);
        double b2NewSpeedXRot = partiallyElasticCollision(b2SpeedXRot,
                b1SpeedXRot, b2.getMass(), b1.getMass(), b1.damping*b2.damping);
        b1.speedX = b1NewSpeedXRot * cos(collisionAngle) + b1SpeedYRot * cos(collisionAngle + PI/2);
        b1.speedY = b1NewSpeedXRot * sin(collisionAngle) + b1SpeedYRot * sin(collisionAngle + PI/2);
        b2.speedX = b2NewSpeedXRot * cos(collisionAngle) + b2SpeedYRot * cos(collisionAngle + PI/2);
        b2.speedY = b2NewSpeedXRot * sin(collisionAngle) + b2SpeedYRot * sin(collisionAngle + PI/2);
        
        double k = overlap / distance;
        b1.x += (b1.x - b2.x)*k/2;
        b1.y += (b1.y - b2.y)*k/2;
        b2.x += (b2.x - b1.x)*k/2;
        b2.y += (b2.y - b1.y)*k/2;
        
    }
    
    public static double elasticCollision(double v1, double v2, double m1, double m2) {
        return (v1*(m1-m2)+2*m2*v2)/(m1+m2);
    }
    
    public static double partiallyElasticCollision(double v1, double v2, double m1, double m2, double restitution) {
        return (restitution*m2*(v2-v1)+m1*v1+m2*v2)/(m1+m2);
    }

    @Override
    public void recalculateMass() {
        //mass = 4.0/3.0*PI*pow(radius, 3)*cubucPixelMass;
        mass = PI * pow(radius, 2);
    }
    
}
