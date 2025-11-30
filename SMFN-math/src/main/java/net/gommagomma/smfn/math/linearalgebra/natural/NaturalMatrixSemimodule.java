package net.gommagomma.smfn.math.linearalgebra.natural;

import net.gommagomma.smfn.math.algebra.core.structures.Semiring;
import net.gommagomma.smfn.math.algebra.numeric.Natural;
import net.gommagomma.smfn.math.algebra.structures.NaturalSemiring;
import net.gommagomma.smfn.math.linearalgebra.core.structures.spaces.SemiringMatrixSemimodule;

/**
 * Rappresenta lo Spazio delle Matrici M x N sui Numeri Naturali (NaturalMatrixSpace).
 * Tutte le matrici in questo spazio hanno dimensioni fisse e utilizzano Natural come scalari.
 */
public final class NaturalMatrixSemimodule 
implements SemiringMatrixSemimodule<Natural, NaturalVector, NaturalMatrix>
{
    private final int rows;
    private final int cols;


    public NaturalMatrixSemimodule(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
    }

    @Override
    public Semiring<Natural> getScalarStructure() {
        return NaturalSemiring.getInstance();
    }

    @Override
    public int getMatrixRows() {
        return this.rows;
    }

    @Override
    public int getMatrixColumns() {
        return this.cols;
    }

    @Override
    public String getName() {
    	return "Natural Matrix Semiring (N^mxn)";
    }

    @Override
    public boolean contains(NaturalMatrix m) {
        return m.getRows() == this.rows && m.getColumns() == this.cols;
    }
}
