package net.gommagomma.smfn.math.linearalgebra.natural;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.math.algebra.numeric.Natural;

@DisplayName("NaturalMatrix: Test del Semianello Matrici su N")
public class NaturalMatrixTest {

    // Scalari di base
    private static Natural N0, N1, N2, N3, N4, N5, N7, N10;
    private static NaturalMatrixFactory factory;

    @BeforeAll
    static void setUp() {
        // Inizializzazione degli scalari Naturali
        factory = NaturalMatrixFactory.getInstance();
        N0 = factory.getScalarFactory().zero();
        N1 = new Natural(1);
        N2 = new Natural(2);
        N3 = new Natural(3);
        N4 = new Natural(4);
        N5 = new Natural(5);
        N7 = new Natural(7);
        N10 = new Natural(10);
    }

    // ------------------- CREAZIONE E COSTRUTTORI -------------------

    @Test
    @DisplayName("Factory: Creazione di una matrice da dati Natural[][]")
    void testFactory_CreateMatrix_Success() {
        Natural[][] data = {
            {N1, N2},
            {N3, N4}
        };
        NaturalMatrix M = factory.createMatrix(data);

        assertEquals(2, M.getRows(), "La matrice deve avere 2 righe.");
        assertEquals(2, M.getColumns(), "La matrice deve avere 2 colonne.");
        assertTrue(N4.isMathematicallyEqualTo(M.get(1, 1)), "L'elemento (1, 1) deve essere N4.");
    }

    @Test
    @DisplayName("Factory: Creazione di una matrice nulla (Zero Matrix)")
    void testFactory_CreateZeroMatrix() {
        NaturalMatrix Z = factory.createZeroMatrix(3, 2);

        assertEquals(3, Z.getRows(), "La matrice zero deve avere 3 righe.");
        assertEquals(2, Z.getColumns(), "La matrice zero deve avere 2 colonne.");
        assertTrue(N0.isMathematicallyEqualTo(Z.get(2, 1)), "Ogni elemento deve essere Natural.ZERO.");
    }
    
    @Test
    @DisplayName("Matrix: getZero() restituisce una matrice nulla delle stesse dimensioni")
    void testGetZero() {
        NaturalMatrix M = factory.createZeroMatrix(2, 3);
        NaturalMatrix Z = M.getZero();
        
        assertEquals(2, Z.getRows());
        assertEquals(3, Z.getColumns());
        assertTrue(Z.isMathematicallyEqualTo(M));
    }

    @Test
    @DisplayName("Factory: Creazione di una matrice da double[][] (conversione e arrotondamento)")
    void testFactory_CreateMatrixFromDouble() {
        double[][] doubleData = {
            {1.1, 2.9},
            {4.0, 0.5}
        };
        // Arrotondamento atteso per Natural: 1.1->1, 2.9->3, 4.0->4, 0.5->1
        NaturalMatrix M = factory.createMatrix(doubleData);
        
        NaturalMatrix expected = factory.createMatrix(new Natural[][] {
            {N1, N3},
            {N4, N1}
        });
        
        // Questo test ora funziona grazie all'override del metodo createMatrix(double[][]) nella Factory
        assertTrue(expected.isMathematicallyEqualTo(M), "La conversione da double deve arrotondare correttamente.");
    }

    @Test
    @DisplayName("Costruttore: Eccezione per dati nulli o non validi")
    void testConstructor_InvalidData() {
        // Matrice con 0 righe
        assertThrows(IllegalArgumentException.class, () -> factory.createMatrix(new Natural[0][0]), 
                     "Deve fallire per 0 righe.");
        // Matrice con colonne non coerenti (gestito nell'AbstractSemiringMatrix)
        Natural[][] invalidData = {
            {N1, N2},
            {N3} // riga più corta
        };
        assertThrows(IllegalArgumentException.class, () -> factory.createMatrix(invalidData), 
                     "Deve fallire per righe di lunghezza diversa.");
    }


    // ------------------- ACCESSO A ELEMENTI E VETTORI -------------------

