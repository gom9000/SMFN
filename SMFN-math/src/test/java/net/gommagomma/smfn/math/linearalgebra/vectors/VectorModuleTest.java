package net.gommagomma.smfn.math.linearalgebra.vectors;

import org.junit.jupiter.api.DisplayName;

import net.gommagomma.smfn.math.algebra.core.structures.composite.Module;
import net.gommagomma.smfn.math.algebra.core.structures.contracts.ModuleAxiomContract;
import net.gommagomma.smfn.math.algebra.numerics.SignedInt;
import net.gommagomma.smfn.math.algebra.structures.IntegerRing;

@DisplayName("VectorModule<SignedInt>: assiomi di Modulo (scalari Ring)")
class VectorModuleTest extends ModuleAxiomContract<Vector<SignedInt>, SignedInt, IntegerRing>
{
	private final IntegerRing Z = IntegerRing.INSTANCE;
	private final VectorModule<SignedInt, IntegerRing> V3 = new VectorModule<>(Z, 3);

	private Vector<SignedInt> v(long... values) {
		SignedInt[] data = new SignedInt[values.length];
		for (int i = 0; i < values.length; i++) data[i] = new SignedInt(values[i]);
		return V3.of(data);
	}

	@Override
	protected Module<Vector<SignedInt>, SignedInt, IntegerRing> structure() { return V3; }

	@Override
	protected Vector<SignedInt> a() { return v(1, -2, 3); }
	@Override
	protected Vector<SignedInt> b() { return v(-4, 0, 1); }
	@Override
	protected Vector<SignedInt> c() { return v(2, 2, -2); }
	@Override
	protected SignedInt k1() { return new SignedInt(-3); }
	@Override
	protected SignedInt k2() { return new SignedInt(5); }
}
