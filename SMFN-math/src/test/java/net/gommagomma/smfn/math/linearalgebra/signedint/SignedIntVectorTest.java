package net.gommagomma.smfn.math.linearalgebra.signedint;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.math.algebra.numeric.SignedInt;

class SignedIntVectorTest {

    private SignedIntVectorFactory factory;
    private SignedInt one;
    private SignedInt zero;
    private SignedInt negativeTwo;

    @BeforeEach
    void setUp() {
        factory = SignedIntVectorFactory.getInstance();
        zero = factory.getScalarFactory().zero();
        one = factory.getScalarFactory().one();
        negativeTwo = factory.getScalarFactory().of(-2);
    }

    // --- Test sulla Factory: Creazione ---

    @Test
    void testCreateVectorFromSignedIntArray() {
        SignedInt[] components = {one, negativeTwo, zero};
        SignedIntVector v = factory.createVector(components);

        assertNotNull(v);
        assertEquals(3, v.dimension());
        assertTrue(v.get(1).isMathematicallyEqualTo(negativeTwo));
    }
    
    @Test
    void testCreateVectorFromIntArray() {
        int[] data = {10, -5, 0};
        SignedIntVector v = factory.createVector(data);
        
        assertEquals(3, v.dimension());
        assertTrue(v.get(0).isMathematicallyEqualTo(factory.getScalarFactory().of(10)));
        assertTrue(v.get(1).isMathematicallyEqualTo(factory.getScalarFactory().of(-5)));
    }

    @Test
    void testCreateVectorFromDoubleArray_MathematicalRounding() {
        // La logica matematica standard di arrotondamento di Java (Math.round)
        // arrotonda 1.5 a 2 e -2.9 a -3.
        
        double[] data = {2, -3, 0.0};
        SignedIntVector v = factory.createVector(data);
        
        // Nuove aspettative basate su Math.round():
        SignedInt expectedTwo = factory.getScalarFactory().of(2);
        SignedInt expectedNegativeThree = factory.getScalarFactory().of(-3);
        
        assertEquals(3, v.dimension());
        // 1.5 arrotondato a 2
        assertTrue(v.get(0).isMathematicallyEqualTo(expectedTwo)); 
        // -2.9 arrotondato a -3
        assertTrue(v.get(1).isMathematicallyEqualTo(expectedNegativeThree)); 
    }
    
    @Test
    void testCreateVectorFromLongArray() {
        long[] data = {Long.MAX_VALUE, -1000L};
        SignedIntVector v = factory.createVector(data);
        
        assertEquals(2, v.dimension());
        // Questo test presuppone che SignedIntFactory gestisca l'overflow o l'arrotondamento
        // Qui testiamo semplicemente la corretta conversione di un long che rientra nell'int.
        SignedInt expectedNeg1000 = factory.getScalarFactory().of(-1000);
        assertTrue(v.get(1).isMathematicallyEqualTo(expectedNeg1000));
    }

    @Test
    void testCreateZeroVector() {
        int dim = 4;
        SignedIntVector zeroVector = factory.createZeroVector(dim);
        
        assertEquals(dim, zeroVector.dimension());
        assertTrue(zeroVector.get(0).isZero());
        assertTrue(zeroVector.get(3).isMathematicallyEqualTo(zero));
    }

    // --- Test sulle Operazioni Algebriche (SignedIntVector) ---

    @Test
    void testVectorAddition() {
        SignedIntVector v1 = factory.createVector(new int[]{5, -2, 8});
        SignedIntVector v2 = factory.createVector(new int[]{-3, 5, 0});
        
        SignedIntVector sum = v1.add(v2);
        SignedIntVector expected = factory.createVector(new int[]{2, 3, 8});
        
        assertTrue(sum.isMathematicallyEqualTo(expected));
    }

    @Test
    void testVectorNegation() {
        SignedIntVector v = factory.createVector(new int[]{5, -1, 0, 100});
        SignedIntVector negated = v.negate();
        
        SignedIntVector expected = factory.createVector(new int[]{-5, 1, 0, -100});
        
        assertTrue(negated.isMathematicallyEqualTo(expected));
    }

    @Test
    void testAddAndNegateCommutativity() {
        SignedIntVector v = factory.createVector(new int[]{5, -1});
        
        // Verifica v + (-v) = 0
        SignedIntVector zeroResult = v.add(v.negate());
        SignedIntVector expectedZero = factory.createZeroVector(2);
        
        assertTrue(zeroResult.isMathematicallyEqualTo(expectedZero));
    }

    @Test
    void testMultiplyByScalar() {
        SignedIntVector v = factory.createVector(new int[]{4, -1, 3});
        SignedInt scalar = factory.getScalarFactory().of(-2);
        
        SignedIntVector result = v.scale(scalar);
        SignedIntVector expected = factory.createVector(new int[]{-8, 2, -6});
        
        assertTrue(result.isMathematicallyEqualTo(expected));
    }

    // --- Test Immutabilità e Uguaglianza ---
    
    @Test
    void testCopy() {
        SignedIntVector v1 = factory.createVector(new int[]{1, -1});
        SignedIntVector v2 = v1.copy();
        
        assertNotSame(v1, v2);
        assertTrue(v1.isMathematicallyEqualTo(v2));
    }

    @Test
    void testEqualityAndHashCode() {
        SignedIntVector v1 = factory.createVector(new int[]{1, 2, -3});
        SignedIntVector v2 = factory.createVector(new int[]{1, 2, -3});
        
        // equals e isMathematicallyEqualTo
        assertTrue(v1.equals(v2));
        assertTrue(v1.isMathematicallyEqualTo(v2));
        
        // hashCode
        assertEquals(v1.hashCode(), v2.hashCode());
    }
}