package net.gommagomma.smfn.math.linearalgebra.rational;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.math.algebra.numeric.Rational;
import net.gommagomma.smfn.math.algebra.numeric.RationalFactory;
import net.gommagomma.smfn.math.algebra.numeric.Real;

class RationalVectorTest {

    private RationalVectorFactory factory;
    private RationalFactory scalarFactory;
    private final double TOLERANCE = 1e-9; 
    
    // Metodo helper per creare razionali direttamente, assumendo l'uso di un costruttore (num, den)
    // Se la tua RationalFactory ha un metodo of(num, den), usalo al posto di 'new Rational'
    private Rational createRational(long num, long den) {
        return new Rational(num, den); 
    }

    @BeforeEach
    void setUp() {
        factory = RationalVectorFactory.getInstance();
        scalarFactory = (RationalFactory) factory.getScalarFactory();
        
        // Assicuriamo che la factory usi 'of' se hai completato il refactoring in RationalVectorFactory
        // (Il codice fornito usava 'to', ma la tua intenzione era 'of')
        // Se RationalVectorFactory usasse ancora 'to', dovresti cambiare i metodi createVector
        // per invocare of() invece di to()
    }

    // --- Test sulla Factory: Creazione e Conversione ---

    @Test
    void testCreateVectorFromIntArray() {
        int[] data = {1, -2, 5};
        RationalVector v = factory.createVector(data);

        assertEquals(3, v.dimension());
        // 1 -> 1/1
        assertTrue(v.get(0).isMathematicallyEqualTo(scalarFactory.of(1)));
        // -2 -> -2/1
        assertTrue(v.get(1).isMathematicallyEqualTo(scalarFactory.of(-2)));
    }
    
    @Test
    void testCreateVectorFromLongArray() {
        long[] data = {100L, 0L, -50L};
        RationalVector v = factory.createVector(data);

        assertEquals(3, v.dimension());
        // 100L -> 100/1
        assertTrue(v.get(0).isMathematicallyEqualTo(scalarFactory.of(100L)));
        // -50L -> -50/1
        assertTrue(v.get(2).isMathematicallyEqualTo(scalarFactory.of(-50L)));
    }

    @Test
    void testCreateVectorFromDoubleArray_Corrected() {
        double[] data = {0.5, 0.25};
        RationalVector v = factory.createVector(data);
        
        assertEquals(2, v.dimension());
        
        // 0.5 -> 1/2.
        // Questo test ORA FORZERA' a fallire se scalarFactory.of(0.5) non è matematicamente
        // equivalente a createRational(1, 2) che è l'output atteso per un sistema razionale ridotto.
        assertTrue(v.get(0).isMathematicallyEqualTo(createRational(1, 2)), 
                   "0.5 dovrebbe essere convertito in 1/2.");
                   
        // 0.25 -> 1/4
        assertTrue(v.get(1).isMathematicallyEqualTo(createRational(1, 4)), 
                   "0.25 dovrebbe essere convertito in 1/4.");
    }

    // --- Test sulle Operazioni Algebriche ---

    @Test
    void testVectorAddition() {
        // v1 = (1/2, 1/3)
        RationalVector v1 = factory.createVector(new Rational[]{
            createRational(1, 2), createRational(1, 3)
        });
        // v2 = (1/2, 2/3)
        RationalVector v2 = factory.createVector(new Rational[]{
            createRational(1, 2), createRational(2, 3)
        });

        // Sum attesa: (1, 1)
        RationalVector expected = factory.createVector(new Rational[]{
            scalarFactory.one(), scalarFactory.one()
        });

        RationalVector sum = v1.add(v2);
        assertTrue(sum.isMathematicallyEqualTo(expected));
    }

    @Test
    void testDotProduct() {
        // V = (1/2, 1/3)
        RationalVector v1 = factory.createVector(new Rational[]{
            createRational(1, 2), createRational(1, 3)
        });
        // W = (1/4, 2/5)
        RationalVector v2 = factory.createVector(new Rational[]{
            createRational(1, 4), createRational(2, 5)
        });

        // V . W = 31/120
        Rational result = v1.dotProduct(v2);
        Rational expected = createRational(31, 120);

        assertTrue(result.isMathematicallyEqualTo(expected));
    }

    @Test
    void testNorm() {
        // V = (3/5, 4/5). Prodotto scalare V.V = 1
        RationalVector v = factory.createVector(new Rational[]{
            createRational(3, 5), createRational(4, 5)
        });

        // Norm attesa: ||V|| = sqrt(1) = 1.0
        
        Real normResult = v.norm();
        double expectedDouble = 1.0;
        
        // Assumiamo che Real abbia un metodo toDouble() o getValue()
        double actualDouble = normResult.getValue(); 
        
        assertTrue(
            Math.abs(actualDouble - expectedDouble) < TOLERANCE, 
            "La norma non corrisponde al valore atteso per la tolleranza.");
    }
}