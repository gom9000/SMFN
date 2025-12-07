package net.gommagomma.smfn.math.linearalgebra.signedint;

import net.gommagomma.smfn.math.algebra.core.NumericFactory;
import net.gommagomma.smfn.math.algebra.numeric.SignedInt;
import net.gommagomma.smfn.math.algebra.numeric.SignedIntFactory;
import net.gommagomma.smfn.math.linearalgebra.core.factories.RingMatrixFactory;
import net.gommagomma.smfn.math.linearalgebra.core.factories.SemimoduleVectorFactory;


/**
 * Factory concreta per la creazione di elementi (scalari, vettori, matrici)
 * basati sul dominio degli interi relativi (SignedInt), che formano un Anello.
 */
public final class SignedIntMatrixFactory 
implements RingMatrixFactory<SignedInt, SignedIntVector, SignedIntMatrix>
{
	private static final SignedIntMatrixFactory INSTANCE = new SignedIntMatrixFactory();
	private final SignedIntVectorFactory vectorFactory = SignedIntVectorFactory.getInstance();
    private final SignedIntFactory scalarFactory = SignedIntFactory.getInstance();


    private SignedIntMatrixFactory() {}
    public static SignedIntMatrixFactory getInstance() { return INSTANCE; }


    @Override // SemiringMatrixFactory impls
    public SemimoduleVectorFactory<SignedInt, SignedIntVector> getVectorFactory() { return this.vectorFactory; }

	@Override // SemiringMatrixFactory impls
    public NumericFactory<SignedInt> getScalarFactory() { return this.scalarFactory; }


    @Override // SemiringMatrixFactory impls
    public SignedIntMatrix createMatrix(SignedInt[][] data) { return new SignedIntMatrix(data); }

    @Override // SemiringMatrixFactory impls
    public SignedIntMatrix createZeroMatrix(int rows, int cols) {
        return new SignedIntMatrix(rows, cols);
    }
}
