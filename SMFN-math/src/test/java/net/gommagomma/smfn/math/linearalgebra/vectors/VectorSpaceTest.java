package net.gommagomma.smfn.math.linearalgebra.vectors;

import org.junit.jupiter.api.DisplayName;

import net.gommagomma.smfn.math.algebra.core.structures.composite.LinearSpace;
import net.gommagomma.smfn.math.algebra.core.structures.contracts.LinearSpaceAxiomContract;
import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.algebra.structures.RealField;

@DisplayName("VectorSpace<Real>: assiomi di Spazio Vettoriale (scalari Field)")
class VectorSpaceTest extends LinearSpaceAxiomContract<Vector<Real>, Real, RealField>
{
	private final RealField R = RealField.INSTANCE;
	private final VectorSpace<Real, RealField> V3 = new VectorSpace<>(R, 3);

	private Vector<Real> v(double... values) {
		Real[] data = new Real[values.length];
		for (int i = 0; i < values.length; i++) data[i] = new Real(values[i]);
		return V3.of(data);
	}

	@Override
	protected LinearSpace<Vector<Real>, Real, RealField> structure() { return V3; }

	@Override
	protected Vector<Real> a() { return v(1.0, -2.0, 3.5); }
	@Override
	protected Vector<Real> b() { return v(-4.0, 0.5, 1.0); }
	@Override
	protected Vector<Real> c() { return v(2.0, 2.0, -2.0); }
	@Override
	protected Real k1() { return new Real(-3.0); }
	@Override
	protected Real k2() { return new Real(0.5); }
	@Override
	protected Real nonZeroScalar() { return new Real(4.0); }
}
