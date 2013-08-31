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
 * average fitness increases. Bias values supplied to the evolver might even (at the
 * discretion of the implementation) have a bit of randomness to them, as in the classic
 * Genetic Algorithm's way of occasionally allowing low-fitness individuals to reproduce.
 * <p>
 * When choosing or implementing a BiasSource, it is important to know which biases are
 * supported by the EvolutionAlgorithm being used, how exactly these biases are used, and
 * what the range of valid values is. These are documented by the various pluggable
 * components which leverage the BiasSource framework.
 * @author David Schmidt
 */
public interface BiasSource
{
	// TODO - Find a better place for these and/or break these out to their
	// implementations.
	@Deprecated
	public static final String BIAS_KEY_SELECTIVITY = "selectivity";
	@Deprecated
	public static final String BIAS_KEY_MUTATION_RATE = "mutationRate";


	/**
	 * Retrieves a bias value. This value may be fixed or dynamically calculated; it may
	 * depend on the given {@code biasKey}, or the implementation may ignore it.
	 * @param biasKey a String containing a clue as to which bias is being requested. Some
	 *            elaborate {@code BiasSources}, such as
	 *            {@link org.agal.impl.CompoundBiasSource} or custom implementations, may
	 *            use this heavily. Others such as {@link org.agal.impl.FixedBiasSource}
	 *            may completely disregard it, as they are designed to supply biases for
	 *            only a single key per instance anyway.
	 * @return the {@code bias} value from this source for the given biasKey, as a double.
	 */
	public double getBias( String biasKey );

}
