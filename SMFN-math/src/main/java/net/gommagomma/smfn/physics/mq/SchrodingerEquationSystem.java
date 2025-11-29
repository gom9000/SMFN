package net.gommagomma.smfn.physics.mq;

import net.gommagomma.smfn.math.analysis.differential.DifferentialSystem;
import net.gommagomma.smfn.math.core.algebra.numeric.Complex;
import net.gommagomma.smfn.math.core.algebra.numeric.Real;
import net.gommagomma.smfn.math.linearalgebra.complex.ComplexVector;


public class SchrodingerEquationSystem
implements DifferentialSystem<Complex, ComplexVector>
{
    private final HamiltonianOperator H;
    // hbar (costante di Planck ridotta) è spesso 1.0 in unità naturali per MQ

    public SchrodingerEquationSystem(HamiltonianOperator h) {
        this.H = h;
    }

    @Override
    public ComplexVector derivative(ComplexVector state, Real time) {
        // L'equazione è d|psi>/dt = (-i/hbar) * H * |psi>
        
        // 1. Calcola H * |psi> (moltiplicazione Matrice-Vettore)
        ComplexVector H_psi = H.evaluate(state); // evaluate() è il metodo ereditato da MathFunction/LinearOperator

        // 2. Calcola lo scalare (-i/hbar). Usiamo hbar = 1.0
        Complex minusIOverHbar = new Complex(0.0, -1.0); 

        // 3. Moltiplica il vettore H_psi per lo scalare (-i/hbar)
        return H_psi.multiplyByScalar(minusIOverHbar);
    }
}
