package net.gommagomma.smfn.math.algebra.structures;

import net.gommagomma.smfn.math.algebra.core.structures.CommutativeRing;
import net.gommagomma.smfn.math.algebra.numeric.SignedInt;
import net.gommagomma.smfn.math.algebra.numeric.SignedIntFactory;
import net.gommagomma.smfn.math.algebra.numeric.ZnElement;

/**
 * Rappresenta la struttura dell'Anello Commutativo Z/nZ (ZModNRing).
 * Agisce da fabbrica per creare elementi validi in Z_n.
 * * Z/nZ è un Campo se e solo se n è primo. Qui è implementato come Anello generico.
 */
public final class ZnRing
implements CommutativeRing<ZnElement>
{
    private final SignedInt modulus;
    private final ZnElement additiveIdentity;
    private final ZnElement multiplicativeIdentity;


    /**
     * Costruisce l'anello Z/nZ specificando il modulo n.
     * @param modulus Il modulo n (deve essere un intero positivo > 0).
     */
    public ZnRing(SignedInt modulus) {
        if (modulus.isZero() || modulus.isLessThan(SignedIntFactory.getInstance().one())) {
            throw new IllegalArgumentException("Modulus for ZModNRing must be a positive integer > 0.");
        }
        this.modulus = modulus;
        this.additiveIdentity = new ZnElement(SignedIntFactory.getInstance().zero(), this.modulus);
        this.multiplicativeIdentity = new ZnElement(SignedIntFactory.getInstance().one(), this.modulus);
    }
    

    /**
     * Restituisce il modulo n che definisce questo anello.
     */
    public SignedInt getModulus() {
        return modulus;
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


    @Override // AlgebraicStructure impls
    public String getName() {
        return "Z/" + this.modulus.toString() + "Z Ring";
    }

    @Override // AlgebraicStructure impls
    public boolean contains(ZnElement e) {
        return e.getModulus().isMathematicallyEqualTo(this.modulus);
    }


    @Override // AdditiveMonoid impls
    public ZnElement additiveIdentity() {
        return this.additiveIdentity;
    }


    @Override // MultiplicativeMonoid impls
    public ZnElement multiplicativeIdentity() {
        return this.multiplicativeIdentity;
    }
 

    @Override // Java Standard impls
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof ZnRing)) return false;
        ZnRing that = (ZnRing) other;
        return this.modulus.isMathematicallyEqualTo(that.modulus);
    }

    @Override // Java Standard impls
    public int hashCode() {
        return java.util.Objects.hash(modulus);
    }
}