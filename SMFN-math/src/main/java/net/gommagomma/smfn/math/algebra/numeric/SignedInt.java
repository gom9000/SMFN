package net.gommagomma.smfn.math.algebra.numeric;

import net.gommagomma.smfn.math.algebra.core.elements.capabilities.ComparableElement;
import net.gommagomma.smfn.math.algebra.core.elements.capabilities.CreatableFromDouble;
import net.gommagomma.smfn.math.algebra.core.elements.capabilities.ExponentiableElement;
import net.gommagomma.smfn.math.algebra.core.elements.euclidean.EuclideanDomainElement;
import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.CommutativeRingElement;

public final class SignedInt
implements CommutativeRingElement<SignedInt>, ExponentiableElement<SignedInt>, ComparableElement<SignedInt>, EuclideanDomainElement<SignedInt, SignedInt>, CreatableFromDouble<SignedInt>
{
	public static final SignedInt ZERO = new SignedInt(0);
    public static final SignedInt ONE = new SignedInt(1);

	private final long value;


    public SignedInt(long value)
    {
        this.value = value;
    }


    public long getValue()
    {
        return value;
    }


    // AlgebraicElement impls

    @Override
    public boolean isMathematicallyEqualTo(SignedInt other)
    {
    	if (this == other) {
            return true;
        }
        if (other == null) {
            return false;
        }

        return this.value == other.value;
    }


    @Override
    public SignedInt copy()
    {
        return new SignedInt(this.value);
    }


	@Override
	public SignedInt getZero()
	{
		return SignedInt.ZERO;
	}


	@Override
	public SignedInt getOne()
	{
		return SignedInt.ONE;
	}


    // MonoidElement impls

    @Override
    public SignedInt add(SignedInt other)
    {
        long result = Math.addExact(this.value, other.value);

        return new SignedInt(result);
    }


   // GroupElement impls

	@Override
	public SignedInt negate()
	{
		if (this.value == Long.MIN_VALUE) {
			throw new ArithmeticException("Negation of Long.MIN_VALUE causes overflow.");
		}

		return new SignedInt(-this.value);
	}


    // MultiplicativeMonoidElement impls

    @Override
    public SignedInt multiply(SignedInt other)
    {
        long result = Math.multiplyExact(this.value, other.value);

        return new SignedInt(result);
    }


    @Override
    public SignedInt power(int exponent)
    {
    	if (exponent < 0) {
            throw new ArithmeticException("Cannot raise a SignedInt to a negative power within the ring of integers.");
        }
        if (exponent == 0) {
            return SignedInt.ONE;
        }
        if (this.isZero()) {
            return SignedInt.ZERO;
        }

        long base = this.value;
        long result = 1;
        int exp = exponent;

        while (exp > 0)
        {
            if (exp % 2 == 1) {
                result = Math.multiplyExact(result, base);
            }
            base = Math.multiplyExact(base, base);
            exp /= 2;
        }

        return new SignedInt(result);
    }


    @Override
    public int compareTo(SignedInt other)
    {
        return Long.compare(this.value, other.value);
    }


// --- EuclideanDomainElement impls ---
    
    /**
     * Implementa la funzione norma euclidea v(n) = |n|.
     * Nota: Stiamo usando SignedInt per la norma, assumendo che i valori positivi
     * di SignedInt agiscano come i Naturali (N).
     */
    @Override
    public SignedInt normValue() {
        return new SignedInt(Math.abs(this.value));
    }

    /**
     * Calcola il quoziente q della Divisione Euclidea: this = q * divisor + remainder.
     */
    @Override
    public SignedInt quotient(SignedInt divisor) {
        if (divisor.isZero()) {
            throw new ArithmeticException("Division by zero in Euclidean domain.");
        }

        long a = this.value;
        long b = divisor.value;

        // 1. Ottiene il resto Euclideo normalizzato r, chiamando il metodo remainder.
        SignedInt remainderElement = this.remainder(divisor);
        long r = remainderElement.value; 

        // 2. Calcola il quoziente q usando la formula: q = (a - r) / b
        //    Questa formula GARANTISCE la coerenza: a = q*b + r.
        
        long numerator = Math.subtractExact(a, r); // Per (10, -3), questo è 10 - 1 = 9
        
        // Esegui la divisione intera standard di Java.
        // Per (10, -3), questo è 9 / -3 = -3. (CORRETTO per la verifica)
        long q = numerator / b; 

        return new SignedInt(q);
    }

    /**
     * Calcola il resto r della Divisione Euclidea: this = q * divisor + remainder.
     * Restituisce r, normalizzato per essere nell'intervallo [0, |divisor| - 1].
     */
    @Override
    public SignedInt remainder(SignedInt divisor) {
        if (divisor.isZero()) {
            throw new ArithmeticException("Division by zero in Euclidean domain.");
        }
        
        // Resto standard di Java (che può essere negativo)
        long rem = this.value % divisor.value;
        
        // Normalizzazione del resto per garantire che sia nell'intervallo [0, |divisor| - 1].
        if (rem < 0) {
            // Aggiunge il modulo assoluto se il resto è negativo.
            rem += Math.abs(divisor.value);
        }

        return new SignedInt(rem);
    }


    // Java Standard impls

    @Override
    public String toString()
    {
    	return String.valueOf(this.value);
    }


    @Override
    public final boolean equals(Object other) 
    {
    	if (this == other) {
            return true;
        }

        if (!(other instanceof SignedInt)) {
            return false;
        }

        SignedInt natural = (SignedInt) other;
        return this.value == natural.value;
    }


    @Override
    public final int hashCode()
    {
        return java.util.Objects.hash(this.value);
    }


    @Override
    public double modulus() {
        return Math.abs(this.value);
    }


	@Override
	public SignedInt valueOf(double value) {
		if (value > Long.MAX_VALUE || value < Long.MIN_VALUE) {
	        throw new ArithmeticException("Value " + value + " is outside the range of SignedInt (long).");
	    }

	    return new SignedInt((long) value);
	}

}
