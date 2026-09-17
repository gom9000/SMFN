package net.gommagomma.smfn.math.linearalgebra.vectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.math.algebra.core.structures.composite.Semimodule;
import net.gommagomma.smfn.math.algebra.core.structures.contracts.SemimoduleAxiomContract;
import net.gommagomma.smfn.math.algebra.numerics.Natural;
import net.gommagomma.smfn.math.algebra.structures.NaturalSemiring;

@DisplayName("VectorSemimodule<Natural>: assiomi di Semimodulo (scalari Semiring, no inverso)")
class VectorSemimoduleTest extends SemimoduleAxiomContract<Vector<Natural>, Natural, NaturalSemiring>
{
	private final NaturalSemiring N = NaturalSemiring.INSTANCE;
	private final VectorSemimodule<Natural, NaturalSemiring> V3 = new VectorSemimodule<>(N, 3);

	private Vector<Natural> v(long... values) {
		Natural[] data = new Natural[values.length];
		for (int i = 0; i < values.length; i++) data[i] = new Natural(values[i]);
		return V3.of(data);
	}

	@Override
	protected Semimodule<Vector<Natural>, Natural, NaturalSemiring> structure() { return V3; }

	@Override
	protected Vector<Natural> a() { return v(1, 2, 3); }
	@Override
	protected Vector<Natural> b() { return v(4, 0, 1); }
	@Override
	protected Vector<Natural> c() { return v(2, 2, 2); }
	@Override
	protected Natural k1() { return new Natural(3); }
	@Override
	protected Natural k2() { return new Natural(5); }

	@Test
	@DisplayName("Dimensione errata viene rifiutata da add()")
	void dimensionMismatchRejected() {
		Vector<Natural> wrongSize = new VectorSemimodule<>(N, 2).of(new Natural[] { new Natural(1), new Natural(1) });
		assertThrows(IllegalArgumentException.class, () -> V3.add(a(), wrongSize));
	}

	@Test
	@DisplayName("Factory: of() e zero()")
	void factoryMethods() {
		assertEquals(v(0, 0, 0), V3.zero());
	}
}
