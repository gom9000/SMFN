package net.gommagomma.smfn.math.linearalgebra.real;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.math.algebra.core.NumericFactory;
import net.gommagomma.smfn.math.algebra.numeric.Real;

class RealVectorTest {

    private RealVectorFactory factory;
    private NumericFactory<Real> scalarFactory;
    // Tolleranza per confronti di numeri reali in virgola mobile (double)
    private final double TOLERANCE = 1e-9; 

    @BeforeEach
    void setUp() {
        factory = RealVectorFactory.getInstance();
        // Cast sicuro, dato che la factory restituisce RealFactory in questo contesto
        scalarFactory = factory.getScalarFactory(); 
    }

    // --- Test sulla Factory: Creazione e Conversione ---

    @Test
    void testCreateVectorFromDoubleArray() {
        double[] data = {1.5, -2.0, 3.14};
        RealVector v = factory.createVector(data);

        assertNotNull(v);
        assertEquals(3, v.dimension());
        // Verifica che la conversione da double sia corretta
        assertTrue(v.get(0).isMathematicallyEqualTo(scalarFactory.of(1.5)));
        assertTrue(v.get(1).isMathematicallyEqualTo(scalarFactory.of(-2.0)));
    }

    @Test
    void testCreateVectorFromIntArray() {
        int[] data = {5, -10, 0};
        RealVector v = factory.createVector(data);

        assertEquals(3, v.dimension());
        // Verifica che int sia convertito in Real (5.0)
        assertTrue(v.get(0).isMathematicallyEqualTo(scalarFactory.of(5.0)));
        assertTrue(v.get(1).isMathematicallyEqualTo(scalarFactory.of(-10.0)));
    }

    @Test
    void testCreateZeroVector() {
        int dim = 3;
        RealVector zeroVector = factory.createZeroVector(dim);

        assertEquals(dim, zeroVector.dimension());
        assertTrue(zeroVector.isZero());
    }

    // --- Test sulle Operazioni Algebriche ---

    @Test
    void testVectorAddition() {
        RealVector v1 = factory.createVector(new double[]{1.0, 2.5, -3.0});
        RealVector v2 = factory.createVector(new double[]{0.5, -1.0, 3.0});

        RealVector sum = v1.add(v2);
        RealVector expected = factory.createVector(new double[]{1.5, 1.5, 0.0});

        assertTrue(sum.isMathematicallyEqualTo(expected));
    }

    @Test
    void testVectorNegation() {
        RealVector v = factory.createVector(new double[]{1.0, -0.5, 10.0});
        RealVector negated = v.negate();

        RealVector expected = factory.createVector(new double[]{-1.0, 0.5, -10.0});

        assertTrue(negated.isMathematicallyEqualTo(expected));
    }

    @Test
    void testMultiplyByScalar() {
        RealVector v = factory.createVector(new double[]{1.0, -2.0});
        Real scalar = scalarFactory.of(3.5);

        RealVector result = v.multiplyByScalar(scalar);
        RealVector expected = factory.createVector(new double[]{3.5, -7.0});

        assertTrue(result.isMathematicallyEqualTo(expected));
    }

    // --- Test Prodotto Scalare e Norma ---

    @Test
    void testDotProduct() {
        // V = (1, 2, 3)
        RealVector v1 = factory.createVector(new double[]{1.0, 2.0, 3.0});
        // W = (4, -5, 6)
        RealVector v2 = factory.createVector(new double[]{4.0, -5.0, 6.0});

        // Risultato atteso: (1*4) + (2*-5) + (3*6) = 4 - 10 + 18 = 12
        Real result = v1.dotProduct(v2);
        Real expected = scalarFactory.of(12.0);

        assertTrue(result.isMathematicallyEqualTo(expected));
    }

    @Test
    void testNorm() {
        // V = (3, 4)
        RealVector v = factory.createVector(new double[]{3.0, 4.0});

        // Risultato atteso: ||V|| = sqrt(3^2 + 4^2) = sqrt(25) = 5
        Real normResult = v.norm();
        Real expected = scalarFactory.of(5.0);

        assertTrue(normResult.isMathematicallyEqualTo(expected));
    }
    
    @Test
    void testNormWithDecimalValues() {
        // V = (1, 1)
        RealVector v = factory.createVector(new double[]{1.0, 1.0});

        // Risultato atteso: ||V|| = sqrt(2)
        Real normResult = v.norm();
        double expectedValue = Math.sqrt(2.0);
        
        // Per testare con tolleranza, assumiamo che Real abbia un metodo per accedere al suo valore double
        // Se la tua implementazione di Real lo supporta, usa:
        
        // Verifica l'approssimazione del valore con la tolleranza
        double actualValue = normResult.getValue(); 
        
        assertTrue(
            Math.abs(actualValue - expectedValue) < TOLERANCE, 
            "La norma non corrisponde al valore atteso per la tolleranza.");
    }

    // --- Test Uguaglianza ---

    @Test
    void testEqualityAndHashCode() {
        RealVector v1 = factory.createVector(new double[]{1.1, 2.2});
        RealVector v2 = factory.createVector(new double[]{1.1, 2.2});
        
        // equals e hashCode
        assertTrue(v1.equals(v2));
        assertEquals(v1.hashCode(), v2.hashCode());
        
        // Mismatch
        RealVector v3 = factory.createVector(new double[]{1.1, 2.3});
        assertFalse(v1.equals(v3));
    }
}