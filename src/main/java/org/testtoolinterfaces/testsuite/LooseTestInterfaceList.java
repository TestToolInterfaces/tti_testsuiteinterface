/**
 * 
 */
package org.testtoolinterfaces.testsuite;

import java.util.Hashtable;
import java.util.Iterator;

/**
 * Wrapper for a list of TestInterfaces. A second list is kept as (read-only) reference.
 * 
 * When requested, it uses the reference list to look for a TestInterface.
 * It creates an UndefinedInterface if it does not exist and stores that in its own list.
 * 
 * @author Arjan Kranenburg
 *
 */
public class LooseTestInterfaceList implements TestInterfaceList
{
	TestInterfaceList myTestInterfaces;
	Hashtable<String, TestInterface> myList;

	/**
	 * Constructs an empty List and keeps the TestInterfaceList as reference.
	 * 
	 * @param myTestInterfaces	The reference TestInterfaceList
	 */
	public LooseTestInterfaceList(TestInterfaceList aTestInterfaces)
	{
		myList = new Hashtable<String, TestInterface>();
		myTestInterfaces = aTestInterfaces;
	}

	/**
	 * Constructs an empty List with no reference to an existing TestInterfaceList.
	 */
	public LooseTestInterfaceList()
	{
		this( null );
	}

	public TestInterface getInterface(String anInterfaceName)
	{
		TestInterface testInterface = null;
		if ( myTestInterfaces != null )
		{
			testInterface = myTestInterfaces.getInterface(anInterfaceName);
		}
		
		if (testInterface == null)
		{
			testInterface = myList.get(anInterfaceName);
			if (testInterface == null)
			{
				testInterface = new UnknownTestInterface( anInterfaceName );
				myList.put(anInterfaceName, testInterface);
			}
		}

		return testInterface;
	}

	public Iterator<TestInterface> iterator()
	{
		return myList.values().iterator();
	}
}
