/*
 * Selector.java
 * 
 * Created on Aug 10, 2013
 * 
 */
package org.agal.core;

import java.util.List;

/**
 * Selector
 * @author Dave
 */
public interface Selector<S>
{
	public void selectParents( Population<S> population, List<S> parents );

}
