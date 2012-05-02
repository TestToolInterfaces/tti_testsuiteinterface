package org.testtoolinterfaces.testsuite;

import java.util.Hashtable;
import java.util.Iterator;

import org.testtoolinterfaces.testsuite.TestInterface;
import org.testtoolinterfaces.testsuite.TestInterfaceList;

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

	public TestInterface getInterface(String anInterfaceName)
	{
		return ifaceList.get(anInterfaceName);
	}

	public Iterator<TestInterface> iterator()
	{
		return ifaceList.values().iterator();
	}

}
