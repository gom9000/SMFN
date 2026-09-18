package net.gommagomma.smfn.demo;

import net.gommagomma.smfn.math.algebra.numerics.Complex;
import net.gommagomma.smfn.math.algebra.structures.ComplexField;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrix;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrixRing;
import net.gommagomma.smfn.math.linearalgebra.vectors.Vector;
import net.gommagomma.smfn.math.linearalgebra.vectors.VectorSemimodule;

public class OperatorDemo {
    static ComplexField C = ComplexField.INSTANCE;

    public static void main(String[] args) {
        System.out.println("--- SquareMatrix come Operatore Lineare (Complex) ---");
        SquareMatrixRing<Complex, ComplexField> M2z = new SquareMatrixRing<>(C, 2);
        VectorSemimodule<Complex, ComplexField> V2 = new VectorSemimodule<>(C, 2);

        // Rotazione di 90 gradi nel piano: [[0,-1],[1,0]]
        SquareMatrix<Complex> rotation90 = M2z.of(new Complex[] {
            new Complex(0, 0), new Complex(-1, 0),
            new Complex(1, 0), new Complex(0, 0)
        });

        Vector<Complex> v = V2.of(new Complex[] { new Complex(1, 0), new Complex(0, 0) }); // (1,0)

        Vector<Complex> rotated = rotation90.apply(v);
        System.out.println("R90 applicata a (1,0) = " + rotated + " (atteso: (0,1))");

        // Composizione di operatori (ereditata da Mapping) coincide col prodotto tra matrici
        Vector<Complex> viaCompose  = rotation90.compose(rotation90).apply(v);
        Vector<Complex> viaProduct  = M2z.multiply(rotation90, rotation90).apply(v);
        System.out.println("R90 . R90 applicata a (1,0)        = " + viaCompose);
        System.out.println("(R90 * R90) applicata direttamente = " + viaProduct);
        System.out.println("Coincidono: " + viaCompose.equals(viaProduct));

        // power(n) (ereditato da Operator): 4 rotazioni di 90 tornano all'identita'
        Vector<Complex> afterFour = rotation90.power(4).apply(v);
        System.out.println("R90^4 applicata a (1,0) = " + afterFour + " (atteso: torna a (1,0))");
    }
}
