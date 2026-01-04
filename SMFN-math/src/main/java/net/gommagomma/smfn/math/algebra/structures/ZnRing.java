package net.gommagomma.smfn.math.algebra.structures;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.gommagomma.smfn.math.algebra.core.elements.factories.NumericFactory;
import net.gommagomma.smfn.math.algebra.core.structures.CommutativeRing;
import net.gommagomma.smfn.math.algebra.core.structures.capabilities.ExactStructure;
import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.algebra.numerics.SignedInt;
import net.gommagomma.smfn.math.algebra.numerics.ZnElement;
import net.gommagomma.smfn.math.utils.MathConstants;

/**
 * Rappresenta la struttura dell'Anello Commutativo Z/nZ (ZModNRing).
 * Agisce da fabbrica per creare elementi validi in Z_n.
 * * Z/nZ è un Campo se e solo se n è primo. Qui è implementato come Anello generico.
 */
public final class ZnRing
implements CommutativeRing<ZnElement>, ExactStructure<ZnElement>, NumericFactory<ZnElement>
{
	private static final Map<SignedInt, ZnRing> CACHE = new ConcurrentHashMap<>();

    private final SignedInt modulus;
    private final ZnElement additiveIdentity;
    private final ZnElement multiplicativeIdentity;


    /**
     * Costruisce l'anello Z/nZ specificando il modulo n.
     * @param modulus Il modulo n (deve essere un intero positivo > 0).
     */
    private ZnRing(SignedInt modulus) {
        if (modulus.getValue() <= 0) {
            throw new IllegalArgumentException("Modulus for ZModNRing must be a positive integer > 0.");
        }
        this.modulus = modulus;
        IntegerRing ring = IntegerRing.INSTANCE;
        this.additiveIdentity = new ZnElement(ring.zero(), this.modulus);
        this.multiplicativeIdentity = new ZnElement(ring.one(), this.modulus);
    }

    public static ZnRing of(SignedInt modulus) {
        return CACHE.computeIfAbsent(modulus, ZnRing::new);
    }

    public SignedInt getModulus() { return modulus; }


    // NumericFactory impls
    @Override public ZnElement zero() { return additiveIdentity; }
    @Override public ZnElement one() { return multiplicativeIdentity; }
    @Override public ZnElement of(long value) { return new ZnElement(IntegerRing.INSTANCE.of(value), this.modulus); }
    @Override public ZnElement of(int value) { return new ZnElement(IntegerRing.INSTANCE.of(value), this.modulus); }
    @Override
	public ZnElement of(double value) {
    	if (!Double.isFinite(value)) {
	        throw new IllegalArgumentException("Cannot create a SignedInt number from a non-finite value: " + value);
	    }
    	long roundedValue = Math.round(value);
        if (Math.abs(value - roundedValue) > MathConstants.EPSILON) {
            throw new IllegalArgumentException("Value " + value + " is not an integer (within epsilon).");
        }

        SignedInt signedInt = IntegerRing.INSTANCE.of(roundedValue);
        return new ZnElement(signedInt, this.modulus);
	}

    /**
     * Crea un elemento ZModNElement, garantendo che sia ridotto modulo n.
     * Questo metodo funge da "fabbrica" per gli elementi dell'anello.
     * * @param value Il rappresentante intero.
     * @return L'elemento [value] in Z/nZ.
     */
    public ZnElement getElement(SignedInt value) {
        return new ZnElement(value, this.modulus);
    }


    // helpers
    private void checkModulus(ZnElement e) {
        if (!e.getModulus().equals(this.modulus)) {
            throw new IllegalArgumentException("Element modulus mismatch. Expected: " + modulus);
        }
    }


    // ScalarStructure impls
    @Override
    public Real magnitude(ZnElement a) {
        return RealField.INSTANCE.of(a.getValue().abs().getValue()); 
    }


    // AdditiveMonoid impls
    @Override
    public ZnElement add(ZnElement a, ZnElement b) {
        checkModulus(a); checkModulus(b);
        return new ZnElement(IntegerRing.INSTANCE.add(a.getValue(), b.getValue()), modulus);
    }


    // MultiplicativeMonoid impls
    @Override
    public ZnElement multiply(ZnElement a, ZnElement b) {
        checkModulus(a); checkModulus(b);
        return new ZnElement(IntegerRing.INSTANCE.multiply(a.getValue(), b.getValue()), modulus);
    }


     // AdditiveGroup impls
    @Override
    public ZnElement negate(ZnElement e) {
        checkModulus(e);
        return new ZnElement(IntegerRing.INSTANCE.negate(e.getValue()), modulus);
    }


    @Override // AlgebraicStructure impls
    public String getName() {
        return "Z/" + this.modulus.toString() + "Z Ring";
    }

    @Override
    public boolean contains(ZnElement e) {
        return e != null && e.getModulus().equals(this.modulus);
    }
 

    @Override // Java Standard impls
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof ZnRing)) return false;
        ZnRing that = (ZnRing) other;
        return this.modulus.equals(that.modulus);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(modulus);
    }
}