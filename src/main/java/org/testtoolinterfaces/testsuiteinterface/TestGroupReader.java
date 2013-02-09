package org.testtoolinterfaces.testsuiteinterface;

import java.io.File;
import java.io.IOError;
import java.util.ArrayList;
import java.util.Hashtable;

import org.testtoolinterfaces.testsuite.TestGroupEntrySequence;
import org.testtoolinterfaces.testsuite.TestGroup;
import org.testtoolinterfaces.testsuite.TestGroupImpl;
import org.testtoolinterfaces.testsuite.TestInterfaceList;
import org.testtoolinterfaces.testsuite.TestStepSequence;
import org.testtoolinterfaces.testsuite.TestSuiteException;
import org.testtoolinterfaces.utils.TTIException;
import org.testtoolinterfaces.utils.Trace;
import org.testtoolinterfaces.utils.XmlHandler;
import org.xml.sax.XMLReader;

/**
 * Class to read TestGroups from a TTI-XML file
 * 
 * @author Arjan Kranenburg
 * @see http://www.testtoolinterfaces.org
 *
 */
public class TestGroupReader
{
	private TestInterfaceList myInterfaceList;
	private boolean myCheckStepParameter;
	
	/**
	 * Creates the reader

	 * @param anInterfaceList		A list of supported interfaces.
	 * @param aCheckStepParameter	Flag to indicate if parameters in steps must be checked.
	 */
	public TestGroupReader( TestInterfaceList anInterfaceList, boolean aCheckStepParameter )
	{
		Trace.println(Trace.CONSTRUCTOR);
		
		myInterfaceList = anInterfaceList;
		myCheckStepParameter = aCheckStepParameter;
	}

	/**
	 * Creates the reader, without checking the parameters of steps
	 * 
	 * @param anInterfaceList		A list of supported interfaces.
	 */
	public TestGroupReader( TestInterfaceList anInterfaceList )
	{
		this( anInterfaceList, false );
	}

	/** 
	 * Reads the TTI-XML file
	 * 
	 * @param aTestGroupFile		The TTI-XML file to read
	 * @return	The TestGroup
	 * 
	 * @throws	IOError when reading fails, although most faults are just ignored to
	 * 			continue with the next Test Group.
	 */
	public TestGroup readTgFile( File aTestGroupFile )
	{
		Trace.println(Trace.SUITE, "readTgFile( " + aTestGroupFile.getName() + " )", true);

		TestGroup testGroup;
		try {
			XMLReader xmlReader = XmlHandler.getNewXmlReader();
			TestGroupXmlHandler handler = new TestGroupXmlHandler(xmlReader, myInterfaceList, myCheckStepParameter);

			handler.parse(xmlReader, aTestGroupFile);
			testGroup = handler.getTestGroup();

		} catch (TTIException e) {
			Trace.print(Trace.SUITE, e);

			Throwable cause = e.getCause();
			if ( ! (e instanceof TestSuiteException) || ( cause instanceof TestSuiteException ) )
			{
				throw new IOError( e );
			}
			
			String description = "Failed to read Test Group: " + aTestGroupFile.getName() + "\n";
			if ( e instanceof TestSuiteException )
			{
				description += e.getLocalizedMessage() + "\n";
			}
			else
			{
				description += cause.getLocalizedMessage() + "\n";
			}

			testGroup = new TestGroupImpl( aTestGroupFile.getName() + "_ERROR",
			                               description,
			                               0,
			                               new ArrayList<String>(),
			                               new TestStepSequence(),
			                               new TestGroupEntrySequence(),
			                               new TestStepSequence(),
			                               new Hashtable<String, String>(),
			                               new Hashtable<String, String>() );
		}
		
		return testGroup;
	}
}
