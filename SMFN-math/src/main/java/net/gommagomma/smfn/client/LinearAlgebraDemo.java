package net.gommagomma.smfn.client;

import net.gommagomma.smfn.math.algebra.numerics.Rational;
import net.gommagomma.smfn.math.algebra.structures.RationalField;
import net.gommagomma.smfn.math.linearalgebra.matrices.Matrix;
import net.gommagomma.smfn.math.linearalgebra.matrices.MatrixSpace;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrixRing;

public class LinearAlgebraDemo {

    public static void main(String[] args) {
        RationalField q = RationalField.INSTANCE;
        int n = 3;
        MatrixSpace<Rational, RationalField> space = new MatrixSpace<>(q, n, n);
        SquareMatrixRing<Rational, RationalField> ring = new SquareMatrixRing<>(q, n);

        System.out.println("=== TEST INTEGRATO ALGEBRA LINEARE ===\n");

        // 1. Definiamo il sistema Ax = b
        // A = [[2, 1, -1], [-3, -1, 2], [-2, 1, 2]]
        Matrix<Rational> A = new Matrix<>(n, n, q);
        fillMatrix(A, new int[][]{
            {2, 1, -1},
            {-3, -1, 2},
            {-2, 1, 2}
        }, q);

        // b = [[8], [-11], [-3]]
        Matrix<Rational> b = new Matrix<>(n, 1, q);
        fillMatrix(b, new int[][]{{8}, {-11}, {-3}}, q);

        System.out.println("Matrice A:\n" + A);
        System.out.println("Vettore b:\n" + b);

        // 2. Calcolo Determinante
        Rational det = space.determinant(A);
        System.out.println("Determinante di A: " + det + " (Atteso: -1)");

        // 3. Risoluzione del sistema Ax = b
        try {
            System.out.println("\n--- Risoluzione Ax = b ---");
            Matrix<Rational> x = space.solve(A, b);
            System.out.println("Soluzione x:\n" + x);
            // Atteso: x=2, y=3, z=-1 (Verifica: 2(2)+3-(-1) = 8 OK)
            
            // Verifica moltiplicando A * x
            Matrix<Rational> bCheck = space.multiply(A, x);
            System.out.println("Verifica (A * x): " + (bCheck.equals(b) ? "CORRETTA" : "ERRATA"));
            System.out.println(bCheck);

        } catch (Exception e) {
            System.err.println("Errore in solve: " + e.getMessage());
        }

        // 4. Inversione e verifica
        try {
            System.out.println("\n--- Inversione di A ---");
            Matrix<Rational> invA = space.invert(A);
            System.out.println("Inversa A^-1:\n" + invA);

            Matrix<Rational> identity = ring.multiply(A, invA);
            System.out.println("Verifica (A * A^-1 = I): " + (ring.isOne(identity) ? "OK" : "FALLITA"));
            System.out.println(identity);

        } catch (Exception e) {
            System.err.println("Errore in invert: " + e.getMessage());
        }

        // 5. Test Rango (Matrice Singolare)
        System.out.println("\n--- Test Rango ---");
        Matrix<Rational> singolare = new Matrix<>(2, 2, q);
        fillMatrix(singolare, new int[][]{{1, 2}, {2, 4}}, q);
        System.out.println("Matrice 2x2 singolare:\n" + singolare);
        System.out.println("Rango calcolato: " + space.rank(singolare) + " (Atteso: 1)");
    }

    private static void fillMatrix(Matrix<Rational> m, int[][] data, RationalField q) {
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[i].length; j++) {
                m.set(i, j, q.of(data[i][j]));
            }
        }
    }
}