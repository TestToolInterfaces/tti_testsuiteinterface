/**
 * 
 */
package org.testtoolinterfaces.testsuiteinterface;

import java.io.File;
import java.util.Iterator;

import org.testtoolinterfaces.testsuite.LooseTestInterfaceList;
import org.testtoolinterfaces.testsuite.TestCaseLink;
import org.testtoolinterfaces.testsuite.TestEntry;
import org.testtoolinterfaces.testsuite.TestEntrySequence;
import org.testtoolinterfaces.testsuite.TestGroup;
import org.testtoolinterfaces.testsuite.TestGroupLink;
import org.testtoolinterfaces.testsuite.TestInterfaceList;
import org.testtoolinterfaces.testsuite.TestStep;
import org.testtoolinterfaces.testsuite.TestStepSequence;
import org.testtoolinterfaces.testsuite.TestEntry.TYPE;
import org.testtoolinterfaces.utils.Trace;

/**
 * @author Arjan
 *
 * Simpel tool to print the Test Case Result File
 * It serves as well as a debug tool for the structure of the result file.
 */
public class TestGroupPrinter
{

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		Trace.getInstance().addBaseClass("org.testtoolinterfaces");

		if ( args.length == 0 )
		{
			System.out.println("Usage: java -jar tgPrinter.jar <Test Group File>");
			System.exit(1);
		}
		String requestedFileName = args[0];

		System.out.println( "Starting Pretty printing for:" );
		System.out.println( requestedFileName );

		File requestedFile = new File( requestedFileName );
		File testSuiteDir = requestedFile.getParentFile();
		
		TestGroup testGroup = readFile(requestedFile);
		
		printTestGroup(testGroup, testSuiteDir, "");
	}

	/**
	 * @param aTcResult
	 */
	public static void printTestGroup(TestGroup aTestGroup, File aBaseDir, String anIndent)
	{
		System.out.println( anIndent + "ID:              " + aTestGroup.getId() );
		System.out.println( anIndent + "Description:" );
		System.out.println( anIndent + aTestGroup.getDescription() );
		System.out.println( anIndent + "Sequence Number: " + aTestGroup.getSequenceNr() );
		System.out.println( anIndent + "=================== Preparation =====================" );
		TestStepSequence prepares = aTestGroup.getPrepareSteps();
		Iterator<TestStep> itr1 = prepares.iterator();
		while(itr1.hasNext() )
		{
			TestStep step = itr1.next();
			System.out.println( anIndent + step.getSequenceNr() + " - " + step.getType() );
		}

		System.out.println( anIndent + "=================== Execution =======================" );
		TestEntrySequence executes = aTestGroup.getExecutionEntries();
		Iterator<TestEntry> itr2 = executes.iterator();
		while(itr2.hasNext() )
		{
			TestEntry entry = itr2.next();
			if ( entry.getType() == TYPE.Group )
			{
				printTestGroup( (TestGroup) entry, aBaseDir, anIndent + "  " );
			}
			else if ( entry.getType() == TYPE.GroupLink )
			{
				TestGroupLink tgLink = (TestGroupLink) entry;
				File tgFile = new File( aBaseDir, tgLink.getLink().getPath() );
				TestGroup testGroup = readFile(tgFile);
				
				File baseDir = tgFile.getParentFile();
				printTestGroup(testGroup, baseDir, anIndent + "  ");
			}
			else
			{
				System.out.println( anIndent + entry.getSequenceNr() + " - " + entry.getId() );
				if ( entry.getType() == TYPE.CaseLink )
				{
					TestCaseLink tcLink = (TestCaseLink) entry;
					File tcFile = new File( aBaseDir, tcLink.getLink().getPath() );
					System.out.println( anIndent + tcFile.getAbsolutePath() );
				}
			}
		}

		System.out.println( anIndent + "=================== Cleanup =========================" );
		TestStepSequence restores = aTestGroup.getRestoreSteps();
		Iterator<TestStep> itr3 = restores.iterator();
		while(itr3.hasNext() )
		{
			TestStep step = itr3.next();
			System.out.println( anIndent + step.getSequenceNr() + " - " + step.getType() );
		}

		System.out.println( anIndent + "=====================================================" );
	}

	/**
	 * Reads the TestCaseResult File
	 * 
	 * @param aRequestedFile
	 */
	private static TestGroup readFile(File aRequestedFile)
	{
		TestInterfaceList interfaceList = new LooseTestInterfaceList();
		TestGroupReader tgReader = new TestGroupReader(interfaceList);
		
		TestGroup testGroup = tgReader.readTgFile(aRequestedFile);
		return testGroup;
	}
}
