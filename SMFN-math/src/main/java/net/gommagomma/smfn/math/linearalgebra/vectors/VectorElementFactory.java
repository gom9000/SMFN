package net.gommagomma.smfn.math.linearalgebra.vectors;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.algebra.core.elements.factories.NumericFactory;
import net.gommagomma.smfn.math.algebra.core.structures.Semiring;
import net.gommagomma.smfn.math.algebra.core.structures.composite.ScalarStructure;

/**
 * Fabbrica pura di Vector<K>: dati gli elementi, restituisce l'istanza, con
 * le stesse tre varianti gia' offerte da SquareMatrixElementFactory e
 * PolynomialElementFactory (List, varargs K, varargs double) -- l'array
 * puro non basta piu' come unica via, come succedeva prima.
 *
 * A differenza di Polynomial/SquareMatrix, per Vector non esiste una
 * "struttura canonica" derivabile automaticamente da K: e' sempre il
 * chiamante a scegliere il livello (Semimodule/Module/Space/...), quindi
 * qui la struttura gia' costruita si passa esplicitamente, non solo lo
 * scalare di base.
 */
public final class VectorElementFactory
{
	private VectorElementFactory() {}

	public static <K extends ScalarElement<K>, S extends Semiring<K> & ScalarStructure<K>>
	Vector<K> of(VectorSemimodule<K, S> space, List<K> elements) {
		@SuppressWarnings("unchecked")
		K[] data = (K[]) elements.toArray(new ScalarElement<?>[0]);
		return space.of(data);
	}

	@SafeVarargs
	public static <K extends ScalarElement<K>, S extends Semiring<K> & ScalarStructure<K>>
	Vector<K> of(VectorSemimodule<K, S> space, K... values) {
		List<K> list = new ArrayList<>(values.length);
		for (K value : values) {
			list.add(Objects.requireNonNull(value));
		}
		return of(space, list);
	}

	public static <K extends ScalarElement<K>, S extends Semiring<K> & ScalarStructure<K>>
	Vector<K> of(VectorSemimodule<K, S> space, double... values) {
		if (!(space.getScalarStructure() instanceof NumericFactory)) {
			throw new UnsupportedOperationException("The scalar structure is not a NumericFactory");
		}
		@SuppressWarnings("unchecked")
		NumericFactory<K> factory = (NumericFactory<K>) space.getScalarStructure();
		List<K> elements = new ArrayList<>(values.length);
		for (double v : values) {
			elements.add(factory.of(v));
		}
		return of(space, elements);
	}
}
