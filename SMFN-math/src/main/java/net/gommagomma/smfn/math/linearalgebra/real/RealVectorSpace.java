package net.gommagomma.smfn.math.linearalgebra.real;

import net.gommagomma.smfn.math.algebra.numeric.Real;
import net.gommagomma.smfn.math.algebra.structures.RealField;
import net.gommagomma.smfn.math.linearalgebra.core.VectorSpace;


public class RealVectorSpace
implements VectorSpace<RealVector, Real>
{
    private static final RealVectorSpace INSTANCE = new RealVectorSpace();

    private RealVectorSpace() { /* singleton */ }

    public static RealVectorSpace getInstance() {
        return INSTANCE;
    }

    @Override
    public RealField getScalarRing() {
        return RealField.getInstance();
    }

    @Override
    public int dimension() {
        // Questo spazio vettoriale ha dimensione infinita in teoria, 
        // ma in un'implementazione software si riferisce spesso allo 
        // spazio in cui operano le istanze correnti (es. R^3, R^N).
        // Potresti lasciare questo metodo da implementare a livello di istanza del Vettore,
        // o decidere che questo singleton rappresenta un generico R^n.
        // Per ora, lo lasciamo con un placeholder logico.
        throw new UnsupportedOperationException("Specific vector instances have dimensions, the space R^n itself is generic.");
    }

    @Override
    public String getName() {
        return "Real Vector Space (R^n)";
    }

    @Override
    public boolean contains(RealVector v) {
        return true;
    }
}