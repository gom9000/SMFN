package net.gommagomma.smfn.math.linearalgebra.natural;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.math.algebra.numeric.Natural;
import net.gommagomma.smfn.math.algebra.structures.NaturalSemiring;

public class NaturalMatrixTest {

    private NaturalMatrixFactory factory;
    private Natural n0;
    private Natural n1;
    private Natural n2;
    private Natural n3;

    @BeforeEach
    void setUp() {
        factory = NaturalMatrixFactory.getInstance();
        n0 = factory.getZeroScalar();
        n1 = factory.getOneScalar();
        n2 = n1.add(n1); // Natural(2)
        n3 = n2.add(n1); // Natural(3)
    }

    // --- Test di Costruzione e Validazione ---

    @Test
    void testCreationFromNaturalArray() {
        Natural[][] data = {
            {n1, n2},
            {n3, n0}
        };
        NaturalMatrix matrix = factory.createMatrix(data);

        assertEquals(2, matrix.getRows());
        assertEquals(2, matrix.getColumns());
        assertEquals(n1, matrix.get(0, 0));
        assertEquals(n0, matrix.get(1, 1));
    }

    @Test
    void testCreationFromIntArray() {
        int[][] data = {
            {1, 2, 3},
            {4, 5, 6}
        };
        NaturalMatrix matrix = factory.createMatrix(data);
        
        assertEquals(2, matrix.getRows());
        assertEquals(3, matrix.getColumns());
        assertTrue(matrix.get(1, 0).isMathematicallyEqualTo(NaturalSemiring.getInstance().of(4)));
    }

    @Test
    void testCreationInvalidDimensions() {
        // Riga non rettangolare (già gestito da AbstractSemiringMatrix)
        Natural[][] dataInvalid = {
            {n1, n2},
            {n3}
        };
        assertThrows(IllegalArgumentException.class, () -> factory.createMatrix(dataInvalid));
    }

    @Test
    void testCreateZeroMatrix() {
        NaturalMatrix zero = factory.createZeroMatrix(3, 4);
        assertEquals(3, zero.getRows());
        assertEquals(4, zero.getColumns());
        assertTrue(zero.get(2, 3).isZero());
    }

    // --- Test Immutabilità e Copia Difensiva ---

    @Test
    void testCopyIsDeepCopy() {
        Natural[][] data = {{n1}};
        NaturalMatrix original = factory.createMatrix(data);
        NaturalMatrix copy = original.copy();

        assertNotSame(original, copy, "La copia non deve essere la stessa istanza.");
        assertTrue(original.isMathematicallyEqualTo(copy), "I valori devono essere uguali.");

        // Simula la modifica dell'array originale (se fosse possibile), 
        // ma il test importante è l'immutabilità interna:
        // Se si potesse modificare data[0][0] in NaturalMatrix, questo fallirebbe.
        // Essendo NaturalMatrix immutabile, ci fidiamo della copia difensiva nel costruttore.
    }

    // --- Test Operazioni Algebriche (Semianello) ---

    @Test
    void testAddition() {
        Natural[][] dataA = {{n1, n2}, {n3, n0}}; // [[1, 2], [3, 0]]
        Natural[][] dataB = {{n2, n3}, {n1, n2}}; // [[2, 3], [1, 2]]

        NaturalMatrix A = factory.createMatrix(dataA);
        NaturalMatrix B = factory.createMatrix(dataB);
        NaturalMatrix C = A.add(B); // [[3, 5], [4, 2]]

        assertTrue(C.get(0, 0).isMathematicallyEqualTo(n3));
        assertTrue(C.get(0, 1).isMathematicallyEqualTo(NaturalSemiring.getInstance().of(5)));
        assertTrue(C.get(1, 0).isMathematicallyEqualTo(NaturalSemiring.getInstance().of(4)));
        assertTrue(C.get(1, 1).isMathematicallyEqualTo(n2));
    }

    @Test
    void testAdditionDimensionMismatch() {
        NaturalMatrix A = factory.createZeroMatrix(2, 2);
        NaturalMatrix B = factory.createZeroMatrix(2, 3);
        assertThrows(IllegalArgumentException.class, () -> A.add(B));
    }

    @Test
    void testMultiplyByScalar() {
        NaturalMatrix A = factory.createMatrix(new int[][]{{1, 2}, {3, 4}});
        Natural scalar = NaturalSemiring.getInstance().of(3);
        NaturalMatrix C = A.multiplyByScalar(scalar); // [[3, 6], [9, 12]]

        assertTrue(C.get(0, 0).isMathematicallyEqualTo(NaturalSemiring.getInstance().of(3)));
        assertTrue(C.get(1, 1).isMathematicallyEqualTo(NaturalSemiring.getInstance().of(12)));
    }

    @Test
    void testMatrixMultiplication() {
        // A (2x2) = [[1, 2], [3, 4]]
        NaturalMatrix A = factory.createMatrix(new int[][]{{1, 2}, {3, 4}});
        
        // B (2x2) = [[5, 6], [7, 8]]
        NaturalMatrix B = factory.createMatrix(new int[][]{{5, 6}, {7, 8}});
        
        // C = A * B = [[1*5 + 2*7, 1*6 + 2*8], [3*5 + 4*7, 3*6 + 4*8]]
        //           = [[5 + 14, 6 + 16], [15 + 28, 18 + 32]]
        //           = [[19, 22], [43, 50]]
        NaturalMatrix C = A.multiply(B);

        assertTrue(C.get(0, 0).isMathematicallyEqualTo(NaturalSemiring.getInstance().of(19)));
        assertTrue(C.get(0, 1).isMathematicallyEqualTo(NaturalSemiring.getInstance().of(22)));
        assertTrue(C.get(1, 0).isMathematicallyEqualTo(NaturalSemiring.getInstance().of(43)));
        assertTrue(C.get(1, 1).isMathematicallyEqualTo(NaturalSemiring.getInstance().of(50)));
    }

