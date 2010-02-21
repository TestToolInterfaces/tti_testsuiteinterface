/**
 * 
 */
package org.testtoolinterfaces.testsuiteinterface;

import org.xml.sax.helpers.LocatorImpl;

/**
 * @author Arjan Kranenburg
 *
 */
public class TestGroupReaderException extends Exception
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= -2021473964661158931L;

	private LocatorImpl myLocation;
	
	/**
	 * @param aMessage
	 */
	public TestGroupReaderException(String aMessage)
	{
		super(aMessage);
	}

	/**
	 * @param aCause
	 */
	public TestGroupReaderException(Throwable aCause)
	{
		super(aCause);
	}

	/**
	 * @param aMessage
	 * @param aCause
	 */
	public TestGroupReaderException(String aMessage, Throwable aCause)
	{
		super(aMessage, aCause);
	}

	public TestGroupReaderException(String aMessage, LocatorImpl aLocatorImpl)
	{
		super(aMessage);
		myLocation = aLocatorImpl;
	}
	
	public LocatorImpl getLocation()
	{
		return myLocation;
	}
}
