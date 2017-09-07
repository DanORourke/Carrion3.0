package GUI;

import Engine.Engine;
import javax.swing.*;
import java.awt.*;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.HashMap;

public class DrawingPanel extends JPanel{
    private final Engine engine;
    private JTextArea hoverArea;
    private final HashMap<Coords, Tile> map;
    private final int mapTileRadius;
    private int radius = 15;//starting zoom
    private int x1;
    private int y1;
    private int y2;
    private int thickness;
    private int xCenter;
    private int yCenter;
    private int camera;


    DrawingPanel(Engine engine){
        //this.radius = radius;
        this.engine = engine;
        this.mapTileRadius = engine.getMapRadius();
        this.camera = engine.getStateInfo()[0] - 1;
        this.map = new HashMap<>();
        setXY();
        createMap();
        setPoints();
        updateMap();
        setBackground(Colors.BACKGROUND);
        MyMouseListener ml = new MyMouseListener();
        addMouseListener(ml);
        addMouseMotionListener(ml);
        addMouseWheelListener(ml);
    }

    private void createMap(){
        map.put(new Coords(0, 0, 0), new Tile());
        for (int i = 1; i < mapTileRadius * 3; i++) {
            map.put(new Coords(i, -i, 0), new Tile());
            map.put(new Coords(-i, i, 0), new Tile());
            map.put(new Coords(i, 0, -i), new Tile());
            map.put(new Coords(-i, 0, i), new Tile());
            map.put(new Coords(0, i, -i), new Tile());
            map.put(new Coords(0, -i, i), new Tile());
            for (int k = 1; k < i; k++) {
                map.put(new Coords(-i, k, (i - k)), new Tile());
                map.put(new Coords(i, -k, -(i - k)), new Tile());
                map.put(new Coords(k, -i, (i - k)), new Tile());
                map.put(new Coords(-k, i, -(i - k)), new Tile());
                map.put(new Coords(-k, -(i - k), i), new Tile());
                map.put(new Coords(k, (i - k), -i), new Tile());
            }
        }
        setInitialPoints();
    }

    private void setXY(){
        y2 = radius;
        y1 = radius/2;
        x1 = (int)(radius * 0.866);
        thickness = radius/7;
        if (thickness < 2){
            thickness = 2;
        }
        xCenter = x1 * 4 * mapTileRadius;
        yCenter = (y1 +y2) * 2 * mapTileRadius;
        setPreferredSize(new Dimension(xCenter*2, yCenter*2));
        revalidate();
    }

    private void setInitialPoints(){
        ArrayList<Coords> dump = new ArrayList<>();
        for (Coords c : map.keySet()){
            int q = c.getQ();
            int r = c.getR();
            int s = c.getS();
            int xAdjust;
            int yAdjust;
            if (camera == 0){
                xAdjust = (q * x1) + (2 * r * x1);
                yAdjust = (y2 + y1) * q;
            }else if (camera == 4){
                //blue
                xAdjust = ((r * x1) + (2 * q * x1)) * -1;
                yAdjust = (y2 + y1) * r;
            }else if (camera == 2){
                //green
                xAdjust = ((s * x1) + (2 * q * x1));
                yAdjust = (y2 + y1) * s;
            }else if (camera == 5){
                //purple
                xAdjust = ((s * x1) + (2 * r * x1));
                yAdjust = (y2 + y1) * s * -1;
            }else if (camera == 1){
                //yellow
                xAdjust = ((r * x1) + (2 * q * x1));
                yAdjust = (y2 + y1) * r * -1;
            }else{
                //3, white
                xAdjust = ((q * x1) + (2 * r * x1)) * -1;
                yAdjust = (y2 + y1) * q * -1;
            }
            GameData data;
            int qA = Math.abs(q);
            int rA = Math.abs(r);
            int sA = Math.abs(s);
            if ((qA == mapTileRadius || rA == mapTileRadius || sA == mapTileRadius) &&
                    !(qA > mapTileRadius) && !(rA > mapTileRadius) && !(sA > mapTileRadius)){
                data = new GameData(true);
                map.put(c, new Tile(xCenter + xAdjust, yCenter + yAdjust, data));
            }
            else if ((qA > mapTileRadius || rA > mapTileRadius || sA > mapTileRadius)){
                //camo
                int color = (int )(Math.random() * 3);
                //reduce greys and colors
                if(color > 1){
                    color = (int )(Math.random() * 3);
                }
                data = new GameData(true, color);
                map.put(c, new Tile(xCenter + xAdjust, yCenter + yAdjust, data));
                //tri split
//                int color;
//                if (q > 0 && r < 0){
//                    color = 0;
//                }else if(s < 0){
//                    color = 1;
//                }else{
//                    color = 2;
//                }
//                data = new GameData(true, color);
//                map.put(c, new Tile(xCenter + xAdjust, yCenter + yAdjust, data));
            }else {
                data = new GameData();
                map.put(c, new Tile(xCenter + xAdjust, yCenter + yAdjust, data));
            }
        }
        for (Coords c : dump){
            map.remove(c);
        }
    }

