package org.testtoolinterfaces.testsuiteinterface;

import java.util.ArrayList;
import java.util.Collection;

import org.testtoolinterfaces.testsuite.TestInterfaceList;
import org.testtoolinterfaces.testsuite.TestStep;
import org.testtoolinterfaces.testsuite.TestStepIteration;
import org.testtoolinterfaces.testsuite.TestSuiteException;
import org.testtoolinterfaces.utils.TTIException;
import org.testtoolinterfaces.utils.Trace;
import org.testtoolinterfaces.utils.XmlHandler;
import org.xml.sax.XMLReader;


/**
 * XmlHandler to read a foreach sequence of teststeps from a TTI-XML file.
 * 
 * <foreach>
 *  <item></item>
 *  <list></list>
 *  <do>
 *    ...
 *  </do>
 *  ...
 * </foreach>
 * 
 * 
 * @author Arjan Kranenburg 
 * @see http://www.testtoolinterfaces.org
 * 
 */
public class ForeachStepXmlHandler extends ForeachXmlHandler<TestStep>
{

    private Collection<TestStep> myDoEntries;

	private TestStepSequenceXmlHandler myDoEntriesXmlHandler;

	public ForeachStepXmlHandler(XMLReader anXmlReader,
			TestInterfaceList anInterfaceList, boolean aCheckStepParameter) {
		super(anXmlReader);

		myDoEntriesXmlHandler = new TestStepSequenceXmlHandler(anXmlReader, DO_ELEMENT, anInterfaceList, aCheckStepParameter);
		this.addElementHandler(myDoEntriesXmlHandler);

		this.reset();
	}

	@Override
	public void handleReturnFromChildElement(String aQualifiedName, XmlHandler aChildXmlHandler)
				throws TestSuiteException
	{
		Trace.println(Trace.SUITE);

		if (aQualifiedName.equalsIgnoreCase(DO_ELEMENT))
    	{
    		myDoEntries = myDoEntriesXmlHandler.getSteps();
    		myDoEntriesXmlHandler.reset();
    	}
    	else
    	{ 
    		super.handleReturnFromChildElement(aQualifiedName, aChildXmlHandler);
    	}
	}

	@Override
	public TestStepIteration getTestEntryIteration() throws TTIException {
		Trace.println(Trace.SUITE);
		
		int sequenceNr = this.getSequenceNr();
		String description = this.getDescription();
		
		String itemName = this.getItemName();
		if ( itemName.isEmpty() ) {
			throw new TTIException( ITEM_ELEMENT + " cannot be empty in TestEntryIteration" );
		}
		
		String listName = this.getListName();
		if ( listName.isEmpty() ) {
			throw new TTIException( LIST_ELEMENT + " cannot be empty in TestEntryIteration" );
		}
		
		return new TestStepIteration( description, sequenceNr, itemName, listName,
				this.myDoEntries );
	}

	@Override
	public void reset()
	{
		Trace.println(Trace.SUITE);
		myDoEntries = new ArrayList<TestStep>();
		
		super.reset();
	}
}
