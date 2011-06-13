package org.testtoolinterfaces.testsuiteinterface;

import java.io.File;
import java.io.IOError;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.testtoolinterfaces.testsuite.TestGroup;
import org.testtoolinterfaces.utils.Trace;
import org.xml.sax.XMLReader;

public class TestGroupReader
{
	private TestInterfaceList myInterfaceList;
	private boolean myCheckStepParameter;
	
	/**
	 * @param aTestGroupFactory
	 */
	public TestGroupReader( TestInterfaceList anInterfaceList )
	{
		this( anInterfaceList, false );
	}

	/**
	 * @param aTestGroupFactory
	 */
	public TestGroupReader( TestInterfaceList anInterfaceList, boolean aCheckStepParameter )
	{
		Trace.println(Trace.CONSTRUCTOR);
		
		myInterfaceList = anInterfaceList;
		myCheckStepParameter = aCheckStepParameter;
	}

	/** 
	 * @throws IOError when reading fails
	 */
	public TestGroup readTgFile( File aTestGroupFile )
	{
		Trace.println(Trace.SUITE, "readTgFile( " + aTestGroupFile.getPath() + " )", true);

		// create a parser
        SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setNamespaceAware(false);
		TestGroup testGroup;
		try
		{
			SAXParser saxParser = spf.newSAXParser();
			XMLReader xmlReader = saxParser.getXMLReader();

	        // create a handler
			TestGroupXmlHandler handler = new TestGroupXmlHandler(xmlReader, myInterfaceList, myCheckStepParameter);

	        // assign the handler to the parser
	        xmlReader.setContentHandler(handler);

	        // parse the document
	        xmlReader.parse(aTestGroupFile.getAbsolutePath());
	        
	        testGroup = handler.getTestGroup();
		}
		catch (Exception e)
		{
			Trace.print(Trace.SUITE, e);
			throw new IOError( e );
		}

		return testGroup;
	}
}
