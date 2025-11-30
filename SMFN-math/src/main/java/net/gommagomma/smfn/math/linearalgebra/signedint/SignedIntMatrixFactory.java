package net.gommagomma.smfn.math.linearalgebra.signedint;

import net.gommagomma.smfn.math.algebra.numeric.SignedInt;
import net.gommagomma.smfn.math.linearalgebra.core.structures.factories.RingMatrixFactory;


/**
 * Factory concreta per la creazione di elementi (scalari, vettori, matrici)
 * basati sul dominio degli interi relativi (SignedInt), che formano un Anello.
 */
public final class SignedIntMatrixFactory 
implements RingMatrixFactory<SignedInt, SignedIntVector, SignedIntMatrix>
{
	private static final SignedIntMatrixFactory INSTANCE = new SignedIntMatrixFactory();

    private SignedIntMatrixFactory() {}

    public static SignedIntMatrixFactory getInstance() {
        return INSTANCE;
    }

    @Override
    public SignedIntMatrix createMatrix(SignedInt[][] data) {
        return new SignedIntMatrix(data);
    }

    @Override
    public SignedIntMatrix createZeroMatrix(int rows, int cols) {
        return new SignedIntMatrix(rows, cols);
    }

    @Override
    public SignedIntVector createVector(SignedInt[] data) {
        return new SignedIntVector(data); 
    }
    
    /** Restituisce l'elemento zero dello scalare (int 0). */
    @Override
    public SignedInt getZeroScalar() {
        return SignedInt.ZERO;
    }

    /** Restituisce l'elemento uno dello scalare (int 1). */
    @Override
    public SignedInt getOneScalar() {
        return SignedInt.ONE;
    }
}
