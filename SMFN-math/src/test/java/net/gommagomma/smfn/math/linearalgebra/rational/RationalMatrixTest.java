package net.gommagomma.smfn.math.linearalgebra.rational;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.math.algebra.core.elements.factories.NumericFactory;
import net.gommagomma.smfn.math.algebra.numerics.Rational;


public class RationalMatrixTest {

    private RationalMatrixFactory factory;
    private NumericFactory<Rational> scalarFactory;
    private Rational r0;
    private Rational r1;
    private Rational r2;
    private Rational r_half;

    @BeforeEach
    void setUp() {
        factory = RationalMatrixFactory.getInstance();
        scalarFactory =  factory.getScalarFactory();
        r0 = scalarFactory.zero();
        r1 = scalarFactory.one();
        r2 = new Rational(2);
        r_half = new Rational(1, 2); // 1/2
    }

    // --- Test di Costruzione e Factory ---

    @Test
    void testCreationFromRationalArray() {
        Rational[][] data = {
            {r1, r_half},
            {r2, r0}
        };
        RationalMatrix matrix = factory.createMatrix(data);

        assertEquals(2, matrix.getRows());
        assertEquals(2, matrix.getColumns());
        assertTrue(matrix.get(0, 1).isMathematicallyEqualTo(r_half));
    }
    
    @Test
    void testCreationFromDoubleArray() {
        double[][] data = {
            {0.5, 1.0},
            {2.0, 0.25}
        };
        RationalMatrix matrix = factory.createMatrix(data);
        
        // 0.25 dovrebbe essere convertito in 1/4
        Rational r_quarter = new Rational(1, 4);
        
        assertEquals(2, matrix.getRows());
        assertTrue(matrix.get(0, 0).isMathematicallyEqualTo(r_half));
        assertTrue(matrix.get(1, 1).isMathematicallyEqualTo(r_quarter));
    }

    @Test
    void testCreateZeroMatrix() {
        RationalMatrix zero = factory.createZeroMatrix(2, 2);
        assertTrue(zero.get(1, 1).isZero());
    }

    // --- Test Operazioni Algebriche (Ring/Semiring ereditato) ---

    @Test
    void testAddition() {
        // A = [[1, 0], [1/2, 2]]
        RationalMatrix A = factory.createMatrix(new double[][]{{1.0, 0.0}, {0.5, 2.0}});
        // B = [[0, 1/2], [1/2, 1]]
        RationalMatrix B = factory.createMatrix(new double[][]{{0.0, 0.5}, {0.5, 1.0}});

        // C = A + B = [[1, 1/2], [1, 3]]
        RationalMatrix C = A.add(B);

        assertTrue(C.get(0, 0).isMathematicallyEqualTo(r1));
        assertTrue(C.get(1, 0).isMathematicallyEqualTo(r1));
        assertTrue(C.get(1, 1).isMathematicallyEqualTo(new Rational(3)));
    }
    
    @Test
    void testMultiplyByScalar() {
        // A = [[1, 1/2], [2, 0]]
        RationalMatrix A = factory.createMatrix(new double[][]{{1.0, 0.5}, {2.0, 0.0}});
        Rational scalar = new Rational(3, 2); // 3/2

        // C = (3/2) * A = [[3/2, 3/4], [3, 0]]
        RationalMatrix C = A.scale(scalar);

        assertTrue(C.get(0, 0).isMathematicallyEqualTo(new Rational(3, 2)));
        assertTrue(C.get(0, 1).isMathematicallyEqualTo(new Rational(3, 4)));
    }


    // --- Test Operazioni Specifiche del Campo (Field-specific) ---

    @Test
    void testDeterminant_2x2() {
        // A = [[2, 1], [4, 3]]
        // det(A) = 2*3 - 1*4 = 6 - 4 = 2
        RationalMatrix A = factory.createMatrix(new int[][]{{2, 1}, {4, 3}});
        Rational det = A.determinant();

        assertTrue(det.isMathematicallyEqualTo(r2));
    }
    
    @Test
    void testDeterminant_3x3() {
        // B = [[1, 0, 2], [0, 1, 1], [1, 2, 0]]
        // det(B) = 1(0-2) - 0 + 2(0-1) = -2 - 2 = -4
        RationalMatrix B = factory.createMatrix(new int[][]{{1, 0, 2}, {0, 1, 1}, {1, 2, 0}});
        Rational det = B.determinant();

        assertTrue(det.isMathematicallyEqualTo(new Rational(-4)));
    }


    @Test
    void testInverse_2x2() {
        // A = [[2, 1], [4, 3]]
        // det(A) = 2. 
        // Aâ�»Â¹ = (1/det(A)) * [[3, -1], [-4, 2]] 
        // Aâ�»Â¹ = 1/2 * [[3, -1], [-4, 2]] = [[3/2, -1/2], [-2, 1]]
        RationalMatrix A = factory.createMatrix(new int[][]{{2, 1}, {4, 3}});
        RationalMatrix A_inv = A.inverse();
        
        Rational r_neg_half = new Rational(-1, 2);
        Rational r_3_2 = new Rational(3, 2);
        Rational r_neg_2 = new Rational(-2);

        assertTrue(A_inv.get(0, 0).isMathematicallyEqualTo(r_3_2));
        assertTrue(A_inv.get(0, 1).isMathematicallyEqualTo(r_neg_half));
        assertTrue(A_inv.get(1, 0).isMathematicallyEqualTo(r_neg_2));
        assertTrue(A_inv.get(1, 1).isMathematicallyEqualTo(r1));

        // Verifica: A * Aâ�»Â¹ deve essere la matrice identitÃ 
        RationalMatrix Identity = A.multiply(A_inv);
        RationalMatrix expectedIdentity = factory.createMatrix(new double[][]{{1.0, 0.0}, {0.0, 1.0}});
        assertTrue(Identity.isMathematicallyEqualTo(expectedIdentity), "A * Aâ�»Â¹ deve essere la matrice identitÃ .");
    }

    @Test
    void testInverse_SingularMatrix() {
        // Matrice singolare: det(A) = 1*4 - 2*2 = 0
        RationalMatrix Singular = factory.createMatrix(new int[][]{{1, 2}, {2, 4}});
        
        // Invert() dovrebbe lanciare una eccezione se il determinante Ã¨ zero
        assertThrows(ArithmeticException.class, () -> Singular.inverse(), 
                     "L'inversione di una matrice singolare deve lanciare un'eccezione.");
    }
}