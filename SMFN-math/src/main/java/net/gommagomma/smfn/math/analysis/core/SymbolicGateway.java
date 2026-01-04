package net.gommagomma.smfn.math.analysis.core;

import net.gommagomma.smfn.math.algebra.core.elements.AlgebraicElement;
import net.gommagomma.smfn.math.algebra.core.structures.AlgebraicStructure;
import net.gommagomma.smfn.math.algebra.core.structures.capabilities.SymbolicDifferentiationProvider;

public class SymbolicGateway {
    public <E extends AlgebraicElement<E>> E derive(E element) {
        // 1. Recupera la struttura dell'elemento
        AlgebraicStructure<E> structure = element.getStructure();
        
        // 2. Verifica se la struttura sa fare calcolo simbolico
        if (structure instanceof SymbolicDifferentiationProvider) {
            return ((SymbolicDifferentiationProvider<E>) structure).derive(element);
        }
        
        throw new UnsupportedOperationException("Questa struttura non supporta la derivata simbolica.");
    }
}
