/**
 * 
 */
package org.testtoolinterfaces.testsuiteinterface;

import org.xml.sax.helpers.LocatorImpl;

/**
 * @author Arjan Kranenburg
 *
 */
public class TestReaderException extends Exception
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= -2021473964661158931L;

	private LocatorImpl myLocation;
	
	/**
	 * @param aMessage
	 */
	public TestReaderException(String aMessage)
	{
		super(aMessage);
	}

	/**
	 * @param aCause
	 */
	public TestReaderException(Throwable aCause)
	{
		super(aCause);
	}

	/**
	 * @param aMessage
	 * @param aCause
	 */
	public TestReaderException(String aMessage, Throwable aCause)
	{
		super(aMessage, aCause);
	}

	public TestReaderException(String aMessage, LocatorImpl aLocatorImpl)
	{
		super(aMessage);
		myLocation = aLocatorImpl;
	}
	
	public LocatorImpl getLocation()
	{
		return myLocation;
	}
}
