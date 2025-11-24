package net.gommagomma.smfn.math.linearalgebra.signedint;

import java.util.Arrays;
import java.util.List;

import net.gommagomma.smfn.math.algebra.numeric.SignedInt;
import net.gommagomma.smfn.math.linearalgebra.core.Module;
import net.gommagomma.smfn.math.linearalgebra.core.VectorElement;

public final class SignedIntVector
implements VectorElement<SignedInt, SignedIntVector>
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


    // --- Implementazione di AlgebraicElement e AdditiveMonoidElement ---

    @Override
    public boolean isEqual(SignedIntVector other) {
        // Usa Arrays.equals per un confronto efficiente degli array
        return Arrays.equals(this.data, other.data);
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

    // --- Implementazione di VectorElement (dotProduct, multiplyByScalar) ---
    
    @Override
    public SignedInt dotProduct(SignedIntVector other) {
        if (this.dimension != other.dimension) {
            throw new IllegalArgumentException("Vectors must have the same dimension for dot product.");
        }
        SignedInt result = SignedInt.ZERO;
        for (int i = 0; i < dimension; i++) {
            SignedInt product = this.data[i].multiply(other.data[i]);
            result = result.add(product);
        }
        return result;
    }

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

    @Override
    public Module<SignedIntVector, SignedInt> getModule() {
        // Restituisce l'istanza singleton del modulo intero
        return IntegerModule.getInstance(); 
    }
    
    // --- Java Standard impls ---

    @Override
    public String toString() {
        // Rappresentazione ad esempio come Z^3
        return "Z^" + dimension + Arrays.toString(data);
    }
    
    @Override
    public final boolean equals(Object other) {
        return (other instanceof SignedIntVector) && isEqual((SignedIntVector)other);
    }

    @Override
    public final int hashCode() {
        return Arrays.hashCode(data);
    }
}