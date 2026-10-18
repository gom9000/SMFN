package net.gommagomma.smfn.demo.geometry;

import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.algebra.structures.RealField;
import net.gommagomma.smfn.math.geometry.Circle;
import net.gommagomma.smfn.math.geometry.Ellipse;
import net.gommagomma.smfn.math.geometry.Point;
import net.gommagomma.smfn.math.linearalgebra.vectors.InnerProductVectorSpace;
import net.gommagomma.smfn.math.linearalgebra.vectors.Vector;

public class GeometryDemo
{
    public static void main(String[] args) {
        System.out.println("--- Punti: distanza euclidea, traslazione ---");
        Point p1 = new Point(1.0, 0.0);
        Point p2 = new Point(0.0, 1.0);

        System.out.println("P1 = " + p1 + ", P2 = " + p2);
        System.out.println("Distanza P1-P2 = " + p1.distanceTo(p2) + " (atteso: sqrt(2) = 1.4142...)");

        Point p3 = p1.translate(new Real(2.0), new Real(3.0));
        System.out.println("P1 traslato di (2,3) = " + p3 + " (atteso: (3,3))");

        System.out.println("\n--- Cerchio: funzione implicita f(P) = dist(P,C) - r ---");
        Circle circle = new Circle(new Point(0.0, 0.0), new Real(2.5));
        Point onCircle = new Point(2.5, 0.0);
        Point insideCircle = new Point(1.0, 0.0);

        System.out.println("Cerchio: " + circle);
        System.out.println("f(2.5, 0) = " + circle.implicitFunctionAt(onCircle) + " (atteso: 0.0, sul bordo)");
        System.out.println("isOnEntity(2.5, 0) = " + circle.isOnEntity(onCircle) + " (atteso: true)");
        System.out.println("f(1, 0) = " + circle.implicitFunctionAt(insideCircle) + " (atteso: -1.5, dentro)");

        System.out.println("\n--- Ellisse: funzione implicita f(P) = (x/a)^2 + (y/b)^2 - 1 ---");
        Ellipse ellipse = new Ellipse(new Point(0.0, 0.0), new Real(3.0), new Real(1.5));
        Point onMajorAxis = new Point(3.0, 0.0);
        Point onMinorAxis = new Point(0.0, 1.5);

        System.out.println("Ellisse: " + ellipse);
        System.out.println("f(3, 0) [estremo asse maggiore] = " + ellipse.implicitFunctionAt(onMajorAxis) + " (atteso: 0.0)");
        System.out.println("f(0, 1.5) [estremo asse minore] = " + ellipse.implicitFunctionAt(onMinorAxis) + " (atteso: 0.0)");

        System.out.println("\n--- Angolo tra vettori, via prodotto interno (collegamento con linearalgebra) ---");
        // Un Point non ha un prodotto interno (e' uno spazio affine); i VETTORI si', tramite
        // InnerProductVectorSpace. cos(theta) = (v1.v2) / (||v1|| * ||v2||).
        RealField R = RealField.INSTANCE;
        InnerProductVectorSpace<Real, RealField> V2 = new InnerProductVectorSpace<>(R, 2);

        Vector<Real> v1 = V2.of(new Real[] { new Real(1.0), new Real(0.0) }); // asse X
        Vector<Real> v2 = V2.of(new Real[] { new Real(0.0), new Real(1.0) }); // asse Y
        Vector<Real> v3 = V2.of(new Real[] { new Real(1.0), new Real(1.0) }); // diagonale

        double angleV1V2 = angleBetween(V2, v1, v2);
        double angleV1V3 = angleBetween(V2, v1, v3);

        System.out.println("Angolo tra asse X e asse Y = " + Math.toDegrees(angleV1V2) + " gradi (atteso: 90)");
        System.out.println("Angolo tra asse X e diagonale (1,1) = " + Math.toDegrees(angleV1V3) + " gradi (atteso: 45)");
    }

    private static double angleBetween(InnerProductVectorSpace<Real, RealField> space, Vector<Real> a, Vector<Real> b) {
        double dot = space.innerProduct(a, b).getValue();
        double normProduct = space.norm(a).getValue() * space.norm(b).getValue();
        return Math.acos(dot / normProduct);
    }
}
