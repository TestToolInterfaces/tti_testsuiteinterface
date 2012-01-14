package org.testtoolinterfaces.testsuiteinterface;

import org.testtoolinterfaces.testsuite.TestInterfaceList;
import org.testtoolinterfaces.testsuite.TestStep;
import org.testtoolinterfaces.testsuite.TestStepSelection;
import org.testtoolinterfaces.testsuite.TestStepSequence;
import org.testtoolinterfaces.testsuite.TestSuiteException;
import org.testtoolinterfaces.utils.GenericTagAndStringXmlHandler;
import org.testtoolinterfaces.utils.Trace;
import org.testtoolinterfaces.utils.XmlHandler;
import org.xml.sax.Attributes;
import org.xml.sax.XMLReader;


/**
 * @author Arjan Kranenburg 
 * 
 * <if sequence="...">
 *  <teststep>
 *  ...
 *  </teststep>
 *  <then>
 *  ...
 *  </then>
 *  <else>
 *  ...
 *  </else>
 * <if>
 * 
 */
public class TestStepSelectionXmlHandler extends XmlHandler
{
	public static final String ELEMENT_START = "if";
	private static final String DESCRIPTION_ELEMENT = "description";
	private static final String THEN_ELEMENT = "then";
	private static final String ELSE_ELEMENT = "else";

	public static final String ATTRIBUTE_SEQUENCE = "sequence";

	private int mySequence;
	private String myDescription;
	private TestStep myIfStep;
	private TestStepSequence myThenSteps;
	private TestStepSequence myElseSteps;

    private GenericTagAndStringXmlHandler myDescriptionXmlHandler;
	private TestStepXmlHandler myIfXmlHandler;
	private TestStepSequenceXmlHandler myThenXmlHandler;
	private TestStepSequenceXmlHandler myElseXmlHandler;

	public TestStepSelectionXmlHandler( XMLReader anXmlReader,
	                              TestInterfaceList anInterfaceList,
	                              boolean aCheckStepParameter )
	{
		super(anXmlReader, ELEMENT_START);
		Trace.println(Trace.CONSTRUCTOR);

		this.reset();

		myDescriptionXmlHandler = new GenericTagAndStringXmlHandler(anXmlReader, DESCRIPTION_ELEMENT);
		this.addStartElementHandler(DESCRIPTION_ELEMENT, myDescriptionXmlHandler);
		myDescriptionXmlHandler.addEndElementHandler(DESCRIPTION_ELEMENT, this);

		myIfXmlHandler = new TestStepXmlHandler(anXmlReader, anInterfaceList, aCheckStepParameter);
		this.addStartElementHandler(TestStepXmlHandler.START_ELEMENT, myIfXmlHandler);
		myIfXmlHandler.addEndElementHandler(TestStepXmlHandler.START_ELEMENT, this);

		myThenXmlHandler = new TestStepSequenceXmlHandler(anXmlReader, THEN_ELEMENT, anInterfaceList, aCheckStepParameter);
		this.addStartElementHandler(THEN_ELEMENT, myThenXmlHandler);
		myThenXmlHandler.addEndElementHandler(THEN_ELEMENT, this);

		myElseXmlHandler = new TestStepSequenceXmlHandler(anXmlReader, ELSE_ELEMENT, anInterfaceList, aCheckStepParameter);
		this.addStartElementHandler(ELSE_ELEMENT, myElseXmlHandler);
		myElseXmlHandler.addEndElementHandler(ELSE_ELEMENT, this);
	}

    public void processElementAttributes(String aQualifiedName, Attributes anAtt)
    {
		Trace.print(Trace.SUITE, "processElementAttributes( " 
		            + aQualifiedName, true );
	    	if (aQualifiedName.equalsIgnoreCase(TestStepSelectionXmlHandler.ELEMENT_START))
	    	{
			    for (int i = 0; i < anAtt.getLength(); i++)
			    {
		    		Trace.append( Trace.SUITE, ", " + anAtt.getQName(i) + "=" + anAtt.getValue(i) );
			    	if (anAtt.getQName(i).equalsIgnoreCase(ATTRIBUTE_SEQUENCE))
			    	{
			        	mySequence = Integer.valueOf( anAtt.getValue(i) ).intValue();
			    	}
			    }
	    	}
			Trace.append( Trace.SUITE, " )\n" );
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
		// nop
	}

	@Override
	public void handleReturnFromChildElement(String aQualifiedName, XmlHandler aChildXmlHandler)
	{
		Trace.println(Trace.SUITE);
    	if (aQualifiedName.equalsIgnoreCase(TestStepXmlHandler.START_ELEMENT))
    	{
    		try
			{
				myIfStep = myIfXmlHandler.getStep();
			}
			catch (TestSuiteException e)
			{
				myIfStep = null;
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		myIfXmlHandler.reset();
    	}
    	else if (aQualifiedName.equalsIgnoreCase(THEN_ELEMENT))
    	{
			myThenSteps = myThenXmlHandler.getSteps();
			myThenXmlHandler.reset();
    	}
    	else if (aQualifiedName.equalsIgnoreCase(ELSE_ELEMENT))
    	{
			myElseSteps = myElseXmlHandler.getSteps();
			myElseXmlHandler.reset();
    	}
    	else if (aQualifiedName.equalsIgnoreCase(DESCRIPTION_ELEMENT))
    	{
    		myDescription  = myDescriptionXmlHandler.getValue();
        	myDescriptionXmlHandler.reset();
    	}
	}

	/**
	 * @return
	 */
	public TestStepSelection getTsSelection()
	{
		Trace.println(Trace.GETTER);
		TestStepSelection tsSelection = new TestStepSelection( mySequence,
		                                                       myDescription,
		                                                       myIfStep,
		                                                       myThenSteps,
		                                                       myElseSteps );
		return tsSelection;
	}

	public void reset()
	{
		Trace.println(Trace.SUITE);
		
		mySequence = 0;
		myDescription = "";
		myIfStep = null;
		myThenSteps = new TestStepSequence();
		myElseSteps = new TestStepSequence();
	}
}
