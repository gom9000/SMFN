
package net.gommagomma.smfn.math.analysis.core.solvers;

import net.gommagomma.smfn.math.algebra.numeric.Real;

/**
* Definisce il test logico per determinare se una sequenza di iterazioni è convergente.
* Il test si basa sulla distanza (Real) calcolata dal Solutore.
*/
public interface ConvergenceCriteria
{
   /**
    * Verifica se la condizione di convergenza è stata raggiunta.
    * @param distance La distanza (Real) tra l'iterazione attuale e la precedente, calcolata dal Solutore.
    * @param params I parametri di convergenza (tolleranza).
    * @param iteration Il numero di iterazione corrente.
    * @return true se il solutore è convergente.
    */
   boolean isConverged(Real distance, ConvergenceParameters params, int iteration);
}
