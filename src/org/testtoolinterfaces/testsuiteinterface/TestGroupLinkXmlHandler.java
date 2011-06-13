package org.testtoolinterfaces.testsuiteinterface;

import java.io.File;

import org.testtoolinterfaces.testsuite.TestGroupLink;
import org.testtoolinterfaces.testsuite.TestSuiteException;
import org.testtoolinterfaces.utils.Trace;
import org.xml.sax.XMLReader;

/**
 * @author Arjan Kranenburg 
 * 
 * <testgrouplink id="..." type="..." sequence="..." [any]="...">
 *   ...
 * </testgrouplink>
 *
 */
public class TestGroupLinkXmlHandler extends TestLinkXmlHandler
{
	public static final String START_ELEMENT = "testgrouplink";

	public TestGroupLinkXmlHandler( XMLReader anXmlReader )
	{
		super(anXmlReader, START_ELEMENT);
		Trace.println(Trace.CONSTRUCTOR);
		
		this.reset();
	}

	public TestGroupLink getTestGroupLink() throws TestSuiteException
	{
		Trace.println(Trace.SUITE);

		File link = this.getLink();
		String id = this.getId();
		
		if ( id.isEmpty() )
		{
			throw new TestSuiteException( "Unknown TestGroup ID" );
		}

		if ( link == null )
		{
			throw new TestSuiteException( "Link to TestGroup not specified", id, this.getSequence() );
		}

		TestGroupLink testGrouplink = new TestGroupLink( id,
		                                                 this.getType(),
			                                             this.getSequence(),
			                                             link,
			                                             this.getAnyAttributes() );

		return testGrouplink;
	}
}
