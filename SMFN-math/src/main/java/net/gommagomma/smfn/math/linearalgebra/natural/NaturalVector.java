package net.gommagomma.smfn.math.linearalgebra.natural;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import net.gommagomma.smfn.math.algebra.numeric.Natural;
import net.gommagomma.smfn.math.linearalgebra.core.elements.vectors.SemimoduleElement;

public final class NaturalVector
implements SemimoduleElement<Natural, NaturalVector>
{
    private final Natural[] data;
    private final int dimension;

    public NaturalVector(int dimension) {
    	if (dimension <= 0) {
            throw new IllegalArgumentException("Dimension must be positive.");
       }
       this.dimension = dimension;
       this.data = new Natural[dimension];
       Arrays.fill(this.data, Natural.ZERO);
    }
    
    public NaturalVector(Natural... components) {
        if (components == null || components.length == 0) {
            throw new IllegalArgumentException("Components cannot be null or empty.");
        }
        // Copia difensiva: garantisce che il vettore interno non sia modificabile dall'esterno
        this.data = Arrays.copyOf(components, components.length);
        this.dimension = components.length;
    }

    public NaturalVector(List<Natural> components) {
        this(components.toArray(new Natural[0]));
    }

    @Override
    public int dimension() { return dimension; }

    @Override
    public Natural get(int index) { return data[index]; }

    // Implementazioni di CommutativeMonoidElement<V> (add, getZero, isEqual, copy)
    @Override
    public NaturalVector add(NaturalVector other) {
        if (this.dimension != other.dimension) throw new IllegalArgumentException("Dimensions must match");
        Natural[] resultData = new Natural[dimension];
        for (int i = 0; i < dimension; i++) {
            resultData[i] = this.data[i].add(other.data[i]);
        }
        return new NaturalVector(resultData);
    }


    // Implementazioni richieste da AlgebraicElement
    @Override
    public boolean isMathematicallyEqualTo(NaturalVector other)
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
    public NaturalVector copy() { return new NaturalVector(this.data); }
    
    @Override
    public NaturalVector getZero() { return new NaturalVector(dimension); }

    @Override
    public NaturalVector multiplyByScalar(Natural scalar) {
    	Natural[] scaledData = new Natural[dimension];
        for (int i = 0; i < dimension; i++) {
            scaledData[i] = this.data[i].multiply(scalar);
        }
        return new NaturalVector(scaledData);
    }


    // --- Java Standard impls ---

    @Override
    public String toString() {
        return "N^" + dimension + Arrays.toString(data);
    }
    
    @Override
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

        return Objects.equals(this.data, that.data);
    }

    @Override
    public final int hashCode() {
        return Arrays.hashCode(data);
    }
}
