package net.gommagomma.smfn.math.analysis.numerical.solvers.ode;

import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.algebra.structures.RealField;

/**
 * Avanzamento del tempo di integrazione. Il tempo e' un numero in virgola mobile: un passo
 * inferiore alla meta' della distanza tra due tempi rappresentabili vicino a {@code t} non
 * modifica {@code t}, e l'integrazione non progredirebbe mai.
 */
final class TimeAdvance
{
	private TimeAdvance() {}

	/**
	 * @return {@code time + step}
	 * @throws IllegalStateException se la somma non e' diversa da {@code time}, cioe' se il passo non e'
	 *         rappresentabile al tempo corrente
	 */
	static Real advance(Real time, Real step) {
		Real next = RealField.INSTANCE.add(time, step);
		if (next.getValue() == time.getValue()) {
			throw new IllegalStateException("Il passo " + step + " non fa avanzare il tempo " + time
				+ ": e' inferiore alla risoluzione dei numeri in virgola mobile a questo istante.");
		}
		return next;
	}
}
