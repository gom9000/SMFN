package net.gommagomma.smfn.math.linearalgebra.natural;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.math.algebra.numeric.Natural;

@DisplayName("NaturalVector: Test del Semimodulo su N")
public class NaturalVectorTest {

    private static Natural N1, N2, N3, N5, N_ZERO;
    private static NaturalVectorFactory factory;

    @BeforeAll
    static void setUp() {
        // Inizializzazione della Factory e degli scalari
        factory = NaturalVectorFactory.getInstance();
        
        N_ZERO = factory.getScalarFactory().zero(); // Prende lo zero dalla Factory
        N1 = new Natural(1);
        N2 = new Natural(2);
        N3 = new Natural(3);
        N5 = new Natural(5);
    }

    // ------------------- COSTRUTTORI / FACTORY -------------------

    @Test
    @DisplayName("Factory: Creazione corretta da array di Natural (K[])")
    void testFactory_CreateVectorFromComponents() {
        Natural[] data = new Natural[] { N1, N2, N3 };
        NaturalVector v = factory.createVector(data);
        
        assertEquals(3, v.dimension(), "La dimensione deve essere 3.");
        assertEquals(N1, v.get(0), "Il primo elemento deve essere N1.");
        assertEquals(N3, v.get(2), "L'ultimo elemento deve essere N3.");
    }

    @Test
    @DisplayName("Factory: Creazione corretta di vettore nullo (int dimension)")
    void testFactory_CreateZeroVector() {
        NaturalVector v = factory.createVector(4);
        
        assertEquals(4, v.dimension(), "La dimensione deve essere 4.");
        // Verifica che ogni componente sia lo zero del Semiring (Natural.ZERO)
        for (int i = 0; i < 4; i++) {
            assertEquals(N_ZERO, v.get(i), "Ogni elemento deve essere Natural.ZERO.");
        }
    }

    @Test
    @DisplayName("Factory: Conversione da double array (default method in SemimoduleVectorFactory)")
    void testFactory_CreateVectorFromDouble() {
        // La Factory dovrebbe usare NaturalFactory.fromDouble(value) che arrotonda
        NaturalVector v = factory.createVector(1.1, 2.9, 3.5); // Arrotondati a [1, 3, 4]
        
        NaturalVector expected = new NaturalVector(N1, N3, new Natural(4));
        
        assertEquals(3, v.dimension(), "La dimensione deve essere 3.");
        assertTrue(expected.isMathematicallyEqualTo(v), "La conversione da double deve usare Math.round().");
    }

    @Test
    @DisplayName("Constructor: Eccezione per array di componenti nullo o vuoto")
    void testConstructor_InvalidComponents() {
        // Questi test verificano la logica interna di NaturalVector.validateAndGetLength
        assertThrows(IllegalArgumentException.class, () -> new NaturalVector((Natural[]) null), 
                     "Deve lanciare eccezione se l'array è null.");
        assertThrows(IllegalArgumentException.class, () -> new NaturalVector(new Natural[0]), 
                     "Deve lanciare eccezione se l'array è vuoto.");
    }
    
    // ------------------- OPERAZIONI ALGEBRICHE (Semimodulo) -------------------

    @Test
    @DisplayName("Addizione: Somma di due vettori")
    void testAdd_Success() {
        NaturalVector v1 = factory.createVector(new Natural[] { N1, N2 }); // [1, 2]
        NaturalVector v2 = factory.createVector(new Natural[] { N3, N5 }); // [3, 5]
        NaturalVector expected = new NaturalVector(new Natural(4), new Natural(7)); // [4, 7]
        
        NaturalVector result = v1.add(v2);
        
        assertTrue(expected.isMathematicallyEqualTo(result), "La somma vettoriale non è corretta.");
    }
    
    @Test
    @DisplayName("Addizione: Somma con il vettore nullo (Identità Additiva)")
    void testAdd_ZeroIdentity() {
        NaturalVector v = factory.createVector(new Natural[] { N5, N3 });
        NaturalVector zero = v.getZero(); // Deve avere la stessa dimensione
        
        NaturalVector result = v.add(zero);
        
        assertTrue(v.isMathematicallyEqualTo(result), "La somma con lo zero deve lasciare il vettore invariato.");
    }

    @Test
    @DisplayName("Moltiplicazione per Scalare: Prodotto corretto")
    void testMultiplyByScalar_Success() {
        NaturalVector v = factory.createVector(new Natural[] { N2, N3 }); // [2, 3]
        Natural scalar = N2; // Scalare 2
        NaturalVector expected = new NaturalVector(new Natural(4), new Natural(6)); // [4, 6]
        
        NaturalVector result = v.multiplyByScalar(scalar);
        
        assertTrue(expected.isMathematicallyEqualTo(result), "La moltiplicazione per scalare non è corretta.");
    }
    
    @Test
    @DisplayName("Moltiplicazione per Scalare: Moltiplicazione per ZERO (Assorbitore)")
    void testMultiplyByScalar_ZeroScalar() {
        NaturalVector v = factory.createVector(new Natural[] { N5, N5 });
        NaturalVector result = v.multiplyByScalar(N_ZERO);
        
        // Verifica che il risultato sia il vettore nullo della stessa dimensione
        assertTrue(v.getZero().isMathematicallyEqualTo(result), "La moltiplicazione per 0 deve dare il vettore nullo.");
    }

    // ------------------- GETTER & EGUAGLIANZA -------------------

    @Test
    @DisplayName("Get: Accesso all'elemento per indice")
    void testGet_Success() {
        NaturalVector v = factory.createVector(new Natural[] { N1, N2, N3 });
        
        assertEquals(N2, v.get(1), "L'elemento all'indice 1 deve essere 2.");
    }
    
    @Test
    @DisplayName("Eguaglianza: isMathematicallyEqualTo")
    void testIsMathematicallyEqualTo() {
        NaturalVector v1 = factory.createVector(new Natural[] { N1, N2 });
        NaturalVector v2 = factory.createVector(new Natural[] { N1, N2 });
        NaturalVector v3 = factory.createVector(new Natural[] { N2, N1 }); 
        
        assertTrue(v1.isMathematicallyEqualTo(v2), "Vettori identici devono essere uguali.");
        assertFalse(v1.isMathematicallyEqualTo(v3), "Ordine diverso implica disuguaglianza.");
        assertFalse(v1.isMathematicallyEqualTo(null), "Confronto con null.");
    }

    @Test
    @DisplayName("Eguaglianza: equals e hashCode (Java Standard)")
    void testEqualsAndHashCode() {
        NaturalVector v1 = factory.createVector(new Natural[] { N1, N2, N3 });
        NaturalVector v2 = factory.createVector(new Natural[] { N1, N2, N3 });
        NaturalVector v3 = factory.createVector(new Natural[] { N2, N1, N3 });
        
        // equals
        assertTrue(v1.equals(v2), "Java equals deve essere true per oggetti uguali.");
        assertFalse(v1.equals(v3), "Java equals deve essere false per oggetti diversi.");
        
        // hashCode
        assertEquals(v1.hashCode(), v2.hashCode(), "L'hash code deve essere lo stesso per oggetti uguali.");
    }
}