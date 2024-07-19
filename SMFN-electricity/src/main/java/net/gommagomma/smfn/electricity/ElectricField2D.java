/*
 * ElectricField2D.java
 *  __ _ ___ _ __  _ __  __ _ __ _ ___ _ __  _ __  __ _ 
 * / _` / _ \ '  \| '  \/ _` / _` / _ \ '  \| '  \/ _` |
 * \__, \___/_|_|_|_|_|_\__,_\__, \___/_|_|_|_|_|_\__,_|
 * |___/                     |___/                      
 *
 * gommagomma.net - SMFN
 */


package net.gommagomma.smfn.electricity;


import java.util.ArrayList;

import net.gommagomma.smfn.math.OrderOfMagnitude;
import net.gommagomma.smfn.math.algebra.Real;
import net.gommagomma.smfn.math.algebra.RealField;
import net.gommagomma.smfn.math.algebra.VectorSpace;


/**
 * inteso come campo di forze... 
 * logica di posizionamento delle cariche (coordinate)...
 * ogni punto contiene un vettore con tutte le grandezze (quali?) calcolate tramite "calculateElectricField()"...
 * e l'evoluzione del campo nel tempo???
 * conviene che estenda o usi VectorSpace???
 *
 */
public class ElectricField2D
extends VectorSpace<Real>
{
	// magnitude // magnitude of electric field at this point


	private ArrayList<Charge> sourceCharges;
	private ArrayList<Charge> fixedCharges;
	private ArrayList<Charge> mobileCharges;


	public ElectricField2D(double sizeX, double sizeY, OrderOfMagnitude order)
	{
		super(2, new RealField());
		sourceCharges = new ArrayList<Charge>();
		fixedCharges = new ArrayList<Charge>();
		mobileCharges = new ArrayList<Charge>();
	}


	public void addSourceCharge(Charge c)
	{
		sourceCharges.add(c);
	}


	public void addFixedCharge(Charge c)
	{
		fixedCharges.add(c);
	}


	public void addMobileCharge(Charge c)
	{
		mobileCharges.add(c);
	}


	public void calculateElectricField()
	{}
}
