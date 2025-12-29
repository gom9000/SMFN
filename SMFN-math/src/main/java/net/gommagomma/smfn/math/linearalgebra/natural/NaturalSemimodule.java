package net.gommagomma.smfn.math.linearalgebra.natural;

import net.gommagomma.smfn.math.algebra.core.structures.Semiring;
import net.gommagomma.smfn.math.algebra.numerics.Natural;
import net.gommagomma.smfn.math.algebra.structures.NaturalSemiring;
import net.gommagomma.smfn.math.linearalgebra.core.structures.spaces.Semimodule;


public final class NaturalSemimodule
implements Semimodule<Natural, NaturalVector>
{
    private static final NaturalSemimodule INSTANCE = new NaturalSemimodule();


    private NaturalSemimodule() {}
    public static NaturalSemimodule getInstance() { return INSTANCE; }


    @Override // AlgebraicStructure impls
    public String getName() {
        return "Naural Semimodule (N^n)";
    }

	@Override // AlgebraicStructure impls
	public boolean contains(NaturalVector e) {
		return true;
	}


    @Override // VectorSpace impls
    public Semiring<Natural> getScalarStructure() {
        return NaturalSemiring.getInstance();
    }


    @Override // VectorElementFactory impls
    public NaturalVector createZeroVector(int dimension) { return new NaturalVector(dimension); }

    @Override // VectorElementFactory impls
    public NaturalVector createVector(Natural[] data) { return new NaturalVector(data); }

    @Override // VectorElementFactory impls
	public NaturalVector createVector(double[] data) {
        Natural[] components = new Natural[data.length];

        for (int i = 0; i < data.length; i++) {
            components[i] = getScalarStructure().of(data[i]); 
        }
 
        return createVector(components);
	}

    @Override // VectorElementFactory impls
    public NaturalVector createVector(long[] data) {
        Natural[] components = new Natural[data.length];

        for (int i = 0; i < data.length; i++) {
            components[i] = getScalarStructure().of(data[i]); 
        }
 
        return createVector(components);
    }

    @Override // VectorElementFactory impls
    public NaturalVector createVector(int[] data) {
        Natural[] components = new Natural[data.length];

        for (int i = 0; i < data.length; i++) {
            components[i] = getScalarStructure().of(data[i]); 
        }
 
        return createVector(components);
    }
}
