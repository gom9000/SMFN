package net.gommagomma.smfn.math.linearalgebra.signedint;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.math.algebra.numeric.SignedInt;
import net.gommagomma.smfn.math.algebra.numeric.SignedIntFactory;

public class SignedIntMatrixTest {

    private SignedIntMatrixFactory factory;
    private SignedInt si_neg2;
    private SignedInt si_0;
    private SignedInt si_1;
    private SignedInt si_3;

    @BeforeEach
    void setUp() {
        factory = SignedIntMatrixFactory.getInstance();
        si_0 = factory.getScalarFactory().zero();
        si_1 = factory.getScalarFactory().one();
        si_3 = SignedIntFactory.getInstance().of(3);
        si_neg2 = SignedIntFactory.getInstance().of(-2);
    }

    // --- Test di Costruzione e Validazione ---

    @Test
    void testCreationFromSignedIntArray() {
        SignedInt[][] data = {
            {si_1, si_neg2},
            {si_3, si_0}
        };
        SignedIntMatrix matrix = factory.createMatrix(data);

        assertEquals(2, matrix.getRows());
        assertEquals(2, matrix.getColumns());
        assertTrue(matrix.get(0, 1).isMathematicallyEqualTo(SignedIntFactory.getInstance().of(-2)));
    }

    @Test
    void testCreationFromIntArray() {
        int[][] data = {
            {1, -5},
            {10, 0}
        };
        SignedIntMatrix matrix = factory.createMatrix(data);
        
        assertEquals(2, matrix.getRows());
        assertEquals(2, matrix.getColumns());
        assertTrue(matrix.get(0, 1).isMathematicallyEqualTo(SignedIntFactory.getInstance().of(-5)));
    }
    
    // Assumendo la Factory modificata per validare l'input double come intero
    @Test
    void testCreationFromDoubleArray_ValidIntegers() {
        double[][] data = {
            {1.0, -2.0},
            {0.0, 5.0}
        };
        SignedIntMatrix matrix = factory.createMatrix(data);
        assertTrue(matrix.get(1, 1).isMathematicallyEqualTo(SignedIntFactory.getInstance().of(5)));
    }
   


    // --- Test Operazioni di Gruppo (Ring-specific) ---

    @Test
    void testNegate() {
        // A = [[1, -2], [3, 0]]
        SignedIntMatrix A = factory.createMatrix(new int[][]{{1, -2}, {3, 0}});
        // Negate(A) = [[-1, 2], [-3, 0]]
        SignedIntMatrix negatedA = A.negate();

        assertTrue(negatedA.get(0, 0).isMathematicallyEqualTo(SignedIntFactory.getInstance().of(-1)));
        assertTrue(negatedA.get(0, 1).isMathematicallyEqualTo(SignedIntFactory.getInstance().of(2)));
        assertTrue(negatedA.get(1, 0).isMathematicallyEqualTo(SignedIntFactory.getInstance().of(-3)));
        assertTrue(negatedA.get(1, 1).isMathematicallyEqualTo(SignedIntFactory.getInstance().of(0)));
    }
    
    @Test
    void testSubtract() {
        // A = [[5, 2], [1, 0]]
        SignedIntMatrix A = factory.createMatrix(new int[][]{{5, 2}, {1, 0}});
        // B = [[1, -3], [2, 1]]
        SignedIntMatrix B = factory.createMatrix(new int[][]{{1, -3}, {2, 1}});
        
        // C = A - B = [[5-1, 2-(-3)], [1-2, 0-1]] = [[4, 5], [-1, -1]]
        SignedIntMatrix C = A.subtract(B);

        assertTrue(C.get(0, 0).isMathematicallyEqualTo(SignedIntFactory.getInstance().of(4)));
        assertTrue(C.get(0, 1).isMathematicallyEqualTo(SignedIntFactory.getInstance().of(5)));
        assertTrue(C.get(1, 0).isMathematicallyEqualTo(SignedIntFactory.getInstance().of(-1)));
        assertTrue(C.get(1, 1).isMathematicallyEqualTo(SignedIntFactory.getInstance().of(-1)));
    }


    // --- Test Operazioni Algebriche (Semiring ereditato) ---

    @Test
    void testMatrixMultiplication_WithNegativeNumbers() {
        // A (2x2) = [[1, -1], [2, 0]]
        SignedIntMatrix A = factory.createMatrix(new int[][]{{1, -1}, {2, 0}});
        
        // B (2x2) = [[3, 4], [-2, 5]]
        SignedIntMatrix B = factory.createMatrix(new int[][]{{3, 4}, {-2, 5}});
        
        // C = A * B
        // C[0, 0] = 1*3 + (-1)*(-2) = 3 + 2 = 5
        // C[0, 1] = 1*4 + (-1)*5    = 4 - 5 = -1
        // C[1, 0] = 2*3 + 0*(-2)    = 6 + 0 = 6
        // C[1, 1] = 2*4 + 0*5       = 8 + 0 = 8
        //           = [[5, -1], [6, 8]]
        SignedIntMatrix C = A.multiply(B);

        assertTrue(C.get(0, 0).isMathematicallyEqualTo(SignedIntFactory.getInstance().of(5)));
        assertTrue(C.get(0, 1).isMathematicallyEqualTo(SignedIntFactory.getInstance().of(-1)));
        assertTrue(C.get(1, 0).isMathematicallyEqualTo(SignedIntFactory.getInstance().of(6)));
        assertTrue(C.get(1, 1).isMathematicallyEqualTo(SignedIntFactory.getInstance().of(8)));
    }

    @Test
    void testMatrixVectorMultiplication_WithNegativeNumbers() {
        // A (2x2) = [[1, -2], [3, 4]]
        SignedIntMatrix A = factory.createMatrix(new int[][]{{1, -2}, {3, 4}});
        
        // v (2x1) = [5, -1]
        SignedIntVector v = SignedIntVectorFactory.getInstance().createVector(new int[]{5, -1});
        
        // result (2x1) = [1*5 + (-2)*(-1), 3*5 + 4*(-1)]
        //              = [5 + 2, 15 - 4]
        //              = [7, 11]
        SignedIntVector result = A.multiply(v);

        assertEquals(2, result.dimension());
        assertTrue(result.get(0).isMathematicallyEqualTo(SignedIntFactory.getInstance().of(7)));
        assertTrue(result.get(1).isMathematicallyEqualTo(SignedIntFactory.getInstance().of(11)));
    }
}