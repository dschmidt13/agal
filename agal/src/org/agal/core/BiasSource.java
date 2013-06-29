/*
 * BiasSource.java
 * 
 * Created on Jun 26, 2013
 * 
 */
package org.agal.core;

/**
 * A BiasSource is a sort of moderator that aims to direct the flow of evolution in an
 * EvolutionAlgorithm. While the algorithm itself provides the container and tools for the
 * evolution process, it needs a BiasSource to supply it with values to catalyze the
 * process by influencing the probability of favorable events and decreasing the
 * probability of unfavorable events. BiasSource provides an easily extended variety of
 * bias values through a simple interface for things such as mutation rates, crossover
 * complexity, and selectivity when finding parents.
 * <p>
 * Implementations vary by the application, and in traditional simple problems it may be
 * sufficient to use largely static bias values. In other cases, techniques such as
 * coupling a {@code BiasSource} with an {@code EvolutionListener} in the same
 * implementation may allow for more intelligent decisions, such as adjusting the mutation
 * rate based on the average fitness of the population, or raising the selectivity as the
 * average fitness increases. Bias values supplied to the evolver may even have a bit of
 * randomness to them, as in the classic Genetic Algorithm's way of occasionally allowing
 * <p>
 * When choosing or implementing a BiasSource, it is important to know which biases are
 * supported by the EvolutionAlgorithm being used, how exactly these biases are used, and
 * what the range of valid values is. For instance, the bias value for
 * {@code BIAS_CODE_MUTATION_PROBABILITY} must be greater than 0. Any value {@code x}
 * greater than 1 and less than 2 will guarantee at least 1 mutation and have an
 * {@code x - 1} probability of a second mutation; a value of {@code 2.3} would guarantee
 * two mutations and cause a 30% chance at a third. (This is the default definition for
 * this bias, and all EvolutionAlgorithms supplied with this library comply with it;
 * custom EvolutionAlgorithm implementations need not necessarily respect it or any other
 * bias defined herein.)
 * @author Dave
 */
public interface BiasSource
{
	// LAM - Bias constants should probably be moved elsewhere if I decide to keep this
	// API format.
	public static final int BIAS_CODE_SELECTIVITY = 0;
	public static final int BIAS_CODE_MUTATION_PROBABILITY = 1;


	public double getBias( int biasCode );

}
