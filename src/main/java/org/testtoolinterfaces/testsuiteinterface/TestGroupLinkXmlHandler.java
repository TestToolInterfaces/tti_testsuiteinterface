package org.testtoolinterfaces.testsuiteinterface;

import org.testtoolinterfaces.testsuite.TestGroupLink;
import org.testtoolinterfaces.testsuite.TestLink;
import org.testtoolinterfaces.testsuite.TestSuiteException;
import org.testtoolinterfaces.utils.Trace;
import org.xml.sax.XMLReader;

/**
 * XmlHandler to read the testgrouplink part of an XML file
 * <testgrouplink id="..." type="..." sequence="..." [any]="...">
 *   ...
 * </testgrouplink>
 *
 * @author Arjan Kranenburg 
 * @see http://www.testtoolinterfaces.org
 * 
 */
public class TestGroupLinkXmlHandler extends TestExecItemLinkXmlHandler
{
	public static final String START_ELEMENT = "testgrouplink";

	/**
	 * Creates the XML Handler
	 * 
	 * @param anXmlReader		The XML Reader
	 */
	public TestGroupLinkXmlHandler( XMLReader anXmlReader )
	{
		super(anXmlReader, START_ELEMENT);
		Trace.println(Trace.CONSTRUCTOR);
		
		this.reset();
	}

	/**
	 * @return the TestGroupLink
	 * @throws TestSuiteException when the Id is not set or when the link is not defined.
	 */
	public TestGroupLink getTestGroupLink() throws TestSuiteException
	{
		Trace.println(Trace.SUITE);

		TestLink link = this.getLink();
		String id = this.getId();
		
		if ( id.isEmpty() )
		{
			throw new TestSuiteException( "Unknown TestGroup ID" );
		}

		if ( link == null )
		{
			throw new TestSuiteException( "Link to TestGroup not specified", id, this.getSequenceNr() );
		}

		TestGroupLink testGrouplink = new TestGroupLink( id,
			                                             this.getSequenceNr(),
			                                             link );
		
		testGrouplink.setAnyAttributes( this.getAnyAttributes() );

		return testGrouplink;
	}
}
