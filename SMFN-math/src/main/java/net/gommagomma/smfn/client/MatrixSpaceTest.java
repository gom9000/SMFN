package net.gommagomma.smfn.client;

import net.gommagomma.smfn.math.algebra.numerics.Rational;
import net.gommagomma.smfn.math.algebra.structures.RationalField;
import net.gommagomma.smfn.math.linearalgebra.matrices.Matrix;
import net.gommagomma.smfn.math.linearalgebra.matrices.MatrixSpace;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrixRing;

public class MatrixSpaceTest {

    public static void main(String[] args) {
        RationalField qField = RationalField.INSTANCE;
        int size = 3;
        MatrixSpace<Rational, RationalField> qSpace = new MatrixSpace<>(qField, size, size);
        SquareMatrixRing<Rational, RationalField> qRing = new SquareMatrixRing<>(qField, size);

        System.out.println("--- INIZIO TEST MATRIX SPACE ---");

        // 1. CREAZIONE MATRICE SINGOLARE (Linearmante dipendente)
        // R1: [1, 2, 3], R2: [4, 5, 6], R3: [5, 7, 9] (R1+R2)
        Matrix<Rational> mSingular = new Matrix<>(size, size, qField);
        fillMatrix(mSingular, new int[][]{{1,2,3}, {4,5,6}, {5,7,9}}, qField);

        System.out.println("Matrice Singolare (R3 = R1 + R2):");
        System.out.println(mSingular);

        Rational detSingular = qSpace.determinant(mSingular);
        System.out.println("Determinante (atteso 0): " + detSingular);

        Matrix<Rational> ref = qSpace.toRowEchelonForm(mSingular);
        System.out.println("Forma a gradini (REF) - l'ultima riga deve essere zero:");
        System.out.println(ref);

        // 2. CREAZIONE MATRICE INVERTIBILE
        // Modifichiamo l'ultimo elemento per rompere la dipendenza
        Matrix<Rational> mInvertible = mSingular.copy();
        mInvertible.set(2, 2, qField.of(10)); // Cambiamo 9 con 10

        System.out.println("\nMatrice Invertibile (cambiato m[2,2] in 10):");
        Rational detInvertible = qSpace.determinant(mInvertible);
        System.out.println("Nuovo Determinante (atteso != 0): " + detInvertible);

        // 3. TEST INVERSIONE (Gauss-Jordan)
        try {
            Matrix<Rational> inv = qSpace.invert(mInvertible);
            System.out.println("Inversa calcolata:");
            System.out.println(inv);

            // Verifica: A * A^-1 = I
            Matrix<Rational> identityCheck = qRing.multiply(mInvertible, inv);
            System.out.println("Verifica Identità (A * Inv):");
            System.out.println(identityCheck);
            
            if(qRing.isOne(identityCheck)) {
                System.out.println(">>> TEST INVERSIONE: SUCCESSO!");
            } else {
                System.err.println(">>> TEST INVERSIONE: FALLITO!");
            }
        } catch (Exception e) {
            System.err.println("Errore durante l'inversione: " + e.getMessage());
        }

        // 4. TEST DIVISIONE PER SCALARE
        Matrix<Rational> halved = qSpace.divide(mInvertible, qField.of(2));
        System.out.println("\nMatrice divisa per 2 (primo elemento atteso 1/2):");
        System.out.println("Elemento [0,0]: " + halved.get(0, 0));
    }

    /**
     * Utility per riempire rapidamente una matrice da un array int[][]
     */
    private static void fillMatrix(Matrix<Rational> m, int[][] data, RationalField q) {
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[i].length; j++) {
                m.set(i, j, q.of(data[i][j]));
            }
        }
    }
}