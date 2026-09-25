package net.gommagomma.smfn.math.linearalgebra.matrices.square;

import java.util.ArrayList;
import java.util.List;

import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.algebra.core.elements.factories.NumericFactory;
import net.gommagomma.smfn.math.algebra.core.structures.Semiring;
import net.gommagomma.smfn.math.algebra.core.structures.composite.ScalarStructure;

/**
 * Fabbrica pura di SquareMatrix<K>: dati degli elementi, restituisce l'istanza.
 * Nessuna decisione algebrica qui dentro -- quella la fa SquareMatrixStructureFactory,
 * a cui questa classe si appoggia per sapere quale struttura usare.
 */
public final class SquareMatrixElementFactory
{
    private SquareMatrixElementFactory() {}

    /**
     * Crea una matrice quadrata da una lista di elementi.
     */
    public static <K extends ScalarElement<K>> SquareMatrix<K> of(ScalarStructure<K> s, List<K> elements) {
    	int totalElements = elements.size();
        int size = (int) Math.sqrt(totalElements);
        if (size * size != totalElements) {
            throw new IllegalArgumentException(
                "Il numero di elementi (" + totalElements + ") non permette di creare una matrice quadrata.");
        }
        ScalarStructure<SquareMatrix<K>> matrixStruct = SquareMatrixStructureFactory.getStructureFor(s, size);

        @SuppressWarnings("unchecked")
        K[] data = (K[]) elements.toArray(new ScalarElement[0]);

        if (matrixStruct instanceof SquareMatrixSemiring) {
            return ((SquareMatrixSemiring<K, ?>) matrixStruct).of(data);
        }
        throw new IllegalStateException("Struttura matrice non valida");
    }

    /**
     * Crea una matrice quadrata usando varargs (risolve il problema dei Complex della demo).
     */
    @SafeVarargs
    public static <K extends ScalarElement<K>> SquareMatrix<K> of(ScalarStructure<K> s, K... values) {
        return of(s, List.of(values));
    }

    /**
     * Crea una matrice quadrata da double (comodissimo per Real e Rational).
     */
    public static <K extends ScalarElement<K>> SquareMatrix<K> of(ScalarStructure<K> s, double... values) {
        if (!(s instanceof NumericFactory)) {
            throw new UnsupportedOperationException("La struttura non e' una NumericFactory");
        }
        @SuppressWarnings("unchecked")
        NumericFactory<K> factory = (NumericFactory<K>) s;
        List<K> elements = new ArrayList<>(values.length);
        for (double v : values) {
            elements.add(factory.of(v));
        }
        return of(s, elements);
    }

    public static <K extends ScalarElement<K>> SquareMatrix<K> identity(ScalarStructure<K> s, int size) {
        // Cast al livello comune (Semiring<SquareMatrix<K>>), non a SquareMatrixRing:
        // getStructureFor puo' restituire un SquareMatrixSemiring (se s e' solo un
        // Semiring), che NON e' un SquareMatrixRing -- il cast stretto lanciava
        // ClassCastException in quel caso. one() e' gia' disponibile al livello Semiring.
        Semiring<SquareMatrix<K>> structure = (Semiring<SquareMatrix<K>>) SquareMatrixStructureFactory.getStructureFor(s, size);
        return structure.one();
    }
}
