package net.gommagomma.smfn.math.linearalgebra.complex;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.math.algebra.numeric.Complex;
import net.gommagomma.smfn.math.algebra.numeric.ComplexFactory;
import net.gommagomma.smfn.math.algebra.numeric.Real;
import net.gommagomma.smfn.math.utils.MathConstants;

class ComplexVectorTest {

    private ComplexVectorFactory factory;
    private ComplexFactory scalarFactory;
    private Complex i; // Unità immaginaria (0 + 1i)
    private Complex onePlusI; // 1 + i
    private Complex twoMinusI; // 2 - i
    private final double TOLERANCE = MathConstants.EPSILON; 

    @BeforeEach
    void setUp() {
        factory = ComplexVectorFactory.getInstance();
        scalarFactory = (ComplexFactory) factory.getScalarFactory();
        
        // Assumiamo che Complex abbia un metodo statico/factory per creare da parti reali/immaginarie
        // che non è esposto nella factory che hai fornito. Lo usiamo per creare i test case complessi:
        // Se ComplexFactory non ha fromParts, assumiamo che i costruttori di Complex lo supportino o
        // creiamo i valori da double e poi aggiungiamo la parte immaginaria (metodo più robusto)
        i = scalarFactory.of(0.0).add(new Complex(0.0, 1.0)); // 0 + 1i
        onePlusI = scalarFactory.of(1.0).add(new Complex(0.0, 1.0)); // 1 + i
        twoMinusI = scalarFactory.of(2.0).add(new Complex(0.0, -1.0)); // 2 - i
    }
    
    // Assumiamo che ComplexFactory abbia un metodo helper per la creazione (o che Complex lo abbia)
    // Per semplicità, ipotizziamo l'esistenza di un costruttore o metodo factory in Complex/ComplexFactory:
    private Complex createComplex(double real, double imag) {
        // Se ComplexFactory avesse fromParts, sarebbe: return scalarFactory.fromParts(real, imag);
        // Usiamo new Complex(real, imag) per i test
        return new Complex(real, imag);
    }


    // --- Test sulla Factory: Creazione e Conversione ---

    @Test
    void testCreateVectorFromIntArray() {
        int[] data = {5, -10, 0};
        ComplexVector v = factory.createVector(data);

        assertEquals(3, v.dimension());
        // Verifica che int sia convertito in Complex (parte immaginaria zero)
        assertTrue(v.get(0).isMathematicallyEqualTo(createComplex(5.0, 0.0)));
        assertTrue(v.get(1).isMathematicallyEqualTo(createComplex(-10.0, 0.0)));
    }
    
    @Test
    void testCreateVectorFromDoubleArray() {
        double[] data = {1.5, -2.0, 3.14};
        ComplexVector v = factory.createVector(data);
        
        // Verifica che double sia convertito in Complex (parte immaginaria zero)
        assertTrue(v.get(0).isMathematicallyEqualTo(createComplex(1.5, 0.0)));
        assertTrue(v.get(1).isMathematicallyEqualTo(createComplex(-2.0, 0.0)));
        // Verifica della parte immaginaria zero (necessaria se Complex.isMathematicallyEqualTo non lo fa)
        // Se Complex espone un metodo per la parte immaginaria:
        // assertTrue(v.get(2).getImaginaryPart().isZero()); 
    }


    // --- Test sulle Operazioni Algebriche ---

    @Test
    void testVectorAddition() {
        // v1 = (1+i, 2-i)
        ComplexVector v1 = factory.createVector(new Complex[]{onePlusI, twoMinusI});
        // v2 = (2i, -1+3i)
        Complex c2_0 = createComplex(0.0, 2.0);
        Complex c2_1 = createComplex(-1.0, 3.0);
        ComplexVector v2 = factory.createVector(new Complex[]{c2_0, c2_1});

        // Sum attesa: (1+3i, 1+2i)
        Complex expected_0 = createComplex(1.0, 3.0);
        Complex expected_1 = createComplex(1.0, 2.0);
        ComplexVector expected = factory.createVector(new Complex[]{expected_0, expected_1});

        ComplexVector sum = v1.add(v2);
        assertTrue(sum.isMathematicallyEqualTo(expected));
    }

