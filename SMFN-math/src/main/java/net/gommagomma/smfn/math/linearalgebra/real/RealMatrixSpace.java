package net.gommagomma.smfn.math.linearalgebra.real;

import net.gommagomma.smfn.math.algebra.core.structures.Field;
import net.gommagomma.smfn.math.algebra.numeric.Real;
import net.gommagomma.smfn.math.algebra.structures.RealField;
import net.gommagomma.smfn.math.linearalgebra.core.structures.spaces.FieldMatrixSpace;

public class RealMatrixSpace
implements FieldMatrixSpace<Real, RealVector, RealMatrix>
{
    private final int rows;
    private final int cols;

    public RealMatrixSpace(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
    }

    @Override
    public Field<Real> getScalarStructure() {
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