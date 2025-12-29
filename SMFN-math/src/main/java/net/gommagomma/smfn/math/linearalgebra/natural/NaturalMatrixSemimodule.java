package net.gommagomma.smfn.math.linearalgebra.natural;

import net.gommagomma.smfn.math.algebra.numerics.Natural;
import net.gommagomma.smfn.math.algebra.structures.NaturalSemiring;
import net.gommagomma.smfn.math.linearalgebra.core.structures.spaces.AbstractSemiringMatrixSpace;
import net.gommagomma.smfn.math.linearalgebra.core.structures.spaces.SemiringMatrixSemimodule;

/**
 * Rappresenta lo Spazio delle Matrici M x N sui Numeri Naturali.
 * Estende AbstractSemiringMatrixSpace per ereditare la logica della factory.
 */
public final class NaturalMatrixSemimodule 
extends AbstractSemiringMatrixSpace<Natural, NaturalVector, NaturalMatrix>
{
    public NaturalMatrixSemimodule(int rows, int cols) {
        super(rows, cols);
    }

    @Override
    public String getName() {
        return "Natural Matrix Semiring (N^" + rows + "x" + cols + ")";
    }

    @Override
    public NaturalSemiring getScalarStructure() {
        return NaturalSemiring.getInstance();
    }

    @Override
    public NaturalSemimodule getVectorStructure() {
        return NaturalSemimodule.getInstance();
    }

    @Override
    public NaturalMatrix createMatrix(Natural[][] data) {
        validateInputDimensions(data.length, data.length > 0 ? data[0].length : 0);
        return new NaturalMatrix(data, this);
    }

    @Override
    protected Natural[][] createDataArray(int r, int c) {
        return new Natural[r][c];
    }

    @Override
    public SemiringMatrixSemimodule<Natural, NaturalVector, NaturalMatrix> getSpaceOfDimensions(int rows, int cols) {
        return new NaturalMatrixSemimodule(rows, cols);
    }
}