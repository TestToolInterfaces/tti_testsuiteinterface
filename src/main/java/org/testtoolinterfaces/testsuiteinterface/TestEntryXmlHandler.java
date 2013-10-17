package org.testtoolinterfaces.testsuiteinterface;

import java.util.Hashtable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testtoolinterfaces.testsuite.TestSuiteException;
import org.testtoolinterfaces.utils.GenericTagAndStringXmlHandler;
import org.testtoolinterfaces.utils.Mark;
import org.testtoolinterfaces.utils.XmlHandler;
import org.xml.sax.Attributes;
import org.xml.sax.XMLReader;


/**
 * XmlHandler to read the testentry (i.e. generic) part from a TTI-XML file.
 * 
 * <tag sequence="..." any="...">
 *  <description>...</description>
 *  <[any]>
 *   ...
 *  </[any]>
 * </tag>
 * 
 * @author Arjan Kranenburg 
 * @see http://www.testtoolinterfaces.org
 * 
 */
public abstract class TestEntryXmlHandler extends XmlHandler
{
    private static final Logger LOG = LoggerFactory.getLogger(TestEntryXmlHandler.class);

    private static final String ATTRIBUTE_SEQUENCE = "sequence";
	
	private static final String DESCRIPTION_ELEMENT = "description";

	private Hashtable<String, String> myAnyAttributes;
	private int mySequenceNr;

	private String myDescription;
	private Hashtable<String, String> myAnyElements;
	private String myCurrentAnyValue;

	private GenericTagAndStringXmlHandler myDescriptionXmlHandler;
	
	/**
	 * Creates the XML Handler
	 * 
	 * @param anXmlReader the xmlReader
	 * @param anInterfaceList a list of interfaces
	 * @param aCheckStepParameter flag to indicate if specified parameters of a step must be verified in the interface
	 */
	public TestEntryXmlHandler( XMLReader anXmlReader, String aTag )
	{
		super(anXmlReader, aTag);
		LOG.trace(Mark.CONSTRUCTOR, "{}, {}", anXmlReader, aTag);
		
	    myDescriptionXmlHandler = new GenericTagAndStringXmlHandler(anXmlReader, DESCRIPTION_ELEMENT);
		this.addElementHandler(myDescriptionXmlHandler);

		this.resetEntryHandler();
	}

	@Override
	public void handleStartElement(String aQualifiedName)
	{
   		//nop;
    }

	@Override
	public void handleCharacters(String aValue)
	{
		myCurrentAnyValue = myCurrentAnyValue + aValue;
    }
    
	@Override
	public void handleEndElement(String aQualifiedName)
	{
		if ( ! aQualifiedName.equalsIgnoreCase(this.getStartElement()) )
    	{
			// TODO This will overwrite previous occurrences of the same elements. But that is possible in XML.
			myAnyElements.put(aQualifiedName, myCurrentAnyValue);
			myCurrentAnyValue = "";
    	}
    }

	@Override
    public void processElementAttributes(String aQualifiedName, Attributes anAtt)
    {
		LOG.trace(Mark.SUITE, "{}, {}", aQualifiedName, anAtt);
     	if (aQualifiedName.equalsIgnoreCase(this.getStartElement()))
    	{
		    for (int i = 0; i < anAtt.getLength(); i++)
		    {
				LOG.trace(Mark.SUITE, "{} = {}", anAtt.getQName(i), anAtt.getValue(i) );
		    	if (anAtt.getQName(i).equalsIgnoreCase(ATTRIBUTE_SEQUENCE))	{
		        	mySequenceNr = Integer.valueOf( anAtt.getValue(i) ).intValue();
		    	} else {
		    		myAnyAttributes.put(anAtt.getQName(i), anAtt.getValue(i));
		    	}
		    }
    	}
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
		LOG.trace(Mark.SUITE, "{}, {}", aQualifiedName, aChildXmlHandler);

		if (aQualifiedName.equalsIgnoreCase(DESCRIPTION_ELEMENT)) {
    		myDescription = myDescriptionXmlHandler.getValue();
    		myDescriptionXmlHandler.reset();
    	} else {

    		// Programming fault
    			throw new Error( "Child XML Handler returned, but not recognized. The handler was probably defined " +
    			                 "in the Constructor but not handled in handleReturnFromChildElement()");
    	}
	}

	/**
	 * @return the SequenceNr
	 */
	protected int getSequenceNr() {
		return mySequenceNr;
	}

	/**
	 * @return the Description
	 */
	protected String getDescription() {
		return myDescription;
	}

	/**
	 * @return the AnyAttributes
	 */
	protected Hashtable<String, String> getAnyAttributes() {
		return myAnyAttributes;
	}

	/**
	 * @return the AnyElements
	 */
	protected Hashtable<String, String> getAnyElements() {
		return myAnyElements;
	}

	@Override
	public void reset()
	{
		this.resetEntryHandler();
	}
	
	public final void resetEntryHandler()
	{
		LOG.trace(Mark.SUITE, "");

	    mySequenceNr = 0;
	    myAnyAttributes = new Hashtable<String, String>();

	    myDescription = "";
		myAnyElements = new Hashtable<String, String>();
	    myCurrentAnyValue = "";
	}
}
