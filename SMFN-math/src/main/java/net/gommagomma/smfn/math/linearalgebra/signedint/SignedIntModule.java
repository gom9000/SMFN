package net.gommagomma.smfn.math.linearalgebra.signedint;

import net.gommagomma.smfn.math.algebra.core.structures.Ring;
import net.gommagomma.smfn.math.algebra.numeric.SignedInt;
import net.gommagomma.smfn.math.algebra.structures.IntegerRing;
import net.gommagomma.smfn.math.linearalgebra.core.structures.spaces.Module;


public class SignedIntModule
implements Module<SignedInt, SignedIntVector>
{
    private static final SignedIntModule INSTANCE = new SignedIntModule();


    private SignedIntModule() {}
    public static SignedIntModule getInstance() { return INSTANCE; }


    @Override // AlgebraicStructure impls
    public String getName() {
        return "Integer Module (Z^n)";
    }

    @Override // AlgebraicStructure impls
    public boolean contains(SignedIntVector v) {
        return true;
    }


    @Override // VectorSpace impls
    public Ring<SignedInt> getScalarStructure() {
        return IntegerRing.getInstance();
    }


    @Override // VectorElementFactory impls
    public SignedIntVector createZeroVector(int dimension) { return new SignedIntVector(dimension); }

    @Override // VectorElementFactory impls
    public SignedIntVector createVector(SignedInt[] data) { return new SignedIntVector(data); }

    @Override // VectorElementFactory impls
	public SignedIntVector createVector(double[] data) {
    	SignedInt[] components = new SignedInt[data.length];

        for (int i = 0; i < data.length; i++) {
            components[i] = getScalarStructure().of(data[i]); 
        }
 
        return createVector(components);
	}

    @Override // VectorElementFactory impls
    public SignedIntVector createVector(long[] data) {
    	SignedInt[] components = new SignedInt[data.length];

        for (int i = 0; i < data.length; i++) {
            components[i] = getScalarStructure().of(data[i]); 
        }
 
        return createVector(components);
    }

    @Override // VectorElementFactory impls
    public SignedIntVector createVector(int[] data) {
    	SignedInt[] components = new SignedInt[data.length];

        for (int i = 0; i < data.length; i++) {
            components[i] = getScalarStructure().of(data[i]); 
        }
 
        return createVector(components);
    }
}
