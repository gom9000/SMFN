package net.gommagomma.smfn.math.linearalgebra.natural;

import java.util.Arrays;

import net.gommagomma.smfn.math.algebra.numeric.Natural;
import net.gommagomma.smfn.math.linearalgebra.core.elements.vectors.AbstractRank1Tensor;

public final class NaturalVector
extends AbstractRank1Tensor<Natural, NaturalVector>
{
    private final Natural[] data;


    // helper
    private static int validateAndGetLength(Natural[] components) {
        if (components == null || components.length == 0) {
            throw new IllegalArgumentException("Components array cannot be null or empty.");
        }
        return components.length;
    }


    NaturalVector(Natural... components) {
    	super(validateAndGetLength(components));
        this.data = Arrays.copyOf(components, components.length);
    }

    NaturalVector(int dimension) {
    	super(dimension);
    	this.data = new Natural[dimension];
    	Arrays.fill(this.data, getZero());
    }


    @Override // AlgebraicElement impls
    public boolean isMathematicallyEqualTo(NaturalVector other)
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
    public NaturalVector copy() { return new NaturalVector(this.data); }


    @Override // AdditiveMonoidElement impls
    public NaturalVector add(NaturalVector other) {
        if (this.dimension != other.dimension) throw new IllegalArgumentException("Dimensions must match");
        Natural[] resultData = new Natural[dimension];
        for (int i = 0; i < dimension; i++) {
            resultData[i] = this.data[i].add(other.data[i]);
        }
        return new NaturalVector(resultData);
    }

    @Override // AdditiveMonoidElement impls
    public NaturalVector getZero() { return new NaturalVector(dimension); }


    @Override // SemimoduleElement impls
    public Natural get(int index) {
    	if (index < 0 || index >= dimension) { 
            throw new IndexOutOfBoundsException("Index out of bounds: " + index);
        }
    	return data[index];
    }

    @Override // SemimoduleElement impls
    public NaturalVector scale(Natural scalar) {
    	Natural[] scaledData = new Natural[dimension];
        for (int i = 0; i < dimension; i++) {
            scaledData[i] = this.data[i].multiply(scalar);
        }
        return new NaturalVector(scaledData);
    }


    @Override // Java Standard impls
    public String toString() {
        return "N^" + dimension + Arrays.toString(data);
    }

    @Override // Java Standard impls
    public final boolean equals(Object other)
    {
    	if (this == other) {
            return true;
        }

        if (!(other instanceof NaturalVector)) {
            return false;
        }

        NaturalVector that = (NaturalVector) other;

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
