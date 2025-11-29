package net.gommagomma.smfn.math.algebra.structures;


import net.gommagomma.smfn.math.core.algebra.numeric.Rational;
import net.gommagomma.smfn.math.core.algebra.structures.Field;


public class RationalField
implements Field<Rational>
{
	public static final RationalField INSTANCE = new RationalField();

    private RationalField() {
    	// Costruttore privato per il singleton
    }

    public static RationalField getInstance() {
        return INSTANCE;
    }
    
	@Override
	public String getName()
	{
		return "Rational Field (Q)";
	}


	@Override
	public boolean contains(Rational e)
	{
		return (e != null);
	}


	@Override
	public Rational additiveIdentity()
	{
		return Rational.ZERO;
	}


	@Override
	public Rational multiplicativeIdentity()
	{
		return Rational.ONE;
	}


	@Override
	public Rational valueOf(double value) {
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            throw new IllegalArgumentException("Cannot convert NaN or Infinity to Rational.");
        }

        final double EPSILON = 0.0000001;
        final long MAX_DENOMINATOR = 10000L;

        long n1 = 1L, d1 = 0L, n2 = 0L, d2 = 1L;
        double x = value;
        
        while (Math.abs(x - (double) n2 / d2) > EPSILON && d2 < MAX_DENOMINATOR) {
            long a = (long) Math.floor(x);
            long n3 = a * n2 + n1;
            long d3 = a * d2 + d1;

            if (d3 > MAX_DENOMINATOR) break;

            n1 = n2; d1 = d2;
            n2 = n3; d2 = d3;
            x = 1.0 / (x - a);
        }

        return new Rational(n2, d2);
    }
}
