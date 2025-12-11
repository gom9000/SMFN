package net.gommagomma.smfn.math.linearalgebra.rational;

import net.gommagomma.smfn.math.algebra.core.structures.Field;
import net.gommagomma.smfn.math.algebra.numeric.Rational;
import net.gommagomma.smfn.math.algebra.structures.RationalField;
import net.gommagomma.smfn.math.linearalgebra.core.structures.spaces.FieldMatrixSpace;

public final class RationalMatrixSpace
implements FieldMatrixSpace<Rational, RationalVector, RationalMatrix>
{    
    private final int rows;
    private final int cols;


    public RationalMatrixSpace(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
    }

    @Override
    public Field<Rational, ?> getScalarStructure() {
        return RationalField.getInstance();
    }

    @Override
    public int getMatrixRows() { return rows; }

    @Override
    public int getMatrixColumns() { return cols; }

    @Override
    public String getName() {
        return "Rational Matrix Space (Q^mxn)";
    }

    @Override
    public boolean contains(RationalMatrix m) {
    	return m.getRows() == this.rows && m.getColumns() == this.cols;
    }
}
