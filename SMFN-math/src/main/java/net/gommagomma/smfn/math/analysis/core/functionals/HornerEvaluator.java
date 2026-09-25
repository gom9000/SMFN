package net.gommagomma.smfn.math.analysis.core.functionals;

import net.gommagomma.smfn.math.algebra.core.Mapping;
import net.gommagomma.smfn.math.algebra.core.elements.AlgebraicElement;
import net.gommagomma.smfn.math.algebra.core.structures.Ring;
import net.gommagomma.smfn.math.analysis.core.functionals.HornerEvaluator.CoefficientSequence;

/**
 * Valutatore di polinomi basato sulla regola di Horner (schema di Horner).
 * Implementa l'interfaccia EvaluationFunctional fornendo un algoritmo per calcolare
 * il valore assunto da un polinomio $P(x) = a_n x^n + .. + a_1 x + a_0$
 * in uno specifico punto appartenente ad un anello algebrico.
 * 
 * @param <K> Il tipo degli elementi algebrici (coefficienti e punto di valutazione)
 * @param <S> Il tipo della struttura algebrica di anello sottostante
 * @param <F> Il tipo della funzione che espone sia l'interfaccia di mapping sia la sequenza di coefficienti
 */
public class HornerEvaluator<K extends AlgebraicElement<K>, S extends Ring<K>, F extends Mapping<K, K> & CoefficientSequence<K>> 
implements EvaluationFunctional<F, K, K>
{
	private final S structure;

	/**
     * Costruisce un valutatore di Horner associato alla struttura algebrica di anello specificata.
     *
     * @param structure La struttura d'anello usata per eseguire le operazioni di somma, moltiplicazione e azzeramento
     */
    public HornerEvaluator(S structure) {
        this.structure = structure;
    }

    /**
     * Valuta la funzione polinomiale nel punto fornito applicando la regola di Horner.
     * Se il grado del polinomio è negativo (rappresentando il polinomio nullo),
     * viene restituito l'elemento neutro additivo (zero) della struttura.
     *
     * @param function Il polinomio da valutare
     * @param point Il punto appartenente all'anello in cui calcolare il valore
     * @return Il valore scalare risultante dalla valutazione $P(point)$
     */
    @Override
    public K evaluate(F function, K point) {
        int n = function.degree();
        if (n < 0) return structure.zero();

        K result = function.getCoefficient(n);
        for (int i = n - 1; i >= 0; i--) {
            result = structure.add(structure.multiply(result, point), function.getCoefficient(i));
        }
        return result;
    }

    /**
     * Astrazione per le strutture che espongono una sequenza finita di coefficienti polinomiali.
     *
     * @param <K> Il tipo dei coefficienti
     */
    public interface CoefficientSequence<K> {
    	/**
        * Restituisce il grado del polinomio.
        *
        * @return Il grado massimo con coefficiente non nullo, oppure un valore negativo per il polinomio nullo
        */
        int degree();

        /**
         * Restituisce il coefficiente del termine di grado index.
         *
         * @param index L'indice del coefficiente
         * @return Il coefficiente scalare corrispondente
         */
        K getCoefficient(int index);
    }
}
