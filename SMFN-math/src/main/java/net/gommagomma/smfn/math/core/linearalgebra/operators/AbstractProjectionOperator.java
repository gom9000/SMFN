package net.gommagomma.smfn.math.core.linearalgebra.operators;

import net.gommagomma.smfn.math.core.algebra.elements.capabilities.NormableElement;
import net.gommagomma.smfn.math.core.algebra.elements.multiplicative.FieldElement;
import net.gommagomma.smfn.math.core.algebra.numeric.Real;
import net.gommagomma.smfn.math.core.linearalgebra.elements.InnerProductSpaceElement;


/**
 * Operatore di proiezione generico che funziona su qualsiasi spazio con prodotto interno.
 */
public abstract class AbstractProjectionOperator<K extends FieldElement<K> & NormableElement<Real, K>, V extends InnerProductSpaceElement<K, V>> 
implements ProjectionOperator<K, V, AbstractProjectionOperator<K, V>>
{
    protected final V direction; // Il vettore normalizzato su cui proiettare

    /**
     * Il costruttore richiede un vettore di direzione che normalizza internamente.
     */
    public AbstractProjectionOperator(V direction) {
        if (direction == null || direction.isZero()) {
            throw new IllegalArgumentException("Direction vector cannot be null or zero.");
        }
        
        // Calcola 1 / ||u||
        Real normValue = direction.norm();
        // Assumiamo che K possa essere costruito da un double o avere un metodo per la divisione scalare
        // Questa parte dipende da come hai implementato FieldElement/Real/Complex
        
        // Esegui la normalizzazione: u_norm = u * (1/||u||)
        // Questo è il punto dove il codice diventa un po' tricky senza un'interfaccia ScalarFactory
        // Dobbiamo creare lo scalare 1/normValue in modo generico...
        
        // METODO A: Assumendo che K possa essere creato da un double
        // K scaleFactor = (K) new Real(1.0 / normValue.modulus()); 
        
        // METODO B: Assumendo di avere accesso a una Factory o un metodo statico in K
        K scaleFactor = getInverseNormScalar(normValue);

        this.direction = direction.multiplyByScalar(scaleFactor);
    }
    
    // Metodo astratto che deve essere implementato per creare lo scalare generico
    protected abstract K getInverseNormScalar(Real normValue);


    @Override
    public V evaluate(V vector) {
        // La logica generica della proiezione: P_u(v) = (v . u) * u
        K dotProduct = vector.dotProduct(direction);
        return direction.multiplyByScalar(dotProduct);
    }
}
