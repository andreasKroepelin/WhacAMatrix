/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package whacamatrix;

import java.awt.Color;

/**
 *
 * @author kroepelin
 */
public class Point3D {

    private double x, y, z;

    public Point3D(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public static double[] normalVector(Point3D p1, Point3D p2, Point3D p3) {
        double[] u = new double[3];
        double[] v = new double[3];

        u[0] = p2.x - p1.x;
        u[1] = p2.y - p1.y;
        u[2] = p2.z - p1.z;

        v[0] = p3.x - p1.x;
        v[1] = p3.y - p1.y;
        v[2] = p3.z - p1.z;

        double[] c = new double[3];

        c[0] = u[1] * v[2] - u[2] * v[1];
        c[1] = u[2] * v[0] - u[0] * v[2];
        c[2] = u[0] * v[1] - u[1] * v[0];

        double norm = Math.sqrt(c[0] * c[0] + c[1] * c[1] + c[2] * c[2]);

        c[0] /= norm;
        c[1] /= norm;
        c[2] /= norm;

        return c;
    }

    public static int normalVectorColor(Point3D p1, Point3D p2, Point3D p3, double lightTheta, double lightPhi) {
        double[] c = normalVector(p1, p2, p3);

        if (c[0] < 0.0) {
            c[0] = -c[0];
            c[1] = -c[1];
            c[2] = -c[2];
        }

        double theta = Math.acos(c[2]);
        double phi = Math.atan2(c[1], c[0]);

        double b1 = Math.abs(theta - lightTheta) / Math.max(lightTheta, Math.PI - lightTheta);
        double b2 = Math.abs(phi - lightPhi) / Math.max(lightPhi + Math.PI, Math.PI - lightPhi);

        float b = (float) ((b1 + b2) / 2.0);

        b = Math.max(b, 0f);
        b = Math.min(b, 1f);
        return Color.HSBtoRGB(0.3f, 1f, b);
    }

    public static Point3D interpolate(Point3D p1, Point3D p2, Point3D p3, double r, double s) {
        double[] u = new double[3];
        double[] v = new double[3];

        u[0] = p2.x - p1.x;
        u[1] = p2.y - p1.y;
        u[2] = p2.z - p1.z;

        v[0] = p3.x - p1.x;
        v[1] = p3.y - p1.y;
        v[2] = p3.z - p1.z;

        double x = r * u[0] + s * v[0] + p1.x;
        double y = r * u[1] + s * v[1] + p1.y;
        double z = r * u[2] + s * v[2] + p1.z;

        return new Point3D(x, y, z);
    }

}
