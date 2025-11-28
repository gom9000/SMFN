package net.gommagomma.smfn.math.linearalgebra.complex;

import net.gommagomma.smfn.math.algebra.numeric.Complex;
import net.gommagomma.smfn.math.algebra.structures.ComplexField;
import net.gommagomma.smfn.math.linearalgebra.core.MatrixSpace;

public final class ComplexMatrixSpace
implements MatrixSpace<Complex, ComplexVector, ComplexMatrix>
{    
    private static final ComplexMatrixSpace INSTANCE = new ComplexMatrixSpace();

    private ComplexMatrixSpace() {
        // Costruttore privato per il singleton
    }

    public static ComplexMatrixSpace getInstance() {
        return INSTANCE;
    }

    @Override
    public ComplexField getScalarField() {
        return ComplexField.getInstance();
    }
    
    // Come discusso, queste proprietà sono specifiche dell'istanza della matrice, non dello spazio generico.
    @Override
    public int getMatrixRows() {
        throw new UnsupportedOperationException("Specific matrix instances have dimensions, the space C^mxn itself is generic.");
    }

    @Override
    public int getMatrixColumns() {
         throw new UnsupportedOperationException("Specific matrix instances have dimensions, the space C^mxn itself is generic.");
    }

    @Override
    public String getName() {
        return "Complex Matrix Space (C^mxn)";
    }

    @Override
    public boolean contains(ComplexMatrix m) {
        return (m != null);
    }
}
