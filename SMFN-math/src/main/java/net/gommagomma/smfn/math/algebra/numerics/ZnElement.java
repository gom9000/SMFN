package net.gommagomma.smfn.math.algebra.numerics;

import net.gommagomma.smfn.math.algebra.core.elements.ExactElement;
import net.gommagomma.smfn.math.algebra.core.elements.capabilities.Exponentiable;
import net.gommagomma.smfn.math.algebra.core.structures.composite.ScalarStructure;
import net.gommagomma.smfn.math.algebra.structures.IntegerRing;
import net.gommagomma.smfn.math.algebra.structures.ZnRing;

public final class ZnElement 
implements ExactElement<ZnElement>, Exponentiable<ZnElement>
{
    private final SignedInt value; 
    private final SignedInt modulus;

    public ZnElement(SignedInt value, SignedInt modulus) {
    	if (modulus.getValue() <= 0) {
            throw new IllegalArgumentException("Modulus must be positive.");
        }
        this.modulus = modulus;
        this.value = IntegerRing.INSTANCE.remainder(value, modulus);
    }

    public SignedInt getValue() { return value; }
    public SignedInt getModulus() { return modulus; }


    @Override // ScalarElement impls
    public ScalarStructure<ZnElement> getStructure() {
    	return ZnRing.of(this.modulus);
    }


    @Override // AlgebraicElement impls
    public ZnElement copy() { return new ZnElement(this.value, this.modulus); }


    @Override // Exponentiable impls
    public ZnElement power(int exponent) {
        if (exponent < 0) {
            // L'inverso esiste solo se MCD(value, modulus) == 1
            // Per ora lanciamo eccezione, o implementiamo l'algoritmo di Euclide Esteso
            throw new ArithmeticException("Negative exponentiation requires Extended Euclidean Algorithm for modular inverse.");
        }
        // Square-and-multiply algorithm
        ZnElement base = this;
        ZnElement result = ZnRing.of(modulus).one();
        int exp = exponent;
        while (exp > 0) {
            if (exp % 2 == 1) result = multiplyInternal(result, base);
            base = multiplyInternal(base, base);
            exp /= 2;
        }
        return result;
    }
    // Helper interno per la potenza
    private ZnElement multiplyInternal(ZnElement a, ZnElement b) {
        SignedInt prod = IntegerRing.INSTANCE.multiply(a.value, b.value);
        return new ZnElement(prod, a.modulus);
    }


    @Override // Java Standard impls
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
}