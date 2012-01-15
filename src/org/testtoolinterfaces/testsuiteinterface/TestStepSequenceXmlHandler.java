package org.testtoolinterfaces.testsuiteinterface;

import org.testtoolinterfaces.testsuite.TestInterfaceList;
import org.testtoolinterfaces.testsuite.TestStepSequence;
import org.testtoolinterfaces.testsuite.TestSuiteException;
import org.testtoolinterfaces.utils.Trace;
import org.testtoolinterfaces.utils.XmlHandler;
import org.xml.sax.Attributes;
import org.xml.sax.XMLReader;


/**
 * XmlHandler to read a sequence of teststeps from a TTI-XML file.
 * 
 * <[tag]>
 *  <teststep>
 *  ...
 *  </teststep>
 * </[tag]>
 * 
 * @author Arjan Kranenburg 
 * @see http://www.testtoolinterfaces.org
 * 
 */
public class TestStepSequenceXmlHandler extends XmlHandler
{
	private static final String TAG_ACTION   = "action";
	private static final String TAG_CHECK    = "check";
	
    private TestStepSequence mySteps;

	private TestStepXmlHandler myStepXmlHandler;
	private TestStepXmlHandler myIfXmlHandler;
	private TestStepXmlHandler myActionXmlHandler;
	private TestStepXmlHandler myCheckXmlHandler;
	
//	private ArrayList<StepType> myAllowedStepTypes;
	
//	private TestInterfaceList myInterfaceList;
//	private boolean myCheckStepParameter;

	/**
	 * Creates the XML Handler
	 * 
	 * @param anXmlReader 			the xmlReader
	 * @param aTag					the start-element for this XML Handler
	 * @param anInterfaceList		a list of interfaces
	 * @param aCheckStepParameter	flag to indicate if specified parameters of a step must be verified in the interface
	 */
	public TestStepSequenceXmlHandler( XMLReader anXmlReader,
	                                   String aTag,
//	                                   ArrayList<StepType> anAllowedStepTypes,
	                                   TestInterfaceList anInterfaceList,
	                                   boolean aCheckStepParameter )
	{
		super(anXmlReader, aTag);
		Trace.println(Trace.CONSTRUCTOR, "TestStepSequenceXmlHandler( anXmlreader, " + aTag + " )", true);

		myStepXmlHandler = new TestStepXmlHandler(anXmlReader, anInterfaceList, aCheckStepParameter);
		this.addStartElementHandler(TestStepXmlHandler.START_ELEMENT, myStepXmlHandler);
		myStepXmlHandler.addEndElementHandler(TestStepXmlHandler.START_ELEMENT, this);

		myIfXmlHandler = new TestStepXmlHandler(anXmlReader, TestStepXmlHandler.IF_ELEMENT, anInterfaceList, aCheckStepParameter);
		this.addStartElementHandler(TestStepXmlHandler.IF_ELEMENT, myIfXmlHandler);
		myIfXmlHandler.addEndElementHandler(TestStepXmlHandler.IF_ELEMENT, this);

		myActionXmlHandler = new TestStepXmlHandler(anXmlReader, TAG_ACTION, anInterfaceList, aCheckStepParameter);
		this.addStartElementHandler(TAG_ACTION, myActionXmlHandler);
		myActionXmlHandler.addEndElementHandler(TAG_ACTION, this);

		myCheckXmlHandler = new TestStepXmlHandler(anXmlReader, TAG_CHECK, anInterfaceList, aCheckStepParameter);
		this.addStartElementHandler(TAG_CHECK, myCheckXmlHandler);
		myCheckXmlHandler.addEndElementHandler(TAG_CHECK, this);

//		myInterfaceList = anInterfaceList;
//		myCheckStepParameter = aCheckStepParameter;

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
    	if (aQualifiedName.equalsIgnoreCase(TestStepXmlHandler.START_ELEMENT))
    	{
    		mySteps.add( myStepXmlHandler.getStep() );
    		myStepXmlHandler.reset();
    	}
    	else if (aQualifiedName.equalsIgnoreCase(TestStepXmlHandler.IF_ELEMENT))
    	{
    		mySteps.add( myIfXmlHandler.getStep() );
    		myIfXmlHandler.reset();
    	}
    	else if (aQualifiedName.equalsIgnoreCase(TAG_CHECK))
    	{
    		mySteps.add( myCheckXmlHandler.getStep() );
        	myCheckXmlHandler.reset();
    	}
    	else if (aQualifiedName.equalsIgnoreCase(TAG_ACTION))
    	{
    		mySteps.add( myActionXmlHandler.getStep() );
        	myActionXmlHandler.reset();
    	}
    	else
    	{ // Programming fault
			throw new Error( "Child XML Handler returned, but not recognized. The handler was probably defined " +
			                 "in the Constructor but not handled in handleReturnFromChildElement()");
    	}
	}

	/**
	 * @return the TestStepSequence
	 */
	public TestStepSequence getSteps()
	{
		Trace.println(Trace.GETTER);
		return mySteps;
	}

	@Override
	public void reset()
	{
		Trace.println(Trace.SUITE);
		mySteps = new TestStepSequence();
	}
}
