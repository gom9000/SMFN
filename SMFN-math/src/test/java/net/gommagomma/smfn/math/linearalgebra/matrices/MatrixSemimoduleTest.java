package net.gommagomma.smfn.math.linearalgebra.matrices;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.math.algebra.core.structures.composite.Semimodule;
import net.gommagomma.smfn.math.algebra.core.structures.contracts.SemimoduleAxiomContract;
import net.gommagomma.smfn.math.algebra.numerics.Natural;
import net.gommagomma.smfn.math.algebra.structures.NaturalSemiring;

@DisplayName("MatrixSemimodule<Natural>: assiomi di Semimodulo (scalari Semiring)")
class MatrixSemimoduleTest extends SemimoduleAxiomContract<Matrix<Natural>, Natural, NaturalSemiring>
{
	private final NaturalSemiring N = NaturalSemiring.INSTANCE;
	private final MatrixSemimodule<Natural, NaturalSemiring> M22 = new MatrixSemimodule<>(N, 2, 2);

	private Matrix<Natural> m(long... values) {
		Natural[] data = new Natural[values.length];
		for (int i = 0; i < values.length; i++) data[i] = new Natural(values[i]);
		return M22.of(data);
	}

	@Override
	protected Semimodule<Matrix<Natural>, Natural, NaturalSemiring> structure() { return M22; }

	@Override
	protected Matrix<Natural> a() { return m(1, 2, 3, 4); }
	@Override
	protected Matrix<Natural> b() { return m(4, 0, 1, 2); }
	@Override
	protected Matrix<Natural> c() { return m(2, 2, 2, 2); }
	@Override
	protected Natural k1() { return new Natural(3); }
	@Override
	protected Natural k2() { return new Natural(5); }

	@Test
	@DisplayName("Trasposta: (A^T)^T = A")
	void transposeIsInvolution() {
		Matrix<Natural> a = a();
		assertTrue(structure().areEqual(M22.transpose(M22.transpose(a)), a));
	}

	@Test
	@DisplayName("of(): array di lunghezza sbagliata lancia IllegalArgumentException")
	void ofRejectsWrongLength() {
		Natural[] tooShort = { new Natural(1), new Natural(2) }; // servono 4 elementi (2x2), non 2
		assertThrows(IllegalArgumentException.class, () -> M22.of(tooShort));
	}
}
