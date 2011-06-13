package org.testtoolinterfaces.testsuiteinterface;

import java.util.Hashtable;

import org.testtoolinterfaces.testsuite.TestInterface;

public class TestInterfaceListHelper implements TestInterfaceList
{
	private Hashtable<String, TestInterface> ifaceList;

	/**
	 * 
	 */
	public TestInterfaceListHelper()
	{
		ifaceList = new Hashtable<String, TestInterface>();
	}
	
	public void put( TestInterface anInterface )
	{
		ifaceList.put(anInterface.getInterfaceName(), anInterface);
	}

	@Override
	public TestInterface getInterface(String anInterfaceName)
	{
		return ifaceList.get(anInterfaceName);
	}

}
