// net.gommagomma.smfn.graphics.plotting.FunctionRenderableAdapter
package net.gommagomma.smfn.graphics.plotting;

import net.gommagomma.smfn.graphics.core.ColorMapper;
import net.gommagomma.smfn.graphics.core.Renderable;
import net.gommagomma.smfn.math.algebra.core.AlgebraicElement;
import net.gommagomma.smfn.math.analysis.core.MathFunction;

import java.util.function.Function;

/**
 * Un adattatore generico che trasforma una MathFunction in un oggetto Renderable.
 */
public class FunctionRenderableAdapter<I extends AlgebraicElement<I>, O extends AlgebraicElement<O>> 
implements Renderable<I> 
{
    private final MathFunction<I, O> mathFunction;
    private final ColorMapper colorMapper;
    private final Function<O, Double> outputToDoubleConverter;

    public FunctionRenderableAdapter(
        MathFunction<I, O> mathFunction, 
        ColorMapper colorMapper,
        Function<O, Double> outputToDoubleConverter) 
    {
        this.mathFunction = mathFunction;
        this.colorMapper = colorMapper;
        this.outputToDoubleConverter = outputToDoubleConverter;
    }

    @Override
    public Integer renderPixel(I coordinate) {
        O result = mathFunction.evaluate(coordinate);
        double value = outputToDoubleConverter.apply(result);
        
        // La logica di colorazione decide se restituire un colore o null/trasparente
        // In questo caso, il ColorMapper deve essere intelligente e restituire un colore
        // che ha senso per l'output.
        
        // Se si vuole supportare la trasparenza (null) qui, bisogna modificare ColorMapper
        // per restituire Integer anziché int e gestire il caso null. 
        // Per semplicità, assumiamo che il ColorMapper restituisca un colore solido.
        return colorMapper.toRGB(value);
    }
}
