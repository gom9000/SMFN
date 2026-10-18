package net.gommagomma.smfn.math.linearalgebra.matrices;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.algebra.core.elements.factories.NumericFactory;
import net.gommagomma.smfn.math.algebra.core.structures.Semiring;
import net.gommagomma.smfn.math.algebra.core.structures.composite.ScalarStructure;

/**
 * Fabbrica pura di Matrix<K>: dati gli elementi, restituisce l'istanza, con
 * le stesse tre varianti gia' offerte da VectorElementFactory,
 * SquareMatrixElementFactory e PolynomialElementFactory.
 *
 * Come per Vector, per Matrix e' sempre il chiamante a scegliere il livello
 * di struttura (Semimodule/Module/Space/...): qui la struttura gia'
 * costruita si passa esplicitamente, non solo lo scalare di base.
 */
public final class MatrixElementFactory
{
	private MatrixElementFactory() {}

	public static <K extends ScalarElement<K>, S extends Semiring<K> & ScalarStructure<K>>
	Matrix<K> of(MatrixSemimodule<K, S> space, List<K> elements) {
		@SuppressWarnings("unchecked")
		K[] data = (K[]) elements.toArray(new ScalarElement<?>[0]);
		return space.of(data);
	}

	@SafeVarargs
	public static <K extends ScalarElement<K>, S extends Semiring<K> & ScalarStructure<K>>
	Matrix<K> of(MatrixSemimodule<K, S> space, K... values) {
		List<K> list = new ArrayList<>(values.length);
		for (K value : values) {
			list.add(Objects.requireNonNull(value));
		}
		return of(space, list);
	}

	public static <K extends ScalarElement<K>, S extends Semiring<K> & ScalarStructure<K>>
	Matrix<K> of(MatrixSemimodule<K, S> space, double... values) {
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
