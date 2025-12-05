package net.gommagomma.smfn.math.linearalgebra.core.elements.vectors;

import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.SemiringElement;


public abstract class AbstractRank1Tensor<K extends SemiringElement<K>, V extends SemimoduleElement<K, V>> 
implements SemimoduleElement<K, V> 
{
     protected final int dimension; 
 

     /**
      * Costruttore per AbstractRank1Tensor. 
      * Da chiamare dal costruttore della sottoclasse.
      * @param dimension La dimensione (numero di componenti) del vettore.
      */
     protected AbstractRank1Tensor(int dimension) {
         if (dimension <= 0) {
             throw new IllegalArgumentException("Vector dimension must be positive.");
         }
         this.dimension = dimension;
     }


     @Override
     public int dimension() {
         return this.dimension;
     }


     // TensorElement impls

 	@Override
 	public int getRank() {
 		return 1;
 	}

 	@Override
 	public int[] getShape() {
 		return new int[] { this.dimension };
 	}

 	@Override
 	public long size() {
 		return this.dimension;
 	}

 	@Override
 	public final K get(int... indices) {
 		if (indices.length != getRank()) {
 			throw new IllegalArgumentException("Indices length must match the Tensor Rank (1).");
 		}

 		return get(indices[0]); 
 	}
}
