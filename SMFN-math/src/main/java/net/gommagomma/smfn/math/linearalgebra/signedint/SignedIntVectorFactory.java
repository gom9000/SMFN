package net.gommagomma.smfn.math.linearalgebra.signedint;

import net.gommagomma.smfn.math.algebra.core.NumericFactory;
import net.gommagomma.smfn.math.algebra.numeric.SignedInt;
import net.gommagomma.smfn.math.algebra.numeric.SignedIntFactory;
import net.gommagomma.smfn.math.linearalgebra.core.factories.SemimoduleVectorFactory;

public class SignedIntVectorFactory
implements SemimoduleVectorFactory<SignedInt, SignedIntVector>
{
    private static final SignedIntVectorFactory INSTANCE = new SignedIntVectorFactory();
    private final SignedIntFactory scalarFactory = SignedIntFactory.getInstance();


    private SignedIntVectorFactory() {}
    public static SignedIntVectorFactory getInstance() { return INSTANCE; }


    @Override // SemimoduleVectorFactory impls
    public Class<SignedInt> getScalarClass() { return SignedInt.class; }

    @Override // SemimoduleVectorFactory impls
    public NumericFactory<SignedInt> getScalarFactory() { return this.scalarFactory; }

    @Override // SemimoduleVectorFactory impls
    public SignedIntVector createZeroVector(int dimension) { return new SignedIntVector(dimension); }

    @Override // SemimoduleVectorFactory impls
    public SignedIntVector createVector(SignedInt[] data) { return new SignedIntVector(data); }

    @Override // SemimoduleVectorFactory impls
	public SignedIntVector createVector(double[] data) {
    	SignedInt[] components = new SignedInt[data.length];

        for (int i = 0; i < data.length; i++) {
            components[i] = scalarFactory.of(data[i]); 
        }
 
        return createVector(components);
	}

    @Override // SemimoduleVectorFactory impls
    public SignedIntVector createVector(long[] data) {
    	SignedInt[] components = new SignedInt[data.length];

        for (int i = 0; i < data.length; i++) {
            components[i] = scalarFactory.of(data[i]); 
        }
 
        return createVector(components);
    }

    @Override // SemimoduleVectorFactory impls
    public SignedIntVector createVector(int[] data) {
    	SignedInt[] components = new SignedInt[data.length];

        for (int i = 0; i < data.length; i++) {
            components[i] = scalarFactory.of(data[i]); 
        }
 
        return createVector(components);
    }
}
