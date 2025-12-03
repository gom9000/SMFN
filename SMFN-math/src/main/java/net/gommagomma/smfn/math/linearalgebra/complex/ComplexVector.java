package net.gommagomma.smfn.math.linearalgebra.complex;

import java.util.Arrays;
import java.util.List;

import net.gommagomma.smfn.math.algebra.numeric.Complex;
import net.gommagomma.smfn.math.algebra.numeric.Real;
import net.gommagomma.smfn.math.linearalgebra.core.elements.vectors.InnerProductSpaceElement;


public final class ComplexVector
implements InnerProductSpaceElement<Complex, ComplexVector>
{
    private final Complex[] data;
    private final int dimension;

    public ComplexVector(Complex... components) {
    	if (components == null || components.length == 0) {
            throw new IllegalArgumentException("Components cannot be null or empty.");
        }
        // Copia difensiva: garantisce che il vettore interno non sia modificabile dall'esterno
        this.data = Arrays.copyOf(components, components.length);
        this.dimension = components.length;
    }

    public ComplexVector(List<Complex> components) {
        this(components.toArray(new Complex[0]));
    }

    /**
     * Helper statico per creare vettori da array di coppie reale/immaginario.
     * Es: fromDoubles(1.0, 0.0, 0.0, 1.0) crea il vettore (1+0i, 0+1i)
     */
    public static ComplexVector fromDoubles(double... data) {
        if (data.length % 2 != 0) {
            throw new IllegalArgumentException("Double array length must be even (real, imaginary pairs).");
        }
        Complex[] complexComponents = new Complex[data.length / 2];
        for (int i = 0; i < data.length; i += 2) {
            complexComponents[i / 2] = new Complex(data[i], data[i+1]);
        }
        return new ComplexVector(complexComponents);
    }

    @Override
    public ComplexVector createNewInstance(Complex... components) {
        return new ComplexVector(components);
    }

    // --- Metodi Algebrici (isEqual, copy, getZero, add, negate, ecc.) ---

    @Override
    public boolean isMathematicallyEqualTo(ComplexVector other) {
        if (this.dimension != other.dimension) {
            return false;
        }
        // Implementazione manuale che usa Complex.isEqual() per gestire la tolleranza EPSILON
        for (int i = 0; i < this.dimension; i++) {
            if (!this.data[i].isMathematicallyEqualTo(other.data[i])) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ComplexVector copy() { return new ComplexVector(this.data); }

	@Override
	public ComplexVector getZero()
	{
		Complex[] zeros = new Complex[dimension];
		Arrays.fill(zeros, Complex.ZERO);
		return new ComplexVector(zeros);
	}

    
    @Override
    public ComplexVector add(ComplexVector other) {
        if (this.dimension != other.dimension) {
            throw new IllegalArgumentException("Vectors must have the same dimension to add.");
        }
        Complex[] resultData = new Complex[dimension];
        for (int i = 0; i < dimension; i++) {
            resultData[i] = this.data[i].add(other.data[i]);
        }
        return new ComplexVector(resultData);
    }
    
    @Override
    public ComplexVector negate() {
    	Complex[] negatedData = new Complex[dimension];
        for (int i = 0; i < dimension; i++) {
            negatedData[i] = this.data[i].negate();
        }
        return new ComplexVector(negatedData);
    }

    // --- Implementazione di VectorElement (dotProduct, multiplyByScalar) ---
    
    @Override
    public Complex dotProduct(ComplexVector other) {
        if (this.dimension != other.dimension) {
            throw new IllegalArgumentException("Vectors must have the same dimension for dot product.");
        }
        
        // Prodotto scalare hermitiano: V1 . V2 = Somma( V1[i] * coniugato(V2[i]) )
        Complex result = Complex.ZERO;
        for (int i = 0; i < dimension; i++) {
            Complex conjugatedOther = other.data[i].conjugate(); 
            Complex product = this.data[i].multiply(conjugatedOther);
            result = result.add(product);
        }
        return result;
    }

    @Override
    public ComplexVector multiplyByScalar(Complex scalar) {
    	Complex[] scaledData = new Complex[dimension];
        for (int i = 0; i < dimension; i++) {
            scaledData[i] = this.data[i].multiply(scalar);
        }
        return new ComplexVector(scaledData);
    }

    // --- Implementazione di SpaceElement/VectorElement (utilità) ---

    @Override
    public int dimension() {
        return this.dimension;
    }

    @Override
    public Complex get(int index) {
        return this.data[index];
    }

    
    // --- Implementazione di Normable<Real, ComplexVector> ---
    
    @Override
    public Real norm() {
        // La norma L2 è la radice quadrata del prodotto scalare (che è un numero reale).
        // dotProduct(this) restituisce Complex, ma la sua norma è il modulo del risultato, che è Real.
        return this.dotProduct(this).norm();
    }

    // --- Java Standard impls ---

    @Override
    public String toString() {
        return "C^" + dimension + Arrays.toString(data);
    }


    @Override
    public final boolean equals(Object other)
    {
        return (other instanceof ComplexVector) && isMathematicallyEqualTo((ComplexVector)other);
    }

    @Override
    public final int hashCode()
    {
        return Arrays.hashCode(data);
    }


    public Complex[] toArray() {
        return Arrays.copyOf(this.data, this.dimension);
    }
}
