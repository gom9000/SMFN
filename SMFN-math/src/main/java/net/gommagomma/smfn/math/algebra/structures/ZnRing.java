package net.gommagomma.smfn.math.algebra.structures;

import java.math.BigInteger;
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
 * Rappresenta la struttura dell'anello commutativo delle classi resto modulo n (Z/nZ, ZModNRing).
 * Implementa un meccanismo di memorizzazione nella cache (thread-safe) per riutilizzare le istanze degli anelli 
 * in base al modulo e funge da fabbrica per la creazione di elementi validi $Zn$.
 */
public final class ZnRing
implements CommutativeRing<ZnElement>, ExactStructure<ZnElement>, NumericFactory<ZnElement>
{
    private static final Map<SignedInt, ZnRing> CACHE = new ConcurrentHashMap<>();

    private final SignedInt modulus;
    private final ZnElement additiveIdentity;
    private final ZnElement multiplicativeIdentity;


    /**
     * Costruisce l'anello $\mathbb{Z}/n\mathbb{Z}$ specificando il modulo $n$.
     * 
     * @param modulus il modulo $n$ dell'anello (deve essere un intero positivo strettamente maggiore di zero)
     * @throws IllegalArgumentException se il modulo è minore o uguale a zero
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

    /**
     * Restituisce un'istanza condivisa (memorizzata in cache) dell'anello $\mathbb{Z}/n\mathbb{Z}$ per il modulo specificato.
     * 
     * @p_aram modulus il modulo dell'anello
     * @return l'istanza di {@link ZnRing} corrispondente
     */
    public static ZnRing forModulus(SignedInt modulus) {
        return CACHE.computeIfAbsent(modulus, ZnRing::new);
    }

    /**
     * Restituisce il modulo $n$ associato a questo anello.
     * 
     * @return il modulo sotto forma di intero con segno
     */
    public SignedInt getModulus() { return modulus; }


    // NumericFactory impls
    @Override public ZnElement zero() { return additiveIdentity; }
    @Override public ZnElement one() { return multiplicativeIdentity; }
    
    @Override 
    public ZnElement of(long value) { 
        return new ZnElement(IntegerRing.INSTANCE.of(value), this.modulus); 
    }
    
    @Override 
    public ZnElement of(int value) { 
        return new ZnElement(IntegerRing.INSTANCE.of(value), this.modulus); 
    }
    
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
     * Crea un elemento modulare $\mathbb{Z}_n$, garantendo che il rappresentante sia ridotto modulo $n$.
     * Funge da fabbrica specifica per la creazione di elementi all'interno di questo anello.
     * 
     * @param value il rappresentante intero iniziale
     * @return l'elemento corrispondente in $\mathbb{Z}/n\mathbb{Z}$
     */
    public ZnElement getElement(SignedInt value) {
        return new ZnElement(value, this.modulus);
    }


    /**
     * Verifica che il modulo dell'elemento fornito corrisponda a quello dell'anello corrente.
     * 
     * @_param e l'elemento modulare da controllare
     * @throws IllegalArgumentException se i moduli non coincidono
     */
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
        return new ZnElement(reducedResult(a, b, BigInteger::add), modulus);
    }


    // MultiplicativeMonoid impls
    @Override
    public ZnElement multiply(ZnElement a, ZnElement b) {
        checkModulus(a); checkModulus(b);
        return new ZnElement(reducedResult(a, b, BigInteger::multiply), modulus);
    }

    /**
     * Applica l'operazione a due valori gia' ridotti modulo n, poi riduce di nuovo
     * modulo n -- tutto in BigInteger, per evitare l'overflow di un long quando n e'
     * vicino a Long.MAX_VALUE (a*b o a+b, prima della riduzione, possono superare
     * abbondantemente il range di un long anche se a e b singolarmente non lo fanno).
     */
    private SignedInt reducedResult(ZnElement a, ZnElement b, java.util.function.BinaryOperator<BigInteger> op) {
        BigInteger bigA = BigInteger.valueOf(a.getValue().getValue());
        BigInteger bigB = BigInteger.valueOf(b.getValue().getValue());
        BigInteger bigN = BigInteger.valueOf(modulus.getValue());
        BigInteger reduced = op.apply(bigA, bigB).mod(bigN);
        return new SignedInt(reduced.longValueExact());
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