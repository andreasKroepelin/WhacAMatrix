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
import java.awt.Shape;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.PathIterator;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.swing.JPanel;

/**
 *
 * @author kroepelin
 */
public final class OrthogonalPanel extends JPanel {

    private Matrix r;
    private double alpha = 0;
    private double lightTheta, lightPhi;
    private int[] offset;
    private int clickcount;
    private int w = 20;
    private int h = 20;
    private int tm = 10;
    private int lm = 10;
    private MatrixPanel mp;
    private ArrayList<ArrayList<Point3D>> letters;
    private ArrayList<ArrayList<Point3D>> lettersB;
    private String text;

    public OrthogonalPanel(MatrixPanel mp) {
        this.mp = mp;
        this.text = "";
        this.offset = new int[]{0, 1, 2};
        clickcount = 0;
        setBackground(new Color(0, 0, 50));

        /*addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                double x = e.getX();
                double y = e.getY();

                lightTheta = x * Math.PI / getWidth();
                lightPhi = 2.0 * y * Math.PI / getHeight() - Math.PI;

                repaint();
            }

        });*/
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
                if (x >= lm && x <= lm + w) {
                    outer:
                    for (int i = 0; i < r.getColumnCount() - 2 + clickcount; i++) {
                        if (y >= tm + i * h && y <= tm + (i + 1) * h) {
                            for (int j = 0; j < clickcount; j++) {
                                if (offset[j] >= i) {
                                    break outer;
                                }
                            }
                            offset[clickcount] = i;
                            clickcount++;
                            if (clickcount >= 3) {
                                clickcount = 0;
                            }
                            repaint();
                            break;
                        }
                    }
                }
            }

        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        r = mp.getLastR();
        //r = Matrix.rot(alpha, 0, 1, 1);
        String t = WhacAMatrix.prefs.get("orth", "JACOBI");

        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        if (clickcount == 0) {
            if (letters == null || letters.isEmpty() || text.isEmpty() || !text.equals(t)) {
                text = t;
                g2.setFont(new Font("Arial", Font.BOLD, 300));

                Shape s = g2.getFont().createGlyphVector(g2.getFontRenderContext(), text).getOutline();
                PathIterator pi = s.getPathIterator(null);

                letters = new ArrayList<>();
                lettersB = new ArrayList<>();
                ArrayList<Point3D> letter = new ArrayList<>();
                ArrayList<Point3D> letterB = new ArrayList<>();
                double x = 0, y = 0;

                while (!pi.isDone()) {
                    double[] seg = new double[6];
                    int type = pi.currentSegment(seg);

                    if (type == PathIterator.SEG_LINETO) {
                        x = seg[0];
                        y = seg[1];

                    } else if (type == PathIterator.SEG_QUADTO) {
                        x = seg[2];
                        y = seg[3];

                    } else if (type == PathIterator.SEG_CUBICTO) {
                        x = seg[4];
                        y = seg[5];

                    } else if (type == PathIterator.SEG_MOVETO) {
                        x = seg[0];
                        y = seg[1];

                    }

                    if (type == PathIterator.SEG_CLOSE) {
                        letters.add(letter);
                        lettersB.add(letterB);
                        letter = new ArrayList<>();
                        letterB = new ArrayList<>();

                    } else {
                        letter.add(new Point3D(x, y, 0));
                        letterB.add(new Point3D(x, y, 50));
                    }

                    pi.next();
                }
            }

            ArrayList<ArrayList<Point3D>> lettersT = new ArrayList<>();
            ArrayList<ArrayList<Point3D>> lettersBT = new ArrayList<>();

            try {
                for (ArrayList<Point3D> l : letters) {
                    ArrayList<Point3D> lT = new ArrayList<>();
                    for (int i = 0; i < l.size(); i++) {
                        lT.add(multWithMatrix(l.get(i)));
                    }
                    lettersT.add(lT);
                }
                for (ArrayList<Point3D> lB : lettersB) {
                    ArrayList<Point3D> lBT = new ArrayList<>();
                    for (int i = 0; i < lB.size(); i++) {
                        lBT.add(multWithMatrix(lB.get(i)));
                    }
                    lettersBT.add(lBT);
                }
            } catch (Exception e) {
                System.out.println(e.getClass() + " " + e.getMessage());
                for (StackTraceElement ste : e.getStackTrace()) {
                    System.out.println(ste);
                }
            }

            ArrayList<Pane3D> pieces = new ArrayList<>();

            for (int i = 0; i < lettersT.size(); i++) {
                ArrayList<Point3D> l = lettersT.get(i);
                ArrayList<Point3D> lB = lettersBT.get(i);

                // RAND
                for (int j = 0; j < l.size() - 1; j++) {

                    Point3D lP = l.get(j);
                    Point3D lBP = lB.get(j);
                    Point3D lPN = l.get(j + 1);
                    Point3D lBPN = lB.get(j + 1);

                    pieces.add(new Pane3D(lP, lBP, lBPN, lPN));
                }

                Point3D lP = l.get(l.size() - 1);
                Point3D lBP = lB.get(lB.size() - 1);
                Point3D lPN = l.get(0);
                Point3D lBPN = lB.get(0);

                pieces.add(new Pane3D(lP, lBP, lBPN, lPN));

                // FRONT, BACK
                //pieces.add(new Pane3D(l));
                //pieces.add(new Pane3D(lB));
            }

            double minX = pieces.stream().mapToDouble(p -> p.getProjMinX()).min().getAsDouble();
            double minY = pieces.stream().mapToDouble(p -> p.getProjMinY()).min().getAsDouble();
            double maxX = pieces.stream().mapToDouble(p -> p.getProjMaxX()).max().getAsDouble();
            double maxY = pieces.stream().mapToDouble(p -> p.getProjMaxY()).max().getAsDouble();

            int midX = (int) ((maxX + minX) / 2.0);
            int midY = (int) ((maxY + minY) / 2.0);

            boolean edges = WhacAMatrix.prefs.getBoolean("edges", true);
            if (!edges) {

                BufferedImage img = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);

                double x, y;

                for (int i = 0; i < img.getWidth() && i + minX < maxX; i++) {
                    for (int j = 0; j < img.getHeight() && j + minY < maxY; j++) {
                        x = i + minX;
                        y = j + minY;

                        double minZ = Double.POSITIVE_INFINITY;
                        double z;
                        int rgb = 0;
                        boolean eins = false;
                        boolean zwei = false;

                        for (Pane3D pane : pieces) {
                            if (x < pane.getProjMinX() || x > pane.getProjMaxX() || y < pane.getProjMinY() || y > pane.getProjMaxY()) {
                                continue;
                            }
                            z = pane.getZAt(x, y);
                            if (z < minZ) {
                                //System.out.println(z + " " + pane.getMinZ() + " " + pane.getMaxZ());
                                if (eins == true) {
                                    //System.out.print("#");
                                    zwei = true;
                                } else {
                                    eins = true;
                                }
                                minZ = z;
                                rgb = pane.getColorAt(x, y, lightTheta, lightPhi);
                            }
                        }
                        if (zwei) {
                            //System.out.println();
                        }

                        img.setRGB(i, j, rgb);
                    }
                }

                g2.drawImage(img, getWidth() / 2 - (int) (maxX - minX) / 2, getHeight() / 2 - (int) (maxY - minY) / 2, this);

            } else {
                g2.translate(getWidth() / 2 - midX, getHeight() / 2 - midY);
                g2.setColor(Color.GREEN);
                for (Pane3D p : pieces) {
                    g2.draw(p.getProjection());
                }
                g2.translate(-(getWidth() / 2 - midX), -(getHeight() / 2 - midY));
            }
        }

        int cc = clickcount;
        if (cc == 0) {
            cc = 3;
        }
        for (int i = 0; i < r.getRowCount(); i++) {
            g2.setColor(Color.ORANGE);
            for (int j = 0; j < cc; j++) {
                if (i == offset[j]) {
                    if (clickcount == 0) {
                        g2.setColor(Color.RED);
                    } else {
                        g2.setColor(Color.PINK.darker());
                    }
                    break;
                }
            }

            g2.fillRect(lm, tm + i * h, w, h);
            g2.setColor(Color.WHITE);
            g2.drawRect(lm, tm + i * h, w, h);
        }

    }

    private Point3D multWithMatrix(Point3D p) throws Exception {
        Matrix v = Matrix.getVectorFromPoint(p, r.getColumnCount(), offset);
        v = Matrix.mult(r, v);

        return new Point3D(v.get(offset[0], 0), v.get(offset[1], 0), v.get(offset[2], 0));
    }

    public void reset() {
        letters = new ArrayList<>();
        lettersB = new ArrayList<>();
        offset[0] = 0;
        offset[1] = 1;
        offset[2] = 2;
    }

}
