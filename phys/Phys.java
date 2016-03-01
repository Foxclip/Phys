package phys;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import static java.lang.Math.*;
import static phys.Globals.*;

//TODO friction
//TODO cross-bonds
//TODO apr collisions
//TODO scroll field

public class Phys {
    
    static Thread t;
    
    public static void main(String[] args) {
        createAndShowGUI();
    }

    private static void createAndShowGUI() {
        JFrame f = new JFrame();
        //f.setBounds(400, 100, 500, 500);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //f.setUndecorated(true);
        f.setExtendedState(JFrame.MAXIMIZED_BOTH);
        MyPanel p = new MyPanel();
        p.addKeyListener(p);
        f.add(p);
        f.setVisible(true);
        p.generateObjects();
        t = new Thread(p);
        t.start();
    }
    
}

class MyPanel extends JPanel implements Runnable, KeyListener {
    
    int fps = 0;
    double simulationSpeed = pow(2, simulationSpeedExponent);
    ArrayList<PhysObject> objects;

    public MyPanel() {
        objects = new ArrayList();
    }
    
    @Override
    public void addNotify() {
        super.addNotify();
        requestFocus();
    }

    public void generateObjects() {
        synchronized(objects) {
//            for(int i = 0; i < 1000; i++) {
//               objects.add(generateRandomPhysObject("ball"));
//            }
            for(int i = 0; i < 30; i++) {
                for(int j = 0; j < 30; j++) {
                    objects.add(new Ball(i*20+100+random()-0.5, j*20+100, 5, 0, 1, Utils.randomColor()));
                }
            }
            //objects.add(new Ball(800, 401, 30, -150, 0, Color.red));
//            objects.add(new Ball(600, 200, 10, 0, 0, Color.green)); 
//            objects.add(new Ball(400, 400, 10, 0, 0, Color.blue));
//        objects.add(new Ball(100, 350, 30, 1, 0, Color.white));
//            for(int i = 0; i < 5; i++) {
//                for(int j = 0; j < i; j++) {
//                    objects.add(new Ball(500 + i*40, 350 + j*40 - i*20 + 20, 20, 0, 0, Utils.randomColor()));
//                }
//            }
            initializeSprings();
        }
    }
    
    public void initializeSprings() {
        synchronized(objects) {
            for(PhysObject object1 : objects) {
                for(PhysObject object2 : objects) {
                    if(object1 == object2) continue;
                    double distance = Utils.distance(object1.x, object2.x, object1.y, object2.y);
                    if(distance <= springInitialDistance) {
                        object1.springConnections.add(object2);
                    }
                }
            }
        }
    }
    
    public void deleteAll() {
        synchronized(objects) {
            objects.clear();
        }
    }
    
    public PhysObject generateRandomPhysObject(String type) {
        double x = Utils.randomBetween(25, getWidth() - 25);
        double y = Utils.randomBetween(25, getHeight() - 25);
//        double radius = Utils.nonLinearRandom(5, 50, new Function() {
//            @Override
//            public double f(double arg) {
//                return pow(arg, 100);
//            }
//        });
        double radius = Utils.randomBetween(5, 5);
        double width = Utils.randomBetween(10, 50);
        double height = Utils.randomBetween(10, 50);
        double speedX = Utils.randomBetween(0, 0);
        double speedY = Utils.randomBetween(0, 0);
        Color color = Utils.randomColor();
        PhysObject result = null;
        switch(type) {
            case "box": result = new Box(x, y, width, height, speedX, speedY, color); break;
            case "ball" : result = new Ball(x, y, radius, speedX, speedY, color); break;
            default: System.out.println("Unknown PhysObject type: " + type + "\n");
        }
        return result;
    }
    
    public void changeSimulationSpeed(int change) {
        simulationSpeedExponent += change;
        simulationSpeed = pow(2, simulationSpeedExponent);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
      
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());
        
        synchronized(objects) {
            if(springsEnabled)
                drawSprings(g);
            drawObjects(g);
        }
        
