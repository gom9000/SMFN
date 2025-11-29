package net.gommagomma.smfn.physics.mq;

import net.gommagomma.smfn.math.algebra.numeric.Complex;
import net.gommagomma.smfn.math.algebra.numeric.Real;
import net.gommagomma.smfn.math.linearalgebra.complex.ComplexMatrix;
import net.gommagomma.smfn.math.linearalgebra.complex.ComplexVector;

/**
 * Rappresenta un operatore Hamiltoniano (energia) implementato usando la composizione.
 * Contiene una ComplexMatrix internamente.
 */
final class HamiltonianOperator implements Observable<Complex, ComplexVector, HamiltonianOperator>
{
    private final ComplexMatrix matrix;

    /**
     * Costruttore parametrizzato per un operatore Hamiltoniano di dimensione NxN.
     * @param data I dati della matrice NxN.
     */
    public HamiltonianOperator(Complex[][] data) {
        // Inizializza la matrice interna
        this.matrix = new ComplexMatrix(data);
    }

    @Override
    public Real expectationValue(ComplexVector state) {
        ComplexVector phi = this.multiply(state); 
        Complex expectationComplex = state.dotProduct(phi);
        return new Real(expectationComplex.getRe());
    }
    
    @Override
    public ComplexVector evaluate(ComplexVector vector) {
        // evaluate() e multiply() sono la stessa operazione logica per un operatore lineare
        return this.multiply(vector);
    }

    /**
     * Esegue la moltiplicazione tra questa matrice e un vettore.
     * La logica è spostata qui o delegata a un helper.
     */
    public ComplexVector multiply(ComplexVector vector) {
         if (this.matrix.getColumns() != vector.dimension()) {
             throw new IllegalArgumentException("Matrix columns must match vector dimension for multiplication.");
         }
         
         Complex[] resultData = new Complex[this.matrix.getRows()];
         for (int i = 0; i < this.matrix.getRows(); i++) {
             Complex sum = Complex.ZERO;
             for (int j = 0; j < this.matrix.getColumns(); j++) {
                 sum = sum.add(this.matrix.get(i, j).multiply(vector.get(j)));
             }
             resultData[i] = sum;
         }
         
         return new ComplexVector(resultData);
    }
}
