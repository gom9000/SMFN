package net.gommagomma.smfn.math.core.algebra.structures;


import net.gommagomma.smfn.math.core.algebra.elements.additive.GroupElement;


// Un gruppo è un monoide con inversi garantiti (gestiti dall'interfaccia GroupElement)
public interface Group<E extends GroupElement<E>>
extends AdditiveMonoid<E>
{}
