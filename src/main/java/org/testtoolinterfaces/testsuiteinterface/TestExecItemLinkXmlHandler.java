package org.testtoolinterfaces.testsuiteinterface;

import org.testtoolinterfaces.testsuite.TestLink;
import org.testtoolinterfaces.testsuite.TestLinkImpl;
import org.testtoolinterfaces.utils.Trace;
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
		Trace.println(Trace.CONSTRUCTOR);
		
		this.resetExecItemLinkHandler();
	}

	@Override
	public void handleCharacters(String aValue)
	{
		Trace.print(Trace.SUITE, "handleCharacters( " 
		            + aValue, true );
		String link = aValue.trim();
		if ( ! link.isEmpty() )
		{
			myLink += link;
		}
    }
    
	@Override
    public void processElementAttributes(String aQualifiedName, Attributes att)
    {
		Trace.print(Trace.SUITE, "processElementAttributes( " 
	            + aQualifiedName, true );
 		Attributes leftAttributes = new AttributesImpl();

 		if (aQualifiedName.equalsIgnoreCase(super.getStartElement()))
    	{
		    for (int i = 0; i < att.getLength(); i++)
		    {
	    		Trace.append( Trace.SUITE, ", " + att.getQName(i) + "=" + att.getValue(i) );
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
		Trace.append( Trace.SUITE, " )\n" );
		
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
		Trace.println(Trace.SUITE);

		myLink = "";
		myType = TestLink.TYPE_TTI; //default
		
		super.resetGroupEntryHandler();
	}
}
