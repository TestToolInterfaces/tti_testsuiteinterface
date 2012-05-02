package org.testtoolinterfaces.testsuiteinterface;

import org.testtoolinterfaces.utils.Trace;
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
		Trace.println(Trace.CONSTRUCTOR);
		
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
		Trace.print(Trace.SUITE, "handleCharacters( " 
		            + aValue, true );
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
		Trace.print(Trace.SUITE, "processElementAttributes( " 
	            + aQualifiedName, true );
     	if (aQualifiedName.equalsIgnoreCase(super.getStartElement()))
    	{
		    for (int i = 0; i < att.getLength(); i++)
		    {
	    		Trace.append( Trace.SUITE, ", " + att.getQName(i) + "=" + att.getValue(i) );
		    	if (att.getQName(i).equalsIgnoreCase(ATTRIBUTE_TYPE))
		    	{
		        	myType = att.getValue(i);
		    	}
		    }
    	}
		Trace.append( Trace.SUITE, " )\n" );
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
		Trace.println(Trace.SUITE);

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
