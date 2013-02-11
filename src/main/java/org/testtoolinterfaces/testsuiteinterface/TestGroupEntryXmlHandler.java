package org.testtoolinterfaces.testsuiteinterface;

import org.testtoolinterfaces.utils.Trace;
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
		Trace.println(Trace.CONSTRUCTOR);
		
		this.resetGroupEntryHandler();
	}

	@Override
    public void processElementAttributes(String aQualifiedName, Attributes att)
    {
		Trace.print(Trace.SUITE, "processElementAttributes( " 
	            + aQualifiedName, true );
 		Attributes leftAttributes = new AttributesImpl();

 		if (aQualifiedName.equalsIgnoreCase(this.getStartElement()))
    	{
		    for (int i = 0; i < att.getLength(); i++)
		    {
	    		Trace.append( Trace.SUITE, ", " + att.getQName(i) + "=" + att.getValue(i) );
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
		Trace.append( Trace.SUITE, " )\n" );
		
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
		Trace.println(Trace.SUITE);
		myId = "";
		
		super.resetEntryHandler();
	}
}
