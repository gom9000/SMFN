package net.gommagomma.smfn.math.linearalgebra.matrices.square;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.math.algebra.core.structures.Semiring;
import net.gommagomma.smfn.math.algebra.core.structures.contracts.SemiringAxiomContract;
import net.gommagomma.smfn.math.algebra.numerics.Natural;
import net.gommagomma.smfn.math.algebra.structures.NaturalSemiring;

/**
 * SquareMatrix e' gia' ScalarElement<Self> (chiusura sotto composizione),
 * quindi il contratto d'assioma di algebra.core per Semiring si applica qui
 * senza modifiche -- stesso pattern gia' usato per PolynomialRing.
 */
@DisplayName("SquareMatrixSemiring<Natural>: assiomi di Semianello (scalari Semiring, no inverso)")
class SquareMatrixSemiringTest extends SemiringAxiomContract<SquareMatrix<Natural>>
{
	private final NaturalSemiring N = NaturalSemiring.INSTANCE;
	private final SquareMatrixSemiring<Natural, NaturalSemiring> M2 = new SquareMatrixSemiring<>(N, 2);

	private SquareMatrix<Natural> m(long... values) {
		Natural[] data = new Natural[values.length];
		for (int i = 0; i < values.length; i++) data[i] = new Natural(values[i]);
		return M2.of(data);
	}

	@Override
	protected Semiring<SquareMatrix<Natural>> structure() { return M2; }

	@Override
	protected SquareMatrix<Natural> a() { return m(1, 2, 3, 4); }
	@Override
	protected SquareMatrix<Natural> b() { return m(2, 0, 1, 1); }
	@Override
	protected SquareMatrix<Natural> c() { return m(1, 1, 0, 2); }

	@Test
	@DisplayName("Trasposta: (A^T)^T = A")
	void transposeIsInvolution() {
		SquareMatrix<Natural> matrix = a();
		assertTrue(structure().areEqual(M2.transpose(M2.transpose(matrix)), matrix));
	}
}
