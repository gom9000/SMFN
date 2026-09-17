package net.gommagomma.smfn.math.linearalgebra.matrices;

import org.junit.jupiter.api.DisplayName;

import net.gommagomma.smfn.math.algebra.core.structures.composite.Module;
import net.gommagomma.smfn.math.algebra.core.structures.contracts.ModuleAxiomContract;
import net.gommagomma.smfn.math.algebra.numerics.SignedInt;
import net.gommagomma.smfn.math.algebra.structures.IntegerRing;

@DisplayName("MatrixModule<SignedInt>: assiomi di Modulo (scalari Ring)")
class MatrixModuleTest extends ModuleAxiomContract<Matrix<SignedInt>, SignedInt, IntegerRing>
{
	private final IntegerRing Z = IntegerRing.INSTANCE;
	private final MatrixModule<SignedInt, IntegerRing> M22 = new MatrixModule<>(Z, 2, 2);

	private Matrix<SignedInt> m(long... values) {
		SignedInt[] data = new SignedInt[values.length];
		for (int i = 0; i < values.length; i++) data[i] = new SignedInt(values[i]);
		return M22.of(data);
	}

	@Override
	protected Module<Matrix<SignedInt>, SignedInt, IntegerRing> structure() { return M22; }

	@Override
	protected Matrix<SignedInt> a() { return m(1, -2, 3, 4); }
	@Override
	protected Matrix<SignedInt> b() { return m(-4, 0, 1, 2); }
	@Override
	protected Matrix<SignedInt> c() { return m(2, 2, -2, 2); }
	@Override
	protected SignedInt k1() { return new SignedInt(-3); }
	@Override
	protected SignedInt k2() { return new SignedInt(5); }
}
