package net.gommagomma.smfn.math.linearalgebra.matrices.square;

import java.util.ArrayList;
import java.util.List;

import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.algebra.core.elements.factories.NumericFactory;
import net.gommagomma.smfn.math.algebra.core.structures.Field;
import net.gommagomma.smfn.math.algebra.core.structures.Ring;
import net.gommagomma.smfn.math.algebra.core.structures.Semiring;
import net.gommagomma.smfn.math.algebra.core.structures.composite.ScalarStructure;

public final class SquareMatrices
{
    private SquareMatrices() {}

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
        ScalarStructure<SquareMatrix<K>> matrixStruct = getStructureFor(s, size);
        
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
    public static <K extends ScalarElement<K>> SquareMatrix<K> of(ScalarStructure<K> s, int size, double... values) {
        if (!(s instanceof NumericFactory)) {
            throw new UnsupportedOperationException("La struttura non è una NumericFactory");
        }
        @SuppressWarnings("unchecked")
        NumericFactory<K> factory = (NumericFactory<K>) s;
        List<K> elements = new ArrayList<>(values.length);
        for (double v : values) {
            elements.add(factory.of(v));
        }
        return of(s, elements);
    }

    /**
     * Identifica la struttura algebrica corretta per la matrice quadrata.
     */
    @SuppressWarnings("unchecked")
    public static <K extends ScalarElement<K>> ScalarStructure<SquareMatrix<K>> getStructureFor(ScalarStructure<K> s, int size) {
        if (s instanceof Field) {
            return new SquareMatrixField<>((Field<K> & ScalarStructure<K>) s, size);
        }
        if (s instanceof Ring) {
            return new SquareMatrixRing<>((Ring<K> & ScalarStructure<K>) s, size);
        }
        return new SquareMatrixSemiring<>((Semiring<K> & ScalarStructure<K>) s, size);
    }

    public static <K extends ScalarElement<K>> SquareMatrix<K> identity(ScalarStructure<K> s, int size) {
        SquareMatrixRing<K, ?> ring = (SquareMatrixRing<K, ?>) getStructureFor(s, size);
        return ring.one();
    }
}
