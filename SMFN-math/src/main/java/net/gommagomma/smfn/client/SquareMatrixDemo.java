package net.gommagomma.smfn.client;

import net.gommagomma.smfn.math.algebra.numerics.Complex;
import net.gommagomma.smfn.math.algebra.numerics.Rational;
import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.algebra.numerics.SignedInt;
import net.gommagomma.smfn.math.algebra.structures.ComplexField;
import net.gommagomma.smfn.math.algebra.structures.IntegerRing;
import net.gommagomma.smfn.math.algebra.structures.RationalField;
import net.gommagomma.smfn.math.algebra.structures.RealField;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrix;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrixField;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrixRing;

public class SquareMatrixDemo {
    public static void main(String[] args) {
        System.out.println("=== DEMO MATRICI QUADRATE (ALGEBRA) ===");

        // 1. Uso di SquareMatrixRing con Interi (Determinante di Laplace)
        IntegerRing Z = IntegerRing.INSTANCE;
        SquareMatrixRing<SignedInt, IntegerRing> intRing = new SquareMatrixRing<>(Z, 3);
        SignedInt[] dataInt = { 
            Z.of(1), Z.of(0), Z.of(2),
            Z.of(-1), Z.of(3), Z.of(1),
            Z.of(0), Z.of(2), Z.of(1) 
        };
        SquareMatrix<SignedInt> m1 = intRing.of(dataInt);
        
        System.out.println("Matrice Quadrata Intera:\n" + m1);
        System.out.println("Traccia: " + m1.trace());
        System.out.println("Determinante (Laplace): " + intRing.determinant(m1));

        System.out.println("\n----------------------------\n");

        
        // 2. Uso di SquareMatrixField con Reali (Inversa e Gauss)
        RealField rf = RealField.INSTANCE;
        SquareMatrixField<Real, RealField> m2 = new SquareMatrixField<>(rf, 2);
        Real[] dataInv = { 
            rf.of(4), rf.of(7),
            rf.of(2), rf.of(6)
        };
        SquareMatrix<Real> A = m2.of(dataInv);
        System.out.println("Matrice Quadrata reale A:\n" + A);
        
        SquareMatrix<Real> Ainv = m2.inverse(A);
        System.out.println("Inversa A^-1:\n" + Ainv);
        
        // Verifica: A * A^-1 = I
        SquareMatrix<Real> identity = m2.multiply(A, Ainv);
        System.out.println("Verifica (A * A^-1 = I):\n" + identity);
        
        System.out.println("\n----------------------------\n");


        // 3. Uso di SquareMatrixField con Rational (Inversa e Gauss)
        RationalField Q = RationalField.INSTANCE;
        SquareMatrixField<Rational, RationalField> qField = new SquareMatrixField<>(Q, 2);
        Rational[] dataQ = {
        		Q.of(1, 2), Q.of(1, 3),
        		Q.of(1, 1), Q.of(1, 4)
        };
        SquareMatrix<Rational> mq = qField.of(dataQ);
        System.out.println("Matrice Quadrata razionale Q:\n" + mq);

        // Calcolo Determinante: (1/2 * 1/4) - (1/3 * 1) = 1/8 - 1/3 = (3-8)/24 = -5/24
        System.out.println("Determinante esatto: " + qField.determinant(mq));

        // Calcolo Inversa
        SquareMatrix<Rational> mqInv = qField.inverse(mq);
        System.out.println("Inversa Q^-1:\n" + mqInv);

        // Verifica esatta: A * A^-1 deve essere esattamente [1, 0 / 0, 1]
        SquareMatrix<Rational> qIdentity = qField.multiply(mq, mqInv);
        System.out.println("Verifica esatta (I):\n" + qIdentity);
        
        System.out.println("\n----------------------------\n");


        // 4. Uso di SquareMatrixField con Complex (Inversa e Gauss)
        ComplexField C = ComplexField.INSTANCE;
        SquareMatrixField<Complex, ComplexField> cField = new SquareMatrixField<>(C, 2);
        Complex[] dataA = {
        		C.of(0, 1), C.of(1, 0),
        		C.of(1, 0), C.of(0, 1)
        };
        SquareMatrix<Complex> B = cField.of(dataA);
        System.out.println("Matrice Quadrata complessa B:\n" + B);

        // Determinante: (i * i) - (1 * 1) = -1 - 1 = -2
        System.out.println("Determinante complesso: " + cField.determinant(B));

        // Inversa
        SquareMatrix<Complex> Binv = cField.inverse(B);
        System.out.println("Inversa B^-1:\n" + Binv);

        // Verifica: B * B^-1 = I
        System.out.println("Verifica Identità:\n" + cField.multiply(B, Binv));
    }
}
