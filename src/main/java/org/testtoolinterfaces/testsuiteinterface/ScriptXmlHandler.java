package org.testtoolinterfaces.testsuiteinterface;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testtoolinterfaces.utils.Mark;
import org.testtoolinterfaces.utils.XmlHandler;
import org.xml.sax.Attributes;
import org.xml.sax.XMLReader;

/**
 * XmlHandler to read the script part of a TestStepScript from a TTI-XML file.
 * 
 * <script type="...">
 *   ...
 * </script>
 *
 * @author Arjan Kranenburg 
 * @see http://www.testtoolinterfaces.org
 * 
 */
public class ScriptXmlHandler extends XmlHandler
{
    private static final Logger LOG = LoggerFactory.getLogger(ScriptXmlHandler.class);
	public static final String ELEMENT_START = "script";

	private static final String ATTRIBUTE_TYPE = "type";
	
	private String myScript;
	private String myType;

	/**
	 * Creates the XML Handler
	 * 
	 * @param anXmlReader the xmlReader
	 */
	public ScriptXmlHandler( XMLReader anXmlReader )
	{
		super(anXmlReader, ELEMENT_START);
		LOG.trace(Mark.CONSTRUCTOR, "{}", anXmlReader);
		
		this.reset();
	}

	@Override
	public void handleStartElement(String aQualifiedName)
	{
   		//nop;
    }

	@Override
	public void handleCharacters(String aValue)
	{
		LOG.trace(Mark.SUITE, "{}", aValue);
		myScript = aValue.trim();
    }
    
	@Override
	public void handleEndElement(String aQualifiedName)
	{
		//nop
    }

	@Override
    public void processElementAttributes(String aQualifiedName, Attributes att)
    {
		LOG.trace(Mark.SUITE, "{}, {}", aQualifiedName, att);
     	if (aQualifiedName.equalsIgnoreCase(super.getStartElement()))
    	{
		    for (int i = 0; i < att.getLength(); i++)
		    {
	    		LOG.trace(Mark.SUITE, "{} = {}", att.getQName(i), att.getValue(i));
		    	if (att.getQName(i).equalsIgnoreCase(ATTRIBUTE_TYPE))
		    	{
		        	myType = att.getValue(i);
		    	}
		    }
    	}
    }

	@Override
	public void handleGoToChildElement(String aQualifiedName)
	{
		// nop
	}

	@Override
	public void handleReturnFromChildElement(String aQualifiedName, XmlHandler aChildXmlHandler)
	{
		// nop
	}

	@Override
	public void reset()
	{
		LOG.trace(Mark.SUITE, "");

		myScript = "";
		myType = "";
	}

	/**
	 * @return the Test script
	 */
	public String getScript()
	{
		return myScript;
	}

	/**
	 * @return the Script Type
	 */
	public String getType()
	{
		return myType;
	}
}
