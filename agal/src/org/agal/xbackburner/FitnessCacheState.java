/*
 * FitnessCacheState.java
 * 
 * Created on Aug 16, 2013
 * 
 */
package org.agal.xbackburner;

/**
 * FitnessCacheState is a simple but useful construct which wraps a candidate solution
 * state of type {@code S} with its calculated fitness value. Its use is supported by the
 * {@code AbstractFitnessEvaluator}, which will automatically cache the fitness value upon
 * first calculation.
 * <p>
 * FitnessCacheState is thread-safe.
 * @author David Schmidt
 */
public class FitnessCacheState<S>
{
	// Data members.
	private final S fieldState;
	private volatile Integer fieldFitness;


	/**
	 * FitnessCacheState constructor.
	 */
	public FitnessCacheState( S state )
	{
		fieldState = state;

	} // FitnessCacheState


	public Integer getFitness( )
	{
		return fieldFitness;

	} // getFitness


	public S getState( )
	{
		return fieldState;

	} // getState


	public void setFitness( Integer fitness )
	{
		fieldFitness = fitness;

	} // setFitness

}
