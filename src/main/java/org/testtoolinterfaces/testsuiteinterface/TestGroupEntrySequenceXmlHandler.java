package org.testtoolinterfaces.testsuiteinterface;

import org.testtoolinterfaces.testsuite.TestGroupEntry;
import org.testtoolinterfaces.testsuite.TestGroupEntrySequence;
import org.testtoolinterfaces.testsuite.TestSuiteException;
import org.testtoolinterfaces.utils.Trace;
import org.testtoolinterfaces.utils.XmlHandler;
import org.xml.sax.Attributes;
import org.xml.sax.XMLReader;


/**
 * XmlHandler to read a sequence of TestGroupEntries from a TTI-XML file.
 * 
 * <[tag]>
 *  <testcaselink>
 *  ...
 *  </testcaselink>
 *  <testgrouplink>
 *  ...
 *  </testgrouplink>
 * </[tag]>
 * 
 * @author Arjan Kranenburg 
 * @see http://www.testtoolinterfaces.org
 * 
 */
public class TestGroupEntrySequenceXmlHandler extends XmlHandler
{
    private TestGroupEntrySequence myEntries;

	private TestCaseLinkXmlHandler myTestCaseLinkXmlHandler;
	private TestGroupLinkXmlHandler myTestGroupLinkXmlHandler;
	
	private int myNextSequenceNr = 0;

	/**
	 * Creates the XML Handler
	 * 
	 * @param anXmlReader 			the xmlReader
	 * @param aTag					the start-element for this XML Handler
	 */
	public TestGroupEntrySequenceXmlHandler( XMLReader anXmlReader,
	                                   String aTag )
	{
		super(anXmlReader, aTag);
		Trace.println(Trace.CONSTRUCTOR, "TestStepSequenceXmlHandler( anXmlreader, " + aTag + " )", true);

		myTestCaseLinkXmlHandler = new TestCaseLinkXmlHandler(anXmlReader);
		this.addElementHandler(myTestCaseLinkXmlHandler);

		myTestGroupLinkXmlHandler = new TestGroupLinkXmlHandler(anXmlReader);
		this.addElementHandler(myTestGroupLinkXmlHandler);

		this.reset();
	}

	@Override
    public void processElementAttributes(String aQualifiedName, Attributes anAtt)
    {
    	//nop
    }

	@Override
	public void handleStartElement(String aQualifiedName)
	{
    	//nop
	}

	@Override
	public void handleCharacters(String aValue)
	{
		//nop
	}

	@Override
	public void handleEndElement(String aQualifiedName)
	{
    	//nop
	}
	
	@Override
	public void handleGoToChildElement(String aQualifiedName)
	{
    	//nop
	}

	@Override
	public void handleReturnFromChildElement(String aQualifiedName, XmlHandler aChildXmlHandler)
				throws TestSuiteException
	{
		Trace.println(Trace.SUITE);

		TestGroupEntry entry = null;
    	if (aQualifiedName.equalsIgnoreCase(TestCaseLinkXmlHandler.START_ELEMENT))
    	{
    		entry = myTestCaseLinkXmlHandler.getTestCaseLink();
    		myTestCaseLinkXmlHandler.reset();
    	}
    	else if (aQualifiedName.equalsIgnoreCase(TestGroupLinkXmlHandler.START_ELEMENT))
    	{
    		entry = myTestGroupLinkXmlHandler.getTestGroupLink();
    		myTestGroupLinkXmlHandler.reset();
    	}
    	else
    	{ // Programming fault
			throw new Error( "Child XML Handler returned, but not recognized. The handler was probably defined " +
			                 "in the Constructor but not handled in handleReturnFromChildElement()");
    	}

    	if ( entry != null ) {
    		if (entry.getSequenceNr() == 0 ) {
        		entry.setSequenceNr( this.myNextSequenceNr );
    		} else {
    			myNextSequenceNr = entry.getSequenceNr();
    		}
    		myEntries.add( entry );
    		
    		myNextSequenceNr++;
    	}

	}

	/**
	 * @return the TestStepSequence
	 */
	public TestGroupEntrySequence getEntries()
	{
		Trace.println(Trace.GETTER);
		return myEntries;
	}

	@Override
	public void reset()
	{
		Trace.println(Trace.SUITE);
		myEntries = new TestGroupEntrySequence();
		myNextSequenceNr = 0;
	}
}
