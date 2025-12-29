package net.gommagomma.smfn.math.linearalgebra.complex;

import net.gommagomma.smfn.math.algebra.core.structures.Field;
import net.gommagomma.smfn.math.algebra.numerics.Complex;
import net.gommagomma.smfn.math.algebra.structures.ComplexField;
import net.gommagomma.smfn.math.linearalgebra.core.structures.spaces.HilbertSpace;

public final class ComplexVectorSpace
implements HilbertSpace<Complex, ComplexVector>
{    
    private static final ComplexVectorSpace INSTANCE = new ComplexVectorSpace();


    private ComplexVectorSpace() {}
    public static ComplexVectorSpace getInstance() { return INSTANCE; }


    @Override // AlgebraicStructure impls
    public String getName() {
        return "Complex Vector Space (C^n)";
    }

    @Override // AlgebraicStructure impls
    public boolean contains(ComplexVector v) {
        return true;
    }


    @Override
    public Field<Complex> getScalarStructure() {
        return ComplexField.getInstance();
    }


    @Override // VectorElementFactory impls
    public ComplexVector createZeroVector(int dimension) { return new ComplexVector(dimension); }

    @Override // VectorElementFactory impls
    public ComplexVector createVector(Complex[] data) { return new ComplexVector(data); }

    @Override // VectorElementFactory impls
	public ComplexVector createVector(double[] data) {
    	Complex[] components = new Complex[data.length];

        for (int i = 0; i < data.length; i++) {
            components[i] = getScalarStructure().of(data[i]); 
        }
 
        return createVector(components);
	}

    @Override // VectorElementFactory impls
    public ComplexVector createVector(long[] data) {
    	Complex[] components = new Complex[data.length];

        for (int i = 0; i < data.length; i++) {
            components[i] = getScalarStructure().of(data[i]); 
        }
 
        return createVector(components);
    }

    @Override // VectorElementFactory impls
    public ComplexVector createVector(int[] data) {
    	Complex[] components = new Complex[data.length];

        for (int i = 0; i < data.length; i++) {
            components[i] = getScalarStructure().of(data[i]); 
        }
 
        return createVector(components);
    }
}
