package net.gommagomma.smfn.math.linearalgebra.real;

import net.gommagomma.smfn.math.algebra.numeric.Real;
import net.gommagomma.smfn.math.linearalgebra.core.operators.AbstractProjectionOperator;


public class RealVectorProjection
extends AbstractProjectionOperator<Real, RealVector>
{
    public RealVectorProjection(RealVector direction) {
        super(direction);
    }

    @Override
    protected Real getInverseNormScalar(Real normValue) {
        return normValue.abs().inverse();
    }
}
