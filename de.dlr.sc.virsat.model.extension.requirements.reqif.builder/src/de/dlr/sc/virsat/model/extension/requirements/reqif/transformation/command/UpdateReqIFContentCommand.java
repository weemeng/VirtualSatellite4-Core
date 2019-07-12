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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.rmf.reqif10.AttributeDefinition;
import org.eclipse.rmf.reqif10.ReqIFContent;
import org.eclipse.rmf.reqif10.SpecObject;
import org.eclipse.rmf.reqif10.SpecObjectType;
import org.eclipse.rmf.reqif10.SpecType;

import de.dlr.sc.virsat.model.extension.requirements.model.AttributeValue;
import de.dlr.sc.virsat.model.extension.requirements.model.Requirement;
import de.dlr.sc.virsat.model.extension.requirements.model.RequirementAttribute;
import de.dlr.sc.virsat.model.extension.requirements.model.RequirementGroup;
import de.dlr.sc.virsat.model.extension.requirements.model.RequirementObject;
import de.dlr.sc.virsat.model.extension.requirements.model.RequirementType;
import de.dlr.sc.virsat.model.extension.requirements.model.RequirementsSpecification;
import de.dlr.sc.virsat.model.extension.requirements.reqif.transformation.ITypeElementProvider;
import de.dlr.sc.virsat.model.extension.requirements.reqif.transformation.ReqIFModelBuilder;

/**
 * @author fran_tb
 *
 */
public class UpdateReqIFContentCommand extends RecordingCommand implements ITypeElementProvider {

	protected ReqIFModelBuilder reqIFModelBuilder = new ReqIFModelBuilder(this);

	protected final ReqIFContent content;
	protected final RequirementsSpecification specification;

	Map<String, SpecObjectType> requirementTypes = new HashMap<>();
	Map<String, AttributeDefinition> attributeTypes = new HashMap<>();
	
	List<Requirement> reqList = new ArrayList<>();
	List<RequirementType> reqTypeList = new ArrayList<>();

	/**
	 * @param domain
	 *            the edidting domain
	 * @param content
	 *            the current reqIF content
	 * @param specification
	 *            the DVLM specification
	 */
	public UpdateReqIFContentCommand(TransactionalEditingDomain domain, ReqIFContent content,
			RequirementsSpecification specification) {
		super(domain);
		this.content = content;
		this.specification = specification;

		for (RequirementObject reqO : specification.getRequirements()) {
			searchForUsedRequirementTypes(reqO);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.emf.transaction.RecordingCommand#doExecute()
	 */
	@Override
	protected void doExecute() {
		//Update the object types
		content.getSpecTypes().clear();
		for (RequirementType type : reqTypeList) {
			SpecType specType = (SpecType) getElementById(type.getUuid());
			removeOldObject(specType.getIdentifier());
			content.getSpecTypes().add(specType);
			
			for (RequirementAttribute attType : type.getAttributes()) {
				AttributeDefinition attDef = (AttributeDefinition) getElementById(attType.getUuid());
				specType.getSpecAttributes().add(attDef);
			}
		}
		//Update the objects itself
		content.getSpecObjects().clear();
		for (Requirement req : reqList) {
			SpecObject object = reqIFModelBuilder.createSpecObject(req);
			
			removeOldObject(object.getIdentifier());
			content.getSpecObjects().add(object);
			
			for (AttributeValue attValue : req.getElements()) {
				org.eclipse.rmf.reqif10.AttributeValue value = reqIFModelBuilder.createAttributeValue(attValue);
				object.getValues().add(value);
			}
		}

	}

	/**
	 * Search for all requirement types in this specification
	 * 
	 * @param object
	 *            the requirement object
	 */
	protected void searchForUsedRequirementTypes(RequirementObject object) {
		if (object instanceof RequirementGroup) {
			for (RequirementObject child : ((RequirementGroup) object).getChildren()) {
				searchForUsedRequirementTypes(child);
			}
		} else if (object instanceof Requirement) {
			
			if (!reqList.contains(object)) {
				reqList.add((Requirement) object);
			}
			
			RequirementType reqType = ((Requirement) object).getReqType();
			if (!requirementTypes.containsKey(reqType.getUuid())) {
				requirementTypes.put(reqType.getUuid(), reqIFModelBuilder.createSpecObjectType(reqType));
				reqTypeList.add(reqType);
			}
			for (RequirementAttribute concepAtt : reqType.getAttributes()) {
				if (!attributeTypes.containsKey(concepAtt.getUuid())) {
					attributeTypes.put(concepAtt.getUuid(), reqIFModelBuilder.createAttributeDef(concepAtt));
				}
			}
		}
	}

	/**
	 * Remove the old content with the same id
	 * @param id the id of the element to be updated
	 */
	protected void removeOldObject(String id) {
		EObject oldObject = content.eResource().getEObject(id);
		if (oldObject != null) {
			EcoreUtil.remove(oldObject);
		}
	}
	
	/* (non-Javadoc)
	 * @see de.dlr.sc.virsat.model.extension.requirements.reqif.transformation.ITypeElementProvider#getElementById(java.lang.String)
	 */
	@Override
	public EObject getElementById(String uuid) {
		
		if (requirementTypes.containsKey(uuid)) {
			return requirementTypes.get(uuid);
		} else if (attributeTypes.containsKey(uuid)) {
			return attributeTypes.get(uuid);
		}
		
		return null;
	}

}
