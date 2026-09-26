
package net.gommagomma.smfn.math.analysis.core.solvers;

import net.gommagomma.smfn.math.algebra.numerics.Real;

/**
* Definisce il test logico per determinare se una sequenza di iterazioni è convergente.
* Il test si basa sulla distanza (Real) calcolata dal Solutore.
*/
public interface StoppingCriteria
{
	/**
     * Valuta se lo stato corrente dell'iterazione soddisfa il criterio di convergenza stabilito.
     *
     * @param distance La misura dello scarto o della distanza corrente (es. norma del residuo o distanza tra iterati consecutivi)
     * @param params I parametri numerici di controllo della convergenza (tolleranza ed iterazioni massime)
     * @param iteration L'indice dell'iterazione corrente (k >= 0)
     * @return true se il processo e' da considerarsi converso con successo, false altrimenti
     */
   boolean shouldStop(Real distance, StoppingParameters params, int iteration);
}
