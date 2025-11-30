package net.gommagomma.smfn.math.linearalgebra.complex;

import net.gommagomma.smfn.math.algebra.numeric.Complex;
import net.gommagomma.smfn.math.algebra.structures.ComplexField;
import net.gommagomma.smfn.math.linearalgebra.core.structures.HilbertSpace;

public class ComplexVectorSpace
implements HilbertSpace<Complex, ComplexVector>
{    
    private static final ComplexVectorSpace INSTANCE = new ComplexVectorSpace();

    private ComplexVectorSpace() { /* singleton */ }


    public static ComplexVectorSpace getInstance() {
        return INSTANCE;
    }

    @Override
    public ComplexField getScalarStructure() {
        return ComplexField.getInstance();
    }

    @Override
    public String getName() {
        return "Complex Vector Space (C^n)";
    }

    @Override
    public boolean contains(ComplexVector v) {
        return true;
    }
}
