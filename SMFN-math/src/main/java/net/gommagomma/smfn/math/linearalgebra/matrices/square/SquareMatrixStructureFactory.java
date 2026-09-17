package net.gommagomma.smfn.math.linearalgebra.matrices.square;

import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.algebra.core.structures.Field;
import net.gommagomma.smfn.math.algebra.core.structures.Ring;
import net.gommagomma.smfn.math.algebra.core.structures.Semiring;
import net.gommagomma.smfn.math.algebra.core.structures.composite.ScalarStructure;

/**
 * Data una struttura scalare S, decide qual e' la struttura di matrici
 * quadrate piu' ricca costruibile sopra di essa (Semiring, Ring o Algebra
 * a seconda di cosa S garantisce algebricamente).
 *
 * A differenza di PolynomialStructureFactory, qui non c'e' un ramo
 * CommutativeRing: anche se lo scalare commuta, il prodotto tra matrici
 * quadrate non commuta in generale (n >= 2) -- promuovere a un ipotetico
 * "CommutativeSquareMatrixRing" sarebbe disonesto quanto lo era, prima
 * della correzione, dichiarare Field su SquareMatrixAlgebra.
 */
public final class SquareMatrixStructureFactory
{
    private SquareMatrixStructureFactory() {}

    @SuppressWarnings("unchecked")
    public static <K extends ScalarElement<K>> ScalarStructure<SquareMatrix<K>> getStructureFor(ScalarStructure<K> s, int size) {
        if (s instanceof Field) {
            return new SquareMatrixAlgebra<>((Field<K> & ScalarStructure<K>) s, size);
        }
        if (s instanceof Ring) {
            return new SquareMatrixRing<>((Ring<K> & ScalarStructure<K>) s, size);
        }
        return new SquareMatrixSemiring<>((Semiring<K> & ScalarStructure<K>) s, size);
    }
}
