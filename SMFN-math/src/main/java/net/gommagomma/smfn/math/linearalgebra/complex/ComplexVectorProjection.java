package net.gommagomma.smfn.math.linearalgebra.complex;

import net.gommagomma.smfn.math.algebra.numeric.Complex;
import net.gommagomma.smfn.math.algebra.numeric.Real;
import net.gommagomma.smfn.math.linearalgebra.core.operators.AbstractProjectionOperator;


public class ComplexVectorProjection
extends AbstractProjectionOperator<Complex, ComplexVector>
{
    public ComplexVectorProjection(ComplexVector direction) {
        super(direction);
    }
    
    @Override
    protected Complex getInverseNormScalar(Real normValue) {
        // Implementazione specifica per i Complessi: 
        // Creiamo un complesso con parte reale 1.0/normValue.modulus() e parte immaginaria 0.0
        return new Complex(normValue.abs().inverse());
    }
}
