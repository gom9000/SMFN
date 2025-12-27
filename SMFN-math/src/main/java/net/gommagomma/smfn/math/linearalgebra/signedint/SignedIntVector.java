package net.gommagomma.smfn.math.linearalgebra.signedint;

import java.util.Arrays;

import net.gommagomma.smfn.math.algebra.numeric.SignedInt;
import net.gommagomma.smfn.math.linearalgebra.core.elements.vectors.AbstractRank1Tensor;
import net.gommagomma.smfn.math.linearalgebra.core.elements.vectors.ModuleElement;

public final class SignedIntVector
extends AbstractRank1Tensor<SignedInt, SignedIntVector>
implements ModuleElement<SignedInt, SignedIntVector>
{
    private final SignedInt[] data;


    // helper
    private static int validateAndGetLength(SignedInt[] components) {
        if (components == null || components.length == 0) {
            throw new IllegalArgumentException("Components array cannot be null or empty.");
        }
        return components.length;
    }


    SignedIntVector(SignedInt... components) {
    	super(validateAndGetLength(components));
        this.data = Arrays.copyOf(components, components.length);
    }

    SignedIntVector(int dimension) {
    	super(dimension);
        this.data = new SignedInt[dimension];
        Arrays.fill(this.data, getZero());
    }


    @Override // AlgebraicElement impls
    public boolean isMathematicallyEqualTo(SignedIntVector other)
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
    public SignedIntVector copy() { return new SignedIntVector(this.data); }


    @Override // AdditiveMonoidElement impls
    public SignedIntVector add(SignedIntVector other) {
        if (this.dimension != other.dimension) {
            throw new IllegalArgumentException("Vectors must have the same dimension to add.");
        }
        SignedInt[] resultData = new SignedInt[dimension];
        for (int i = 0; i < dimension; i++) {
            resultData[i] = this.data[i].add(other.data[i]);
        }
        return new SignedIntVector(resultData);
    }

	@Override // AdditiveMonoidElement impls
	public SignedIntVector getZero() {
		return new SignedIntVector(dimension);
	}

    
    @Override // GroupElement impls
    public SignedIntVector negate() {
        SignedInt[] negatedData = new SignedInt[dimension];
        for (int i = 0; i < dimension; i++) {
            negatedData[i] = this.data[i].negate();
        }
        return new SignedIntVector(negatedData);
    }


    @Override // SemimoduleElement impls
    public SignedIntVector scale(SignedInt scalar) {
        SignedInt[] scaledData = new SignedInt[dimension];
        for (int i = 0; i < dimension; i++) {
            scaledData[i] = this.data[i].multiply(scalar);
        }
        return new SignedIntVector(scaledData);
    }

    @Override // SemimoduleElement impls
    public SignedInt get(int index) {
        return this.data[index];
    }


    @Override // Java Standard impls
    public String toString() {
        return "Z^" + dimension + Arrays.toString(data);
    }
    
    @Override // Java Standard impls
    public final boolean equals(Object other)
    {
    	if (this == other) {
            return true;
        }

        if (!(other instanceof SignedIntVector)) {
            return false;
        }

        SignedIntVector that = (SignedIntVector) other;

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