        drawText(g);
        
    }
    
    public void drawObjects(Graphics g) {
        for(PhysObject object : objects) {
            object.draw(g);
        }
    }
    
    public void drawSprings(Graphics g) {
        for(PhysObject object1 : objects) {
            g.setColor(Color.yellow);
            for(PhysObject object2 : object1.springConnections)
                g.drawLine((int)object1.x, (int)object1.y, (int)object2.x, (int)object2.y);
        }
    }
    
    public void drawText(Graphics g) {
        FontMetrics fontMetrics = g.getFontMetrics();
        String str;
        g.setColor(Color.yellow);
        str = "fps: "+fps;
        g.drawString(str, 0, getFont().getSize());
        str = "Collisions (1)";
        g.setColor(collisionsEnabled ? Color.GREEN : Color.RED);
        g.drawString(str, getWidth() - fontMetrics.stringWidth(str), getFont().getSize());
        str = "Gravity radial (2)";
        g.setColor(gravityRadialEnabled ? Color.GREEN : Color.RED);
        g.drawString(str, getWidth() - fontMetrics.stringWidth(str), getFont().getSize()*2);
        str = "Gravity vertical (3)";
        g.setColor(gravityVerticalEnabled ? Color.GREEN : Color.RED);
        g.drawString(str, getWidth() - fontMetrics.stringWidth(str), getFont().getSize()*3);
        str = "Backgroud friction (4)";
        g.setColor(backgroundFrictionEnabled ? Color.GREEN : Color.RED);
        g.drawString(str, getWidth() - fontMetrics.stringWidth(str), getFont().getSize()*4);
        str = "Springs (5)";
        g.setColor(springsEnabled ? Color.GREEN : Color.RED);
        g.drawString(str, getWidth() - fontMetrics.stringWidth(str), getFont().getSize()*5);
        str = "Simulation speed: " + simulationSpeed + " (" + simulationSpeedExponent + ")";
        g.setColor(Color.yellow);
        g.drawString(str, getWidth() - fontMetrics.stringWidth(str), getFont().getSize()*7);
        g.setColor(Color.yellow);
        g.setFont(getFont().deriveFont(50f));
        fontMetrics = g.getFontMetrics();
        if(pause)
            g.drawString("PAUSE", getWidth()/2 - fontMetrics.stringWidth("PAUSE")/2, getHeight()/2 - getFont().getSize()/2);
    }
    
    public void gameUpdate(double delta) {
        synchronized(objects) {
            if(pause) return;
            if(gravityRadialEnabled)
                for(PhysObject object1 : objects)
                    for(PhysObject object2 : objects) {
                        if(object1 == object2) continue;
                        object1.calculateGravity(object2, delta);
                    }
            if(gravityVerticalEnabled)
                for(PhysObject object : objects)
                    object.calculateVerticalGravity(delta);
            if(springsEnabled) {
                for(PhysObject object1 : objects) {
                    if(object1.springConnections.size() >= springMaxConnections) continue;
                    for(PhysObject object2 : objects) {
                        if(object1 == object2) continue;
                        if(object1.springConnections.size() >= springMaxConnections) break;
                        if(object2.incomingSpringConnections >= springMaxConnections) continue;
                        if(object1.springConnections.indexOf(object2) != -1) continue;
                        if(Utils.distance(object1.x, object2.x, object1.y, object2.y) < springMaxDistance) {
                            object1.springConnections.add(object2);
                            object2.incomingSpringConnections++;
                        }
                    }
                }
                for(PhysObject object : objects)
                    for(int i = object.springConnections.size() - 1; i >= 0; i--)
                        object.calculateSprings(object.springConnections.get(i), delta);
            }
            if(backgroundFrictionEnabled)
                for(PhysObject object : objects)
                    object.calculateBackgroudFriction(delta);
            for(PhysObject object : objects)
                object.move(0, 0, getWidth(), getHeight(), delta);
            if(collisionsEnabled)
                for(PhysObject object1 : objects)
                    for(PhysObject object2 : objects) {
                        if(object1 == object2) continue;
                        PhysObject.collide(object1, object2);
                    }
        }
    }
    
    @Override
    public void run() {
        
        final long OPTIMAL_TIME = 1000000000 / TARGET_FPS;
        long frameStartTime = System.nanoTime();
        long fpsTime = 0;
        int fpsCount = 0;
        
        while(true) {
            
            long now = System.nanoTime();
            long updateLength = now - frameStartTime;
            frameStartTime = now;
            double delta = updateLength / (double)OPTIMAL_TIME;
            fpsTime += updateLength;
            if(fpsTime >= 1000000000) {
                fpsTime = 0;
                fps = fpsCount;
                fpsCount = 0;
            }
            fpsCount++;
            
            if(!variableTimestep)
                delta = 1;
            delta *= simulationSpeed;
            double maxDelta = MAX_FRAME_TIME / OPTIMAL_TIME;
            if(delta > maxDelta)
                delta = maxDelta;
            
            gameUpdate(delta);
            repaint();
            
            try {
                if(!variableTimestep)
                     Thread.sleep(10);
                else
                    Thread.sleep(max(1,(OPTIMAL_TIME -
                            (System.nanoTime() - frameStartTime)) / 1000000));
                   
            } catch (Exception e) {
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent ke) {
    }

    @Override
    public void keyPressed(KeyEvent ke) {
        switch(ke.getKeyCode()) {
            case KeyEvent.VK_ESCAPE:
                System.exit(0);
                break;
            case KeyEvent.VK_SPACE:
                pause = !pause;
                break;
            case KeyEvent.VK_R:
                deleteAll();
                generateObjects();
                break;
            case KeyEvent.VK_1:
                collisionsEnabled = !collisionsEnabled;
                break;
            case KeyEvent.VK_2:
                gravityRadialEnabled = !gravityRadialEnabled;
                break;
            case KeyEvent.VK_3:
                gravityVerticalEnabled = !gravityVerticalEnabled;
                break;
            case KeyEvent.VK_4:
                backgroundFrictionEnabled = !backgroundFrictionEnabled;
                break;
            case KeyEvent.VK_5:
                springsEnabled = !springsEnabled;
                break;
            case 107:
                changeSimulationSpeed(1);
                break;
            case 109:
                changeSimulationSpeed(-1);
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent ke) {
    }
    
}