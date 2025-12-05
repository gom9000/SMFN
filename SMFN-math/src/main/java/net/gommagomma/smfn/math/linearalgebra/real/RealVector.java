package net.gommagomma.smfn.math.linearalgebra.real;

import java.util.Arrays;
import java.util.List;

import net.gommagomma.smfn.math.algebra.numeric.Real;
import net.gommagomma.smfn.math.linearalgebra.core.elements.vectors.AbstractRank1Tensor;
import net.gommagomma.smfn.math.linearalgebra.core.elements.vectors.InnerProductSpaceElement;

public final class RealVector
extends AbstractRank1Tensor<Real, RealVector>
implements InnerProductSpaceElement<Real, RealVector>
{
	private final Real[] data;

    private static int validateAndGetLength(Real[] components) {
        if (components == null || components.length == 0) {
            throw new IllegalArgumentException("Components array cannot be null or empty.");
        }
        return components.length;
    }


	/**
	 * Costruttore principale per creare un RealVector da un array di componenti.
	 * @param components Un array di componenti Real. L'array viene copiato internamente per garantire l'immutabilità.
	 */
	public RealVector(Real... components) {
		super(validateAndGetLength(components));
        // Copia difensiva: garantisce che il vettore interno non sia modificabile dall'esterno
        this.data = Arrays.copyOf(components, components.length);
	}

	public RealVector(List<Real> components) {
        this(components.toArray(new Real[0]));
    }

	/**
	 * Costruttore per creare un vettore nullo di una data dimensione.
	 * @param dimension La dimensione del vettore.
	 */
	public RealVector(int dimension) {
		super(dimension);
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
	public boolean isMathematicallyEqualTo(RealVector other)
	{
		if (this == other) {
            return true;
        }

		if (other == null || this.data.length != other.data.length) {
            return false;
        }

		if (this.dimension != other.dimension) {
			return false;
		}

		for (int ii = 0; ii < dimension; ii++) {
			if (!this.data[ii].isMathematicallyEqualTo(other.data[ii])) {
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
	public Real get(int index) {
		return this.data[index];
	}


	// --- Java Standard impls ---

	@Override
	public String toString() {
		return "R^" + dimension + Arrays.toString(data);
	}


	@Override
	public final boolean equals(Object other)
	{
		if (this == other) {
            return true;
        }

        if (!(other instanceof RealVector)) {
            return false;
        }

        RealVector that = (RealVector) other;

        if (this.dimension != that.dimension) {
            return false;
        }

        return Arrays.equals(this.data, that.data);
	}


	@Override
	public final int hashCode() {
		return Arrays.hashCode(data);
	}
}
