package org.testtoolinterfaces.testsuiteinterface;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testtoolinterfaces.testsuite.TestInterfaceList;
import org.testtoolinterfaces.testsuite.TestStep;
import org.testtoolinterfaces.testsuite.TestStepSequence;
import org.testtoolinterfaces.testsuite.TestSuiteException;
import org.testtoolinterfaces.utils.Mark;
import org.testtoolinterfaces.utils.TTIException;
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
 *  <if>
 *  ...
 *  </if>
 *  <foreach>
 *   ...
 *  </foreach>
 * </[tag]>
 * 
 * @author Arjan Kranenburg 
 * @see http://www.testtoolinterfaces.org
 * 
 */
public class TestStepSequenceXmlHandler extends XmlHandler
{
    private static final Logger LOG = LoggerFactory.getLogger(TestStepSequenceXmlHandler.class);

    // These are used for legacy reasons
	private static final String TAG_ACTION   = "action";
	private static final String TAG_CHECK    = "check";
	
    private TestStepSequence mySteps;

	private TestStepXmlHandler myStepXmlHandler;
	private TestStepXmlHandler myIfXmlHandler;
	private TestStepXmlHandler myActionXmlHandler;
	private TestStepXmlHandler myCheckXmlHandler;
	private ForeachStepXmlHandler myForeachXmlHandler;
	
//	private ArrayList<StepType> myAllowedStepTypes;
	
	private TestInterfaceList myInterfaceList;
	private boolean myCheckStepParameter;
	
	private int myNextSequenceNr = 0;

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
		LOG.trace(Mark.CONSTRUCTOR, "{}, {}, {}, {}",
				anXmlReader, aTag, anInterfaceList, aCheckStepParameter);

		myStepXmlHandler = new TestStepXmlHandler(anXmlReader, anInterfaceList, aCheckStepParameter);
		this.addElementHandler(myStepXmlHandler);

		// myIfXmlHandler is created when needed to prevent loops
		
		myActionXmlHandler = new TestStepXmlHandler(anXmlReader, TAG_ACTION, anInterfaceList, aCheckStepParameter);
		this.addElementHandler(myActionXmlHandler);

		myCheckXmlHandler = new TestStepXmlHandler(anXmlReader, TAG_CHECK, anInterfaceList, aCheckStepParameter);
		this.addElementHandler(myCheckXmlHandler);

		// myForeachXmlHandler is created when needed to prevent loops

		myInterfaceList = anInterfaceList;
		myCheckStepParameter = aCheckStepParameter;
		
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
     	if ( myIfXmlHandler == null && aQualifiedName.equalsIgnoreCase(TestStepXmlHandler.IF_ELEMENT) )
    	{
     		// We'll create a TestStepXmlHandler for if-steps only when we need it.
     		// Otherwise it would create an endless loop.
    		myIfXmlHandler = new TestStepXmlHandler(this.getXmlReader(), TestStepXmlHandler.IF_ELEMENT, myInterfaceList, myCheckStepParameter);
    		this.addElementHandler(myIfXmlHandler);
    	}
     	else if ( myForeachXmlHandler == null && aQualifiedName.equalsIgnoreCase(ForeachStepXmlHandler.START_ELEMENT) )
    	{
     		// We'll create a TestStepXmlHandler for foreach entries only when we need it.
     		// Otherwise it would create an endless loop.
    		myForeachXmlHandler = new ForeachStepXmlHandler(this.getXmlReader(), myInterfaceList, myCheckStepParameter);
    		this.addElementHandler(myForeachXmlHandler);
    	}
	}

	@Override
	public void handleReturnFromChildElement(String aQualifiedName, XmlHandler aChildXmlHandler)
				throws TestSuiteException
	{
		LOG.trace(Mark.SUITE, "{}, {}", aQualifiedName, aChildXmlHandler);

		TestStep step = null;
    	if (aQualifiedName.equalsIgnoreCase(TestStepXmlHandler.START_ELEMENT))
    	{
    		step = myStepXmlHandler.getStep();
    		myStepXmlHandler.reset();
    	}
    	else if (aQualifiedName.equalsIgnoreCase(TestStepXmlHandler.IF_ELEMENT))
    	{
    		step = myIfXmlHandler.getStep();
    		myIfXmlHandler.reset();
    	}
    	else if (aQualifiedName.equalsIgnoreCase(TAG_CHECK))
    	{
    		step = myCheckXmlHandler.getStep();
        	myCheckXmlHandler.reset();
    	}
    	else if (aQualifiedName.equalsIgnoreCase(TAG_ACTION))
    	{
    		step = myActionXmlHandler.getStep();
        	myActionXmlHandler.reset();
    	}
    	else if (aQualifiedName.equalsIgnoreCase(ForeachXmlHandler.START_ELEMENT))
    	{
    		try {
				step = myForeachXmlHandler.getTestEntryIteration();
			} catch (TTIException e) {
				LOG.trace(Mark.SUITE, "", e);
				throw new TestSuiteException( "Cannot add an iteration of TestStepEntries", e );
			}
    		myForeachXmlHandler.reset();
    	}
    	else
    	{ // Programming fault
			throw new Error( "Child XML Handler returned, but not recognized. The handler was probably defined " +
			                 "in the Constructor but not handled in handleReturnFromChildElement()");
    	}

    	if ( step != null ) {
    		if (step.getSequenceNr() == 0 ) {
        		step.setSequenceNr( this.myNextSequenceNr );
    		} else {
    			myNextSequenceNr = step.getSequenceNr();
    		}
    		mySteps.add( step );
    		
    		myNextSequenceNr++;
    	}

	}

	/**
	 * @return the TestStepSequence
	 */
	public TestStepSequence getSteps()
	{
		LOG.trace(Mark.GETTER, "");
		return mySteps;
	}

	@Override
	public void reset()
	{
		LOG.trace(Mark.SUITE, "");
		mySteps = new TestStepSequence();
	}
}
