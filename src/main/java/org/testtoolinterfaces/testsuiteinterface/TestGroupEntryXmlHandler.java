package org.testtoolinterfaces.testsuiteinterface;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testtoolinterfaces.utils.Mark;
import org.xml.sax.Attributes;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.AttributesImpl;

/**
 * Abstract XmlHandler to read the TestGroupEntry part of an XML file.
 * Known subclasses are TestExecItemLinkXmlHandler.
 * 
 * <tag id="..." ...>
 * </tag>
 * 
 * @author Arjan Kranenburg 
 * @see http://www.testtoolinterfaces.org
 * 
 */
public abstract class TestGroupEntryXmlHandler extends TestEntryXmlHandler
{
    private static final Logger LOG = LoggerFactory.getLogger(TestGroupEntryXmlHandler.class);

    private static final String ELEMENT_ID = "id";

	private String myId;

	/**
	 * Creates the XML Handler
	 * 
	 * @param anXmlReader		The XML Reader
	 * @param aStartElement		The start element
	 */
	public TestGroupEntryXmlHandler( XMLReader anXmlReader, String aStartElement )
	{
		super(anXmlReader, aStartElement);
		LOG.trace(Mark.CONSTRUCTOR, "{}, {}", anXmlReader, aStartElement);
		
		this.resetGroupEntryHandler();
	}

	@Override
    public void processElementAttributes(String aQualifiedName, Attributes att)
    {
		LOG.trace(Mark.SUITE, "{}, {}", aQualifiedName, att);
 		Attributes leftAttributes = new AttributesImpl();

 		if (aQualifiedName.equalsIgnoreCase(this.getStartElement()))
    	{
		    for (int i = 0; i < att.getLength(); i++)
		    {
				LOG.trace(Mark.SUITE, "{} = {}", att.getQName(i), att.getValue(i) );
		    	if (att.getQName(i).equalsIgnoreCase(ELEMENT_ID))
		    	{
		        	myId = att.getValue(i);
		    	} else {
		    		((AttributesImpl) leftAttributes).addAttribute( att.getURI(i), att.getLocalName(i),
		    				att.getQName(i), att.getType(i), att.getValue(i));
		    	}
		    }
    	} else {
    		leftAttributes = att;
    	}
		
		super.processElementAttributes(aQualifiedName, leftAttributes);
    }

	/**
	 * @return the id
	 */
	protected String getId()
	{
		return myId;
	}

	@Override
	public void reset()
	{
		this.resetGroupEntryHandler();
	}

	public final void resetGroupEntryHandler()
	{
		LOG.trace(Mark.SUITE, "");
		myId = "";
		
		super.resetEntryHandler();
	}
}
