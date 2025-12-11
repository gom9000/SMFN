package net.gommagomma.smfn.math.linearalgebra.complex;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.math.algebra.core.NumericFactory;
import net.gommagomma.smfn.math.algebra.numeric.Complex;
import net.gommagomma.smfn.math.utils.MathConstants;

public class ComplexMatrixTest {

    private ComplexMatrixFactory matrixFactory;
    private NumericFactory<Complex> complexFactory;
    private static final double EPSILON = MathConstants.EPSILON;

    // Helper method per creare Complex facilmente
    private Complex c(double real, double imag) {
        return new Complex(real, imag);
    }
    
    // Helper method per creare Complex da un double (parte immaginaria zero)
    private Complex c(double real) {
        return complexFactory.of(real);
    }

    @BeforeEach
    void setUp() {
        matrixFactory = ComplexMatrixFactory.getInstance();
        complexFactory = matrixFactory.getScalarFactory();
    }

    // ========================================================================
    // 1. TEST FACTORY E CREAZIONE
    // ========================================================================

    @Test
    @DisplayName("Factory - Creazione da array di Complex")
    void testCreateMatrixFromComplexArray() {
        Complex[][] data = {
            {c(1, 1), c(0, -2)},
            {c(3, 0), c(-4, 4)}
        };
        ComplexMatrix matrix = matrixFactory.createMatrix(data);

        assertEquals(2, matrix.getRows());
        assertEquals(2, matrix.getColumns());
        assertTrue(matrix.get(0, 0).isMathematicallyEqualTo(c(1, 1)));
        assertTrue(matrix.get(1, 1).isMathematicallyEqualTo(c(-4, 4)));
    }
    
    @Test
    @DisplayName("Factory - Creazione da array di double (numeri reali)")
    void testCreateMatrixFromDoubleArray() {
        double[][] data = {
            {1.0, 2.0},
            {3.0, 4.0}
        };
        ComplexMatrix matrix = matrixFactory.createMatrix(data);
        
        // Deve convertire i double in Complex con parte immaginaria nulla
        assertTrue(matrix.get(0, 0).isMathematicallyEqualTo(c(1.0, 0.0)));
        assertTrue(matrix.get(1, 1).isMathematicallyEqualTo(c(4.0, 0.0)));
    }
    
    @Test
    @DisplayName("Factory - Array non quadrato (rettangolare)")
    void testCreateRectangularMatrix() {
        Complex[][] data = {
            {c(1), c(2), c(3)},
            {c(4), c(5), c(6)}
        };
        ComplexMatrix matrix = matrixFactory.createMatrix(data);
        assertEquals(2, matrix.getRows());
        assertEquals(3, matrix.getColumns());
    }

    // ========================================================================
    // 2. TEST OPERAZIONI ALGEBRICHE (Modulo/Campo)
    // ========================================================================

    @Test
    @DisplayName("Somma di Matrici Complesse")
    void testMatrixAddition() {
        Complex[][] dataA = {{c(1, 1), c(0, -2)}, {c(3, 0), c(-4, 4)}};
        Complex[][] dataB = {{c(2, -1), c(5, 2)}, {c(1, 1), c(4, -3)}};
        ComplexMatrix a = matrixFactory.createMatrix(dataA);
        ComplexMatrix b = matrixFactory.createMatrix(dataB);

        ComplexMatrix c = a.add(b);
        // Risultato atteso: 
        // [ (1+2, 1-1)  (0+5, -2+2) ]   = [ (3, 0) (5, 0) ]
        // [ (3+1, 0+1)  (-4+4, 4-3) ]   = [ (4, 1) (0, 1) ]
        ComplexMatrix expected = matrixFactory.createMatrix(new Complex[][]{
            {c(3, 0), c(5, 0)}, 
            {c(4, 1), c(0, 1)}
        });

        assertTrue(c.isMathematicallyEqualTo(expected));
    }

