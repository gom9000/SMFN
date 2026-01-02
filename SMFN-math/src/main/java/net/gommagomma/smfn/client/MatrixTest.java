package net.gommagomma.smfn.client;

import net.gommagomma.smfn.math.algebra.numerics.Rational;
import net.gommagomma.smfn.math.algebra.structures.RationalField;
import net.gommagomma.smfn.math.linearalgebra.matrices.Matrix;
import net.gommagomma.smfn.math.linearalgebra.matrices.MatrixSpace;

public class MatrixTest {
    public static void main(String[] args) {
        // 1. Setup Ambiente (Razionali)
        RationalField qField = RationalField.INSTANCE;
        MatrixSpace<Rational, RationalField> qSpace = new MatrixSpace<>(qField, 3, 3);

        // 2. Creazione Matrice
        Matrix<Rational> m = new Matrix<>(3, 3, qField);
        // Riempiamo la matrice (1,2,3...9) usando il factory
        int val = 1;
        for(int i=0; i<3; i++) {
            for(int j=0; j<3; j++) {
                m.set(i, j, qField.of(val++));
            }
        }

        System.out.println("Matrice Originale:\n" + m);

        // 3. Test Determinante
        Rational det = qSpace.determinant(m);
        System.out.println("Determinante calcolato: " + det); 
        // Deve stampare 0/1 (ovvero zero)

        // 4. Test Forma a Gradini
        Matrix<Rational> ref = qSpace.toRowEchelonForm(m);
        System.out.println("Forma a gradini (REF):\n" + ref);
        // L'ultima riga deve essere composta da soli zeri!
        
        // 5. Test con una matrice invertibile
        // Cambiamo un solo valore per renderla non singolare (es. l'ultimo elemento da 9 a 10)
        m.set(2, 2, qField.of(10));
        System.out.println("\nModifica: m[2,2] = 10 (Matrice ora invertibile)");
        System.out.println("Nuovo Determinante: " + qSpace.determinant(m));
        // Dovrebbe essere -3/1
    }
}
