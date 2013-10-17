package org.testtoolinterfaces.testsuiteinterface;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testtoolinterfaces.testsuite.TestLink;
import org.testtoolinterfaces.testsuite.impl.TestLinkImpl;
import org.testtoolinterfaces.utils.Mark;
import org.xml.sax.Attributes;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.AttributesImpl;

/**
 * Abstract XmlHandler to read the link part of an XML file.
 * Known subclasses are testgrouplink and testcaselink.
 * 
 * <testgrouplink id="..." type="..." sequence="..." [any]="...">
 *   ...
 * </testgrouplink>
 *
 * <testcaselink id="..." type="..." sequence="..." [any]="...">
 *   ...
 * </testcaselink>
 * 
 * @author Arjan Kranenburg 
 * @see http://www.testtoolinterfaces.org
 * 
 */
public abstract class TestExecItemLinkXmlHandler extends TestGroupEntryXmlHandler
{
    private static final Logger LOG = LoggerFactory.getLogger(TestExecItemLinkXmlHandler.class);

    private static final String ELEMENT_TYPE = "type";
	
	private String myLink;
	private String myType;

	/**
	 * Creates the XML Handler
	 * 
	 * @param anXmlReader		The XML Reader
	 * @param aStartElement		The start element
	 */
	public TestExecItemLinkXmlHandler( XMLReader anXmlReader, String aStartElement )
	{
		super(anXmlReader, aStartElement);
		LOG.trace(Mark.CONSTRUCTOR, "{}, {}", anXmlReader, aStartElement);
		
		this.resetExecItemLinkHandler();
	}

	@Override
	public void handleCharacters(String aValue)
	{
		LOG.trace(Mark.SUITE, "{}", aValue );
		String link = aValue.trim();
		if ( ! link.isEmpty() )
		{
			myLink += link;
		}
    }
    
	@Override
    public void processElementAttributes(String aQualifiedName, Attributes att)
    {
		LOG.trace(Mark.SUITE, "{}, {}", aQualifiedName, att);
 		Attributes leftAttributes = new AttributesImpl();

 		if (aQualifiedName.equalsIgnoreCase(super.getStartElement()))
    	{
		    for (int i = 0; i < att.getLength(); i++)
		    {
				LOG.trace(Mark.SUITE, "{} = {}", att.getQName(i), att.getValue(i) );
		    	if (att.getQName(i).equalsIgnoreCase(ELEMENT_TYPE))
		    	{
		        	myType = att.getValue(i);
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
	 * @return the link
	 */
	protected TestLink getLink()
	{
		return new TestLinkImpl( myLink, myType );
	}

	@Override
	public void reset()
	{
		this.resetExecItemLinkHandler();
	}
	
	public final void resetExecItemLinkHandler()
	{
		LOG.trace(Mark.SUITE, "");

		myLink = "";
		myType = TestLink.TYPE_TTI; //default
		
		super.resetGroupEntryHandler();
	}
}
