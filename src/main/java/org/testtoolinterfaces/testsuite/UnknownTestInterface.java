/**
 * 
 */
package org.testtoolinterfaces.testsuite;

import java.util.ArrayList;

/**
 * @author Arjan Kranenburg
 *
 * Class for Unknown TestInterfaces
 * This can be used when TestGroup or TestCase files are read and where interfaces
 * are mentioned that are not known within this application.
 * This is not necessarily an error, since other applications can have these
 * interfaces defined.
 * 
 * It serves only as place-holder for the interface name.
 * It does not have any commands and the other functions are only implemented
 * to satisfy the interface.
 * The constructor and getInterfaceName() are the only methods that should be
 * used.
 */
public class UnknownTestInterface implements TestInterface
{
	private String myName;

	/**
	 * Constructor for the UnknownTestInterface
	 * 
	 * @param aName	The name of the unknown interface
	 */
	public UnknownTestInterface( String aName )
	{
		myName = aName;
	}

	@Deprecated
	public ParameterImpl createParameter(String aName, String aType, String aValue)
					 throws TestSuiteException
	{
		ParameterImpl param = new ParameterImpl(aName, aValue);
		return param;
	}

	@Deprecated
	public ArrayList<String> getCommands()
	{
		return new ArrayList<String>();
	}

	public String getInterfaceName()
	{
		return myName;
	}

	@Deprecated
	public boolean hasCommand(String aCommand)
	{
		return false;
	}

	@Deprecated
	public boolean verifyParameters( String aCommand,
									 ParameterArrayList aParameters )
				   throws TestSuiteException
	{
		return true;
	}

}
