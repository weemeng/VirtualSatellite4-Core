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
package de.dlr.sc.virsat.model.extension.requirements.reqif.builder;

import static org.junit.Assert.assertEquals;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.dlr.sc.virsat.project.test.AProjectTestCase;

/**
 * @author fran_tb
 *
 */
public class ReqIFNatureTest extends AProjectTestCase {
	
	public static final String BUILDER_ID = "de.dlr.sc.virsat.model.extension.requirements.reqif.builder.ReqIFBuilder";
	
	protected ReqIFNature nature;
	
	
	@Before
	public void setUp() throws CoreException {
		super.setUp();
		addEditingDomainAndRepository();
		nature = new ReqIFNature();
		nature.setProject(testProject);
	}

	@After
	public void tearDown() throws CoreException {
		super.tearDown();
	}
	
	/**
	 * Checks if trace nature is active
	 * @param project the project to test
	 * @return true if it is configured, false if not
	 * @throws CoreException throws core exception
	 */
	protected boolean isReqIFNatureActive(IProject project) throws CoreException {
		boolean hasTraceBuilder = false;
		IProjectDescription desc = project.getDescription();
		ICommand[] commands = desc.getBuildSpec();
		for (int i = 0; i < commands.length; ++i) {
			if (commands[i].getBuilderName().equals(BUILDER_ID)) {
				hasTraceBuilder = true;
			} 
		}
		return hasTraceBuilder;
	}
	
	@Test
	public void testConfigure() throws CoreException {
		nature.configure();
		
		assertEquals("ReqIF builder not added to the project", true, isReqIFNatureActive(testProject));
		
	}
	
	@Test
	public void testUnConfigure() throws CoreException {
		nature.deconfigure();

		assertEquals("ReqIF builder not added to the project", false, isReqIFNatureActive(testProject));
		
		nature.configure();
		assertEquals("ReqIF builder not added to the project", true, isReqIFNatureActive(testProject));
		
		nature.deconfigure();
		assertEquals("ReqIF builder not added to the project", false, isReqIFNatureActive(testProject));
		
	}

}
