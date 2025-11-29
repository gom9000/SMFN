package net.gommagomma.smfn.math.algebra.structures;


import net.gommagomma.smfn.math.algebra.core.structures.Semiring;
import net.gommagomma.smfn.math.algebra.numeric.Natural;


public class NaturalSemiring
implements Semiring<Natural>
{
	public static final NaturalSemiring INSTANCE = new NaturalSemiring();

    private NaturalSemiring() {
    	// Costruttore privato per il singleton
    }

    public static NaturalSemiring getInstance() {
        return INSTANCE;
    }

    @Override
    public String getName()
    {
        return "Natural Semiring (N)";
    }


    @Override
    public boolean contains(Natural e)
    {
    	return (e != null);
    }


    @Override
    public Natural additiveIdentity()
    {
        return Natural.ZERO;
    }


    @Override
    public Natural multiplicativeIdentity()
    {
        return Natural.ONE;
    }
}
