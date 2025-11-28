package net.gommagomma.smfn.math.linearalgebra.real;

import java.util.Arrays;
import java.util.List;

import net.gommagomma.smfn.math.algebra.numeric.Real;
import net.gommagomma.smfn.math.linearalgebra.core.InnerProductSpaceElement;

public final class RealVector
implements InnerProductSpaceElement<Real, RealVector>
{
	private final Real[] data;
	private final int dimension;

	/**
	 * Costruttore principale per creare un RealVector da un array di componenti.
	 * @param components Un array di componenti Real. L'array viene copiato internamente per garantire l'immutabilità.
	 */
	public RealVector(Real... components) {
		if (components == null || components.length == 0) {
			throw new IllegalArgumentException("Components cannot be null or empty.");
		}
		// Copia difensiva: garantisce che il vettore interno non sia modificabile dall'esterno
		this.data = Arrays.copyOf(components, components.length); 
		this.dimension = components.length;
	}

	public RealVector(List<Real> components) {
        this(components.toArray(new Real[0]));
    }

	/**
	 * Costruttore per creare un vettore nullo di una data dimensione.
	 * @param dimension La dimensione del vettore.
	 */
	public RealVector(int dimension) {
		if (dimension <= 0) {
			throw new IllegalArgumentException("Dimension must be positive.");
		}
		this.dimension = dimension;
		this.data = new Real[dimension];
		Arrays.fill(this.data, Real.ZERO);
	}

	/**
	 * Helper statico per creare vettori da array di double primitivi.
	 * @param data L'array di double.
	 * @return Una nuova istanza di RealVector.
	 */
	public static RealVector fromDoubles(double... data) {
		Real[] realComponents = Arrays.stream(data)
				.mapToObj(Real::new) 
				.toArray(Real[]::new);
		return new RealVector(realComponents);
	}

	/**
	 * Converte una lista in un array per uniformità con il costruttore principale.
	 */
	public static RealVector fromList(List<Real> components) {
		return new RealVector(components.toArray(new Real[0]));
	}

	@Override
    public RealVector createNewInstance(Real... components) {
        return new RealVector(components);
    }

	// --- Implementazione di AlgebraicElement e AdditiveMonoidElement ---

	@Override
	public boolean isEqual(RealVector other) {
		// Usa il metodo isEqual() sensibile all'epsilon della classe Real per ogni elemento
		if (this.dimension != other.dimension) return false;
		for (int i = 0; i < dimension; i++) {
			if (!this.data[i].isEqual(other.data[i])) {
				return false;
			}
		}
		return true;
	}

	@Override
	public RealVector copy() {
		return new RealVector(this.data); 
	}

	@Override
	public RealVector getZero()
	{
		// Restituisce un nuovo vettore nullo della stessa dimensione
		return new RealVector(dimension);
	}


	// --- Implementazione di AbelianGroupElement (add, negate) ---

	@Override
	public RealVector add(RealVector other) {
		if (this.dimension != other.dimension) {
			throw new IllegalArgumentException("Vectors must have the same dimension to add.");
		}
		Real[] resultData = new Real[dimension];
		for (int i = 0; i < dimension; i++) {
			resultData[i] = this.data[i].add(other.data[i]);
		}
		return new RealVector(resultData);
	}

	@Override
	public RealVector negate() {
		Real[] negatedData = new Real[dimension];
		for (int i = 0; i < dimension; i++) {
			negatedData[i] = this.data[i].negate();
		}
		return new RealVector(negatedData);
	}


	// VectorElement impls

	@Override
	public RealVector multiplyByScalar(Real scalar) {
		Real[] scaledData = new Real[dimension];
		for (int i = 0; i < dimension; i++) {
			scaledData[i] = this.data[i].multiply(scalar);
		}
		return new RealVector(scaledData);
	}

	// --- Implementazione di Normable<Real, RealVector> (norm) ---

	@Override
	public Real dotProduct(RealVector other) {
		if (this.dimension != other.dimension) {
			throw new IllegalArgumentException("Vectors must have the same dimension for dot product.");
		}
		Real result = Real.ZERO;
		for (int i = 0; i < dimension; i++) {
			result = result.add(this.get(i).multiply(other.get(i)));
		}
		return result;
	}

	@Override
	public Real norm() {
		return this.dotProduct(this).sqrt(); 
	}

	// --- Implementazione di SpaceElement/VectorElement (utilità) ---

	@Override
	public int dimension() {
		return this.dimension;
	}

	@Override
	public Real get(int index) {
		return this.data[index];
	}


	// --- Java Standard impls ---

	@Override
	public String toString() {
		return "R^" + dimension + Arrays.toString(data);
	}

	/**
	 * WARNING: This equals method uses epsilon comparisons via Real.isEqual,
	 * violating the strict transitivity contract of Object.equals() in standard Java collections.
	 */
	@Override
	public final boolean equals(Object other) {
		return (other instanceof RealVector) && isEqual((RealVector)other);
	}

	/**
	 * Hash code consistent with the epsilon-based equals, but uses exact double values internally.
	 */
	@Override
	public final int hashCode() {
		return Arrays.hashCode(data);
	}
}
