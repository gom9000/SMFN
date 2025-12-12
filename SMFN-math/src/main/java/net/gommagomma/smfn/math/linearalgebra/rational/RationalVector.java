package net.gommagomma.smfn.math.linearalgebra.rational;

import java.util.Arrays;

import net.gommagomma.smfn.math.algebra.numeric.Rational;
import net.gommagomma.smfn.math.algebra.numeric.Real;
import net.gommagomma.smfn.math.algebra.structures.RationalField;
import net.gommagomma.smfn.math.linearalgebra.core.elements.vectors.AbstractRank1Tensor;
import net.gommagomma.smfn.math.linearalgebra.core.elements.vectors.InnerProductSpaceElement;

/**
 * Rappresenta un vettore immutabile di numeri razionali.
 * Forma uno Spazio Vettoriale (VectorSpace) sul Campo dei Razionali (Q).
 */
public final class RationalVector
extends AbstractRank1Tensor<Rational, RationalVector>
implements InnerProductSpaceElement<Rational, RationalVector>
{
    private final Rational[] data;


    // helper
    private static int validateAndGetLength(Rational[] components) {
        if (components == null || components.length == 0) {
            throw new IllegalArgumentException("Components array cannot be null or empty.");
        }
        return components.length;
    }


    public RationalVector(Rational... components) {
    	super(validateAndGetLength(components));
        this.data = Arrays.copyOf(components, components.length);
    }

    public RationalVector(int dimension) {
        super(dimension);
        this.data = new Rational[dimension];
        Arrays.fill(this.data, getZero());
    }


    @Override // AlgebraicElement impls
    public boolean isMathematicallyEqualTo(RationalVector other)
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
    public RationalVector copy() {
        return new RationalVector(this.data);
    }


    @Override // AdditiveMonoidElement impls
    public RationalVector add(RationalVector other) {
        if (this.dimension != other.dimension) throw new IllegalArgumentException("Dimensions must match.");
        Rational[] resultData = new Rational[dimension];
        for (int i = 0; i < dimension; i++) {
            resultData[i] = this.data[i].add(other.data[i]);
        }
        return new RationalVector(resultData);
    }

	@Override // AdditiveMonoidElement impls
	public RationalVector getZero() {
		return new RationalVector(dimension);
	}


    @Override // GroupElement impls
    public RationalVector negate() {
        Rational[] negatedData = new Rational[dimension];
        for (int i = 0; i < dimension; i++) {
            negatedData[i] = this.data[i].negate();
        }
        return new RationalVector(negatedData);
    }


    @Override // SemimoduleElement impls
    public RationalVector multiplyByScalar(Rational scalar) {
        Rational[] scaledData = new Rational[dimension];
        for (int i = 0; i < dimension; i++) {
            scaledData[i] = this.data[i].multiply(scalar);
        }
        return new RationalVector(scaledData);
    }

    @Override // SemimoduleElement impls
    public Rational get(int index) { return this.data[index]; }


    @Override // InnerProductSpaceElement impls
    public Rational dotProduct(RationalVector other) {
        if (this.dimension != other.dimension) {
            throw new IllegalArgumentException("Vectors must have the same dimension for dot product.");
        }
        Rational result = RationalField.getInstance().zero();
        for (int i = 0; i < dimension; i++) {
            result = result.add(this.data[i].multiply(other.data[i]));
        }
        return result;
    }

    @Override // NormableElement impls
    public Real norm() {
        Rational normSquaredRational = this.dotProduct(this);
        double normValue = Math.sqrt((double)normSquaredRational.getNumerator() / normSquaredRational.getDenominator());

        return new Real(normValue);
    }


    @Override // Java Standard impls
    public String toString() {
        return "Q^" + dimension + Arrays.toString(data);
    }
    
    @Override // Java Standard impls
    public final boolean equals(Object other)
    {
    	if (this == other) {
            return true;
        }

        if (!(other instanceof RationalVector)) {
            return false;
        }

        RationalVector that = (RationalVector) other;

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
