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

import java.util.function.Predicate;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.rmf.reqif10.ReqIFContent;
import org.eclipse.rmf.reqif10.SpecType;
import org.eclipse.rmf.reqif10.Specification;
import org.eclipse.rmf.reqif10.SpecificationType;

import de.dlr.sc.virsat.model.extension.requirements.model.RequirementsSpecification;
import de.dlr.sc.virsat.model.extension.requirements.reqif.transformation.ITypeElementProvider;
import de.dlr.sc.virsat.model.extension.requirements.reqif.transformation.ReqIFModelBuilder;

/**
 * @author fran_tb
 *
 */
public class UpdateSpecificationHierarchyCommand extends RecordingCommand implements ITypeElementProvider {

	
	protected ReqIFModelBuilder reqIFModelBuilder = new ReqIFModelBuilder(this);

	protected final ReqIFContent content;
	protected final RequirementsSpecification specification;
	
	/**
	 * @param domain
	 *            the edidting domain
	 * @param content
	 *            the current reqIF content
	 * @param specification
	 *            the DVLM specification
	 */
	public UpdateSpecificationHierarchyCommand(TransactionalEditingDomain domain, ReqIFContent content,
			RequirementsSpecification specification) {
		super(domain);
		this.content = content;
		this.specification = specification;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.emf.transaction.RecordingCommand#doExecute()
	 */
	@Override
	protected void doExecute() {
		//Update the specification hierarchy
		content.getSpecifications().clear();
		Predicate<SpecType> predicate = p -> p instanceof SpecificationType;
		content.getSpecTypes().removeIf(predicate);
		SpecificationType specificationType = reqIFModelBuilder.createSpecificationType(specification);
		Specification specificationReqIF = reqIFModelBuilder.createSpecification(specification);
		content.getSpecTypes().add(specificationType);
		content.getSpecifications().add(specificationReqIF);
	}

	/* (non-Javadoc)
	 * @see de.dlr.sc.virsat.model.extension.requirements.reqif.transformation.ITypeElementProvider#getElementById(java.lang.String)
	 */
	@Override
	public EObject getElementById(String uuid) {
		return content.eResource().getEObject(uuid);
	}

}
