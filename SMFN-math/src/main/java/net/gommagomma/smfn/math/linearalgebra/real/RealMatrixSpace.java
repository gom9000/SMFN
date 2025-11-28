package net.gommagomma.smfn.math.linearalgebra.real;

import net.gommagomma.smfn.math.algebra.structures.RealField;
import net.gommagomma.smfn.math.core.algebra.numeric.Real;
import net.gommagomma.smfn.math.core.algebra.structures.Field;
import net.gommagomma.smfn.math.core.linearalgebra.structures.MatrixSpace;

public class RealMatrixSpace
implements MatrixSpace<Real, RealVector, RealMatrix>
{
    private final int rows;
    private final int cols;

    public RealMatrixSpace(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
    }

    @Override
    public Field<Real> getScalarField() {
        return RealField.getInstance(); // Restituisce l'istanza del campo dei reali
    }
    
    @Override
    public int getMatrixRows() { return rows; }
    @Override
    public int getMatrixColumns() { return cols; }

    @Override
    public String getName() {
        return "Space of " + rows + "x" + cols + " Real Matrices";
    }
    
    @Override
    public boolean contains(RealMatrix m) {
        return m.getRows() == rows && m.getColumns() == cols;
    }
}