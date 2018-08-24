/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package whacamatrix;

import java.util.Arrays;

/**
 *
 * @author kroepelin
 */
public class Matrix {

    private double[][] e;
    private Circle[] gc;
    private double maxAbs;

    public Matrix(double[][] e) {
        this.e = e;
        this.gc = null;
        this.maxAbs = Double.NaN;
    }

    public int getRowCount() {
        return e.length;
    }

    public int getColumnCount() {
        return e[0].length;
    }

    public double get(int i, int j) {
        return e[i][j];
    }

    public double getAbsMax() {
        if (Double.isNaN(maxAbs)) {
            maxAbs = Arrays.stream(e).map(row -> Arrays.stream(row).map(t -> Math.abs(t)).max().getAsDouble()).mapToDouble(d -> d).max().getAsDouble();
        }
        return maxAbs;
    }

    public int[] getAbsMaxIdxNonDiag() {
        int iMax = 0, jMax = 0;
        double max = 0.0;
        for (int i = 0; i < e.length; i++) {
            for (int j = 0; j < e[i].length; j++) {
                if (i != j && Math.abs(e[i][j]) > max) {
                    max = Math.abs(e[i][j]);
                    iMax = i;
                    jMax = j;
                }
            }
        }

        return new int[]{iMax, jMax};
    }

    public Matrix transpose() {
        double[][] t = new double[getColumnCount()][getRowCount()];
        for (int i = 0; i < t.length; i++) {
            for (int j = 0; j < t[i].length; j++) {
                t[i][j] = e[j][i];
            }
        }
        return new Matrix(t);
    }

    public Matrix mirrorUpperHalf() {
        double[][] t = new double[getColumnCount()][getRowCount()];
        for (int i = 0; i < t.length; i++) {
            for (int j = 0; j < t[i].length; j++) {
                if (j <= i) {
                    t[i][j] = e[j][i];
                } else {
                    t[i][j] = e[i][j];
                }
            }
        }
        return new Matrix(t);
    }

    public Matrix mirrorLowerHalf() {
        double[][] t = new double[getColumnCount()][getRowCount()];
        for (int i = 0; i < t.length; i++) {
            for (int j = 0; j < t[i].length; j++) {
                if (j >= i) {
                    t[i][j] = e[j][i];
                } else {
                    t[i][j] = e[i][j];
                }
            }
        }
        return new Matrix(t);
    }

    public Matrix scale(double t) {
        double[][] s = new double[getRowCount()][getColumnCount()];

        for (int i = 0; i < s.length; i++) {
            for (int j = 0; j < s[0].length; j++) {
                s[i][j] = t * e[i][j];
            }
        }

        return new Matrix(s);
    }

    /**
     *
     * @param i1 the first row to include
     * @param j1 the first column to include
     * @param i2 the first row not to include
     * @param j2 the first column not to include
     * @return a (<code>i2</code> - <code>i1</code>) x (<code>j2</code> -
     * <code>j1</code>) matrix
     */
    public Matrix getSubMatrix(int i1, int j1, int i2, int j2) {
        double[][] s = new double[i2 - i1][j2 - j1];

        for (int i = i1; i < i2; i++) {
            for (int j = j1; j < j2; j++) {
                s[i - i1][j - j1] = e[i][j];
            }
        }

        return new Matrix(s);
    }

    public static Matrix add(Matrix m1, Matrix m2) throws Exception {
        if (m1.getRowCount() != m2.getRowCount() || m1.getColumnCount() != m2.getColumnCount()) {
            throw new Exception("Can\'t add " + m1.getRowCount() + " x " + m1.getColumnCount() + " matrix and " + m2.getRowCount() + " x " + m2.getColumnCount() + " matrix.");
        }
        double[][] s = new double[m1.getRowCount()][m1.getColumnCount()];

        for (int i = 0; i < s.length; i++) {
            for (int j = 0; j < s[0].length; j++) {
                s[i][j] = m1.e[i][j] + m2.e[i][j];
            }
        }

        return new Matrix(s);

    }

    public static Matrix diff(Matrix m1, Matrix m2) throws Exception {
        if (m1.getRowCount() != m2.getRowCount() || m1.getColumnCount() != m2.getColumnCount()) {
            throw new Exception("Can\'t add " + m1.getRowCount() + " x " + m1.getColumnCount() + " matrix and " + m2.getRowCount() + " x " + m2.getColumnCount() + " matrix.");
        }
        double[][] s = new double[m1.getRowCount()][m1.getColumnCount()];

        for (int i = 0; i < s.length; i++) {
            for (int j = 0; j < s[0].length; j++) {
                s[i][j] = m1.e[i][j] - m2.e[i][j];
            }
        }

        return new Matrix(s);

    }

    public static Matrix mult(Matrix m1, Matrix m2) throws Exception {
        if (m1.getColumnCount() != m2.getRowCount()) {
            throw new Exception("Can\'t multiply " + m1.getRowCount() + " x " + m1.getColumnCount() + " matrix with " + m2.getRowCount() + " x " + m2.getColumnCount() + " matrix.");
        }

        double[][] p = new double[m1.getRowCount()][m2.getColumnCount()];

        for (int i = 0; i < p.length; i++) {
            for (int j = 0; j < p[0].length; j++) {
                p[i][j] = 0.0;
                for (int k = 0; k < m1.getColumnCount(); k++) {
                    p[i][j] += m1.e[i][k] * m2.e[k][j];
                }
            }
        }

        return new Matrix(p);
    }

