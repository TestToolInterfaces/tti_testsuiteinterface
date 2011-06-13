package org.testtoolinterfaces.testsuiteinterface;

import java.io.File;

import org.testtoolinterfaces.testsuite.TestCaseLink;
import org.testtoolinterfaces.testsuite.TestSuiteException;
import org.testtoolinterfaces.utils.Trace;
import org.xml.sax.XMLReader;

/**
 * @author Arjan Kranenburg 
 *
 * <testcaselink id="..." type="..." sequence="..." [any]="...">
 *   ...
 * </testcaselink>
 * 
 */
public class TestCaseLinkXmlHandler extends TestLinkXmlHandler
{
	public static final String START_ELEMENT = "testcaselink";

	public TestCaseLinkXmlHandler( XMLReader anXmlReader )
	{
		super(anXmlReader, START_ELEMENT);
		Trace.println(Trace.CONSTRUCTOR);
		
		this.reset();
	}

	public TestCaseLink getTestCaseLink() throws TestSuiteException
	{
		Trace.println(Trace.SUITE);

		File link = this.getLink();
		String id = this.getId();
		
		if ( id.isEmpty() )
		{
			throw new TestSuiteException( "Unknown TestCase ID" );
		}

		if ( link == null )
		{
			throw new TestSuiteException( "Link to TestCase not specified", id, this.getSequence() );
		}

		TestCaseLink testCaseLink = new TestCaseLink( id,
		                                              this.getType(),
		                                              this.getSequence(),
		                                              link,
		                                              this.getAnyAttributes() );

		return testCaseLink;
	}
}
