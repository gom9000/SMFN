package net.gommagomma.smfn.client;

import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.algebra.structures.RealField;
import net.gommagomma.smfn.math.linearalgebra.matrices.Matrix;
import net.gommagomma.smfn.math.linearalgebra.matrices.MatrixSemimodule;
import net.gommagomma.smfn.math.linearalgebra.matrices.MatrixSpace;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrix;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrixAlgebra;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrixRing;

public class MatrixDemo {

    static RealField R = RealField.INSTANCE;

    public static void main(String[] args) {

        System.out.println("--- Costruzione e operazioni di base (matrici rettangolari) ---");
        MatrixSemimodule<Real, RealField> M23 = new MatrixSemimodule<>(R, 2, 3);
        Matrix<Real> A = M23.of(re(1, 2, 0, 0, 1, -1));
        Matrix<Real> B = M23.of(re(0, 1, 1, 2, 0, 3));
        System.out.println("A =\n" + A);
        System.out.println("B =\n" + B);

        Matrix<Real> sum = M23.add(A, B);
        System.out.println("A + B =\n" + sum);

        Matrix<Real> scaled = M23.scale(new Real(2.0), A);
        System.out.println("2*A =\n" + scaled);

        Matrix<Real> aTranspose = M23.transpose(A);
        System.out.println("A^T (3x2) =\n" + aTranspose);

        System.out.println("--- Rango (matrice rango-deficiente) ---");
        MatrixSpace<Real, RealField> space23 = new MatrixSpace<>(R, 2, 3);
        Matrix<Real> deficient = M23.of(re(1, 2, 3, 2, 4, 6)); // riga 2 = 2 * riga 1
        System.out.println("Rango di A =                " + space23.rank(A));
        System.out.println("Rango di [1,2,3; 2,4,6] =   " + space23.rank(deficient) + " (atteso: 1)");

        System.out.println("\n--- Matrici quadrate: prodotto, traccia, determinante ---");
        SquareMatrixRing<Real, RealField> M2 = new SquareMatrixRing<>(R, 2);
        SquareMatrix<Real> S1 = M2.of(re(1, 2, 3, 4));
        SquareMatrix<Real> S2 = M2.of(re(0, 1, 1, 0));
        System.out.println("S1 =\n" + S1);
        System.out.println("S2 =\n" + S2);

        SquareMatrix<Real> product = M2.multiply(S1, S2);
        System.out.println("S1 * S2 =\n" + product);
        System.out.println("Traccia(S1) = " + S1.trace() + " (atteso: 5)");
        System.out.println("Det(S1) [Laplace] = " + M2.determinant(S1) + " (atteso: -2)");

        System.out.println("\n--- Determinante 4x4: Laplace vs Gauss devono coincidere ---");
        // Matrice a blocchi: [[0,1],[1,0]] (det=-1) e diag(2,3) (det=6) -> det totale = -6
        Real[] data4 = re(
            0, 1, 0, 0,
            1, 0, 0, 0,
            0, 0, 2, 0,
            0, 0, 0, 3
        );
        SquareMatrixRing<Real, RealField> M4Ring = new SquareMatrixRing<>(R, 4);
        SquareMatrixAlgebra<Real, RealField> M4Algebra = new SquareMatrixAlgebra<>(R, 4);
        SquareMatrix<Real> S4 = M4Ring.of(data4);

        Real detLaplace = M4Ring.determinant(S4);      // sempre Laplace
        Real detGauss = M4Algebra.determinant(S4);     // Gauss per n>3
        System.out.println("Det(S4) [Laplace] = " + detLaplace);
        System.out.println("Det(S4) [Gauss]   = " + detGauss + " (atteso in entrambi i casi: -6)");
        System.out.println("Concordano: " + R.areEqual(detLaplace, detGauss));

        System.out.println("\n--- Inversa e InvertibleElements ---");
        SquareMatrixAlgebra<Real, RealField> M2Algebra = new SquareMatrixAlgebra<>(R, 2);
        System.out.println("S1 e' invertibile: " + M2Algebra.isInvertible(S1));
        SquareMatrix<Real> inverse = M2Algebra.inverse(S1);
        System.out.println("S1^-1 =\n" + inverse);
        SquareMatrix<Real> identityCheck = M2.multiply(S1, inverse);
        System.out.println("S1 * S1^-1 =\n" + identityCheck);
        System.out.println("E' l'identita': " + M2.isOne(identityCheck));

        SquareMatrix<Real> singular = M2.of(re(1, 2, 2, 4)); // righe proporzionali
        System.out.println("\n[1,2;2,4] e' invertibile: " + M2Algebra.isInvertible(singular) + " (atteso: false)");
        try {
            M2Algebra.inverse(singular);
            System.out.println("ERRORE: doveva lanciare eccezione");
        } catch (ArithmeticException e) {
            System.out.println("Correttamente rifiutata: " + e.getMessage());
        }
    }

    private static Real[] re(double... values) {
        Real[] result = new Real[values.length];
        for (int i = 0; i < values.length; i++) result[i] = new Real(values[i]);
        return result;
    }
}
