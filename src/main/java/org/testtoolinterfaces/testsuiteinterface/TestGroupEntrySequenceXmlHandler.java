package org.testtoolinterfaces.testsuiteinterface;

import org.testtoolinterfaces.testsuite.TestGroupEntry;
import org.testtoolinterfaces.testsuite.TestGroupEntrySequence;
import org.testtoolinterfaces.testsuite.TestInterfaceList;
import org.testtoolinterfaces.testsuite.TestSuiteException;
import org.testtoolinterfaces.utils.TTIException;
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
 *  <if>
 *  ...
 *  </if
 *  <foreach>
 *  ...
 *  </foreach>
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
	
	private ForeachEntryXmlHandler myForeachXmlHandler;
	private IfExecItemLinkXmlHandler myIfXmlHandler;

    private TestInterfaceList myInterfaceList;
	private boolean myCheckStepParameter = false;

	private int myNextSequenceNr = 0;

	/**
	 * Creates the XML Handler
	 * 
	 * @param anXmlReader 			the xmlReader
	 * @param aTag					the start-element for this XML Handler
	 */
	public TestGroupEntrySequenceXmlHandler( XMLReader anXmlReader,
			String aTag, TestInterfaceList anInterfaceList, boolean aCheckStepParameter )
	{
		super(anXmlReader, aTag);
		Trace.println(Trace.CONSTRUCTOR, "TestStepSequenceXmlHandler( anXmlreader, " + aTag + " )", true);

		myTestCaseLinkXmlHandler = new TestCaseLinkXmlHandler(anXmlReader);
		this.addElementHandler(myTestCaseLinkXmlHandler);

		myTestGroupLinkXmlHandler = new TestGroupLinkXmlHandler(anXmlReader);
		this.addElementHandler(myTestGroupLinkXmlHandler);

		// myForeachXmlHandler is created when needed to prevent loops

		// myIfXmlHandler is created when needed to prevent loops

		myInterfaceList = anInterfaceList;
		myCheckStepParameter = aCheckStepParameter;

		this.reset();
	}

	@Override
	public void handleGoToChildElement(String aQualifiedName)
	{
     	if ( myIfXmlHandler == null && aQualifiedName.equalsIgnoreCase(TestStepXmlHandler.IF_ELEMENT) )
    	{
     		// We'll create a XmlHandler for if-steps only when we need it.
     		// Otherwise it would create an endless loop.
    		myIfXmlHandler = new IfExecItemLinkXmlHandler(this.getXmlReader(), myInterfaceList, myCheckStepParameter);
    		this.addElementHandler(myIfXmlHandler);
    	}
     	else if ( myForeachXmlHandler == null && aQualifiedName.equalsIgnoreCase(ForeachEntryXmlHandler.START_ELEMENT) )
    	{
     		// We'll create a XmlHandler for foreach-steps only when we need it.
     		// Otherwise it would create an endless loop.
    		myForeachXmlHandler = new ForeachEntryXmlHandler(this.getXmlReader(), myInterfaceList, myCheckStepParameter);
    		this.addElementHandler(myForeachXmlHandler);
    	}
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
    	else if (aQualifiedName.equalsIgnoreCase(ForeachXmlHandler.START_ELEMENT))
    	{
			try {
				entry = myForeachXmlHandler.getTestEntryIteration();
			} catch (TTIException e) {
				Trace.print(Trace.SUITE, e);
				throw new TestSuiteException( "Cannot add an iteration of TestGroupEntries", e );
			}
    	}
    	else if (aQualifiedName.equalsIgnoreCase(IfExecItemLinkXmlHandler.START_ELEMENT))
    	{
    		entry = myIfXmlHandler.getIf();
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
