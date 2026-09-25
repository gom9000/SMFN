package net.gommagomma.smfn.math.algebra.polynomial;

import java.util.ArrayList;
import java.util.List;

import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.algebra.core.elements.factories.CompositeElementFactory;
import net.gommagomma.smfn.math.algebra.core.elements.factories.NumericFactory;
import net.gommagomma.smfn.math.algebra.core.structures.composite.ScalarStructure;

/**
 * Factory per la creazione semplificata di istanze di polinomi
 * a partire da liste di coefficienti, array di elementi scalari o valori numerici primitivi.
 */
public final class PolynomialElementFactory
{
    private PolynomialElementFactory() {}

    /**
     * Crea un nuovo polinomio a partire da una struttura scalare e da una lista di coefficienti.
     * 
     * @param <K> il tipo degli elementi scalari
     * @param scalarStructure la struttura algebrica dei coefficienti
     * @param coefficients la lista dei coefficienti ordinati per grado crescente
     * @return l'istanza di Polynomial corrispondente
     */
    public static <K extends ScalarElement<K>> Polynomial<K> of(ScalarStructure<K> scalarStructure, List<K> coefficients) {
        ScalarStructure<Polynomial<K>> polynomialStructure = PolynomialStructureFactory.getStructureFor(scalarStructure);
        @SuppressWarnings("unchecked")
        CompositeElementFactory<Polynomial<K>, List<K>> factory = (CompositeElementFactory<Polynomial<K>, List<K>>) polynomialStructure;
        return factory.of(coefficients);
    }

    /**
     * Crea un nuovo polinomio a partire da una struttura scalare e da un numero variabile (varargs) di elementi scalari.
     * 
     * @param <K> il tipo degli elementi scalari
     * @param scalarStructure la struttura algebrica dei coefficienti
     * @param values i coefficienti scalari passati come argomenti
     * @return l'istanza di Polynomial corrispondente
     */
    @SafeVarargs
    public static <K extends ScalarElement<K>> Polynomial<K> of(ScalarStructure<K> scalarStructure, K... values) {
        return of(scalarStructure, List.of(values));
    }

    /**
     * Crea un nuovo polinomio a partire da una struttura scalare e da un array di valori numerici primitivi (`double`), 
     * sfruttando la fabbrica numerica associata alla struttura scalare.
     * 
     * @param <K> il tipo degli elementi scalari
     * @param scalarStructure la struttura algebrica dei coefficienti (deve implementare {@link NumericFactory})
     * @param values i valori numerici primitivi per i coefficienti ordinati per grado crescente
     * @return l'istanza di Polynomial corrispondente
     */
    public static <K extends ScalarElement<K>> Polynomial<K> of(ScalarStructure<K> scalarStructure, double... values) {
        @SuppressWarnings("unchecked")
        NumericFactory<K> factory = (NumericFactory<K>) scalarStructure;
        List<K> coefficients = new ArrayList<>(values.length);
        for (double v : values) {
            coefficients.add(factory.of(v));
        }

        return of(scalarStructure, coefficients);
    }
}