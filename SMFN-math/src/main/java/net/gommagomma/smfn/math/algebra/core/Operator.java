package net.gommagomma.smfn.math.algebra.core;

/**
 * Rappresenta un operatore su uno spazio, ovvero un mapping in cui il dominio 
 * e il codominio coincidono (f: T -> T).
 *
 * @param <T> il tipo degli elementi su cui agisce l'operatore
 */
public interface Operator<T>
extends Mapping<T, T>
{
	/**
     * Restituisce l'operatore identità
     * 
     * @param <T> il tipo degli elementi
     * @return l'operatore identità
     */
    static <T> Operator<T> identity() {
        return t -> t;
    }

    /**
     * Esegue la concatenazione di questo operatore con un successivo.
     * 
     * @param next l'operatore da applicare successivamente
     * @return un nuovo operatore risultante dalla concatenazione
     */
    default Operator<T> then(Operator<T> next) {
        return (T t) -> next.apply(this.apply(t));
    }

    /**
     * Calcola la potenza n-esima dell'operatore (applicazione ripetuta n volte).
     * 
     * @param n il numero di volte che l'operatore deve essere applicato
     * @return un operatore che rappresenta l'applicazione iterata
     * @throws IllegalArgumentException se n è negativo
     */
    default Operator<T> power(int n) {
        if (n < 0) throw new IllegalArgumentException("Negative power not supported for general operators.");
        if (n == 0) return identity();
        return (T t) -> {
            T result = t;
            for (int i = 0; i < n; i++) result = this.apply(result);
            return result;
        };
    }
}
