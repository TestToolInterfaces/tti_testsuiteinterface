package org.testtoolinterfaces.testsuiteinterface;

import javax.xml.parsers.ParserConfigurationException;

import org.testtoolinterfaces.testsuite.*;
import org.testtoolinterfaces.utils.GenericTagAndStringXmlHandler;
import org.testtoolinterfaces.utils.Trace;
import org.testtoolinterfaces.utils.XmlHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;


/**
 * XmlHandler to read a foreach sequence of teststeps from a TTI-XML file.
 * 
 * <foreach>
 *  <item></itme>
 *  <list></list>
 *  <do>
 *  ...
 *  </do>
 * </foreach>
 * 
 * 
 * @author Arjan Kranenburg 
 * @see http://www.testtoolinterfaces.org
 * 
 */
public class ForeachXmlHandler extends XmlHandler
{
	public static final String START_ELEMENT = "foreach";
	private static final String ATTRIBUTE_SEQUENCE = "sequence";

	private static final String DESCRIPTION_ELEMENT = "description";
	private static final String ITEM_ELEMENT   = "item";
	private static final String LIST_ELEMENT   = "list";
	private static final String DO_ELEMENT     = "do";
	
	private int mySequenceNr;

	private String myDescription;
    private String itemName;
    private String listName;
    private TestEntrySequence myDoEntries;

	private GenericTagAndStringXmlHandler myDescriptionXmlHandler;
	private GenericTagAndStringXmlHandler myItemNameXmlHandler;
	private GenericTagAndStringXmlHandler myListNameXmlHandler;
	private TestEntrySequenceXmlHandler myDoEntriesXmlHandler;
	
	/**
	 * Creates the XML Handler
	 * 
	 * @param anXmlReader 			the xmlReader
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 */
	public ForeachXmlHandler( XMLReader anXmlReader,
			TestInterfaceList anInterfaceList, boolean aCheckStepParameter ) {
		super( anXmlReader, START_ELEMENT );
		Trace.println(Trace.CONSTRUCTOR, "TestStepSequenceXmlHandler( anXmlreader )", true);

	    myDescriptionXmlHandler = new GenericTagAndStringXmlHandler(anXmlReader, DESCRIPTION_ELEMENT);
		this.addElementHandler(myDescriptionXmlHandler);

		myItemNameXmlHandler = new GenericTagAndStringXmlHandler(anXmlReader, ITEM_ELEMENT);
		this.addElementHandler(myItemNameXmlHandler);

		myListNameXmlHandler = new GenericTagAndStringXmlHandler(anXmlReader, LIST_ELEMENT);
		this.addElementHandler(myListNameXmlHandler);

		myDoEntriesXmlHandler = new TestEntrySequenceXmlHandler(anXmlReader, DO_ELEMENT, anInterfaceList, aCheckStepParameter);
		this.addElementHandler(myDoEntriesXmlHandler);

		this.reset();
	}

	@Override
    public void processElementAttributes(String aQualifiedName, Attributes anAtt)
    {
		Trace.print(Trace.SUITE, "processElementAttributes( " 
	            + aQualifiedName, true );
     	if (aQualifiedName.equalsIgnoreCase(START_ELEMENT))
    	{
		    for (int i = 0; i < anAtt.getLength(); i++)
		    {
	    		Trace.append( Trace.SUITE, ", " + anAtt.getQName(i) + "=" + anAtt.getValue(i) );
	    		if (anAtt.getQName(i).equalsIgnoreCase(ATTRIBUTE_SEQUENCE))
		    	{
		        	mySequenceNr = Integer.valueOf( anAtt.getValue(i) ).intValue();
		    	}
// TODDO
//		    	else
//		    	{
//		    		myAnyAttributes.put(anAtt.getQName(i), anAtt.getValue(i));
//		    	}
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
// TODO
//		if ( ! aQualifiedName.equalsIgnoreCase(START_ELEMENT) )
//    	{
//			// TODO This will overwrite previous occurrences of the same elements. But that is possible in XML.
//			myAnyElements.put(aQualifiedName, myCurrentAnyValue);
//			myCurrentAnyValue = "";
//    	}
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

		if (aQualifiedName.equalsIgnoreCase(DESCRIPTION_ELEMENT))
    	{
    		myDescription = myDescriptionXmlHandler.getValue();
    		myDescriptionXmlHandler.reset();
    	}
    	else if (aQualifiedName.equalsIgnoreCase(ITEM_ELEMENT))
    	{
    		this.itemName = myItemNameXmlHandler.getValue();
    		myItemNameXmlHandler.reset();
    	}
    	else if (aQualifiedName.equalsIgnoreCase(LIST_ELEMENT))
    	{
    		this.listName = myListNameXmlHandler.getValue();
    		myListNameXmlHandler.reset();
    	}
    	else if (aQualifiedName.equalsIgnoreCase(DO_ELEMENT))
    	{
    		myDoEntries = myDoEntriesXmlHandler.getEntries();
    		myDoEntriesXmlHandler.reset();
    	}
    	else
    	{ // Programming fault
			throw new Error( "Child XML Handler returned, but not recognized. The handler was probably defined " +
			                 "in the Constructor but not handled in handleReturnFromChildElement()");
    	}
	}

	/**
	 * @return the TestEntryIteration
	 */
	public TestEntryIteration getTestEntryIteration()
//	public TestEntryIteration<TestEntry> getTestEntryIteration()
	{
		Trace.println(Trace.GETTER);
		
		return new TestEntryIteration( myDescription, mySequenceNr,
//		return new TestEntryIteration<TestEntry>( myDescription, mySequenceNr,
				this.itemName, this.listName, myDoEntries );
	}

	@Override
	public void reset()
	{
		Trace.println(Trace.SUITE);
		myDoEntries = new TestEntrySequence();
	}
}