    @Test
    @DisplayName("Moltiplicazione per Scalare Complesso")
    void testMultiplyByComplexScalar() {
        Complex[][] data = {{c(2, 0), c(0, 1)}, {c(1, -1), c(3, 3)}};
        ComplexMatrix a = matrixFactory.createMatrix(data);
        
        // Scalare (1 + i)
        Complex scalar = c(1, 1); 

        ComplexMatrix c = a.multiplyByScalar(scalar);
        
        // Risultato atteso: 
        // (2)*(1+i) = 2+2i
        // (i)*(1+i) = i + i^2 = -1+i
        // (1-i)*(1+i) = 1 - i^2 = 2
        // (3+3i)*(1+i) = 3 + 3i + 3i + 3i^2 = 3 + 6i - 3 = 6i
        ComplexMatrix expected = matrixFactory.createMatrix(new Complex[][]{
            {c(2, 2), c(-1, 1)}, 
            {c(2, 0), c(0, 6)}
        });

        assertTrue(c.isMathematicallyEqualTo(expected));
    }

    @Test
    @DisplayName("Moltiplicazione tra Matrici Complesse (Prodotto)")
    void testMatrixMultiplication() {
        // A(2x2) * B(2x2) = C(2x2)
        Complex[][] dataA = {{c(1, 0), c(0, 1)}, {c(0, -1), c(1, 0)}}; // A = [1, i; -i, 1]
        Complex[][] dataB = {{c(2, 0), c(1, 0)}, {c(0, 1), c(0, 0)}}; // B = [2, 1; i, 0]
        ComplexMatrix a = matrixFactory.createMatrix(dataA);
        ComplexMatrix b = matrixFactory.createMatrix(dataB);

        ComplexMatrix c = a.multiply(b);
        
        // C[0][0] = (1)*(2) + (i)*(i) = 2 + i^2 = 2 - 1 = 1
        // C[0][1] = (1)*(1) + (i)*(0) = 1
        // C[1][0] = (-i)*(2) + (1)*(i) = -2i + i = -i
        // C[1][1] = (-i)*(1) + (1)*(0) = -i
        ComplexMatrix expected = matrixFactory.createMatrix(new Complex[][]{
            {c(1, 0), c(1, 0)}, 
            {c(0, -1), c(0, -1)}
        });

        assertTrue(c.isMathematicallyEqualTo(expected));
    }
    
    // ========================================================================
    // 3. TEST PROPRIETÀ HERMITIANE (TRASPOSTA CONIUGATA)
    // ========================================================================

    @Test
    @DisplayName("Trasposta Coniugata (Hermitiana)")
    void testConjugateTranspose() {
        // Matrice 2x3
        Complex[][] data = {
            {c(1, 2), c(3, 0), c(0, -4)},
            {c(-5, 0), c(0, 0), c(6, 1)}
        }; 
        ComplexMatrix a = matrixFactory.createMatrix(data);

        ComplexMatrix conjugateTranspose = a.conjugateTranspose();
        
        // Risultato atteso (3x2):
        // [ coniugato(1+2i)  coniugato(-5) ]   = [ (1-2i) (-5)   ]
        // [ coniugato(3)     coniugato(0)  ]   = [ (3)    (0)    ]
        // [ coniugato(-4i)   coniugato(6+i) ]  = [ (4i)   (6-i) ]
        ComplexMatrix expected = matrixFactory.createMatrix(new Complex[][]{
            {c(1, -2), c(-5, 0)},
            {c(3, 0), c(0, 0)},
            {c(0, 4), c(6, -1)}
        });

        assertEquals(3, conjugateTranspose.getRows());
        assertEquals(2, conjugateTranspose.getColumns());
        assertTrue(conjugateTranspose.isMathematicallyEqualTo(expected));
    }
    
    @Test
    @DisplayName("Uguaglianza Matematica con Tolleranza")
    void testIsMathematicallyEqualToTolerance() {
        double diff = EPSILON / 10.0;
        
        // Un elemento ha un errore entro la tolleranza
        Complex[][] dataA = {{c(1.0 + diff, 0.0), c(2.0, 0.0)}};
        Complex[][] dataB = {{c(1.0, 0.0), c(2.0, 0.0)}};
        ComplexMatrix a = matrixFactory.createMatrix(dataA);
        ComplexMatrix b = matrixFactory.createMatrix(dataB);
        
        // Deve essere TRUE
        assertTrue(a.isMathematicallyEqualTo(b));
        
        // Un elemento ha un errore maggiore della tolleranza
        Complex[][] dataC = {{c(1.0 + EPSILON * 2.0, 0.0), c(2.0, 0.0)}};
        ComplexMatrix c = matrixFactory.createMatrix(dataC);
        
        // Deve essere FALSE
        assertFalse(a.isMathematicallyEqualTo(c));
    }
}