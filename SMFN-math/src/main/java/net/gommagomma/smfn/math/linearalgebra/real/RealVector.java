package net.gommagomma.smfn.math.linearalgebra.real;

import java.util.Arrays;

import net.gommagomma.smfn.math.algebra.numeric.Real;
import net.gommagomma.smfn.math.algebra.structures.RealField;
import net.gommagomma.smfn.math.linearalgebra.core.elements.vectors.AbstractRank1Tensor;
import net.gommagomma.smfn.math.linearalgebra.core.elements.vectors.InnerProductSpaceElement;

public final class RealVector
extends AbstractRank1Tensor<Real, RealVector>
implements InnerProductSpaceElement<Real, RealVector>
{
	private final Real[] data;


	// helper
    private static int validateAndGetLength(Real[] components) {
        if (components == null || components.length == 0) {
            throw new IllegalArgumentException("Components array cannot be null or empty.");
        }
        return components.length;
    }


	public RealVector(Real... components) {
		super(validateAndGetLength(components));
        this.data = Arrays.copyOf(components, components.length);
	}

	public RealVector(int dimension) {
		super(dimension);
		this.data = new Real[dimension];
		Arrays.fill(this.data, getZero());
	}


	@Override // AlgebraicElement impls
	public boolean isMathematicallyEqualTo(RealVector other)
	{
		if (this == other) {
            return true;
        }

		if (other == null || this.data.length != other.data.length) {
            return false;
        }

		for (int ii = 0; ii < dimension; ii++) {
			if (!this.data[ii].isMathematicallyEqualTo(other.data[ii])) {
				return false;
			}
		}
		return true;
	}

	@Override // AlgebraicElement impls
	public RealVector copy() {
		return new RealVector(this.data); 
	}


	@Override // AdditiveMonoidElement impls
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

	@Override // AdditiveMonoidElement impls
	public RealVector getZero()
	{
		return new RealVector(dimension);
	}


	@Override // GroupElement impls
	public RealVector negate() {
		Real[] negatedData = new Real[dimension];
		for (int i = 0; i < dimension; i++) {
			negatedData[i] = this.data[i].negate();
		}
		return new RealVector(negatedData);
	}


	@Override // SemimoduleElement impls
	public RealVector multiplyByScalar(Real scalar) {
		Real[] scaledData = new Real[dimension];
		for (int i = 0; i < dimension; i++) {
			scaledData[i] = this.data[i].multiply(scalar);
		}
		return new RealVector(scaledData);
	}

	@Override // SemimoduleElement impls
	public Real get(int index) {
		return this.data[index];
	}


	@Override // InnerProductSpaceElement impls
	public Real dotProduct(RealVector other) {
		if (this.dimension != other.dimension) {
			throw new IllegalArgumentException("Vectors must have the same dimension for dot product.");
		}
		Real result = RealField.getInstance().zero();
		for (int i = 0; i < dimension; i++) {
			result = result.add(this.get(i).multiply(other.get(i)));
		}
		return result;
	}

	@Override // NormableElement impls
	public Real norm() {
		return this.dotProduct(this).sqrt(); 
	}
	

	@Override // Java Standard impls
	public String toString() {
		return "R^" + dimension + Arrays.toString(data);
	}

	@Override // Java Standard impls
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

	@Override // Java Standard impls
	public final int hashCode() {
		return Arrays.hashCode(data);
	}
}
