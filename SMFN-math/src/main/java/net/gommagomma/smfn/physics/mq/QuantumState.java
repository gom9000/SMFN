package net.gommagomma.smfn.physics.mq;

import net.gommagomma.smfn.math.algebra.numerics.Complex;
import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.algebra.structures.ComplexField;
import net.gommagomma.smfn.math.linearalgebra.vectors.InnerProductVectorSpace;
import net.gommagomma.smfn.math.linearalgebra.vectors.Vector;
import net.gommagomma.smfn.math.linearalgebra.vectors.VectorElementFactory;

/**
 * Uno stato quantistico puro: un Vector<Complex> che porta con se' il
 * proprio spazio con prodotto interno, cosi' chi lo usa non deve costruirlo
 * e passarlo ad ogni chiamata -- e' sempre lo stesso, fissato una volta
 * sola alla dimensione del sistema.
 *
 * Non generico su K, deliberatamente, come Observable/Hamiltonian: uno
 * stato quantistico vive sempre in uno spazio di Hilbert su Complex, mai
 * su un K qualunque -- non e' una scelta di comodo, e' quello che la
 * fisica richiede sempre.
 *
 * asVector() resta la via di fuga verso la matematica pura, mai
 * obbligatoria -- stesso principio di Observable.asOperator().
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
