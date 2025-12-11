package net.gommagomma.smfn.math.linearalgebra.complex;

import net.gommagomma.smfn.math.algebra.core.NumericFactory;
import net.gommagomma.smfn.math.algebra.numeric.Complex;
import net.gommagomma.smfn.math.algebra.structures.ComplexField;
import net.gommagomma.smfn.math.linearalgebra.core.factories.SemimoduleVectorFactory;

public final class ComplexVectorFactory
implements SemimoduleVectorFactory<Complex, ComplexVector>
{
    private static final ComplexVectorFactory INSTANCE = new ComplexVectorFactory();
    private final ComplexField scalarFactory = ComplexField.getInstance();


    private ComplexVectorFactory() {}
    public static ComplexVectorFactory getInstance() { return INSTANCE; }


    @Override // SemimoduleVectorFactory impls
    public Class<Complex> getScalarClass() { return Complex.class; }

    @Override // SemimoduleVectorFactory impls
    public NumericFactory<Complex> getScalarFactory() { return this.scalarFactory; }

    @Override // SemimoduleVectorFactory impls
    public ComplexVector createZeroVector(int dimension) { return new ComplexVector(dimension); }

    @Override // SemimoduleVectorFactory impls
    public ComplexVector createVector(Complex[] data) { return new ComplexVector(data); }

    @Override // SemimoduleVectorFactory impls
	public ComplexVector createVector(double[] data) {
    	Complex[] components = new Complex[data.length];

        for (int i = 0; i < data.length; i++) {
            components[i] = scalarFactory.of(data[i]); 
        }
 
        return createVector(components);
	}

    @Override // SemimoduleVectorFactory impls
    public ComplexVector createVector(long[] data) {
    	Complex[] components = new Complex[data.length];

        for (int i = 0; i < data.length; i++) {
            components[i] = scalarFactory.of(data[i]); 
        }
 
        return createVector(components);
    }

    @Override // SemimoduleVectorFactory impls
    public ComplexVector createVector(int[] data) {
    	Complex[] components = new Complex[data.length];

        for (int i = 0; i < data.length; i++) {
            components[i] = scalarFactory.of(data[i]); 
        }
 
        return createVector(components);
    }
}
