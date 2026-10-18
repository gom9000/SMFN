package net.gommagomma.smfn.physics.mq;

import net.gommagomma.smfn.math.algebra.numerics.Complex;
import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.algebra.structures.ComplexField;
import net.gommagomma.smfn.math.linearalgebra.vectors.InnerProductVectorSpace;
import net.gommagomma.smfn.math.linearalgebra.vectors.Vector;
import net.gommagomma.smfn.math.linearalgebra.vectors.VectorElementFactory;

/**
 * Rappresenta uno stato quantistico |psi> nello spazio di Hilbert complesso a dimensione finita C^n.
 * 
 * La classe incapsula un vettore di ampiezze di probabilita' complesse e uno spazio vettoriale con prodotto interno
 * (InnerProductVectorSpace} per la gestione della metrica complessa (prodotto hermitiano).
 * Operazioni algebriche e fisiche fondamentali incluse:
 * - Prodotto Interno (Bra-Ket): Calcolo di <phi|psi> tramite innerProduct(QuantumState)
 * - Norma dello Stato: Calcolo della norma L2 \sqrt{<phi|psi>} tramite norm()
 * - Normalizzazione: Proiezione dello stato sulla sfera unitaria |psi>/{||psi||} tramite normalize(
 * - Sovrapposizione e Scalamento: Principi di sovrapposizione lineare |psi_1> + |psi_2> e moltiplicazione per uno scalare c |psi>
 */
public final class QuantumState
{
	private static final ComplexField C = ComplexField.INSTANCE;

	private final Vector<Complex> vector;
	private final InnerProductVectorSpace<Complex, ComplexField> space;

	private QuantumState(Vector<Complex> vector, InnerProductVectorSpace<Complex, ComplexField> space) {
		this.vector = vector;
		this.space = space;
	}

	public static QuantumState of(Complex... amplitudes) {
		InnerProductVectorSpace<Complex, ComplexField> space = new InnerProductVectorSpace<>(C, amplitudes.length);
		Vector<Complex> vector = VectorElementFactory.of(space, amplitudes);
		return new QuantumState(vector, space);
	}

	public static QuantumState from(Vector<Complex> vector) {
		InnerProductVectorSpace<Complex, ComplexField> space = new InnerProductVectorSpace<>(C, (int) vector.size());
		return new QuantumState(vector, space);
	}

	public Complex innerProduct(QuantumState other) {
		return space.innerProduct(vector, other.vector);
	}

	public Real norm() {
		return space.norm(vector);
	}

	public QuantumState normalize() {
		Real n = norm();
		if (n.getValue() <= 0.0) {
			throw new IllegalStateException("Cannot normalize a state with zero norm.");
		}
		Complex invNorm = C.of(1.0 / n.getValue());
		return new QuantumState(space.scale(invNorm, vector), space);
	}

	public QuantumState plus(QuantumState other) {
		return new QuantumState(space.add(vector, other.vector), space);
	}

	public QuantumState scale(Complex c) {
		return new QuantumState(space.scale(c, vector), space);
	}

	public int dimension() { return (int) vector.size(); }

	public Vector<Complex> asVector() { return vector; }

	@Override
	public String toString() { return vector.toString(); }
}
