/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package whacamatrix;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.text.DecimalFormat;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 * @author kroepelin
 */
public final class MatrixDisplay extends JPanel {

    private JTextField[][] t;
    private Matrix m;

    private static final DecimalFormat DF = new DecimalFormat("#.00#");

    public MatrixDisplay(MatrixPanel mp) {
        this.m = mp.getLast();
        t = new JTextField[m.getRowCount()][m.getColumnCount()];

        setLayout(new GridLayout(m.getRowCount(), m.getColumnCount()));

        for (int i = 0; i < t.length; i++) {
            for (int j = 0; j < t[i].length; j++) {
                t[i][j] = new JTextField(DF.format(m.get(i, j)));
                t[i][j].setHorizontalAlignment(JTextField.CENTER);
                t[i][j].setCaretPosition(0);
                t[i][j].setEditable(false);
                t[i][j].setBackground(getColor(m.get(i, j)));

                add(t[i][j]);
            }
        }

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                updateTextfields(mp);
            }
        });
    }

    public MatrixDisplay(int k, int n) {
        t = new JTextField[k][n];

        setLayout(new GridLayout(k, n));

        for (int i = 0; i < t.length; i++) {
            for (int j = 0; j < t[i].length; j++) {
                t[i][j] = new JTextField();
                t[i][j].setHorizontalAlignment(JTextField.CENTER);
                t[i][j].setEditable(true);

                add(t[i][j]);
            }
        }
    }

    public Matrix getEnteredMatrix() {
        double[][] e = new double[t.length][t[0].length];

        for (int i = 0; i < e.length; i++) {
            for (int j = 0; j < e[i].length; j++) {
                String d = t[i][j].getText().trim();
                if (d.isEmpty()) {
                    e[i][j] = 0.0;
                } else {
                    e[i][j] = Double.parseDouble(d);
                }
            }
        }

        return new Matrix(e);
    }

    public void updateTextfields(MatrixPanel mp) {
        this.m = mp.getLast();
        if (t.length != m.getRowCount() || t[0].length != m.getColumnCount()) {
            removeAll();
            t = new JTextField[m.getRowCount()][m.getColumnCount()];
            setLayout(new GridLayout(m.getRowCount(), m.getColumnCount()));
            for (int i = 0; i < t.length; i++) {
                for (int j = 0; j < t[i].length; j++) {
                    t[i][j] = new JTextField();
                    t[i][j].setHorizontalAlignment(JTextField.CENTER);
                    t[i][j].setEditable(false);

                    add(t[i][j]);
                }
            }
        }
        for (int i = 0; i < t.length; i++) {
            for (int j = 0; j < t[i].length; j++) {
                t[i][j].setText(DF.format(m.get(i, j)));
                t[i][j].setCaretPosition(0);
                t[i][j].setEditable(false);
                t[i][j].setBackground(getColor(m.get(i, j)));
            }
        }

        repaint();
    }

    private Color getColor(double d) {
        double max = m.getAbsMax();
        int a = (int) (Math.abs(d) * 255.0 / max);
        a = Math.max(a, 0);
        a = Math.min(a, 255);
        Color c;
        if (d > 0) {
            c = new Color(255 - a, 255, 255 - a);
        } else {
            c = new Color(255, 255 - a, 255 - a);
        }

        return c;
    }

}
