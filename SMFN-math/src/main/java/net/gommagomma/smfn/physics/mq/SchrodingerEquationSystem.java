package net.gommagomma.smfn.physics.mq;

import net.gommagomma.smfn.math.algebra.numerics.Complex;
import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.algebra.structures.ComplexField;
import net.gommagomma.smfn.math.analysis.core.problems.DifferentialEquationProblem;
import net.gommagomma.smfn.math.linearalgebra.vectors.Vector;
import net.gommagomma.smfn.math.linearalgebra.vectors.VectorSpace;

/**
 * Equazione di Schrodinger dipendente dal tempo: d|psi>/dt = (-i/hbar) H|psi>.
 * hbar = 1 in unita' naturali.
 *
 * E' esattamente un DifferentialEquationProblem<Complex, Vector<Complex>> --
 * lo stesso concetto generalizzato in analysis per accogliere stati a
 * valori complessi, non serviva nessuna estensione ulteriore.
 */
public final class SchrodingerEquationSystem
implements DifferentialEquationProblem<Complex, Vector<Complex>>
{
	private static final Complex MINUS_I = new Complex(0.0, -1.0);

	private final Observable hamiltonian;
	private final VectorSpace<Complex, ComplexField> space;

	public SchrodingerEquationSystem(Observable hamiltonian, int dimension) {
		this.hamiltonian = hamiltonian;
		this.space = new VectorSpace<>(ComplexField.INSTANCE, dimension);
	}

	@Override
	public Vector<Complex> derivative(Vector<Complex> state, Real time) {
		Vector<Complex> H_psi = hamiltonian.asOperator().apply(state);
		return space.scale(MINUS_I, H_psi);
	}
}