    @Test
    @DisplayName("Accesso: get(row, col) per lo scalare")
    void testGetScalar() {
        NaturalMatrix M = factory.createMatrix(new Natural[][] {
            {N1, N2},
            {N3, N4}
        });
        
        assertTrue(N2.isMathematicallyEqualTo(M.get(0, 1)));
        assertThrows(IndexOutOfBoundsException.class, () -> M.get(2, 0), "Indice di riga fuori dai limiti.");
        assertThrows(IndexOutOfBoundsException.class, () -> M.get(0, 2), "Indice di colonna fuori dai limiti.");
    }

    @Test
    @DisplayName("Accesso: getRowVector() per un vettore riga")
    void testGetRowVector() {
        NaturalMatrix M = factory.createMatrix(new Natural[][] {
            {N1, N2, N3},
            {N4, N5, N10}
        });
        
        // CORREZIONE: Uso l'array esplicito per creare il vettore
        NaturalVector expectedRow1 = NaturalVectorFactory.getInstance().createVector(new Natural[] {N4, N5, N10});
        
        NaturalVector row = M.getRowVector(1);
        
        assertTrue(expectedRow1.isMathematicallyEqualTo(row), "Il vettore riga estratto non è corretto.");
        assertEquals(3, row.dimension());
    }

    @Test
    @DisplayName("Accesso: getColumnVector() per un vettore colonna")
    void testGetColumnVector() {
        NaturalMatrix M = factory.createMatrix(new Natural[][] {
            {N1, N2},
            {N3, N4},
            {N5, N10}
        });
        
        // CORREZIONE: Uso l'array esplicito per creare il vettore
        NaturalVector expectedCol1 = NaturalVectorFactory.getInstance().createVector(new Natural[] {N2, N4, N10});
        
        NaturalVector col = M.getColumnVector(1);
        
        assertTrue(expectedCol1.isMathematicallyEqualTo(col), "Il vettore colonna estratto non è corretto.");
        assertEquals(3, col.dimension());
    }


    // ------------------- OPERAZIONI ALGEBRICHE -------------------

    @Test
    @DisplayName("Addizione: Somma di due matrici (A + B)")
    void testAdd() {
        NaturalMatrix A = factory.createMatrix(new Natural[][] {{N1, N2}, {N3, N4}});
        NaturalMatrix B = factory.createMatrix(new Natural[][] {{N2, N3}, {N4, N5}});
        // Risultato atteso: {{3, 5}, {7, 9}}
        NaturalMatrix expected = factory.createMatrix(new Natural[][] {{N3, N5}, {N7, new Natural(9)}});
        
        NaturalMatrix result = A.add(B);
        
        assertTrue(expected.isMathematicallyEqualTo(result), "L'addizione di matrici non è corretta.");
    }

    @Test
    @DisplayName("Moltiplicazione per Scalare: Prodotto c * A")
    void testMultiplyByScalar() {
        NaturalMatrix A = factory.createMatrix(new Natural[][] {{N1, N2}, {N3, N4}});
        Natural scalar = N3; // Scalare 3
        // Risultato atteso: {{3, 6}, {9, 12}}
        NaturalMatrix expected = factory.createMatrix(new Natural[][] {{N3, new Natural(6)}, {new Natural(9), new Natural(12)}});
        
        NaturalMatrix result = A.multiplyByScalar(scalar);
        
        assertTrue(expected.isMathematicallyEqualTo(result), "La moltiplicazione per scalare non è corretta.");
    }

    @Test
    @DisplayName("Moltiplicazione Matrice-Matrice: A * B")
    void testMultiply_Success() {
        NaturalMatrix A = factory.createMatrix(new Natural[][] { // 2x2
            {N1, N2},
            {N3, N0}
        });
        NaturalMatrix B = factory.createMatrix(new Natural[][] { // 2x2
            {N1, N1},
            {N2, N3}
        });
        // C[0][0] = (1*1) + (2*2) = 5
        // C[0][1] = (1*1) + (2*3) = 7
        // C[1][0] = (3*1) + (0*2) = 3
        // C[1][1] = (3*1) + (0*3) = 3
        NaturalMatrix expected = factory.createMatrix(new Natural[][] {
            {N5, N7},
            {N3, N3}
        });
        
        NaturalMatrix result = A.multiply(B);
        
        assertTrue(expected.isMathematicallyEqualTo(result), "La moltiplicazione A * B non è corretta.");
    }

