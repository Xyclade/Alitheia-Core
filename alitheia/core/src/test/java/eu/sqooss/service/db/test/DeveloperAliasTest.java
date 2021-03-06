package eu.sqooss.service.db.test;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import eu.sqooss.core.AlitheiaCore;
import eu.sqooss.service.db.Branch;
import eu.sqooss.service.db.DBService;
import eu.sqooss.service.db.Developer;
import eu.sqooss.service.db.DeveloperAlias;
import eu.sqooss.service.db.EncapsulationUnit;
import eu.sqooss.service.db.EncapsulationUnitMeasurement;
import eu.sqooss.service.db.ExecutionUnit;
import eu.sqooss.service.db.ExecutionUnitMeasurement;
import eu.sqooss.service.db.MailingListThread;
import eu.sqooss.service.db.MailingListThreadMeasurement;
import eu.sqooss.service.db.Metric;
import eu.sqooss.service.db.NameSpace;
import eu.sqooss.service.db.NameSpaceMeasurement;
import eu.sqooss.service.db.ProjectFile;
import eu.sqooss.service.db.ProjectFileMeasurement;
import eu.sqooss.service.db.ProjectVersion;
import eu.sqooss.service.db.ProjectVersionMeasurement;
import eu.sqooss.service.db.ProjectVersionParent;
import eu.sqooss.service.db.StoredProject;
import eu.sqooss.service.db.StoredProjectMeasurement;
import eu.sqooss.service.db.Tag;
import static org.mockito.Mockito.*;


@RunWith(PowerMockRunner.class)
@PrepareForTest({DBService.class, AlitheiaCore.class, Branch.class})
public class DeveloperAliasTest {
	
	
static DeveloperAlias testObject;



	@BeforeClass
	public static void setUp() {
		testObject = new DeveloperAlias();
		assertEquals(961,testObject.hashCode());
	}

	@Test
	public void testInitialiser() {
		Developer dev = new Developer();
		DeveloperAlias testValue = new DeveloperAlias("testEmail",dev);
	
		Developer setDeveloper = Whitebox.<Developer> getInternalState(testValue, "developer");
		String setEmail = Whitebox.<String> getInternalState(testValue, "email");
		
		assertEquals(dev, setDeveloper);
		assertEquals("testEmail", setEmail);
	}

	@Test
	public void testIdGetterSetter()
	{
		//set the value via the setter
		testObject.setId(1900L);
		
		//Pull out property in order to compare
		long actualValue = Whitebox.<Long>getInternalState(testObject, "id");
		
		//Compare the expected with the actual result
		assertEquals(actualValue,1900L);
		
		long getValue=  testObject.getId();
		//Compare the expected with the actual result
		assertEquals(getValue,1900L);	
	}

	@Test
	public void testEmailGetterSetter()
	{
		//set the value via the setter
		testObject.setEmail("testEmail");
		
		//Pull out property in order to compare
		String actualValue = Whitebox.<String>getInternalState(testObject, "email");
		
		//Compare the expected with the actual result
		assertEquals(actualValue,"testEmail");
		
		String getValue=  testObject.getEmail();
		//Compare the expected with the actual result
		assertEquals(getValue,"testEmail");	
	}
	@Test
	public void testDeveloperGetterSetter() {

		Developer testValue = mock(Developer.class);
		// set the value via the setter
		testObject.setDeveloper(testValue);

		// Pull out property in order to compare
		Developer actualValue = Whitebox.<Developer> getInternalState(
				testObject, "developer");

		// Compare the expected with the actual result
		assertEquals(actualValue, testValue);

		Developer getValue = testObject.getDeveloper();
		// Compare the expected with the actual result
		assertEquals(getValue, testValue);
	}
	
	@Test
	public void testToStringMethod()
	{
		testObject.setEmail("testEmail");
		assertEquals("testEmail",testObject.toString());	
	}
	
	@Test
	public void testEqualsMethod()
	{
		testObject.setEmail("testEmail");
		assertTrue(testObject.equals(testObject));	
		assertFalse(testObject.equals(null));
	}
	
}
