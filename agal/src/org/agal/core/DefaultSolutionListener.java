/*
 * DefaultSolutionListener.java
 * 
 * Created on Aug 10, 2013
 * 
 */
package org.agal.core;

/**
 * DefaultSolutionListener
 * @author Dave
 */
public class DefaultSolutionListener<S> implements EvolutionListener
{
	// Data members.
	private final SearchContext<S> fieldSearchContext;


	public DefaultSolutionListener( SearchContext<S> searchContext )
	{
		fieldSearchContext = searchContext;

	} // DefaultSolutionListener


	@Override
	@SuppressWarnings( "unchecked" )
	public void onEvent( int eventId, Object eventObject )
	{
		if ( eventId == EvolutionListener.EVENT_ID_MEMBER_ADDED_TO_POPULATION )
			{
			fieldSearchContext.tryUpdateBestResult( ( S ) eventObject );
			}

	} // onEvent

}
