package net.gommagomma.smfn.math.linearalgebra.natural;

import net.gommagomma.smfn.math.algebra.numeric.Natural;
import net.gommagomma.smfn.math.linearalgebra.core.structures.factories.SemiringMatrixFactory;

/**
 * Factory concreta per la creazione di elementi basati sul dominio
 * dei numeri naturali (Natural), che formano un Semianello.
 */
public final class NaturalMatrixFactory
implements SemiringMatrixFactory<Natural, NaturalVector, NaturalMatrix>
{
	private static final NaturalMatrixFactory INSTANCE = new NaturalMatrixFactory();

	private NaturalMatrixFactory() {}

	public static NaturalMatrixFactory getInstance() {
        return INSTANCE;
    }

    @Override
    public NaturalMatrix createMatrix(Natural[][] data) {
        // La factory usa il costruttore della classe concreta
        return new NaturalMatrix(data);
    }

    @Override
    public NaturalMatrix createZeroMatrix(int rows, int cols) {
        // La factory usa il costruttore helper della classe concreta
        return new NaturalMatrix(rows, cols);
    }

    @Override
    public NaturalVector createVector(Natural[] data) {
        // Supponendo che esista un costruttore appropriato per NaturalVector
        return new NaturalVector(data); 
    }

    /** Restituisce l'elemento zero dello scalare (Natural 0). */
    @Override
    public Natural getZeroScalar() {
        return Natural.ZERO;
    }

    /** Restituisce l'elemento uno dello scalare (Natural 1). */
    @Override
    public Natural getOneScalar() {
        return Natural.ONE;
    }
}
