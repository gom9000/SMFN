package net.gommagomma.smfn.math.linearalgebra.real;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.math.algebra.numeric.Real;
import net.gommagomma.smfn.math.utils.MathConstants;

public class RealMatrixTest {

    private RealMatrixFactory factory;
    private static final double EPSILON = MathConstants.EPSILON;

    @BeforeEach
    void setUp() {
        factory = RealMatrixFactory.getInstance();
    }

    // ========================================================================
    // TEST FACTORY METHODS
    // ========================================================================

    @Test
    void testCreateMatrixFromDoubleArray() {
        double[][] data = {
            {1.0, 2.0},
            {3.0, 4.0}
        };
        RealMatrix matrix = factory.createMatrix(data);

        assertEquals(2, matrix.getRows());
        assertEquals(2, matrix.getColumns());
        assertTrue(matrix.get(0, 0).isMathematicallyEqualTo(new Real(1.0)));
        assertTrue(matrix.get(1, 1).isMathematicallyEqualTo(new Real(4.0)));
    }

    @Test
    void testCreateZeroMatrix() {
        int rows = 3;
        int cols = 4;
        RealMatrix zeroMatrix = factory.createZeroMatrix(rows, cols);
        Real zero = factory.getZeroScalar();

        assertEquals(rows, zeroMatrix.getRows());
        assertEquals(cols, zeroMatrix.getColumns());
        
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                assertTrue(zeroMatrix.get(i, j).isMathematicallyEqualTo(zero));
            }
        }
    }

    @Test
    void testFactoryWithJaggedArrayThrowsException() {
        double[][] jaggedData = {
            {1.0, 2.0},
            {3.0}
        };
        assertThrows(IllegalArgumentException.class, () -> {
            factory.createMatrix(jaggedData);
        });
    }

    // ========================================================================
    // TEST ALGEBRAIC OPERATIONS
    // ========================================================================

    @Test
    void testMatrixAddition() {
        double[][] dataA = {{1.0, 2.0}, {3.0, 4.0}};
        double[][] dataB = {{5.0, 6.0}, {7.0, 8.0}};
        RealMatrix a = factory.createMatrix(dataA);
        RealMatrix b = factory.createMatrix(dataB);

        RealMatrix c = a.add(b);
        RealMatrix expected = factory.createMatrix(new double[][]{{6.0, 8.0}, {10.0, 12.0}});

        assertTrue(c.isMathematicallyEqualTo(expected));
    }

    @Test
    void testMatrixSubtraction() {
        double[][] dataA = {{5.0, 5.0}, {10.0, 10.0}};
        double[][] dataB = {{1.0, 2.0}, {3.0, 4.0}};
        RealMatrix a = factory.createMatrix(dataA);
        RealMatrix b = factory.createMatrix(dataB);

        // Subtraction is inherited via default method in GroupElement
        RealMatrix c = a.subtract(b); 
        RealMatrix expected = factory.createMatrix(new double[][]{{4.0, 3.0}, {7.0, 6.0}});

        assertTrue(c.isMathematicallyEqualTo(expected));
    }
    
    @Test
    void testMatrixNegation() {
        double[][] data = {{1.0, -2.0}, {3.0, 4.0}};
        RealMatrix a = factory.createMatrix(data);
        
        RealMatrix negated = a.negate();
        RealMatrix expected = factory.createMatrix(new double[][]{{-1.0, 2.0}, {-3.0, -4.0}});

        assertTrue(negated.isMathematicallyEqualTo(expected));
    }


    @Test
    void testMultiplyByScalar() {
        double[][] data = {{1.0, 2.0}, {3.0, 4.0}};
        RealMatrix a = factory.createMatrix(data);
        Real scalar = new Real(2.5);

        RealMatrix c = a.multiplyByScalar(scalar);
        RealMatrix expected = factory.createMatrix(new double[][]{{2.5, 5.0}, {7.5, 10.0}});

        assertTrue(c.isMathematicallyEqualTo(expected));
    }

    @Test
    void testMatrixMultiplication() {
        // A (2x3) * B (3x2) = C (2x2)
        double[][] dataA = {{1.0, 2.0, 3.0}, {4.0, 5.0, 6.0}};
        double[][] dataB = {{7.0, 8.0}, {9.0, 10.0}, {11.0, 12.0}};
        RealMatrix a = factory.createMatrix(dataA);
        RealMatrix b = factory.createMatrix(dataB);

        RealMatrix c = a.multiply(b);
        // C[0][0] = 1*7 + 2*9 + 3*11 = 7 + 18 + 33 = 58
        // C[0][1] = 1*8 + 2*10 + 3*12 = 8 + 20 + 36 = 64
        // C[1][0] = 4*7 + 5*9 + 6*11 = 28 + 45 + 66 = 139
        // C[1][1] = 4*8 + 5*10 + 6*12 = 32 + 50 + 72 = 154
        RealMatrix expected = factory.createMatrix(new double[][]{{58.0, 64.0}, {139.0, 154.0}});

        assertTrue(c.isMathematicallyEqualTo(expected));
    }

    @Test
    void testMatrixVectorMultiplication() {
        // M (2x2) * V (2x1) = V' (2x1)
        double[][] dataM = {{1.0, 2.0}, {3.0, 4.0}};
        double[] dataV = {5.0, 6.0};
        RealMatrix m = factory.createMatrix(dataM);
        RealVector v = factory.getVectorFactory().createVector(dataV);

        RealVector result = m.multiply(v);
        // R[0] = 1*5 + 2*6 = 17
        // R[1] = 3*5 + 4*6 = 39
        RealVector expected = factory.getVectorFactory().createVector(new double[]{17.0, 39.0});

        assertTrue(result.isMathematicallyEqualTo(expected));
    }


    // ========================================================================
    // TEST FIELD MATRIX PROPERTIES
    // ========================================================================

    @Test
    void testTranspose() {
        double[][] data = {{1.0, 2.0, 3.0}, {4.0, 5.0, 6.0}};
        RealMatrix a = factory.createMatrix(data);

        RealMatrix transposed = a.transpose();
        RealMatrix expected = factory.createMatrix(new double[][]{
            {1.0, 4.0}, 
            {2.0, 5.0}, 
            {3.0, 6.0}
        });

        assertEquals(3, transposed.getRows());
        assertEquals(2, transposed.getColumns());
        assertTrue(transposed.isMathematicallyEqualTo(expected));
    }

    @Test
    void testDeterminant2x2() {
        // det = (ad - bc) = 4*5 - 2*9 = 20 - 18 = 2
        double[][] data = {{4.0, 2.0}, {9.0, 5.0}}; 
        RealMatrix a = factory.createMatrix(data);

        Real det = a.determinant();
        Real expected = new Real(2.0);

        assertTrue(det.isMathematicallyEqualTo(expected));
    }
    
    @Test
    void testDeterminant3x3() {
        // 3x3 with det = 0
        double[][] data = {
            {1.0, 2.0, 3.0},
            {4.0, 5.0, 6.0},
            {7.0, 8.0, 9.0}
        }; 
        RealMatrix a = factory.createMatrix(data);
        Real det = a.determinant();
        
        assertTrue(det.isMathematicallyEqualTo(new Real(0.0)));
    }


    @Test
    void testInverse2x2() {
        // A = [[4, 2], [9, 5]], det=2
        // A^-1 = 1/2 * [[5, -2], [-9, 4]] = [[2.5, -1.0], [-4.5, 2.0]]
        double[][] data = {{4.0, 2.0}, {9.0, 5.0}}; 
        RealMatrix a = factory.createMatrix(data);
        RealMatrix inverse = a.inverse();

        RealMatrix expected = factory.createMatrix(new double[][]{
            {2.5, -1.0}, 
            {-4.5, 2.0}
        });

        assertTrue(inverse.isMathematicallyEqualTo(expected));

        // Test A * A^-1 = I (Identità)
        RealMatrix identity = a.multiply(inverse);
        RealMatrix expectedIdentity = factory.createMatrix(new double[][]{
            {1.0, 0.0}, 
            {0.0, 1.0}
        });

        assertTrue(identity.isMathematicallyEqualTo(expectedIdentity));
    }

    @Test
    void testInverseOfSingularMatrixThrowsException() {
        // Singular matrix (det=0)
        double[][] data = {{1.0, 2.0}, {2.0, 4.0}}; 
        RealMatrix a = factory.createMatrix(data);

        assertThrows(ArithmeticException.class, a::inverse);
    }
    
    // ========================================================================
    // TEST ACCESSORS AND UTILITIES
    // ========================================================================
    
    @Test
    void testGetRowAndColumnVectors() {
        double[][] data = {{1.0, 2.0}, {3.0, 4.0}};
        RealMatrix m = factory.createMatrix(data);
        
        RealVector row0 = m.getRowVector(0);
        RealVector col1 = m.getColumnVector(1);
        
        RealVector expectedRow0 = factory.getVectorFactory().createVector(new double[]{1.0, 2.0});
        RealVector expectedCol1 = factory.getVectorFactory().createVector(new double[]{2.0, 4.0});
        
        assertTrue(row0.isMathematicallyEqualTo(expectedRow0));
        assertTrue(col1.isMathematicallyEqualTo(expectedCol1));
    }
    
    @Test
    void testIsMathematicallyEqualToTolerance() {
        double[][] dataA = {{1.0+EPSILON/3.0, 2.0}, {3.0, 4.0}};
        double[][] dataB = {{1.0, 2.0}, {3.0, 4.0}};
        RealMatrix a = factory.createMatrix(dataA);
        RealMatrix b = factory.createMatrix(dataB);
        RealMatrix differentSize = factory.createZeroMatrix(3, 3);
        // Should be equal within default EPSILON
        assertTrue(a.isMathematicallyEqualTo(b));

        // Should not be equal due to different sizes
        assertFalse(a.isMathematicallyEqualTo(differentSize));
    }
}