/*
 * OrderOfMagnitude.java
 *  __ _ ___ _ __  _ __  __ _ __ _ ___ _ __  _ __  __ _ 
 * / _` / _ \ '  \| '  \/ _` / _` / _ \ '  \| '  \/ _` |
 * \__, \___/_|_|_|_|_|_\__,_\__, \___/_|_|_|_|_|_\__,_|
 * |___/                     |___/                      
 *
 * gommagomma.net - SMFN
 */


package net.gommagomma.smfn.math;


public enum OrderOfMagnitude
{
	PETA(15),
	TERA(12),
	GIGA(9),
	MEGA(6),
	KILO(3),
    UNITY(0),
    MILLI(-3),
    MICRO(-6),
    NANO(-9),
    PICO(-12),
    FEMTO(-15);

	public final int order;


	private OrderOfMagnitude(int order)
	{
		this.order = order;
	}


	public double getValue()
    {
    	return Math.pow(10, order);
    }
}
