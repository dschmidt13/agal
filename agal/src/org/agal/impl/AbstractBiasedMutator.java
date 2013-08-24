/*
 * AbstractBiasedMutator.java
 * 
 * Created on Aug 24, 2013
 * 
 */
package org.agal.impl;

import java.util.Random;

import org.agal.core.Mutator;
import org.agal.core.SearchContext;

/**
 * AbstractBiasedMutator is a base {@link Mutator} implementation which relies on the bias
 * named by {@code BIAS_KEY_MUTATION_RATE} and random numbers provided by the
 * {@code SearchContext} to determine how frequently to mutate.
 * <p>
 * AbstractBiasedMutator is thread safe.
 * @author David Schmidt
 */
public abstract class AbstractBiasedMutator<S> implements Mutator<S>
{
	// Class constants.
	public static final String BIAS_KEY_MUTATION_RATE = AbstractBiasedMutator.class.getName( )
			+ ".mutationRate";

	// Data members.
	private final SearchContext<S> fieldSearchContext;


	public AbstractBiasedMutator( SearchContext<S> searchContext )
	{
		fieldSearchContext = searchContext;

	} // AbstractBiasedMutator


	protected final SearchContext<S> getSearchContext( )
	{
		return fieldSearchContext;

	} // getSearchContext


	@Override
	public int mutateCount( S state )
	{
		Random random = fieldSearchContext.getRandom( );

		double chance = random.nextDouble( );

		double softBias = fieldSearchContext.getBias( BIAS_KEY_MUTATION_RATE );
		int hardBias = ( int ) softBias;
		softBias -= ( int ) softBias;

		return hardBias + ( chance > ( 1 - softBias ) ? 1 : 0 );

	} // mutateCount

}
