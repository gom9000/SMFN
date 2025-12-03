package net.gommagomma.smfn.math.linearalgebra.signedint;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import net.gommagomma.smfn.math.algebra.numeric.SignedInt;
import net.gommagomma.smfn.math.linearalgebra.core.elements.vectors.ModuleElement;

public final class SignedIntVector
implements ModuleElement<SignedInt, SignedIntVector>
{
    private final SignedInt[] data;
    private final int dimension;

    /**
     * Costruisce un SignedIntVector da un numero variabile di componenti SignedInt.
     * Esegue una copia difensiva per garantire l'immutabilità.
     *
     * @param components Le componenti del vettore.
     * @throws IllegalArgumentException se components sono null o vuoti.
     */
    public SignedIntVector(SignedInt... components) {
        if (components == null || components.length == 0) {
            throw new IllegalArgumentException("Components cannot be null or empty.");
        }
        // Copia difensiva: garantisce che il vettore interno non sia modificabile dall'esterno
        this.data = Arrays.copyOf(components, components.length); 
        this.dimension = components.length;
    }
    
    /**
     * Costruisce un vettore nullo della dimensione specificata.
     * @param dimension La dimensione del vettore.
     */
    public SignedIntVector(int dimension) {
        if (dimension <= 0) {
             throw new IllegalArgumentException("Dimension must be positive.");
        }
        this.dimension = dimension;
        this.data = new SignedInt[dimension];
        Arrays.fill(this.data, SignedInt.ZERO);
    }


	public SignedIntVector(List<SignedInt> components) {
        this(components.toArray(new SignedInt[0]));
    }

    /**
     * Helper statico per creare vettori da array di long primitivi.
     */
    public static SignedIntVector fromLongs(long... data) {
        SignedInt[] intComponents = Arrays.stream(data)
                                          .mapToObj(SignedInt::new) 
                                          .toArray(SignedInt[]::new);
        return new SignedIntVector(intComponents);
    }

    @Override
    public SignedIntVector createNewInstance(SignedInt... components) {
        return new SignedIntVector(components);
    }

    // --- Implementazione di AlgebraicElement e AdditiveMonoidElement ---

    @Override
    public boolean isMathematicallyEqualTo(SignedIntVector other)
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
    public SignedIntVector copy() {
        return new SignedIntVector(this.data);
    }

	@Override
	public SignedIntVector getZero()
	{
		// Restituisce un nuovo vettore nullo della stessa dimensione
		return new SignedIntVector(dimension);
	}

    
    // --- Implementazione di AbelianGroupElement (add, negate) ---

    @Override
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
    
    @Override
    public SignedIntVector negate() {
        SignedInt[] negatedData = new SignedInt[dimension];
        for (int i = 0; i < dimension; i++) {
            negatedData[i] = this.data[i].negate();
        }
        return new SignedIntVector(negatedData);
    }

    // --- Implementazione di VectorElement

    @Override
    public SignedIntVector multiplyByScalar(SignedInt scalar) {
        SignedInt[] scaledData = new SignedInt[dimension];
        for (int i = 0; i < dimension; i++) {
            scaledData[i] = this.data[i].multiply(scalar);
        }
        return new SignedIntVector(scaledData);
    }

    // --- Implementazione di SpaceElement/VectorElement (utilità) ---

    @Override
    public int dimension() {
        return this.dimension;
    }

    @Override
    public SignedInt get(int index) {
        return this.data[index];
    }

    
    // --- Java Standard impls ---

    @Override
    public String toString() {
        return "Z^" + dimension + Arrays.toString(data);
    }
    
    @Override
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

        return Objects.equals(this.data, that.data);
    }

    @Override
    public final int hashCode() {
        return Arrays.hashCode(data);
    }
}