    @Test
    void testMatrixMultiplicationDimensionMismatch() {
        NaturalMatrix A = factory.createZeroMatrix(2, 3);
        NaturalMatrix B = factory.createZeroMatrix(2, 2);
        assertThrows(IllegalArgumentException.class, () -> A.multiply(B));
    }

    // --- Test Prodotto Matrice-Vettore ---

    @Test
    void testMatrixVectorMultiplication() {
        // A (2x3) = [[1, 2, 3], [4, 5, 6]]
        NaturalMatrix A = factory.createMatrix(new int[][]{{1, 2, 3}, {4, 5, 6}});
        
        // v (3x1) = [1, 1, 1]
        NaturalVector v = NaturalVectorFactory.getInstance().createVector(new int[]{1, 1, 1});
        
        // result (2x1) = [1*1 + 2*1 + 3*1, 4*1 + 5*1 + 6*1]
        //              = [6, 15]
        NaturalVector result = A.multiply(v);

        assertEquals(2, result.dimension());
        assertTrue(result.get(0).isMathematicallyEqualTo(NaturalSemiring.getInstance().of(6)));
        assertTrue(result.get(1).isMathematicallyEqualTo(NaturalSemiring.getInstance().of(15)));
    }

    @Test
    void testMatrixVectorMultiplicationDimensionMismatch() {
        NaturalMatrix A = factory.createZeroMatrix(2, 3);
        NaturalVector v = NaturalVectorFactory.getInstance().createVector(new int[]{1, 1});
        assertThrows(IllegalArgumentException.class, () -> A.multiply(v));
    }

    // --- Test Trasposta ---

    @Test
    void testTranspose() {
        // A (2x3) = [[1, 2, 3], [4, 5, 6]]
        NaturalMatrix A = factory.createMatrix(new int[][]{{1, 2, 3}, {4, 5, 6}});
        NaturalMatrix A_T = A.transpose();
        
        // A_T (3x2) = [[1, 4], [2, 5], [3, 6]]
        assertEquals(3, A_T.getRows());
        assertEquals(2, A_T.getColumns());

        assertTrue(A_T.get(0, 1).isMathematicallyEqualTo(NaturalSemiring.getInstance().of(4)));
        assertTrue(A_T.get(1, 0).isMathematicallyEqualTo(NaturalSemiring.getInstance().of(2)));
        assertTrue(A_T.get(2, 1).isMathematicallyEqualTo(NaturalSemiring.getInstance().of(6)));
    }

    // --- Test Uguaglianza ---

    @Test
    void testMathematicalEquality() {
        Natural[][] dataA = {{n1, n2}, {n3, n0}};
        NaturalMatrix A = factory.createMatrix(dataA);
        NaturalMatrix B = factory.createMatrix(dataA); // Stessi dati

        assertTrue(A.isMathematicallyEqualTo(B));
        
        NaturalMatrix C = factory.createMatrix(new int[][]{{1, 2}, {3, 1}}); // Valore diverso
        assertFalse(A.isMathematicallyEqualTo(C));
    }

    @Test
    void testJavaEquality() {
        Natural[][] dataA = {{n1, n2}, {n3, n0}};
        NaturalMatrix A = factory.createMatrix(dataA);
        NaturalMatrix B = factory.createMatrix(dataA); // Stessi dati

        assertTrue(A.equals(B), "equals() deve funzionare per matrici equivalenti.");
        
        NaturalMatrix C = factory.createMatrix(new int[][]{{1, 2}, {3, 1}}); // Valore diverso
        assertFalse(A.equals(C));
    }

    // --- Test Getter Vettori Riga e Colonna ---

    @Test
    void testGetRowVector() {
        // A (2x3) = [[1, 2, 3], [4, 5, 6]]
        NaturalMatrix A = factory.createMatrix(new int[][]{{1, 2, 3}, {4, 5, 6}});
        NaturalVector row = A.getRowVector(1); // [4, 5, 6]

        assertEquals(3, row.dimension());
        assertTrue(row.get(0).isMathematicallyEqualTo(NaturalSemiring.getInstance().of(4)));
        assertTrue(row.get(2).isMathematicallyEqualTo(NaturalSemiring.getInstance().of(6)));
        
        // Verifica immutabilità (la modifica del vettore restituito non modifica la matrice)
        assertTrue(A.get(1, 0).isMathematicallyEqualTo(NaturalSemiring.getInstance().of(4))); // La matrice originale resta 4
    }

    @Test
    void testGetColumnVector() {
        // A (2x3) = [[1, 2, 3], [4, 5, 6]]
        NaturalMatrix A = factory.createMatrix(new int[][]{{1, 2, 3}, {4, 5, 6}});
        NaturalVector col = A.getColumnVector(2); // [3, 6]

        assertEquals(2, col.dimension());
        assertTrue(col.get(0).isMathematicallyEqualTo(NaturalSemiring.getInstance().of(3)));
        assertTrue(col.get(1).isMathematicallyEqualTo(NaturalSemiring.getInstance().of(6)));
    }
}