package net.gommagomma.smfn.math.linearalgebra.signedint;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.math.algebra.numeric.SignedInt;

@DisplayName("SignedIntVector: Test del Modulo su Anello Z")
public class SignedIntVectorTest {

    private static SignedInt S_N2, S_N1, S0, S1, S2, S3, S5;
    private static SignedIntVectorFactory factory;

    @BeforeAll
    static void setUp() {
        // Inizializzazione della Factory e degli scalari SignedInt
        factory = SignedIntVectorFactory.getInstance();
        
        S0 = factory.getScalarFactory().zero(); 
        S1 = new SignedInt(1);
        S2 = new SignedInt(2);
        S3 = new SignedInt(3);
        S5 = new SignedInt(5);
        S_N1 = new SignedInt(-1);
        S_N2 = new SignedInt(-2);
    }

    // ------------------- COSTRUTTORI / FACTORY -------------------

    @Test
    @DisplayName("Factory: Creazione corretta da array di SignedInt")
    void testFactory_CreateVectorFromComponents() {
        SignedInt[] data = new SignedInt[] { S1, S_N2, S3 };
        SignedIntVector v = factory.createVector(data);
        
        assertEquals(3, v.dimension(), "La dimensione deve essere 3.");
        assertEquals(S_N2, v.get(1), "Il secondo elemento deve essere -2.");
    }

    @Test
    @DisplayName("Factory: Creazione corretta di vettore nullo (zero)")
    void testFactory_CreateZeroVector() {
        SignedIntVector v = factory.createVector(2);
        
        assertEquals(2, v.dimension(), "La dimensione deve essere 2.");
        assertTrue(v.get(0).isMathematicallyEqualTo(S0), "Ogni elemento deve essere SignedInt.ZERO.");
    }
    
    @Test
    @DisplayName("Factory: Conversione da double array")
    void testFactory_CreateVectorFromDouble() {
        // La SignedIntFactory usa Math.round(), quindi i negativi arrotondano a -2, -1, 1, 3
        SignedIntVector v = factory.createVector(-2.1, -1.9, 0.5, 2.9); 
        
        SignedInt expectedN2 = new SignedInt(-2);
        SignedInt expectedN3 = new SignedInt(3);
        SignedInt expected0 = new SignedInt(1);
        
        SignedIntVector expected = new SignedIntVector(expectedN2, expectedN2, expected0, expectedN3);
        
        assertTrue(expected.isMathematicallyEqualTo(v), "La conversione da double deve usare Math.round().");
    }

    @Test
    @DisplayName("Constructor: Eccezione per array di componenti nullo o vuoto")
    void testConstructor_InvalidComponents() {
        assertThrows(IllegalArgumentException.class, () -> new SignedIntVector((SignedInt[]) null), 
                     "Deve lanciare eccezione se l'array è null.");
        assertThrows(IllegalArgumentException.class, () -> new SignedIntVector(new SignedInt[0]), 
                     "Deve lanciare eccezione se l'array è vuoto.");
    }
    
    // ------------------- OPERAZIONI DI MODULO (Anello) -------------------

    @Test
    @DisplayName("Negazione: Calcolo corretto dell'inverso additivo (negate)")
    void testNegate() {
        SignedIntVector v = factory.createVector(new SignedInt[] { S2, S_N1, S0 }); // [2, -1, 0]
        SignedIntVector expected = new SignedIntVector(S_N2, S1, S0); // [-2, 1, 0]
        
        SignedIntVector result = v.negate();
        
        assertTrue(expected.isMathematicallyEqualTo(result), "La negazione (inverso additivo) non è corretta.");
    }

    @Test
    @DisplayName("Sottrazione: Sottrazione (implicita: addizione con il negativo)")
    void testSubtract() {
        SignedIntVector v1 = factory.createVector(new SignedInt[] { S5, S3 }); // [5, 3]
        SignedIntVector v2 = factory.createVector(new SignedInt[] { S2, S_N1 }); // [2, -1]
        // [5, 3] - [2, -1] = [5-2, 3-(-1)] = [3, 4]
        
        // Simulo la sottrazione: v1.add(v2.negate())
        SignedIntVector result = v1.add(v2.negate());
        SignedIntVector expected = new SignedIntVector(S3, new SignedInt(4)); 
        
        assertTrue(expected.isMathematicallyEqualTo(result), "La sottrazione implicita non è corretta.");
    }


    @Test
    @DisplayName("Addizione: Somma con l'inverso additivo (V + (-V) = 0)")
    void testAdd_AdditiveInverse() {
        SignedIntVector v = factory.createVector(new SignedInt[] { S2, S_N1, S3 });
        SignedIntVector negatedV = v.negate();
        SignedIntVector expectedZero = v.getZero();
        
        SignedIntVector result = v.add(negatedV);
        
        assertTrue(expectedZero.isMathematicallyEqualTo(result), "V + (-V) deve essere il vettore nullo.");
    }

    @Test
    @DisplayName("Moltiplicazione per Scalare: Prodotto per scalare negativo")
    void testMultiplyByScalar_NegativeScalar() {
        SignedIntVector v = factory.createVector(new SignedInt[] { S2, S_N1 }); // [2, -1]
        SignedInt scalar = S_N2; // Scalare -2
        // [2, -1] * (-2) = [-4, 2]
        
        SignedInt expectedN4 = new SignedInt(-4);
        SignedIntVector expected = new SignedIntVector(expectedN4, S2); 
        
        SignedIntVector result = v.multiplyByScalar(scalar);
        
        assertTrue(expected.isMathematicallyEqualTo(result), "La moltiplicazione per scalare negativo non è corretta.");
    }

    @Test
    @DisplayName("Moltiplicazione per Scalare: Moltiplicazione per -1 (Identità Negativa)")
    void testMultiplyByScalar_NegativeOne() {
        SignedIntVector v = factory.createVector(new SignedInt[] { S5, S_N2 });
        SignedInt scalar = S_N1;
        
        SignedIntVector result = v.multiplyByScalar(scalar);
        
        assertTrue(v.negate().isMathematicallyEqualTo(result), "La moltiplicazione per -1 deve essere equivalente a negate().");
    }

    // ------------------- EGUAGLIANZA E UTILITY -------------------

    @Test
    @DisplayName("Eguaglianza: isMathematicallyEqualTo")
    void testIsMathematicallyEqualTo() {
        SignedIntVector v1 = factory.createVector(new SignedInt[] { S1, S_N1 });
        SignedIntVector v2 = factory.createVector(new SignedInt[] { S1, S_N1 });
        SignedIntVector v3 = factory.createVector(new SignedInt[] { S_N1, S1 });
        
        assertTrue(v1.isMathematicallyEqualTo(v2), "Vettori identici devono essere uguali.");
        assertFalse(v1.isMathematicallyEqualTo(v3), "Ordine diverso implica disuguaglianza.");
    }
    
    @Test
    @DisplayName("Eguaglianza: equals e hashCode (Java Standard)")
    void testEqualsAndHashCode() {
        SignedIntVector v1 = factory.createVector(new SignedInt[] { S2, S_N2, S0 });
        SignedIntVector v2 = factory.createVector(new SignedInt[] { S2, S_N2, S0 });
        
        assertTrue(v1.equals(v2), "Java equals deve essere true per oggetti uguali.");
        assertEquals(v1.hashCode(), v2.hashCode(), "L'hash code deve essere lo stesso per oggetti uguali.");
    }
}