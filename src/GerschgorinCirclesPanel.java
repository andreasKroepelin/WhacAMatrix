/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package whacamatrix;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import javax.swing.JPanel;

/**
 *
 * @author kroepelin
 */
public class GerschgorinCirclesPanel extends JPanel {

    private MatrixPanel mp;

    private static final Color B = new Color(0, 200, 200, 20);

    public GerschgorinCirclesPanel(MatrixPanel mp) {
        this.mp = mp;
        this.setBackground(new Color(0, 0, 50));
        this.setOpaque(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        int centerR = 3;
        boolean hist = WhacAMatrix.prefs.getBoolean("hist", false);
        int speed = WhacAMatrix.prefs.getInt("speed", 1);

        Circle[] circles = mp.getLast().getGerschgorinCircles();
        double min = Double.POSITIVE_INFINITY;
        double max = Double.NEGATIVE_INFINITY;
        double maxR = Double.NEGATIVE_INFINITY;
        double minR = Double.POSITIVE_INFINITY;
        for (int i = 0; i < circles.length; i++) {
            if (circles[i].x - circles[i].r < min) {
                min = circles[i].x - circles[i].r;
            }
            if (circles[i].x + circles[i].r > max) {
                max = circles[i].x + circles[i].r;
            }
            if (circles[i].r > maxR) {
                maxR = circles[i].r;
            }
            if (circles[i].r < minR) {
                minR = circles[i].r;
            }
        }
        //System.out.println(max + " " + min);

        g2.translate(getWidth() / 2, getHeight() / 2);
        //g2.scale(0.9 * getWidth() / (max - min), 1);

        g2.setStroke(new BasicStroke(3));

        double s11 = getWidth() / (2.0 * Math.abs(max));
        double s12 = getWidth() / (2.0 * Math.abs(min));
        double s1 = Math.min(s11, s12);
        double s2 = getHeight() / (2.0 * maxR);
        double s = 0.9 * Math.min(s1, s2);
        //System.out.println(s);

        // DRAW CIRCLES
        for (int i = 0; i < circles.length; i++) {
            //System.out.println(circles[i].x);
            double r = circles[i].r * s;
            double x = circles[i].x * s;
            double y = circles[i].y;

            Ellipse2D.Double c = new Ellipse2D.Double(x - r, y - r, 2 * r, 2 * r);
            //Ellipse2D.Double m = new Ellipse2D.Double(x - centerR, y - centerR, 2 * centerR, 2 * centerR);

            g2.setColor(Color.YELLOW);
            g2.draw(c);

        }

        if (hist) {
            g2.setColor(B);
            for (int i = 0; i < circles.length; i++) {
                double oldX;

                for (int up = 1; speed * up < getHeight() / 2; up++) {
                    try {
                        Circle oldC = mp.getLast(up).getGerschgorinCircles()[i];
                        oldX = oldC.x;

                        g2.drawLine((int) (s * (oldX - oldC.r)), -speed * up, (int) (s * (oldX + oldC.r)), -speed * up);

                    } catch (Exception e) {
                        //System.out.println(e.getMessage());
                        break;
                    }
                }

            }
            g2.setColor(Color.ORANGE);
            for (int i = 0; i < circles.length; i++) {
                double oldX;
                double old2X = circles[i].x;

                for (int up = 1; speed * up < getHeight() / 2; up++) {
                    try {
                        Circle oldC = mp.getLast(up).getGerschgorinCircles()[i];
                        oldX = oldC.x;

                        g2.drawLine((int) (s * old2X), -speed * up, (int) (s * oldX), -speed - 1 - speed * up);
                        old2X = oldX;
                    } catch (Exception e) {
                        //System.out.println(e.getMessage());
                        break;
                    }
                }
            }
        }

        // DRAW AXES
        g2.setColor(Color.WHITE);
        g2.drawLine(-getWidth() / 2, 0, getWidth() / 2, 0);
        g2.drawLine(0, -getHeight() / 2, 0, getHeight() / 2);

        double unit = 10000.0;
        while (unit * s > Math.min(getWidth(), getHeight())) {
            unit /= 10.0;
        }

        int intUnit = (int) (s * unit);
        for (int i = 0; intUnit * i < getWidth() / 2; i++) {
            g2.drawLine(i * intUnit, -10, i * intUnit, 10);
            g2.drawLine(-i * intUnit, -10, -i * intUnit, 10);
        }

        g2.setColor(Color.WHITE);
        g2.drawString("max. radius: " + maxR, 10 - getWidth() / 2, g2.getFontMetrics().getHeight() + 10 - getHeight() / 2);
        g2.drawString("min. radius: " + minR, 10 - getWidth() / 2, 2 * g2.getFontMetrics().getHeight() + 20 - getHeight() / 2);
        g2.drawString("unit: " + unit, 10 - getWidth() / 2, 3 * g2.getFontMetrics().getHeight() + 30 - getHeight() / 2);
        g2.setFont(new Font("Tahoma", Font.BOLD, 60));

        /*Shape outline = g2.getFont().createGlyphVector(g2.getFontRenderContext(), "Gerschgorin").getOutline();
        AffineTransform at = new AffineTransform();
        at.translate(getWidth() / 2 - outline.getBounds2D().getWidth(), -getHeight() / 2 + outline.getBounds2D().getHeight());
        outline = at.createTransformedShape(outline);
        g2.draw(outline);*/
    }

}