    private void setPoints(){
        for (Coords c : map.keySet()){
            int q = c.getQ();
            int r = c.getR();
            int s = c.getS();
            Tile tile = map.get(c);
            int xAdjust;
            int yAdjust;
            if (camera == 0){
                xAdjust = (q * x1) + (2 * r * x1);
                yAdjust = (y2 + y1) * q;
            }else if (camera == 4){
                //blue
                xAdjust = ((r * x1) + (2 * q * x1)) * -1;
                yAdjust = (y2 + y1) * r;
            }else if (camera == 2){
                //green
                xAdjust = ((s * x1) + (2 * q * x1));
                yAdjust = (y2 + y1) * s;
            }else if (camera == 5){
                //purple
                xAdjust = ((s * x1) + (2 * r * x1));
                yAdjust = (y2 + y1) * s * -1;
            }else if (camera == 1){
                //yellow
                xAdjust = ((r * x1) + (2 * q * x1));
                yAdjust = (y2 + y1) * r * -1;
            }else{
                //3, white
                xAdjust = ((q * x1) + (2 * r * x1)) * -1;
                yAdjust = (y2 + y1) * q * -1;
            }
            GameData data = tile.getGd();
            map.put(c, new Tile(xCenter + xAdjust, yCenter + yAdjust, data));
        }
    }

    private void rotateCamera(int addition){
        camera += addition;
        //modulo not working for some reason
        if (camera > 5){
            camera -= 6;
        }
        if (camera < 0){
            camera += 6;
        }
        setPoints();
        revalidate();
    }

    void updateMap(){
        clearMap();
        HashMap<Coords, GameData> change = engine.getTotalUpdate();
        for (Coords c : change.keySet()){
            Tile tile = map.get(c);
            map.put(c, new Tile(tile.getX(), tile.getY(), change.get(c)));
        }
        revalidate();
        repaint();
    }

    private void clearMap(){
        for (Coords c : map.keySet()){
            GameData gd = map.get(c).getGd();
            if (!gd.isBorder() && !gd.isOutskirts()){
                Tile t = map.get(c);
                map.put(c, new Tile(t.getX(), t.getY(), new GameData()));
            }
        }
    }

    public void paintComponent(Graphics g){
        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setFont(new Font("TimesRoman", Font.PLAIN, 20));
        super.paintComponent(g2);
        //draw grid
        for (Coords c : map.keySet()){
            Tile tile = map.get(c);
            int x = tile.getX();
            int y = tile.getY();
            GameData gd = tile.getGd();
            paintHex(x, y, gd, g2);
        }
    }

    private Color getColor(int col){
        if (col == 1){
            return Colors.RED;
        }if (col == 2){
            return Colors.ORANGE;
        }if (col == 3){
            return Colors.YELLOW;
        }if (col == 4){
            return Colors.GREEN;
        }if (col == 5){
            return Colors.BLUE;
        }if (col == 6){
            return Colors.PURPLE;
        }
        return Colors.UNOCCUPIED;
    }

    private Color getOutColor(int col){
        if (col == 0){
            return Colors.BACKGROUND;
        }if (col == 1){
            return Colors.HOME;
        }if (col == 2){
            return Colors.UNOCCUPIED;
        }if (col == 3){
            return Colors.GREEN;
        }else{
            return Colors.BLUE;
        }
    }

