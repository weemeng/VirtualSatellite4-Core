/*******************************************************************************
 * Copyright (c) 2008-2019 German Aerospace Center (DLR), Simulation and Software Technology, Germany.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
/**
 * 
 */
package de.dlr.sc.virsat.model.extension.requirements.reqif.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.rmf.reqif10.ReqIF10Factory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.dlr.sc.virsat.model.dvlm.categories.CategoriesFactory;
import de.dlr.sc.virsat.model.dvlm.categories.CategoryAssignment;
import de.dlr.sc.virsat.model.dvlm.categories.propertydefinitions.PropertydefinitionsFactory;
import de.dlr.sc.virsat.model.dvlm.categories.propertydefinitions.StringProperty;
import de.dlr.sc.virsat.model.dvlm.categories.propertyinstances.PropertyinstancesFactory;
import de.dlr.sc.virsat.model.dvlm.categories.propertyinstances.ValuePropertyInstance;
import de.dlr.sc.virsat.model.extension.requirements.model.RequirementsSpecification;
import de.dlr.sc.virsat.project.resources.VirSatResourceSet;
import de.dlr.sc.virsat.project.test.AProjectTestCase;

/**
 * @author fran_tb
 *
 */
public class ReqIFBuilderUtilTest extends AProjectTestCase {

	VirSatResourceSet resSetRepository;
	protected static final String TEST_MODEL = "data/sadsadasf34fg.dvlm";
	protected static final String TEST_REQIF_FILE = "data/documents/TestFileName.reqif";

	protected static final String TEST_SPEC_NAME = "TestSpec";
	protected static final String TEST_FILE_NAME = "TestFileName";

	protected static final String TEST_SPEC_NAME_EXPECTED = "TestSpec.reqif";
	protected static final String TEST_FILE_NAME_EXPECTED = "TestFileName.reqif";

	protected IFile testFile;
	protected IFile expectedReqIFFile;
	protected Resource testFileResource;
	CategoryAssignment testCA;
	ValuePropertyInstance pInstance;

	/**
	 * @throws CoreException
	 */
	@Before
	public void setUp() throws CoreException {
		super.setUp();
		addEditingDomainAndRepository();
		resSetRepository = VirSatResourceSet.getResourceSet(testProject, false);

		testFile = testProject.getFile(TEST_MODEL);
		expectedReqIFFile = testProject.getFile(TEST_REQIF_FILE);

		testFileResource = resSetRepository
				.createResource(URI.createPlatformResourceURI(testFile.getFullPath().toString(), true));


		editingDomain.getCommandStack().execute(new RecordingCommand(editingDomain) {
			@Override
			protected void doExecute() {
				testCA = CategoriesFactory.eINSTANCE.createCategoryAssignment();
				StringProperty propFilename = PropertydefinitionsFactory.eINSTANCE.createStringProperty();
				propFilename.setName(RequirementsSpecification.PROPERTY_FILENAME);
				pInstance = PropertyinstancesFactory.eINSTANCE.createValuePropertyInstance();
				pInstance.setType(propFilename);
				testCA.getPropertyInstances().add(pInstance);
				testCA.setName(TEST_SPEC_NAME);
				testFileResource.getContents().add(testCA);
			}
		});
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws CoreException {
		super.setUp();
		testFile.delete(true, null);
		expectedReqIFFile.delete(true, null);
	}

	/**
	 * Test method for
	 * {@link de.dlr.sc.virsat.model.extension.requirements.reqif.util.ReqIFBuilderUtil#ReqIFBuilderUtil()}.
	 */
	@Test
	public void testReqIFBuilderUtil() {

		ReqIFBuilderUtil utilExtension = new ReqIFBuilderUtilTestExtension();
		assertNotNull(utilExtension);

	}

	/**
	 * Test method for
	 * {@link de.dlr.sc.virsat.model.extension.requirements.reqif.util.ReqIFBuilderUtil#getReqIFFilenameFromSpecification(de.dlr.sc.virsat.model.dvlm.categories.CategoryAssignment)}.
	 */
	@Test
	public void testGetReqIFFilenameFromSpecification() {

		assertEquals("Wrong filename of model without filename definition", TEST_SPEC_NAME_EXPECTED,
				ReqIFBuilderUtil.getReqIFFilenameFromSpecification(testCA));

		editingDomain.getCommandStack().execute(new RecordingCommand(editingDomain) {
			@Override
			protected void doExecute() {
				pInstance.setValue(TEST_FILE_NAME);
			}
		});
		assertEquals("Wrong filename of model with filename definition", TEST_FILE_NAME_EXPECTED,
				ReqIFBuilderUtil.getReqIFFilenameFromSpecification(testCA));

	}

	/**
	 * Test method for
	 * {@link de.dlr.sc.virsat.model.extension.requirements.reqif.util.ReqIFBuilderUtil#getReqIFUriFromDVLMModelSpecification(de.dlr.sc.virsat.model.dvlm.categories.CategoryAssignment)}.
	 */
	@Test
	public void testGetReqIFUriFromDVLMModelSpecification() {

		editingDomain.getCommandStack().execute(new RecordingCommand(editingDomain) {
			@Override
			protected void doExecute() {
				pInstance.setValue(TEST_FILE_NAME);
			}
		});

		URI resultURI = ReqIFBuilderUtil.getReqIFUriFromDVLMModelSpecification(testCA);

		assertEquals("URIs are not the same",
				URI.createPlatformResourceURI(expectedReqIFFile.getFullPath().toString(), true), resultURI);
	}

	/**
	 * Test method for
	 * {@link de.dlr.sc.virsat.model.extension.requirements.reqif.util.ReqIFBuilderUtil#isReqIFModel(org.eclipse.emf.ecore.resource.Resource)}.
	 */
	@Test
	public void testIsReqIFModel() {
		Resource testResource = resSetRepository.createResource(URI.createPlatformResourceURI(expectedReqIFFile.getFullPath().toString(), true));
		
		assertFalse("File does not contain reqIF content yet", ReqIFBuilderUtil.isReqIFModel(testResource));
		
		editingDomain.getCommandStack().execute(new RecordingCommand(editingDomain) {
			@Override
			protected void doExecute() {
				testCA = CategoriesFactory.eINSTANCE.createCategoryAssignment();
				testResource.getContents().add(testCA);
			}
		});
		
		assertFalse("File does not contain reqIF content", ReqIFBuilderUtil.isReqIFModel(testResource));
		
		editingDomain.getCommandStack().execute(new RecordingCommand(editingDomain) {
			@Override
			protected void doExecute() {
				testResource.getContents().clear();
				testResource.getContents().add(ReqIF10Factory.eINSTANCE.createReqIF());
			}
		});
		
		assertTrue("File does contain reqIF content", ReqIFBuilderUtil.isReqIFModel(testResource));
		
	}

}
