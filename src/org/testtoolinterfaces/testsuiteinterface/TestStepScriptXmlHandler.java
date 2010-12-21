package org.testtoolinterfaces.testsuiteinterface;

import java.util.Hashtable;

import org.testtoolinterfaces.utils.Trace;
import org.testtoolinterfaces.utils.XmlHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

/**
 * @author Arjan Kranenburg 
 * 
 * <script type="..." [any]="...">
 *   ...
 * </script>
 *
 */
public class TestStepScriptXmlHandler extends XmlHandler
{
	public static final String ELEMENT_START = "script";

	private static final String ATTRIBUTE_TYPE = "type";
	
	private String myScript;

	private String myType;
	private Hashtable<String, String> myAnyAttributes;

	public TestStepScriptXmlHandler( XMLReader anXmlReader )
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
		    	else
		    	{
		    		myAnyAttributes.put(att.getQName(i), att.getValue(i));
		    	}
		    }
    	}
		Trace.append( Trace.SUITE, " )\n" );
    }

	@Override
	/** 
	 * @param aQualifiedName the name of the childElement
	 * 
	 */
	public void handleGoToChildElement(String aQualifiedName) throws SAXParseException
	{
		// nop
	}

	@Override
	public void handleReturnFromChildElement(String aQualifiedName, XmlHandler aChildXmlHandler)
	{
		// nop
	}

	public void reset()
	{
		Trace.println(Trace.SUITE);

		myScript = "";
		myType = "";
		myAnyAttributes = new Hashtable<String, String>();
	}

	/**
	 * @return the myScript
	 */
	public String getScript()
	{
		return myScript;
	}

	/**
	 * @return the myType
	 */
	public String getScriptType()
	{
		return myType;
	}

	/**
	 * @return the myAnyAttributes
	 */
	public Hashtable<String, String> getAnyAttributes()
	{
		return myAnyAttributes;
	}
}
