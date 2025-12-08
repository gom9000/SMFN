package net.gommagomma.smfn.math.linearalgebra.rational;

import net.gommagomma.smfn.math.algebra.core.NumericFactory;
import net.gommagomma.smfn.math.algebra.numeric.Rational;
import net.gommagomma.smfn.math.algebra.numeric.RationalFactory;
import net.gommagomma.smfn.math.linearalgebra.core.factories.SemimoduleVectorFactory;

public final class RationalVectorFactory
implements SemimoduleVectorFactory<Rational, RationalVector>
{
    private static final RationalVectorFactory INSTANCE = new RationalVectorFactory();
    private final RationalFactory scalarFactory = RationalFactory.getInstance();


    private RationalVectorFactory() {}
    public static RationalVectorFactory getInstance() { return INSTANCE; }


    @Override // SemimoduleVectorFactory impls
    public Class<Rational> getScalarClass() { return Rational.class; }

    @Override // SemimoduleVectorFactory impls
    public NumericFactory<Rational> getScalarFactory() { return this.scalarFactory; }

    @Override // SemimoduleVectorFactory impls
    public RationalVector createZeroVector(int dimension) { return new RationalVector(dimension); }

    @Override // SemimoduleVectorFactory impls
    public RationalVector createVector(Rational[] data) { return new RationalVector(data); }

    @Override // SemimoduleVectorFactory impls
	public RationalVector createVector(double[] data) {
    	Rational[] components = new Rational[data.length];

        for (int i = 0; i < data.length; i++) {
            components[i] = scalarFactory.of(data[i]); 
        }
 
        return createVector(components);
	}

    @Override // SemimoduleVectorFactory impls
    public RationalVector createVector(long[] data) {
    	Rational[] components = new Rational[data.length];

        for (int i = 0; i < data.length; i++) {
            components[i] = scalarFactory.of(data[i]); 
        }
 
        return createVector(components);
    }

    @Override // SemimoduleVectorFactory impls
    public RationalVector createVector(int[] data) {
    	Rational[] components = new Rational[data.length];

        for (int i = 0; i < data.length; i++) {
            components[i] = scalarFactory.of(data[i]); 
        }
 
        return createVector(components);
    }
}
