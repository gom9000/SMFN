package net.gommagomma.smfn.math.linearalgebra.rational;

import net.gommagomma.smfn.math.algebra.numeric.Rational;
import net.gommagomma.smfn.math.algebra.structures.RationalField;
import net.gommagomma.smfn.math.linearalgebra.core.structures.MatrixSpace;

public final class RationalMatrixSpace
implements MatrixSpace<Rational, RationalVector, RationalMatrix>
{    
    private static final RationalMatrixSpace INSTANCE = new RationalMatrixSpace();

    private RationalMatrixSpace() {
        // Costruttore privato per il singleton
    }

    public static RationalMatrixSpace getInstance() {
        return INSTANCE;
    }

    @Override
    public RationalField getScalarField() {
        return RationalField.getInstance();
    }
    
    // Come discusso, queste proprietà sono specifiche dell'istanza della matrice, non dello spazio generico.
    @Override
    public int getMatrixRows() {
        throw new UnsupportedOperationException("Specific matrix instances have dimensions, the space Q^mxn itself is generic.");
    }

    @Override
    public int getMatrixColumns() {
         throw new UnsupportedOperationException("Specific matrix instances have dimensions, the space Q^mxn itself is generic.");
    }

    @Override
    public String getName() {
        return "Rational Matrix Space (Q^mxn)";
    }

    @Override
    public boolean contains(RationalMatrix m) {
        return (m != null);
    }
}
