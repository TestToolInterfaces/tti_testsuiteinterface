package org.testtoolinterfaces.testsuiteinterface;

import java.util.ArrayList;
import java.util.Collection;

import org.testtoolinterfaces.testsuite.TestGroupEntry;
import org.testtoolinterfaces.testsuite.TestGroupEntryIteration;
import org.testtoolinterfaces.testsuite.TestInterfaceList;
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
public class ForeachEntryXmlHandler extends ForeachXmlHandler<TestGroupEntry>
{
    private Collection<TestGroupEntry> myDoEntries;

	private TestGroupEntrySequenceXmlHandler myDoEntriesXmlHandler;

	public ForeachEntryXmlHandler(XMLReader anXmlReader,
			TestInterfaceList anInterfaceList, boolean aCheckStepParameter) {
		super(anXmlReader, anInterfaceList, aCheckStepParameter);

		myDoEntriesXmlHandler = new TestGroupEntrySequenceXmlHandler(anXmlReader,
				DO_ELEMENT, anInterfaceList, aCheckStepParameter);
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
    		myDoEntries = myDoEntriesXmlHandler.getEntries();
    		myDoEntriesXmlHandler.reset();
    	}
    	else
    	{ 
    		super.handleReturnFromChildElement(aQualifiedName, aChildXmlHandler);
    	}
	}

	@Override
	public TestGroupEntryIteration getTestEntryIteration() throws TTIException {
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
		
		return new TestGroupEntryIteration( description, sequenceNr, itemName, listName,
				this.myDoEntries, this.getUntilStep() );
	}

	@Override
	public void reset()
	{
		Trace.println(Trace.SUITE);
		myDoEntries = new ArrayList<TestGroupEntry>();
		
		super.reset();
	}
}