    public static Matrix mult(Matrix... ms) throws Exception {
        if (ms.length > 1) {
            Matrix p = Matrix.mult(ms[0], ms[1]);
            for (int i = 2; i < ms.length; i++) {
                p = mult(p, ms[i]);
            }
            return p;
        } else {
            return ms[0];
        }
    }

    public Matrix calcGivens(int p, int q) {
        double[][] g = eye(getRowCount()).e;

        double T = (e[q][q] - e[p][p]) / (2.0 * e[p][q]);
        double t = T != 0 ? 1.0 / (T + Math.signum(T) * Math.sqrt(1.0 + T * T)) : 1.0;
        double c = 1.0 / Math.sqrt(1.0 + t * t);
        double s = t * c;

        g[p][p] = c;
        g[q][q] = c;
        g[p][q] = s;
        g[q][p] = -s;

        return new Matrix(g);
    }

    public Matrix[] applyGivens(int p, int q) throws Exception {
        Matrix g = calcGivens(p, q);
        Matrix gt = g.transpose();

        Matrix a = mult(gt, this, g);
        a.e[p][q] = 0.0;

        return new Matrix[]{gt, a, g};
    }

    public Matrix[] doJacobi() throws Exception {
        double sne = Double.POSITIVE_INFINITY;
        Matrix a = this;
        Matrix st = eye(getRowCount());
        Matrix s = eye(getRowCount());

        while (sne > 0.01) {
            int p = 0, q = 0;
            double maxE = 0.0;
            double abs;
            sne = 0.0;
            for (int i = 0; i < a.e.length; i++) {
                for (int j = 0; j < i; j++) {
                    abs = Math.abs(a.e[i][j]);
                    sne += abs;
                    if (abs > maxE) {
                        maxE = abs;
                        p = i;
                        q = j;
                    }
                }
            }

            Matrix[] givensResult = a.applyGivens(p, q);
            st = mult(st, givensResult[0]);
            a = givensResult[1];
            s = mult(givensResult[2], s);
        }

        return new Matrix[]{st, a, s};
    }

    public Circle[] calcGerschgorinCircles() {
        Circle[] circles = new Circle[getRowCount()];

        for (int i = 0; i < getRowCount(); i++) {
            circles[i] = new Circle(e[i][i], 0, 0);
            for (int j = 0; j < getColumnCount(); j++) {
                if (i != j) {
                    circles[i].r += Math.abs(e[i][j]);
                }
            }

        }
        this.gc = circles;
        return circles;
    }

    public Circle[] getGerschgorinCircles() {
        if (gc == null) {
            return calcGerschgorinCircles();
        }
        return gc;
    }

    @Override
    public String toString() {
        return Arrays.deepToString(e);
    }

    /**
     *
     * @param s A string in the format 1, 2, 3; 4, 5, 6; 7, 8, 9 represents
     * matrix <br>
     * 1 2 3 <br>
     * 4 5 6 <br>
     * 7 8 9 <br>
     * @return matrix
     */
    public static Matrix fromString(String s) {
        String[] rows = s.split(";");
        double[][] e = new double[rows.length][];
        for (int i = 0; i < e.length; i++) {
            String[] es = rows[i].split(",");
            e[i] = new double[es.length];
            for (int j = 0; j < e[i].length; j++) {
                e[i][j] = Double.parseDouble(es[j].trim());
            }
        }

        return new Matrix(e);
    }

    public static Matrix eye(int n) {
        double[][] e = new double[n][n];
        for (int i = 0; i < e.length; i++) {
            for (int j = 0; j < e[i].length; j++) {
                if (i == j) {
                    e[i][j] = 1.0;
                } else {
                    e[i][j] = 0.0;
                }
            }
        }
        return new Matrix(e);
    }

    public static Matrix rot(double a, double n1, double n2, double n3) {
        double norm = Math.sqrt(n1 * n1 + n2 * n2 + n3 * n3);
        n1 /= norm;
        n2 /= norm;
        n3 /= norm;

        double c = Math.cos(a);
        double s = Math.sin(a);

        double[][] e = new double[3][3];

        e[0][0] = n1 * n1 * (1 - c) + c;
        e[0][1] = n1 * n2 * (1 - c) - n3 * s;
        e[0][2] = n1 * n3 * (1 - c) + n2 * s;

        e[1][0] = n2 * n1 * (1 - c) + n3 * s;
        e[1][1] = n2 * n2 * (1 - c) + c;
        e[1][2] = n2 * n3 * (1 - c) - n1 * s;

        e[2][0] = n3 * n1 * (1 - c) - n2 * s;
        e[2][1] = n3 * n2 * (1 - c) + n1 * s;
        e[2][2] = n3 * n3 * (1 - c) + c;

        return new Matrix(e);
    }

    public static Matrix random(int m, int n, double min, double max) {
        double[][] e = new double[m][n];
        for (int i = 0; i < e.length; i++) {
            for (int j = 0; j < e[i].length; j++) {
                e[i][j] = (max - min) * Math.random() + min;
            }
        }
        return new Matrix(e);
    }

    public static Matrix getVectorFromPoint(Point3D p, int n, int[] offset) {
        double[][] e = new double[n][1];
        for (int i = 0; i < e.length; i++) {
            e[i][0] = 0.0;
        }

        e[offset[0]][0] = p.getX();
        e[offset[1]][0] = p.getY();
        e[offset[2]][0] = p.getZ();

        return new Matrix(e);
    }
}
