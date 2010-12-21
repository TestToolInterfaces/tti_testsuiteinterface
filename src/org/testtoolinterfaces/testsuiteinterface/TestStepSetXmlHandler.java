package org.testtoolinterfaces.testsuiteinterface;

import java.util.ArrayList;

import org.testtoolinterfaces.testsuite.TestStep;
import org.testtoolinterfaces.testsuite.TestStepSet;
import org.testtoolinterfaces.testsuite.TestStepSetCase;
import org.testtoolinterfaces.utils.Trace;
import org.testtoolinterfaces.utils.XmlHandler;
import org.xml.sax.Attributes;
import org.xml.sax.XMLReader;


/**
 * @author Arjan Kranenburg 
 * 
 * <set sequence="...">
 *  <case>
 *  ...
 *  </case>
 *  <case>
 *  ...
 *  </case>
 * <set>
 * 
 */
public class TestStepSetXmlHandler extends XmlHandler
{
	public static final String START_ELEMENT = "set";
	public static final String ATTRIBUTE_SEQUENCE = "sequence";

	private int mySequence;
    private ArrayList<TestStepSetCase> myCases;

	private TestStepSetCaseXmlHandler myCaseXmlHandler;

	public TestStepSetXmlHandler( XMLReader anXmlReader, ArrayList<TestStep.StepType> anAllowedStepTypes )
	{
		super(anXmlReader, START_ELEMENT);
		Trace.println(Trace.LEVEL.CONSTRUCTOR);

    	myCaseXmlHandler = new TestStepSetCaseXmlHandler(anXmlReader, anAllowedStepTypes);
		this.addStartElementHandler(TestStepSetCaseXmlHandler.START_ELEMENT, myCaseXmlHandler);
		myCaseXmlHandler.addEndElementHandler(TestStepSetCaseXmlHandler.START_ELEMENT, this);

		this.reset();
	}

    public void processElementAttributes(String aQualifiedName, Attributes anAtt)
    {
		Trace.print(Trace.SUITE, "processElementAttributes( " 
		            + aQualifiedName, true );
	    	if (aQualifiedName.equalsIgnoreCase(TestStepSetXmlHandler.START_ELEMENT))
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
		Trace.println(Trace.LEVEL.SUITE);
    	if (aQualifiedName.equalsIgnoreCase(TestStepSetCaseXmlHandler.START_ELEMENT))
    	{
    		myCases.add(myCaseXmlHandler.getCase());
    		myCaseXmlHandler.reset();
    	}
	}

	/**
	 * @return
	 */
	public TestStepSet getSet()
	{
		Trace.println(Trace.LEVEL.GETTER);
		return new TestStepSet( this.mySequence, myCases );
	}

	public void reset()
	{
		Trace.println(Trace.LEVEL.SUITE);
		
		mySequence = 0;
    	myCases = new ArrayList<TestStepSetCase>();
	}
}
