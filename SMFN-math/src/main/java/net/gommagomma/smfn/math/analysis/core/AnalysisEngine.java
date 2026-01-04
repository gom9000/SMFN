package net.gommagomma.smfn.math.analysis.core;

public final class AnalysisEngine
{    
    // Gateway per operazioni che restituiscono nuovi elementi (es. derivata di un polinomio)
    public SymbolicGateway symbolic() { return new SymbolicGateway(); }
    
    // Gateway per operazioni che restituiscono risultati numerici (es. area, radici)
    public NumericalGateway numerical() { return new NumericalGateway(); }
    
    // Engine di valutazione per trasformare Elementi in Funzioni (Morphisms)
    public EvaluationEngine evaluator() { return new EvaluationEngine(); }
}
