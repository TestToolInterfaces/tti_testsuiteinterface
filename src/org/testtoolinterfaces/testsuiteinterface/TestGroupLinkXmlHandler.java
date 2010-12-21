package org.testtoolinterfaces.testsuiteinterface;

import java.io.File;

import org.testtoolinterfaces.testsuite.TestGroupLink;
import org.testtoolinterfaces.utils.Trace;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.LocatorImpl;

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

	public TestGroupLink getTestGroupLink() throws TestReaderException
	{
		Trace.println(Trace.SUITE);

		File link = this.getLink();
		String id = this.getId();
		
		if ( id.isEmpty() )
		{
			throw new TestReaderException( "Unknown TestGroup ID", new LocatorImpl());
		}

		if ( link == null )
		{
			throw new TestReaderException( "Link to TestGroup not specified", new LocatorImpl());
		}

		TestGroupLink testGrouplink = new TestGroupLink( id,
		                                                         this.getType(),
			                                                     this.getSequence(),
			                                                     link,
			                                                     this.getAnyAttributes() );

		return testGrouplink;
	}
}
