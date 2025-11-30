package net.gommagomma.smfn.math.linearalgebra.natural;

import net.gommagomma.smfn.math.algebra.numeric.Natural;
import net.gommagomma.smfn.math.linearalgebra.core.elements.SemimoduleElement;

import java.util.Arrays;
import java.util.List;

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
    public boolean isEqual(NaturalVector other) { return Arrays.equals(this.data, other.data); }
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
}
