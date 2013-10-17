package org.testtoolinterfaces.testsuiteinterface;

import java.util.ArrayList;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testtoolinterfaces.testsuite.TestInterfaceList;
import org.testtoolinterfaces.testsuite.TestStep;
import org.testtoolinterfaces.testsuite.TestSuiteException;
import org.testtoolinterfaces.testsuite.impl.TestStepIteration;
import org.testtoolinterfaces.utils.Mark;
import org.testtoolinterfaces.utils.TTIException;
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
    private static final Logger LOG = LoggerFactory.getLogger(ForeachStepXmlHandler.class);

    private Collection<TestStep> myDoEntries;

	private TestStepSequenceXmlHandler myDoEntriesXmlHandler;

	public ForeachStepXmlHandler(XMLReader anXmlReader,
			TestInterfaceList anInterfaceList, boolean aCheckStepParameter) {
		super(anXmlReader, anInterfaceList, aCheckStepParameter);

		myDoEntriesXmlHandler = new TestStepSequenceXmlHandler(anXmlReader, DO_ELEMENT, anInterfaceList, aCheckStepParameter);
		this.addElementHandler(myDoEntriesXmlHandler);

		this.reset();
	}

	@Override
	public void handleReturnFromChildElement(String aQualifiedName, XmlHandler aChildXmlHandler)
				throws TestSuiteException
	{
		LOG.trace(Mark.CONSTRUCTOR, "{}, {}", aQualifiedName, aChildXmlHandler);

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
		LOG.trace(Mark.SUITE, "");
		
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
				this.myDoEntries, this.getUntilStep() );
	}

	@Override
	public void reset()
	{
		LOG.trace(Mark.SUITE, "");
		myDoEntries = new ArrayList<TestStep>();
		
		super.reset();
	}
}