    @Test
    @DisplayName("Moltiplicazione Matrice-Matrice: Eccezione per dimensioni non conformi")
    void testMultiply_DimensionMismatch() {
        NaturalMatrix A = factory.createMatrix(new Natural[][] {{N1, N2}}); // 1x2
        NaturalMatrix B = factory.createMatrix(new Natural[][] {{N1, N2, N3}, {N4, N5, N10}}); // 2x3 (conforme)
        NaturalMatrix C = factory.createMatrix(new Natural[][] {{N1}, {N2}, {N3}}); // 3x1
        
        // A (1x2) * C (3x1) => Non conforme (2 != 3)
        assertThrows(IllegalArgumentException.class, () -> A.multiply(C), 
                     "La moltiplicazione deve fallire per dimensioni interne diverse.");
        
        // A (1x2) * B (2x3) => Conforme (risultato 1x3)
        assertDoesNotThrow(() -> A.multiply(B));
    }
    
    @Test
    @DisplayName("Trasposta: Calcolo corretto della matrice trasposta")
    void testTranspose() {
        NaturalMatrix M = factory.createMatrix(new Natural[][] { // 2x3
            {N1, N2, N3},
            {N4, N5, N10}
        });
        
        NaturalMatrix expected = factory.createMatrix(new Natural[][] { // 3x2
            {N1, N4},
            {N2, N5},
            {N3, N10}
        });
        
        NaturalMatrix result = M.transpose();
        
        assertTrue(expected.isMathematicallyEqualTo(result), "La trasposta non è corretta.");
        assertEquals(3, result.getRows());
        assertEquals(2, result.getColumns());
    }

    // ------------------- EGUAGLIANZA E UTILITY -------------------

    @Test
    @DisplayName("Eguaglianza: isMathematicallyEqualTo e copy()")
    void testIsMathematicallyEqualTo_And_Copy() {
        NaturalMatrix M1 = factory.createMatrix(new Natural[][] {{N1, N2}, {N3, N4}});
        NaturalMatrix M2 = factory.createMatrix(new Natural[][] {{N1, N2}, {N3, N4}});
        NaturalMatrix M3 = factory.createMatrix(new Natural[][] {{N4, N3}, {N2, N1}});
        
        // Copia
        NaturalMatrix M_copy = M1.copy();

        assertTrue(M1.isMathematicallyEqualTo(M2), "Matrici con stessi valori devono essere uguali.");
        assertTrue(M1.isMathematicallyEqualTo(M_copy), "Una copia deve essere uguale all'originale.");
        assertFalse(M1.isMathematicallyEqualTo(M3), "Matrici con valori diversi devono essere diverse.");
        assertFalse(M1.isMathematicallyEqualTo(null), "Confronto con null.");
    }
    
    @Test
    @DisplayName("Eguaglianza: equals e hashCode (Java Standard)")
    void testEqualsAndHashCode() {
        NaturalMatrix M1 = factory.createMatrix(new Natural[][] {{N1, N2}, {N3, N4}});
        NaturalMatrix M2 = factory.createMatrix(new Natural[][] {{N1, N2}, {N3, N4}});
        NaturalMatrix M3 = factory.createMatrix(new Natural[][] {{N4, N3}, {N2, N1}});
        
        // equals
        assertTrue(M1.equals(M2), "Java equals deve essere true per oggetti uguali.");
        assertFalse(M1.equals(M3), "Java equals deve essere false per oggetti diversi.");
        
        // hashCode
        assertEquals(M1.hashCode(), M2.hashCode(), "L'hash code deve essere lo stesso per oggetti uguali.");
    }

    @Test
    @DisplayName("Utility: toString()")
    void testToString() {
        NaturalMatrix M = factory.createMatrix(new Natural[][] {{N1, N2}, {N3, N4}});
        String expectedStart = "2x2 Matrix:\n";
        String expectedRow1 = "[1, 2]\n";
        
        String result = M.toString();
        
        assertTrue(result.startsWith(expectedStart));
        assertTrue(result.contains(expectedRow1));
    }
}