    @Test
    void testVectorNegation() {
        // v = (1+i, -2i)
        Complex c1 = createComplex(1.0, 1.0);
        Complex c2 = createComplex(0.0, -2.0);
        ComplexVector v = factory.createVector(new Complex[]{c1, c2});

        // Negate attesa: (-1-i, 2i)
        Complex expected_0 = createComplex(-1.0, -1.0);
        Complex expected_1 = createComplex(0.0, 2.0);
        ComplexVector expected = factory.createVector(new Complex[]{expected_0, expected_1});

        ComplexVector negated = v.negate();
        assertTrue(negated.isMathematicallyEqualTo(expected));
    }

    @Test
    void testMultiplyByScalar() {
        // v = (1, i)
        ComplexVector v = factory.createVector(new Complex[]{scalarFactory.one(), i});
        // scalar = 2i
        Complex scalar = createComplex(0.0, 2.0);

        // Result atteso: (2i, -2)
        Complex expected_0 = createComplex(0.0, 2.0);
        Complex expected_1 = createComplex(-2.0, 0.0);
        ComplexVector expected = factory.createVector(new Complex[]{expected_0, expected_1});

        ComplexVector result = v.multiplyByScalar(scalar);
        assertTrue(result.isMathematicallyEqualTo(expected));
    }

    // --- Test Prodotto Scalare Hermitiano e Norma ---

    @Test
    void testHermitianDotProduct_ZeroResult() {
        // V = (i, 1)
        ComplexVector v = factory.createVector(new Complex[]{i, scalarFactory.one()});
        // W = (1, i)
        ComplexVector w = factory.createVector(new Complex[]{scalarFactory.one(), i});
        
        // V . W = (i * 1*) + (1 * i*) = i + (1 * -i) = i - i = 0
        Complex result = v.dotProduct(w);
        Complex expected = scalarFactory.zero();

        assertTrue(result.isMathematicallyEqualTo(expected));
    }
    
    @Test
    void testHermitianDotProduct_NonZero() {
        // V = (1+i, 2)
        Complex c1 = createComplex(1.0, 1.0);
        Complex c2 = createComplex(2.0, 0.0);
        ComplexVector v = factory.createVector(new Complex[]{c1, c2});

        // W = (i, 1-i)
        Complex c3 = createComplex(0.0, 1.0);
        Complex c4 = createComplex(1.0, -1.0);
        ComplexVector w = factory.createVector(new Complex[]{c3, c4});
        
        // Calcolo:
        // W[0]* = -i
        // W[1]* = 1+i
        // V[0]*W[0]* = (1+i) * (-i) = 1 - i
        // V[1]*W[1]* = 2 * (1+i) = 2 + 2i
        // Sum = (1-i) + (2+2i) = 3 + i
        
        Complex result = v.dotProduct(w);
        Complex expected = createComplex(3.0, 1.0);

        assertTrue(result.isMathematicallyEqualTo(expected));
    }

    @Test
    void testNorm() {
        // V = (1 + i, 2)
        Complex c1 = createComplex(1.0, 1.0);
        Complex c2 = createComplex(2.0, 0.0);
        ComplexVector v = factory.createVector(new Complex[]{c1, c2});

        // V . V = ||V[0]||^2 + ||V[1]||^2 = (1^2 + 1^2) + (2^2 + 0^2) = 2 + 4 = 6
        // Norm attesa: ||V|| = sqrt(6)
        
        Real normResult = v.norm();
        
        // Verifica l'approssimazione del valore con la tolleranza
        // Assumendo che Real.toDouble() esista per l'accesso primitivo
        double expectedDouble = Math.sqrt(6.0);
        
        assertTrue(
            Math.abs(normResult.getValue() - expectedDouble) < TOLERANCE, 
            "La norma non corrisponde al valore atteso per la tolleranza.");
    }
}