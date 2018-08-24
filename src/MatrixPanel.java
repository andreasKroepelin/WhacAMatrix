/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package whacamatrix;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.text.DecimalFormat;
import java.util.LinkedList;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

/**
 *
 * @author kroepelin
 */
public final class MatrixPanel extends JPanel {

    private LinkedList<Matrix> ms;
    private LinkedList<Matrix> rs;
    private Ellipse2D.Double[][] topOvals;
    private Cursor normal = null;
    private Cursor down = null;
    private static final DecimalFormat DF = new DecimalFormat("#.00#");
    private static final Color TEXT_COLOR = new Color(100, 100, 255);
    private static final Color TEXT_SHADOW = new Color(50, 50, 255);

    public MatrixPanel(Matrix m) throws Exception {
        this.ms = new LinkedList<>();
        this.rs = new LinkedList<>();
        beginWith(m);
        this.setDoubleBuffered(true);
        this.setBackground(new Color(0, 0, 50));
        this.setOpaque(true);

        initCursors();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                outer:
                for (int i = 0; i < topOvals.length; i++) {
                    for (int j = 0; j < topOvals[i].length; j++) {
                        if (topOvals[i][j].contains(e.getX(), e.getY())) {
                            whac(i, j);
                            repaint();
                            break outer;
                        }
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                setCursor(normal);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                setCursor(down);
            }

        });
    }

    private void initCursors() {
        try {
            Image normalImg = ImageIO.read(this.getClass().getResource("hammer0.png"));
            normal = Toolkit.getDefaultToolkit().createCustomCursor(normalImg, new Point(9, 35), "normal");
            Image downImg = ImageIO.read(this.getClass().getResource("hammer1.png"));
            down = Toolkit.getDefaultToolkit().createCustomCursor(downImg, new Point(5, 42), "normal");
            setCursor(normal);
        } catch (Exception e) {
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        //g.clearRect(0, 0, getWidth(), getHeight());
        Graphics2D g2 = (Graphics2D) g;
        g2.setFont(new Font("Tahoma", Font.BOLD, 15));
        g2.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        boolean diffB = WhacAMatrix.prefs.getBoolean("diff", false);

        Matrix m = ms.getLast();
        Matrix before = diffB ? (ms.size() > 1 ? ms.get(ms.size() - 2) : m) : null;
        Matrix diff = null;
        if (diffB) {
            try {
                diff = Matrix.diff(m, before);
            } catch (Exception e) {
                System.out.println("ehh");
                System.out.println(e.getMessage());
                return;
            }
        }

        double max = m.getAbsMax();
        double diffMax = diffB ? diff.getAbsMax() : 0.0;
        int spaceX = 10;
        int spaceY = 30;
        int ovalHeight = 8;
        int width = getWidth() / m.getColumnCount() - spaceX - spaceX / m.getColumnCount();
        int maxHeight = getHeight() / m.getRowCount() - spaceY - spaceY / m.getRowCount();

        for (int i = 0; i < m.getRowCount(); i++) {
            for (int j = 0; j < m.getColumnCount(); j++) {
                int height = (int) (Math.abs(m.get(i, j)) * maxHeight / max);
                int x = spaceX + (j + 0) * (width + spaceX);
                int y = (i + 1) * (maxHeight + spaceY);
                Color c = (i == j) ? Color.RED : Color.ORANGE;

                topOvals[i][j] = new Ellipse2D.Double(x, y - height - ovalHeight, width, 2 * ovalHeight);

                if (diffMax > 0.0) {
                    g2.setColor(new Color(100, 100, 255, (int) (Math.abs(diff.get(i, j)) * 255.0 / diffMax)));
                    g2.fillRoundRect(x, y - maxHeight - ovalHeight, width, maxHeight + 2 * ovalHeight, Math.min(width, maxHeight) / 10, Math.min(width, maxHeight) / 10);
                }

                g2.setColor(c);
                g2.fillOval(x, y - ovalHeight, width, 2 * ovalHeight);
                g2.fillOval(x, y - height - ovalHeight, width, 2 * ovalHeight);
                g2.setColor(Color.LIGHT_GRAY);
                g2.drawOval(x, y - ovalHeight, width, 2 * ovalHeight);
                g2.setColor(c);
                g2.fillRect(x, y - height, width, height);
                g2.setColor(Color.LIGHT_GRAY);
                g2.drawOval(x, y - height - ovalHeight, width, 2 * ovalHeight);
                g2.drawLine(x, y - height, x, y);
                g2.drawLine(x + width, y - height, x + width, y);

                g2.setColor(Color.WHITE);
                //g2.drawString(DF.format(m.get(i, j)), x + width / 2, y);
                //g2.setColor(TEXT_COLOR);
                String lab = DF.format(m.get(i, j));
                g2.drawString(lab, x + width / 2 - g2.getFontMetrics().stringWidth(lab) / 2, y - 2);

            }
        }
    }

    public void beginWith(Matrix m) throws Exception {
        ms.clear();
        rs.clear();
        m = Matrix.add(m, m.transpose()).scale(0.5);
        this.topOvals = new Ellipse2D.Double[m.getRowCount()][m.getColumnCount()];
        ms.add(m);
        rs.add(Matrix.eye(m.getRowCount()));
    }

    public Matrix getLast() {
        return ms.getLast();
    }

    public Matrix getLastR() {
        return rs.getLast();
    }

    public Matrix getLast(int i) {
        return ms.get(ms.size() - i - 1);
    }

    public void whac(int i, int j) {
        if (i != j) {
            try {
                Matrix[] jac = ms.getLast().applyGivens(i, j);
                ms.add(jac[1]);
                rs.add(Matrix.mult(jac[2], rs.getLast()));
            } catch (Exception exc) {
                System.out.println(exc.getMessage());
            }
        }
    }

    public void whacHighest() {
        int[] idx = ms.getLast().getAbsMaxIdxNonDiag();
        whac(idx[0], idx[1]);
    }

    public void whacHighest(int iter) {
        for (int i = 0; i < iter; i++) {
            whacHighest();
        }
    }

}