    private void paintHex(int x, int y, GameData gd, Graphics2D g2){
        int[] cx = new int[]{x-x1, x, x+x1, x+x1, x, x-x1};
        int[] cy = new int[]{y-y1, y-y2, y-y1, y+y1, y+y2, y+y1};
        Polygon poly = new Polygon(cx,cy,6);
        g2.setStroke(new BasicStroke(thickness));
        if (gd.isBorder()){
            g2.setColor(Colors.GRID);
            g2.fillPolygon(poly);
            return;
        }
        if (gd.isOutskirts()){
            g2.setColor(getOutColor(gd.getOutColor()));
            g2.fillPolygon(poly);
            return;
        }
        int[] pieces = gd.getData();
        if (pieces.length == 0){
            g2.setColor(Colors.GRID);
            g2.drawPolygon(poly);
            return;
        }
        int i = 0;
        while(i < pieces.length){
            g2.setStroke(new BasicStroke(thickness + thickness));
            int type = pieces[i];
            i++;
            if (type == 9){
                int color1 = pieces[i];
                i++;
                Color c1 = getColor(color1);
                g2.setColor(c1);
                g2.drawLine(x-x1+thickness+thickness, y - y1, x+x1-thickness-thickness, y+y1);
                int color2 = pieces[i];
                i++;
                Color c2 = getColor(color2);
                g2.setColor(c2);
                g2.drawLine(x+x1-thickness-thickness, y - y1, x-x1+thickness+thickness, y+y1);
            }else if (type == 10){
                g2.setColor(Colors.HOME);
                g2.fillPolygon(poly);
            }else{
                int color = pieces[i];
                i++;
                Color c = getColor(color);
                g2.setColor(c);
                if (type == 0){
                    Ellipse2D.Double circle =
                            new Ellipse2D.Double(x - x1 + thickness, y - y2 + thickness +thickness,
                                    (x1 - thickness) *2, (y2-thickness-thickness)*2);
                    g2.draw(circle);
                }else if(type == 1){
                    g2.drawLine(x+x1-thickness-thickness, y + y1, x-x1+thickness+thickness, y + y1);

                }else if(type == 2){
                    g2.drawLine(x+x1-thickness-thickness, y, x-x1+thickness+thickness, y);

                }else if(type == 3){
                    g2.drawLine(x+x1-thickness-thickness, y + y1, x-x1+thickness+thickness, y+y1);
                    g2.drawLine(x+x1-thickness-thickness, y, x-x1+thickness+thickness, y);

                }else if(type == 4){
                    g2.drawLine(x+x1-thickness-thickness, y - y1, x-x1+thickness+thickness, y-y1);

                }else if(type == 5){
                    g2.drawLine(x+x1-thickness-thickness, y + y1, x-x1+thickness+thickness, y+y1);
                    g2.drawLine(x+x1-thickness-thickness, y - y1, x-x1+thickness+thickness, y-y1);

                }else if(type == 6){
                    g2.drawLine(x, y-y2+thickness+thickness, x-x1+thickness, y+y1-thickness);
                    g2.drawLine(x, y-y2+thickness+thickness, x+x1-thickness, y+y1-thickness);

                }else if(type == 7){
                    g2.drawLine(x, y-y2+thickness+thickness, x-x1+thickness, y+y1-thickness);
                    g2.drawLine(x, y-y2+thickness+thickness, x+x1-thickness, y+y1-thickness);
                    g2.drawLine(x, y+y2-thickness-thickness, x-x1+thickness, y-y1+thickness);
                    g2.drawLine(x, y+y2-thickness-thickness, x+x1-thickness, y-y1+thickness);

                }else if(type == 8){
                    g2.drawLine(x, y-y2+thickness+thickness, x, y+y2-thickness-thickness);
                }
            }
        }
        g2.setStroke(new BasicStroke(thickness));
        g2.setColor(Colors.GRID);
        g2.drawPolygon(poly);
    }

    private Coords getClosestHex(int x,int y){
        Coords closest = new Coords(0,0,0);
        double distance = Double.MAX_VALUE;
        for (Coords c : map.keySet()){
            Tile tile = map.get(c);
            int xt = tile.getX();
            int yt = tile.getY();
            int dx = x- xt;
            int dy = y-yt;
            double testD = Math.sqrt((dx * dx) +(dy *dy));
            if (testD < distance){
                distance = testD;
                closest = c;
            }
        }
        return closest;
    }

    private void sendClickInfo(Coords c){
        //?? maybe do something different
        sendHoverInfo(c);
    }

    void setHoverArea(JTextArea hoverArea){
        this.hoverArea = hoverArea;
    }

    private void sendHoverInfo(Coords c){
        //get pieces from engine, send info about each
        if (hoverArea != null){
            String s = engine.hoverTileInfo(c);
            if (!s.equals("")){
                hoverArea.setText(s);
            }
        }
    }

