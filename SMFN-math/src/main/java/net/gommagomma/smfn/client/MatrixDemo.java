package net.gommagomma.smfn.client;

import net.gommagomma.smfn.math.algebra.numerics.Complex;
import net.gommagomma.smfn.math.algebra.numerics.Natural;
import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.algebra.structures.ComplexField;
import net.gommagomma.smfn.math.algebra.structures.NaturalSemiring;
import net.gommagomma.smfn.math.algebra.structures.RealField;
import net.gommagomma.smfn.math.linearalgebra.matrices.Matrix;
import net.gommagomma.smfn.math.linearalgebra.matrices.MatrixSemimodule;
import net.gommagomma.smfn.math.linearalgebra.matrices.MatrixSpace;

public class MatrixDemo {
	public static void main(String[] args) {
        System.out.println("=== DEMO MATRICI RETTANGOLARI ===");

        // 1. Matrici su Naturali (Semiring)
        NaturalSemiring N = NaturalSemiring.INSTANCE;
        MatrixSemimodule<Natural, NaturalSemiring> semimodule = new MatrixSemimodule<>(N, 2, 3);
        
        Natural[] dataNat = { N.of(1), N.of(2), N.of(3), 
                              N.of(4), N.of(5), N.of(6) };
        Matrix<Natural> m1 = semimodule.of(dataNat);
        System.out.println("Matrice Naturali 2x3:\n" + m1);
        
        System.out.println("\n----------------------------\n");

        
        // 2. Matrici su Reali (Space) - Esempio Forma a Gradini
        RealField R = RealField.INSTANCE;
        MatrixSpace<Real, RealField> realSpace = new MatrixSpace<>(R, 3, 3);
        Real[] dataReal = { R.of(1), R.of(2), R.of(1),
                            R.of(0), R.of(1), R.of(1),
                            R.of(2), R.of(0), R.of(1) };
        Matrix<Real> m2 = realSpace.of(dataReal);
        
        System.out.println("Matrice Reale originale:\n" + m2);
        Matrix<Real> ref = realSpace.toRowEchelonForm(m2);
        System.out.println("Forma a gradini (REF):\n" + ref);
        System.out.println("Rango della matrice: " + realSpace.rank(m2));
        
        System.out.println("\n----------------------------\n");

        
        // 3. Matrici su Complessi (Space)
        ComplexField C = ComplexField.INSTANCE;
        MatrixSpace<Complex, ComplexField> cSpace = new MatrixSpace<>(C, 2, 3);
        Complex[] data1 = {
            C.of(1, 1), C.zero(), C.of(2, -1),
            C.zero(),   C.of(0, 1), C.of(3, 0)
        };
        Matrix<Complex> m3 = cSpace.of(data1);
        System.out.println("Matrice Complessa originale:\n" + m3);

        // Scaliamo per lo scalare (0 + 2i)
        Complex scalar = C.of(0, 2); 
        Matrix<Complex> mScaled = cSpace.scale(scalar, m3);

        System.out.println("Matrice Complessa Scalata per 2i:\n" + mScaled);
    }
}
