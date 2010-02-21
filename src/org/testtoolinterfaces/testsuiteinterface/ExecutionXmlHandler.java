package org.testtoolinterfaces.testsuiteinterface;

import org.testtoolinterfaces.testsuite.TestStep;
import org.testtoolinterfaces.testsuite.TestStepArrayList;
import org.testtoolinterfaces.testsuite.TestStepFactory;
import org.testtoolinterfaces.utils.Trace;
import org.testtoolinterfaces.utils.XmlHandler;
import org.xml.sax.Attributes;
import org.xml.sax.XMLReader;


/**
 * @author Arjan Kranenburg 
 * 
 * <execution>
 *  <action>
 *  ...
 *  </action>
 *  <check>
 *  ...
 *  </check>
 * <execution>
 * 
 */
public class ExecutionXmlHandler extends XmlHandler
{
	public static final String START_ELEMENT = "execution";

    private TestStepArrayList mySteps;

	private TestStepXmlHandler myActionXmlHandler;
	private TestStepXmlHandler myCheckXmlHandler;

	public ExecutionXmlHandler( XMLReader anXmlReader, TestStepFactory aFactory )
	{
		super(anXmlReader, START_ELEMENT);
		Trace.println(Trace.LEVEL.CONSTRUCTOR);

    	this.reset();

    	myActionXmlHandler = new TestStepXmlHandler(anXmlReader, aFactory, TestStep.ActionType.action);
		this.addStartElementHandler(TestStep.ActionType.action.toString(), myActionXmlHandler);
		myActionXmlHandler.addEndElementHandler(TestStep.ActionType.action.toString(), this);

		myCheckXmlHandler = new TestStepXmlHandler(anXmlReader, aFactory, TestStep.ActionType.check);
		this.addStartElementHandler(TestStep.ActionType.check.toString(), myCheckXmlHandler);
		myCheckXmlHandler.addEndElementHandler(TestStep.ActionType.check.toString(), this);
	}

    public void processElementAttributes(String qualifiedName, Attributes att)
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
		// nop
	}

	@Override
	public void handleReturnFromChildElement(String aQualifiedName, XmlHandler aChildXmlHandler)
	{
		Trace.println(Trace.LEVEL.SUITE);
    	if (aQualifiedName.equalsIgnoreCase(TestStep.ActionType.action.toString()))
    	{
    		mySteps.add(myActionXmlHandler.getActionStep());
    		myActionXmlHandler.reset();
    	}
    	if (aQualifiedName.equalsIgnoreCase(TestStep.ActionType.check.toString()))
    	{
    		mySteps.add(myCheckXmlHandler.getActionStep());
    		myCheckXmlHandler.reset();
    	}
	}

	/**
	 * @return
	 */
	public TestStepArrayList getExecutionSteps()
	{
		Trace.println(Trace.LEVEL.GETTER);
		return mySteps;
	}

	public void reset()
	{
		Trace.println(Trace.LEVEL.SUITE);
    	mySteps = new TestStepArrayList();
	}
}
