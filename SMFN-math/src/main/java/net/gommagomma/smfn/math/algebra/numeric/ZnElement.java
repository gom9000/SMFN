package net.gommagomma.smfn.math.algebra.numeric;

import net.gommagomma.smfn.math.algebra.core.elements.capabilities.CreatableFromDouble;
import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.CommutativeRingElement;


/**
 * Rappresenta un elemento [r] dell'anello Z/nZ (ZModNRing).
 * Implementa CommutativeRingElement direttamente, senza l'astrazione Ideal/QuotientRing.
 */
public final class ZnElement 
implements CommutativeRingElement<ZnElement>, CreatableFromDouble<ZnElement>
{
    private final SignedInt value; 
    private final SignedInt modulus;

    /**
     * Costruisce un nuovo elemento modulare [value] mod n.
     * @param value L'intero rappresentante.
     * @param modulus Il modulo n.
     */
    public ZnElement(SignedInt value, SignedInt modulus) {
        if (modulus.isZero() || modulus.isLessThan(new SignedInt(1))) {
            throw new IllegalArgumentException("Modulus must be a positive integer > 0.");
        }
        this.modulus = modulus;
        this.value = value.mod(modulus);
    }

    /**
     * Restituisce il valore del rappresentante canonico [0, n-1].
     */
    public SignedInt getValue() {
        return value;
    }

    // AlgebraicElement impls

    @Override
    public boolean isMathematicallyEqualTo(ZnElement other) {
        if (!this.modulus.isMathematicallyEqualTo(other.modulus)) {
             return false;
        }

        return this.value.isMathematicallyEqualTo(other.value);
    }

    @Override
    public ZnElement copy() {
        return new ZnElement(this.value, this.modulus); 
    }
    
    // CommutativeRingElement impls

    @Override
    public ZnElement getZero() {
        return new ZnElement(new SignedInt(0), this.modulus);
    }

    @Override
    public ZnElement getOne() {
        return new ZnElement(new SignedInt(1), this.modulus);
    }
    
    @Override
    public boolean isZero() {
        return this.value.isMathematicallyEqualTo(getZero().value);
    }
    
    // AdditiveMonoidElement impls

    @Override
    public ZnElement add(ZnElement other) {
        if (!this.modulus.isMathematicallyEqualTo(other.modulus)) {
            throw new IllegalArgumentException("Cannot add elements from different modulus rings.");
        }
        // [a] + [b] = [a + b] mod n
        SignedInt sum = this.value.add(other.value);
        return new ZnElement(sum, this.modulus); // Il costruttore normalizza
    }

    // GroupElement impls

    @Override
    public ZnElement negate() {
        // [a] -> [-a] mod n
        SignedInt negatedValue = this.value.negate();
        return new ZnElement(negatedValue, this.modulus); // Il costruttore normalizza
    }
    
    // MultiplicativeMonoidElement impls

    @Override
    public ZnElement multiply(ZnElement other) {
        if (!this.modulus.isMathematicallyEqualTo(other.modulus)) {
            throw new IllegalArgumentException("Cannot multiply elements from different modulus rings.");
        }
        // [a] * [b] = [a * b] mod n
        SignedInt product = this.value.multiply(other.value);
        return new ZnElement(product, this.modulus); // Il costruttore normalizza
    }

    
    // CreatableFromDouble impls

    @Override
    public ZnElement valueOf(double value) {
    	if (value > Long.MAX_VALUE || value < Long.MIN_VALUE) {
	        throw new ArithmeticException("Value " + value + " is outside the range of SignedInt (long).");
	    }
        SignedInt intValue = new SignedInt((long) value);
        return new ZnElement(intValue, this.modulus);
    }
    
    // Java Standard impls

    @Override
    public String toString() {
        return "[" + this.value.toString() + " mod " + this.modulus.toString() + "]";
    }

    @Override
    public final boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof ZnElement)) return false;
        ZnElement that = (ZnElement) other;
        return this.value.equals(that.value) && this.modulus.equals(that.modulus);
    }

    @Override
    public final int hashCode() {
        return java.util.Objects.hash(this.value, this.modulus);
    }


    public SignedInt getModulus() {
        return modulus;
    }
}