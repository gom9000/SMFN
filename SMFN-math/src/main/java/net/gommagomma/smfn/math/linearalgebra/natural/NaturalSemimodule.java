package net.gommagomma.smfn.math.linearalgebra.natural;

import net.gommagomma.smfn.math.algebra.core.structures.Semiring;
import net.gommagomma.smfn.math.algebra.numeric.Natural;
import net.gommagomma.smfn.math.algebra.structures.NaturalSemiring;
import net.gommagomma.smfn.math.linearalgebra.core.structures.spaces.Semimodule;


public final class NaturalSemimodule
implements Semimodule<Natural, NaturalVector>
{
    private static final NaturalSemimodule INSTANCE = new NaturalSemimodule();

    private NaturalSemimodule() {}

    public static NaturalSemimodule getInstance() {
        return INSTANCE;
    }

    @Override
    public Semiring<Natural> getScalarStructure() {
        return NaturalSemiring.getInstance();
    }

    @Override
    public String getName() {
        return "Naural Semimodule (N^n)";
    }

	@Override
	public boolean contains(NaturalVector e) {
		return true;
	}
}
