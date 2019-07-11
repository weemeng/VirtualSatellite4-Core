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

import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.rmf.reqif10.ReqIFContent;

import de.dlr.sc.virsat.model.extension.requirements.model.RequirementsSpecification;

/**
 * @author fran_tb
 *
 */
public class UpdateReqIFContentCommand extends RecordingCommand {
	
	protected final ReqIFContent content;
	protected final RequirementsSpecification specification;
	
	/**
	 * @param domain the edidting domain
	 * @param content the current reqIF content
	 * @param specification the DVLM specification
	 */
	public UpdateReqIFContentCommand(TransactionalEditingDomain domain, ReqIFContent content, RequirementsSpecification specification) {
		super(domain);
		this.content = content;
		this.specification = specification;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.emf.transaction.RecordingCommand#doExecute()
	 */
	@Override
	protected void doExecute() {
		// TODO Auto-generated method stub
		
	}

}
