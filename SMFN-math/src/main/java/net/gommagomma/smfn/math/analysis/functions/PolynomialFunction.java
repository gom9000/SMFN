package net.gommagomma.smfn.math.analysis.functions;

import net.gommagomma.smfn.math.core.algebra.MathFunction;
import net.gommagomma.smfn.math.core.algebra.elements.multiplicative.CommutativeRingElement;
import net.gommagomma.smfn.math.core.algebra.elements.multiplicative.FieldElement;
import net.gommagomma.smfn.math.core.linearalgebra.elements.VectorElement;


/**
 * Rappresenta un polinomio con coefficienti in un campo K.
 * P(x) = c0 + c1*x + c2*x^2 + ... + cn*x^n
 * 
 * @param <K> Il tipo di campo dei coefficienti (es. Real, Rational, Complex)
 */
public final class PolynomialFunction<K extends FieldElement<K>> 
implements CommutativeRingElement<PolynomialFunction<K>>, MathFunction<K, K>
{
    // Usiamo un VectorElement per archiviare i coefficienti [c0, c1, ..., cn]
    private final VectorElement<K, ? extends VectorElement<K, ?>> coefficients;
    private final int degree;
    private final K zeroScalar; 


    /**
     * Costruisce un polinomio dai suoi coefficienti.
     * Il coefficiente a indice 0 è il termine costante.
     * @param coeffs Un vettore di coefficienti.
     */
    public PolynomialFunction(VectorElement<K, ? extends VectorElement<K, ?>> coeffs)
    {
        if (coeffs == null || coeffs.dimension() == 0) {
            throw new IllegalArgumentException("I coefficienti non possono essere nulli o vuoti.");
        }
        this.coefficients = coeffs;
        this.degree = coeffs.dimension() - 1;
        zeroScalar = coeffs.get(0).getZero();
    }

    public int getDegree() {
        return this.degree;
    }

    // --- Implementazione di MathFunction ---

    /**
     * Valuta il polinomio in un dato punto x utilizzando l'algoritmo di Horner.
     * Efficiente O(n) operazioni.
     * @param x Il punto in cui valutare il polinomio.
     * @return Il risultato R(x).
     */
    @Override
    public K evaluate(K x)
    {
        // Inizia con l'ultimo coefficiente: result = cn
        K result = coefficients.get(degree);
        
        // Iterazione all'indietro: result = result * x + c(i-1)
        for (int i = degree - 1; i >= 0; i--) {
            result = result.multiply(x).add(coefficients.get(i));
        }
        return result;
    }

    // --- Implementazione di AlgebraicElement (per sommare, copiare polinomi) ---

    @Override
    public boolean isEqual(PolynomialFunction<K> other)
    {
    	if (this.degree != other.degree) {
            return false;
        }

        for (int i = 0; i <= this.degree; i++) {
            if (!this.coefficients.get(i).isEqual(other.coefficients.get(i))) {
                return false;
            }
        }

        return true;
    }

    @Override
    public PolynomialFunction<K> copy() {
        return new PolynomialFunction<>(this.coefficients.copy());
    }

	@Override
	@SuppressWarnings("unchecked")
	public PolynomialFunction<K> getZero() {
        K zero = zeroScalar.getZero();
        // Chiamiamo createNewInstance su un'istanza esistente (coefficients)
        VectorElement<K, ?> zeroVector = this.coefficients.createNewInstance(zero); 
        return new PolynomialFunction<>(zeroVector);
	}

	@Override
	@SuppressWarnings("unchecked")
	public PolynomialFunction<K> getOne() {
        K one = zeroScalar.getOne();
        VectorElement<K, ?> oneVector = this.coefficients.createNewInstance(one);
        return new PolynomialFunction<>(oneVector);
	}

	@Override
    @SuppressWarnings("unchecked")
    public PolynomialFunction<K> negate() {
        int n = this.coefficients.dimension();
        K[] negatedCoeffs = (K[]) new FieldElement[n]; 

        for (int i = 0; i < n; i++) {
            negatedCoeffs[i] = this.coefficients.get(i).negate();
        }

        VectorElement<K, ?> resultVector = this.coefficients.createNewInstance(negatedCoeffs);
        return new PolynomialFunction<>(resultVector);
    }

	@Override
    @SuppressWarnings("unchecked")
    public PolynomialFunction<K> add(PolynomialFunction<K> other) {
        int maxLength = Math.max(this.coefficients.dimension(), other.coefficients.dimension());
        K[] resultCoeffs = (K[]) new FieldElement[maxLength];

        for (int i = 0; i < maxLength; i++) {
            K coeff1 = (i < this.coefficients.dimension()) ? this.coefficients.get(i) : zeroScalar.getZero();
            K coeff2 = (i < other.coefficients.dimension()) ? other.coefficients.get(i) : zeroScalar.getZero();
            resultCoeffs[i] = coeff1.add(coeff2);
        }

        int actualLength = maxLength;
        while (actualLength > 0 && resultCoeffs[actualLength - 1].isEqual(zeroScalar.getZero())) {
            actualLength--;
        }

        if (actualLength == 0) {
            return this.getZero();
        }
        
        K[] finalCoeffs = (K[]) new FieldElement[actualLength];
        System.arraycopy(resultCoeffs, 0, finalCoeffs, 0, actualLength);
        
        // Usiamo createNewInstance per creare il nuovo vettore
        VectorElement<K, ?> resultVector = this.coefficients.createNewInstance(finalCoeffs);
        return new PolynomialFunction<>(resultVector);
    }

	@Override
    @SuppressWarnings("unchecked")
    public PolynomialFunction<K> multiply(PolynomialFunction<K> other) {
        int degree1 = this.degree;
        int degree2 = other.degree;
        int resultDegree = degree1 + degree2;
        
        // Il prodotto avrà grado (grado1 + grado2) + 1 coefficienti
        K[] resultCoeffs = (K[]) new FieldElement[resultDegree + 1];
        
        // Inizializza l'array risultante a zero
        for (int i = 0; i <= resultDegree; i++) {
            resultCoeffs[i] = zeroScalar.getZero();
        }

        // Algoritmo di moltiplicazione (prodotto di Cauchy)
        for (int i = 0; i <= degree1; i++) {
            for (int j = 0; j <= degree2; j++) {
                // Calcola il termine di grado i+j = (c1[i] * c2[j])
                K product = this.coefficients.get(i).multiply(other.coefficients.get(j));
                // Aggiunge al coefficiente risultante
                resultCoeffs[i + j] = resultCoeffs[i + j].add(product);
            }
        }
        
        // Rimuove gli zeri finali se necessario (gestione dei polinomi nulli)
        int actualLength = resultDegree + 1;
        while (actualLength > 0 && resultCoeffs[actualLength - 1].isEqual(zeroScalar.getZero())) {
            actualLength--;
        }

        if (actualLength == 0) {
            return this.getZero();
        }

        // Se la dimensione finale è diversa, creiamo un array copiato della giusta lunghezza
        K[] finalCoeffs;
        if (actualLength < resultDegree + 1) {
            finalCoeffs = (K[]) new FieldElement[actualLength];
            System.arraycopy(resultCoeffs, 0, finalCoeffs, 0, actualLength);
        } else {
            finalCoeffs = resultCoeffs;
        }

        VectorElement<K, ?> resultVector = this.coefficients.createNewInstance(finalCoeffs);
            
        return new PolynomialFunction<>(resultVector);
    }
}
