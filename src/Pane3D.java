/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package whacamatrix;

import java.awt.Color;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.ToDoubleFunction;

/**
 *
 * @author kroepelin
 */
public class Pane3D {

    private static final ToDoubleFunction<Double> X_SHIFT = z -> 0.5 * z;
    private static final ToDoubleFunction<Double> Y_SHIFT = z -> -0.5 * z;

    private Point3D[] p;
    private GeneralPath projection;
    private Rectangle2D projBounds;

    public Pane3D(Point3D... p) {
        this.p = p;
        this.projection = null;
    }

    public Pane3D(ArrayList<Point3D> pList) {
        this.p = pList.toArray(new Point3D[0]);
    }

    public GeneralPath getProjection() {
        if (projection == null) {
            projection = new GeneralPath();

            projection.moveTo(p[0].getX() + X_SHIFT.applyAsDouble(p[0].getZ()), p[0].getY() + Y_SHIFT.applyAsDouble(p[0].getZ()));
            for (int i = 1; i <= p.length; i++) {
                projection.lineTo(p[i % p.length].getX() + X_SHIFT.applyAsDouble(p[i % p.length].getZ()), p[i % p.length].getY() + Y_SHIFT.applyAsDouble(p[i % p.length].getZ()));
            }
        }
        return projection;
    }

    public Rectangle2D getProjBounds() {
        if (projBounds == null) {
            projBounds = getProjection().getBounds2D();
        }
        return projBounds;
    }

    public double getZAt(double x, double y) {
        if (getProjection().contains(x, y)) {

            double[] d = new double[p.length];
            double[] w = new double[p.length];
            for (int i = 0; i < d.length; i++) {
                d[i] = Math.sqrt((p[i].getX() - x) * (p[i].getX() - x) + (p[i].getY() - y) * (p[i].getY() - y));
                //w[i] = Math.exp(-0.5 * d[i]);
                w[i] = 1.0;
            }

            double z = 0;
            double wSum = 0;
            for (int i = 0; i < d.length; i++) {
                z += w[i] * p[i].getZ();
                wSum += w[i];
            }
            z /= wSum;

            return z;

        } else {
            return Double.POSITIVE_INFINITY;
        }
    }

    public int getColorAt(double x, double y, double lightTheta, double lightPhi) {
        /*double r = 3;
        if ((p1.getX() - x) * (p1.getX() - x) + (p1.getY() - y) * (p1.getY() - y) < r * r
                || (p2.getX() - x) * (p2.getX() - x) + (p2.getY() - y) * (p2.getY() - y) < r * r
                || (p3.getX() - x) * (p3.getX() - x) + (p3.getY() - y) * (p3.getY() - y) < r * r
                || (p4.getX() - x) * (p4.getX() - x) + (p4.getY() - y) * (p4.getY() - y) < r * r) {
            return Color.BLUE.getRGB();
        } else {*/
        return normalVectorColor(lightTheta, lightPhi);
        //}
    }

    public double[] normalVector() {
        return Point3D.normalVector(p[0], p[1], p[2]);
    }

    public int normalVectorColor(double lightTheta, double lightPhi) {
        return Point3D.normalVectorColor(p[0], p[1], p[2], lightTheta, lightPhi);
    }

    public double getProjMaxX() {
        return getProjBounds().getMaxX();
    }

    public double getProjMaxY() {
        return getProjBounds().getMaxY();
    }

    public double getProjMinX() {
        return getProjBounds().getMinX();
    }

    public double getProjMinY() {
        return getProjBounds().getMinY();
    }

    /*public double getMinX() {
        return Math.min(Math.min(p1.getX(), p2.getX()), Math.min(p3.getX(), p4.getX()));
    }

    public double getMinY() {
        return Math.min(Math.min(p1.getY(), p2.getY()), Math.min(p3.getY(), p4.getY()));
    }

    public double getMinZ() {
        return Math.min(Math.min(p1.getZ(), p2.getZ()), Math.min(p3.getZ(), p4.getZ()));
    }

    public double getMaxX() {
        return Math.max(Math.max(p1.getX(), p2.getX()), Math.max(p3.getX(), p4.getX()));
    }

    public double getMaxY() {
        return Math.max(Math.max(p1.getY(), p2.getY()), Math.max(p3.getY(), p4.getY()));
    }

    public double getMaxZ() {
        return Math.max(Math.max(p1.getZ(), p2.getZ()), Math.max(p3.getZ(), p4.getZ()));
    }*/
}
