package net.gommagomma.smfn.math.algebra.core.elements.multiplicative;

import net.gommagomma.smfn.math.algebra.core.elements.capabilities.ComparableElement;


/**
* Estende i CommutativeRingElement per modellare gli elementi di un Dominio Euclideo (DE).
* Un DE è un anello commutativo con unità su cui è definita una Funzione Norma (o Valutazione)
* che permette la Divisione Euclidea.
*/
public interface EuclideanDomainElement<E extends EuclideanDomainElement<E, N>, N extends ComparableElement<N>> 
extends CommutativeRingElement<E>
{
   /**
    * Calcola la Funzione Norma Euclidea (o Valutazione) di questo elemento.
    * Questa funzione mappa l'elemento a un valore in un anello ordinato (solitamente Naturali o SignedInt).
    * @return Il valore della norma.
    */
   N normValue();
   
   /**
    * Esegue la Divisione Euclidea: this = q * divisor + remainder, dove normValue(remainder) < normValue(divisor)
    * Ritorna il resto r, garantendo che 0 <= r < |divisor|.
    * @param divisor L'elemento per cui dividere.
    * @return Il resto della divisione.
    * @throws ArithmeticException se il divisore è zero.
    */
   E remainder(E divisor);

   /**
    * Esegue la Divisione Euclidea: this = q * divisor + remainder.
    * Ritorna il quoziente q.
    * @param divisor L'elemento per cui dividere.
    * @return Il quoziente della divisione.
    */
   E quotient(E divisor);

   /**
    * Alias per remainder, comunemente usato per l'aritmetica modulare.
    */
   default E mod(E divisor) {
       return remainder(divisor);
   }
}