    class MyMouseListener extends MouseAdapter {	//inner class inside GUI.DrawingPanel
        private Point origin;
        private Coords infoC;
        public void mouseClicked(MouseEvent e) {
            //interact with tile
            int x = e.getX();
            int y = e.getY();
            Coords c = getClosestHex(x, y);
            Tile tile = map.get(c);
            GameData gd = tile.getGd();
            if (gd.isBorder() || gd.isOutskirts()){
                engine.clearActive();
                return;
            }
            boolean leftClick = true;
            if (SwingUtilities.isRightMouseButton(e)){
                leftClick = false;
            }
            HashMap<Coords, GameData> update = engine.click(c, leftClick);

            for (Coords cn : update.keySet()){
                Tile ct = map.get(cn);
                map.put(cn, new Tile(ct.getX(), ct.getY(), update.get(cn)));
            }
            sendClickInfo(c);
            revalidate();
            repaint();
        }
        public void mousePressed(MouseEvent e) {
            //set start location to move the view from
            origin = new Point(e.getPoint());
        }

        public void mouseReleased(MouseEvent e) {
            origin = null;
        }

        public void mouseMoved(MouseEvent e){
            //highlight tile mouse is in, give info about contents of the tile
                int x = e.getX();
                int y = e.getY();
                Coords c = getClosestHex(x, y);
            if (infoC == null  || infoC != c){
                Tile tile = map.get(c);
                GameData gd = tile.getGd();
                if (!gd.isBorder() && !gd.isOutskirts()){
                    infoC = c;
                    sendHoverInfo(infoC);
                }
            }
//            int x = e.getX();
//            int y = e.getY();
//            Coords c = getClosestHex(x, y);
//            Tile tile = map.get(c);
//            GameData gd = tile.getGd();
//            if (gd.isBorder()){
//                return;
//            }
//            if (gd.getData().length != 0){
//                return;
//            }
//            map.put(c, new Tile(tile.getX(), tile.getY(), new GameData(6)));
//            revalidate();
//            repaint();
        }
        public void mouseDragged(MouseEvent e) {
            //move the view around the engine
            if (SwingUtilities.isLeftMouseButton(e) && origin != null) {
                JViewport viewPort =
                        (JViewport) SwingUtilities.getAncestorOfClass(JViewport.class, DrawingPanel.this);
                if (viewPort != null) {
                    int deltaX = origin.x - e.getX();
                    int deltaY = origin.y - e.getY();

                    Rectangle view = viewPort.getViewRect();
                    view.x += deltaX;
                    view.y += deltaY;

                    DrawingPanel.this.scrollRectToVisible(view);
                }
            }if (SwingUtilities.isRightMouseButton(e) && origin != null){
                //rotate camera with right drag
                int deltaX = origin.x - e.getX();
                //dull sensitivity to limit accidents
                if (Math.abs(deltaX) > 50){
                    if (deltaX > 0){
                        rotateCamera(-1);
                    }else{
                        rotateCamera(1);
                    }
                    origin = new Point(e.getPoint());
                    revalidate();
                    repaint();
                }
            }
        }
        public void mouseWheelMoved(MouseWheelEvent e) {
            //zoom in and out
            int notches = e.getWheelRotation();
            double oldR = radius;
            if (notches > 0){
                radius -= radius/5;
            }else{
                radius += radius/5;
            }
            if (radius < 6){
                radius = 5;
            }
            if (radius > 100){
                radius = 100;
            }
            int w = 0;
            int h = 0;
            int cx = 0;
            int cy = 0;
            JViewport viewPort =
                    (JViewport) SwingUtilities.getAncestorOfClass(JViewport.class, DrawingPanel.this);
            if (viewPort != null) {
                Rectangle view = viewPort.getViewRect();
                w = view.width;
                h = view.height;
                cx = view.x + w/2;
                cy = view.y + h/2;
            }
            setXY();
            setPoints();
            repaint();
            double factor = radius/oldR;
            int freshX = (int)(cx * factor) - w/2;
            int freshY = (int)(cy * factor) - h/2;

            JScrollPane scroll =
                    (JScrollPane) SwingUtilities.getAncestorOfClass(JScrollPane.class, DrawingPanel.this);
            if (scroll != null){
                //this gets scroll to update max on scrollbars for some reason
                //even though revalidate does not
                scroll.setViewportView(DrawingPanel.this);
            }
            Rectangle rec = new Rectangle(freshX, freshY, w, h);
            DrawingPanel.this.scrollRectToVisible(rec);
        }
    } //end of MyMouseListener class
}
