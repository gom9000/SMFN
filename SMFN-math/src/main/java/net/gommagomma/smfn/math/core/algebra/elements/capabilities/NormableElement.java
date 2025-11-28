package net.gommagomma.smfn.math.core.algebra.elements.capabilities;

import net.gommagomma.smfn.math.core.algebra.AlgebraicElement;
import net.gommagomma.smfn.math.core.algebra.elements.multiplicative.FieldElement;

// Interfaccia base per qualsiasi elemento che possiede una norma.
// N è il tipo del campo su cui la norma è definita (es. Real, Rational).
// E è il tipo dell'elemento stesso (es. Complex, RealVector).
public interface NormableElement<N extends FieldElement<N>, E extends NormableElement<N, E>> 
extends AlgebraicElement<E> 
{
    N norm();
}