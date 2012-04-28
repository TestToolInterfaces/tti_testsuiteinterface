package org.testtoolinterfaces.testsuite;

import java.util.ArrayList;

public class TestInterface_stub implements TestInterface
{
	private String myName;
	private ArrayList<String> myCommands;

	/**
	 * 
	 */
	public TestInterface_stub( String aName )
	{
		myName = aName;
		myCommands = new ArrayList<String>();
	}

	@Override
	public String getInterfaceName()
	{
		return myName;
	}

	@Override
	public ArrayList<String> getCommands()
	{
		return myCommands;
	}

	public void addCommand( String aCommand )
	{
		myCommands.add( aCommand );
	}

	@Override
	public boolean hasCommand(String aCommand)
	{
		return myCommands.contains(aCommand);
	}

	@Override
	public boolean verifyParameters( String aCommand,
									 ParameterArrayList aParameters )
			throws TestSuiteException
	{
		return true;
	}

	@Override
	public ParameterImpl createParameter( String aName,
	                                  String aType,
	                                  String aValue ) throws TestSuiteException
	{
		if ( aType.equalsIgnoreCase( "string" ) )
		{
			return new ParameterImpl(aName, (String) aValue);
		}			

		if ( aType.equalsIgnoreCase( "int" ) )
		{
			return new ParameterImpl(aName, new Integer(aValue) );
		}

		throw new TestSuiteException("Parameter type " + aType + " is not supported for this interface", aName);
	}
}
