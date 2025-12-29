package net.gommagomma.smfn.math.linearalgebra.natural;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.math.algebra.numerics.Natural;


class NaturalVectorTest {

    private NaturalVectorFactory factory;
    private Natural one;
    private Natural two;

    @BeforeEach
    void setUp() {
        factory = NaturalVectorFactory.getInstance();
        one = factory.getScalarFactory().one();
        two = factory.getScalarFactory().of(2);
    }

    // --- Test sulla Factory ---

    @Test
    void testCreateVectorFromNaturalArray() {
        Natural[] components = {one, two, one};
        NaturalVector v = factory.createVector(components);

        assertNotNull(v);
        assertEquals(3, v.dimension());
        assertTrue(v.get(1).isMathematicallyEqualTo(two));
    }

    @Test
    void testCreateVectorFromIntArray() {
        int[] data = {10, 5, 0};
        NaturalVector v = factory.createVector(data);
        
        assertEquals(3, v.dimension());
        assertTrue(v.get(0).isMathematicallyEqualTo(factory.getScalarFactory().of(10)));
    }
    
    @Test
    void testCreateVectorFromDoubleArray_InvalidNegativeValue() {
        double[] data = {1.0, -2.5}; // Naturali non possono essere negativi
        
        // Questo test presuppone che NaturalFactory.fromDouble() lanci IllegalArgumentException se negativo
        assertThrows(IllegalArgumentException.class, () -> {
            factory.createVector(data);
        });
    }

    @Test
    void testCreateZeroVector() {
        int dim = 5;
        NaturalVector zeroVector = factory.createZeroVector(dim);
        
        assertEquals(dim, zeroVector.dimension());
        assertTrue(zeroVector.get(0).isZero());
        assertTrue(zeroVector.get(4).isZero());
    }

    // --- Test sulle Operazioni Algebriche (NaturalVector) ---

    @Test
    void testVectorAddition() {
        NaturalVector v1 = factory.createVector(new int[]{1, 2, 3});
        NaturalVector v2 = factory.createVector(new int[]{4, 5, 6});
        
        NaturalVector sum = v1.add(v2);
        NaturalVector expected = factory.createVector(new int[]{5, 7, 9});
        
        assertTrue(sum.isMathematicallyEqualTo(expected));
    }

    @Test
    void testVectorAddition_DimensionMismatch() {
        NaturalVector v1 = factory.createVector(new int[]{1, 2});
        NaturalVector v2 = factory.createVector(new int[]{1, 2, 3});
        
        assertThrows(IllegalArgumentException.class, () -> {
            v1.add(v2);
        });
    }

    @Test
    void testMultiplyByScalar() {
        NaturalVector v = factory.createVector(new int[]{3, 4});
        Natural scalar = factory.getScalarFactory().of(5);
        
        NaturalVector result = v.scale(scalar);
        NaturalVector expected = factory.createVector(new int[]{15, 20});
        
        assertTrue(result.isMathematicallyEqualTo(expected));
    }
    
    @Test
    void testMultiplyByScalar_Identity() {
        NaturalVector v = factory.createVector(new int[]{10, 20});
        Natural scalarOne = factory.getScalarFactory().one();
        
        NaturalVector result = v.scale(scalarOne);
        
        assertTrue(result.isMathematicallyEqualTo(v));
    }

    // --- Test Immutabilità e Uguaglianza ---
    
    @Test
    void testCopy() {
        NaturalVector v1 = factory.createVector(new int[]{7, 8});
        NaturalVector v2 = v1.copy();
        
        assertNotSame(v1, v2);
        assertTrue(v1.isMathematicallyEqualTo(v2));
        
        // Verifica l'immutabilità
        // Se Natural fosse mutabile (non lo è), dovremmo testare se la modifica dell'array interno impatta il copy.
    }

    @Test
    void testEqualityAndHashCode() {
        NaturalVector v1 = factory.createVector(new int[]{1, 2, 3});
        NaturalVector v2 = factory.createVector(new int[]{1, 2, 3});
        NaturalVector v3 = factory.createVector(new int[]{3, 2, 1});

        // equals e isMathematicallyEqualTo
        assertTrue(v1.equals(v2));
        assertTrue(v1.isMathematicallyEqualTo(v2));
        assertFalse(v1.equals(v3));
        
        // hashCode
        assertEquals(v1.hashCode(), v2.hashCode());
        assertNotEquals(v1.hashCode(), v3.hashCode());
    }
    
    @Test
    void testGetZero() {
        NaturalVector v = factory.createVector(new int[]{1, 2, 3});
        NaturalVector zero = v.getZero();
        
        assertEquals(3, zero.dimension());
        assertTrue(zero.isZero());
        
        // Verifica che getZero() restituisca un nuovo vettore (perché è un'operazione factory-like)
        assertNotSame(v, zero);
    }
}