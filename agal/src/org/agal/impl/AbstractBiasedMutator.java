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
 * {@code SearchContext}'s Random to determine how frequently to mutate.
 * <p>
 * AbstractBiasedMutator is thread safe.
 * @author David Schmidt
 */
public abstract class AbstractBiasedMutator<S> implements Mutator<S>
{
	// Class constants.
	/**
	 * Defines the average number of mutations that should occur per state evaluated. For
	 * instance, any value {@code x} greater than 1 and less than 2 will guarantee at
	 * least 1 mutation and have an {@code x - 1} probability of a second mutation; a
	 * value of {@code 2.3} would guarantee two mutations and cause a 30% chance at a
	 * third.
	 * <p>
	 * A bias source capable of supplying values for {@code BIAS_KEY_MUTATION_RATE} must
	 * be defined.
	 */
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

		// For a bias of 3.5: hardBias = 3, softBias = 0.5.
		double softBias = fieldSearchContext.getBias( BIAS_KEY_MUTATION_RATE );
		int hardBias = ( int ) softBias;
		softBias -= ( int ) softBias;

		// With the remainder (softBias), treat that as the % chance of mutating. (0.4 =
		// 40%, 1 - 0.4 = 0.6, random double between 0 and 1 should produce a value > 0.6
		// 40% of the time.)
		return hardBias + ( chance > ( 1 - softBias ) ? 1 : 0 );

	} // mutateCount

}
