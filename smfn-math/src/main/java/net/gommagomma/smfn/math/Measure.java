/*
 * Measure.java
 *  __ _ ___ _ __  _ __  __ _ __ _ ___ _ __  _ __  __ _ 
 * / _` / _ \ '  \| '  \/ _` / _` / _ \ '  \| '  \/ _` |
 * \__, \___/_|_|_|_|_|_\__,_\__, \___/_|_|_|_|_|_\__,_|
 * |___/                     |___/                      
 *
 * gommagomma.net - SMFN
 */


package net.gommagomma.smfn.math;


/**
 * Represents a measure.
 * @author gommagomma.net
 */
public class Measure
{
    private double magnitude;
    private OrderOfMagnitude order;
    private double value;


    /**
     * Create a measure of the given value.
     * @param value the value of the measure
     */
    public Measure(double value)
    {
    	this(value, OrderOfMagnitude.UNITY);
    }


    /**
     * Create a measure of the given magnitude and order-of-magnitude.
     * @param magnitude the magnitude of the measure
     * @param order the order-of-magnitude of the measure
     */
    public Measure(double magnitude, OrderOfMagnitude order)
    {
    	this.magnitude = magnitude;
    	this.order = order;
    	this.value = magnitude * order.getValue();
    }


	/**
	 * Return the magnitude of the measure.
     * @return the magnitude of the measure
     */
    public double getMagnitude()
    {
    	return magnitude;
    }


	/**
	 * Return the order of magnitude associated to the measure.
     * @return the order of magnitude of the measure
     */
    public OrderOfMagnitude getOrder()
    {
    	return order;
    }


	/**
	 * Return the effective value of the measure, calculated as magnitude * order-of-magnitude.
     * @return the effective value of the measure
     */
    public double getValue()
    {
    	return value;
    }
}
