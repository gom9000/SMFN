package net.gommagomma.smfn.math.algebra.polynomial;

import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.algebra.core.structures.CommutativeRing;
import net.gommagomma.smfn.math.algebra.core.structures.Field;
import net.gommagomma.smfn.math.algebra.core.structures.Ring;
import net.gommagomma.smfn.math.algebra.core.structures.Semiring;
import net.gommagomma.smfn.math.algebra.core.structures.composite.ScalarStructure;

/**
 * Factory per la creazione e la selezione della struttura algebrica polinomiale appropriata
 * (dominio euclideo, anello commutativo, anello o semianello) in base alla struttura scalare dei coefficienti fornita.
 */
public final class PolynomialStructureFactory
{
    private PolynomialStructureFactory() {}

    /**
     * Restituisce la struttura algebrica polinomiale appropriata per i coefficienti dotati della struttura scalare specificata,
     * seguendo la gerarchia algebrica (Campo/Dominio Euclideo, Anello Commutativo, Anello, Semianello).
     * 
     * @param <K> il tipo degli elementi scalari (coefficienti)
     * @param s la struttura scalare associata ai coefficienti
     * @return la struttura algebrica polinomiale corrispondente più specifica
     */
    public static <K extends ScalarElement<K>> ScalarStructure<Polynomial<K>> getStructureFor(ScalarStructure<K> s) {
        if (s instanceof Field) {
            return createEuclidean(s);
        }
        if (s instanceof CommutativeRing) {
            return createCommutativeRing(s);
        }
        if (s instanceof Ring) {
            return createRing(s);
        }
        return createSemiring(s);
    }

    /**
     * Crea un dominio euclideo di polinomi per una struttura di campo sottostante.
     * 
     * @param <K> il tipo degli elementi scalari
     * @param <S> il tipo della struttura scalare di campo
     * @param s la struttura scalare di campo
     * @return un'istanza di {@link EuclideanPolynomialRing}
     */
    @SuppressWarnings("unchecked")
    private static <K extends ScalarElement<K>, S extends Field<K> & ScalarStructure<K>> EuclideanPolynomialRing<K, S> createEuclidean(ScalarStructure<K> s) {
        return new EuclideanPolynomialRing<>((S) s);
    }

    /**
     * Crea un anello commutativo di polinomi per una struttura di anello commutativo sottostante.
     * 
     * @param <K> il tipo degli elementi scalari
     * @param <S> il tipo della struttura scalare di anello commutativo
     * @param s la struttura scalare di anello commutativo
     * @return un'istanza di {@link CommutativePolynomialRing}
     */
    @SuppressWarnings("unchecked")
    private static <K extends ScalarElement<K>, S extends CommutativeRing<K> & ScalarStructure<K>> PolynomialRing<K, S> createCommutativeRing(ScalarStructure<K> s) {
        return new CommutativePolynomialRing<>((S) s);
    }

    /**
     * Crea un anello di polinomi per una struttura di anello sottostante.
     * 
     * @param <K> il tipo degli elementi scalari
     * @param <S> il tipo della struttura scalare di anello
     * @param s la struttura scalare di anello
     * @return un'istanza di {@link PolynomialRing}
     */
    @SuppressWarnings("unchecked")
    private static <K extends ScalarElement<K>, S extends Ring<K> & ScalarStructure<K>> PolynomialRing<K, S> createRing(ScalarStructure<K> s) {
        return new PolynomialRing<>((S) s);
    }

    /**
     * Crea un semianello di polinomi per una struttura di semianello sottostante.
     * 
     * @param <K> il tipo degli elementi scalari
     * @param <S> il tipo della struttura scalare di semianello
     * @param s la struttura scalare di semianello
     * @return un'istanza di {@link PolynomialSemiring}
     */
    @SuppressWarnings("unchecked")
    private static <K extends ScalarElement<K>, S extends Semiring<K> & ScalarStructure<K>> PolynomialSemiring<K, S> createSemiring(ScalarStructure<K> s) {
        return new PolynomialSemiring<>((S) s);
    }
}