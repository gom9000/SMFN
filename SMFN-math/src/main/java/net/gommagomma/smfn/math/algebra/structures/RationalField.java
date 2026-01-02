package net.gommagomma.smfn.math.algebra.structures;


import net.gommagomma.smfn.math.algebra.core.structures.Field;
import net.gommagomma.smfn.math.algebra.core.structures.capabilities.ExactStructure;
import net.gommagomma.smfn.math.algebra.core.structures.capabilities.NumericFactory;
import net.gommagomma.smfn.math.algebra.numerics.Rational;
import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.utils.MathUtils;


public final class RationalField
implements Field<Rational>, ExactStructure<Rational>, NumericFactory<Rational>
{
	private static final Rational ZERO = new Rational(0, 1);
    private static final Rational ONE = new Rational(1, 1);
    
    public static final RationalField INSTANCE = new RationalField();
    private RationalField() {}


    // NumericFactory impls
    @Override public Rational zero() { return ZERO; }
    @Override public Rational one() { return ONE; }
    @Override public Rational of(long value) { return new Rational(value); }
	@Override public Rational of(int value) { return new Rational(value);}
    @Override
    public Rational of(double value) {
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            throw new IllegalArgumentException("Cannot create a Rational number from NaN or Infinity.");
        }

        if (value == 0.0) {
            return zero();
        }

        if (value == Math.floor(value)) {
            try {
                return of((long) value); 
            } catch (ArithmeticException e) {
                throw new ArithmeticException("Integer value " + value + " is outside the Long range.");
            }
        }

        // --- Standard IEEE 754 (64-bit double) ---
        long bits = Double.doubleToLongBits(value);
        
        // Estrazione di esponente (11 bit) e mantissa (52 bit)
        int exponent = (int) ((bits >> 52) & 0x7FFL);
        
        long fractionalMask = (1L << 52) - 1; // 0x000FFFFFFFFFFFFL     
        long mantissa = bits & fractionalMask;
        //long mantissa = bits & 0x000FFFFFFFFFFFFL;
        
        // Bit di segno: 0 se positivo, diverso da 0 se negativo
        boolean negative = (bits & 0x8000000000000000L) != 0;

        // Se l'esponente è 0, è un valore denormalizzato. Altrimenti è normalizzato.
        if (exponent == 0) { 
            // Denormalizzato: l'esponente è -1022, il bit implicito non è aggiunto
            exponent = -1022; 
        } else {
            // Normalizzato: aggiunge il bit implicito (il 53° bit della mantissa)
            mantissa |= 0x0010000000000000L; 
            // Il bias dell'esponente è 1023
            exponent -= 1023;
        }

        // Il valore è: mantissa * 2^exponent
        
        long num;
        long den;

        if (exponent >= 0) {
        	// Calcolo del numeratore (Mantissa * 2^Exponent)
            num = Math.multiplyExact(mantissa, MathUtils.power(2L, exponent));
            
            // Il denominatore corretto è 2^52 (la scala della mantissa)
            // 2^52 = 4503599627370496L
            den = 1L << 52; // Usiamo 1L << 52 per coerenza
        } else {
            // Potenza di due nel denominatore
            long powerOfTwo = 52 - exponent; // Es: 0.5 -> 52 - (-1) = 53
            
            // Controlliamo se la potenza eccede il limite di 62 per un long
            if (powerOfTwo > 62) {
                 throw new ArithmeticException("Exponent magnitude is too large to represent the Rational denominator (overflow).");
            }

            // Calcoliamo il denominatore usando lo shift bit, evitando MathUtils.power
            // (1L << N è 2^N)
            den = 1L << powerOfTwo; 
            num = mantissa;
        }

        // 4. Applica il segno
        if (negative) {
            num = -num;
        }

        return new Rational(num, den);
    }


    // ScalarStructure impls
    @Override
    public Real magnitude(Rational a) {
    	Rational aa = a.abs();
        return RealField.INSTANCE.of((double) aa.getNumerator()/aa.getDenominator()); 
    }


    // AdditiveMonoid impls
    @Override
    public Rational add(Rational a, Rational b) {
        long num = Math.addExact(
            Math.multiplyExact(a.getNumerator(), b.getDenominator()),
            Math.multiplyExact(b.getNumerator(), a.getDenominator())
        );
        long den = Math.multiplyExact(a.getDenominator(), b.getDenominator());
        return new Rational(num, den);
    }


    // MultiplicativeMonoid impls
    @Override
    public Rational multiply(Rational a, Rational b) {
        return new Rational(
            Math.multiplyExact(a.getNumerator(), b.getNumerator()),
            Math.multiplyExact(a.getDenominator(), b.getDenominator())
        );
    }


    // AdditiveGroup impls
    @Override
    public Rational negate(Rational e) {
        return new Rational(-e.getNumerator(), e.getDenominator());
    }


    // MultiplicativeGroup impls
    @Override
    public Rational inverse(Rational e) {
        if (isZero(e)) throw new ArithmeticException("Division by zero");
        return new Rational(e.getDenominator(), e.getNumerator());
    }


	@Override // AlgebraicStructure impls
	public String getName()
	{
		return "Rational Field (Q)";
	}

	@Override
	public boolean contains(Rational e)
	{
		return (e != null);
	}
}
