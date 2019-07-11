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
package de.dlr.sc.virsat.model.extension.requirements.reqif.transformation.command;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.rmf.reqif10.ReqIF;
import org.eclipse.rmf.reqif10.ReqIF10Factory;

import de.dlr.sc.virsat.model.extension.requirements.model.RequirementsSpecification;
import de.dlr.sc.virsat.model.extension.requirements.reqif.transformation.ReqIFModelBuilder;

/**
 * @author fran_tb
 *
 */
public class InitializeReqIFCommand extends RecordingCommand {

	protected final Resource resource;
	protected final RequirementsSpecification specification;
	
	protected final ReqIFModelBuilder builder;
	
	/**
	 * Create a command to initialize a ReqIF model
	 * @param domain the editing domain
	 * @param resource the resource
	 * @param specification the specification
	 */
	public InitializeReqIFCommand(TransactionalEditingDomain domain, Resource resource, RequirementsSpecification specification) {
		super(domain);
		this.resource = resource;
		this.specification = specification;
		this.builder = new ReqIFModelBuilder();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.emf.transaction.RecordingCommand#doExecute()
	 */
	@Override
	protected void doExecute() {
		ReqIF modelRoot = ReqIF10Factory.eINSTANCE.createReqIF();
		modelRoot.setTheHeader(builder.createHeader(specification));
		resource.getContents().add(modelRoot);
	}

}
