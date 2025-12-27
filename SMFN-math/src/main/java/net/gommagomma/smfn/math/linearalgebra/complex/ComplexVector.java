package net.gommagomma.smfn.math.linearalgebra.complex;

import java.util.Arrays;

import net.gommagomma.smfn.math.algebra.numeric.Complex;
import net.gommagomma.smfn.math.algebra.numeric.Real;
import net.gommagomma.smfn.math.algebra.structures.ComplexField;
import net.gommagomma.smfn.math.linearalgebra.core.elements.vectors.AbstractRank1Tensor;
import net.gommagomma.smfn.math.linearalgebra.core.elements.vectors.InnerProductSpaceElement;


public final class ComplexVector
extends AbstractRank1Tensor<Complex, ComplexVector>
implements InnerProductSpaceElement<Complex, ComplexVector>
{
    private final Complex[] data;


    // helper
    private static int validateAndGetLength(Complex[] components) {
        if (components == null || components.length == 0) {
            throw new IllegalArgumentException("Components array cannot be null or empty.");
        }
        return components.length;
    }

    public ComplexVector(Complex... components) {
    	super(validateAndGetLength(components));
        this.data = Arrays.copyOf(components, components.length);
    }

    public ComplexVector(int dimension) {
		super(dimension);
		this.data = new Complex[dimension];
		Arrays.fill(this.data, getZero());
	}


    @Override // AlgebraicElement impls
    public boolean isMathematicallyEqualTo(ComplexVector other)
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
    public ComplexVector copy() { return new ComplexVector(this.data); }


    @Override // AdditiveMonoidElement impls
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

	@Override // AdditiveMonoidElement impls
	public ComplexVector getZero()
	{
		return new ComplexVector(dimension);
	}

    
    @Override // GroupElement impls
    public ComplexVector negate() {
    	Complex[] negatedData = new Complex[dimension];
        for (int i = 0; i < dimension; i++) {
            negatedData[i] = this.data[i].negate();
        }
        return new ComplexVector(negatedData);
    }


    @Override // SemimoduleElement impls
    public ComplexVector scale(Complex scalar) {
    	Complex[] scaledData = new Complex[dimension];
        for (int i = 0; i < dimension; i++) {
            scaledData[i] = this.data[i].multiply(scalar);
        }
        return new ComplexVector(scaledData);
    }

    @Override // SemimoduleElement impls
    public Complex get(int index) {
        return this.data[index];
    }

    
    @Override // InnerProductSpaceElement impls
    public Complex dotProduct(ComplexVector other) {
        if (this.dimension != other.dimension) {
            throw new IllegalArgumentException("Vectors must have the same dimension for dot product.");
        }

        // Prodotto scalare hermitiano: V1 . V2 = Somma( V1[i] * coniugato(V2[i]) )
        Complex result = ComplexField.getInstance().zero();
        for (int i = 0; i < dimension; i++) {
            Complex conjugatedOther = other.data[i].conjugate(); 
            Complex product = this.data[i].multiply(conjugatedOther);
            result = result.add(product);
        }
        return result;
    }

    
    @Override // NormableElement impls
    public Real norm() {
        return this.dotProduct(this).norm().sqrt();
    }


    @Override // Java Standard impls
    public String toString() {
        return "C^" + dimension + Arrays.toString(data);
    }

    @Override // Java Standard impls
    public final boolean equals(Object other)
    {
    	if (this == other) {
            return true;
        }

        if (!(other instanceof ComplexVector)) {
            return false;
        }

        ComplexVector that = (ComplexVector) other;

        if (this.dimension != that.dimension) {
            return false;
        }

        return Arrays.equals(this.data, that.data);
    }

    @Override // Java Standard impls
    public final int hashCode()
    {
        return Arrays.hashCode(data);
    }
}
