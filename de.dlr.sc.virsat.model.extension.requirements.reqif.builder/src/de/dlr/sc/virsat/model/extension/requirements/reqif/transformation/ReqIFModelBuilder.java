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
package de.dlr.sc.virsat.model.extension.requirements.reqif.transformation;

import java.math.BigInteger;

import org.eclipse.rmf.reqif10.AttributeDefinition;
import org.eclipse.rmf.reqif10.AttributeDefinitionBoolean;
import org.eclipse.rmf.reqif10.AttributeDefinitionDate;
import org.eclipse.rmf.reqif10.AttributeDefinitionEnumeration;
import org.eclipse.rmf.reqif10.AttributeDefinitionInteger;
import org.eclipse.rmf.reqif10.AttributeDefinitionReal;
import org.eclipse.rmf.reqif10.AttributeDefinitionString;
import org.eclipse.rmf.reqif10.AttributeValue;
import org.eclipse.rmf.reqif10.AttributeValueBoolean;
import org.eclipse.rmf.reqif10.AttributeValueDate;
import org.eclipse.rmf.reqif10.AttributeValueEnumeration;
import org.eclipse.rmf.reqif10.AttributeValueInteger;
import org.eclipse.rmf.reqif10.AttributeValueReal;
import org.eclipse.rmf.reqif10.AttributeValueString;
import org.eclipse.rmf.reqif10.ReqIF10Factory;
import org.eclipse.rmf.reqif10.ReqIFContent;
import org.eclipse.rmf.reqif10.ReqIFHeader;
import org.eclipse.rmf.reqif10.SpecObject;
import org.eclipse.rmf.reqif10.SpecObjectType;
import org.eclipse.rmf.reqif10.Specification;
import org.eclipse.rmf.reqif10.SpecificationType;

import de.dlr.sc.virsat.model.extension.requirements.model.Requirement;
import de.dlr.sc.virsat.model.extension.requirements.model.RequirementAttribute;
import de.dlr.sc.virsat.model.extension.requirements.model.RequirementType;
import de.dlr.sc.virsat.model.extension.requirements.model.RequirementsSpecification;

/**
 * @author fran_tb
 *
 */
public class ReqIFModelBuilder {
	
	protected ITypeElementProvider typeElementProvider;
	
	/**
	 * Class to build model elements for reqIF models
	 * @param typeElementProvider a class that provides ElementTypes by their ID
	 */
	public ReqIFModelBuilder(ITypeElementProvider typeElementProvider) {
		this.typeElementProvider = typeElementProvider; 
	}
	
	/**
	 * Create a header element for a ReqIF model
	 * @param specification the specification as concept
	 * @return the header element
	 */
	public ReqIFHeader createHeader(RequirementsSpecification specification) {
		ReqIFHeader header = ReqIF10Factory.eINSTANCE.createReqIFHeader();
		//configure Header
		return header;
	}
	
	/**
	 * Create a content element for a ReqIF model
	 * @param specification the specification as concept
	 * @return the content element
	 */
	public ReqIFContent createContent(RequirementsSpecification specification) {
		ReqIFContent content = ReqIF10Factory.eINSTANCE.createReqIFContent();
		//configure Content
		return content;
	}
	
	/**
	 * Create a specification element for a ReqIF model
	 * @param conceptSpecification the specification as concept
	 * @return the reqif specification element
	 */
	public Specification createSpecification(RequirementsSpecification conceptSpecification) {
		Specification specification = ReqIF10Factory.eINSTANCE.createSpecification();
		specification.setLongName(conceptSpecification.getName());
		specification.setIdentifier(conceptSpecification.getUuid());
		//configure Content
		return specification;
	}
	
	/**
	 * Create a specification type element for a ReqIF model
	 * @param conceptSpecification the specification as concept
	 * @return the reqif specification element
	 */
	public SpecificationType createSpecificationType(RequirementsSpecification conceptSpecification) {
		SpecificationType specification = ReqIF10Factory.eINSTANCE.createSpecificationType();
		specification.setLongName(conceptSpecification.getName());
		specification.setIdentifier(conceptSpecification.getUuid());
		//configure Content
		return specification;
	}
	
	/**
	 * Create a spec object for corresponding requirement types
	 * @param type the concept requirement type
	 * @return the spec object
	 */
	public SpecObjectType createSpecObjectType(RequirementType type) {
		SpecObjectType specType = ReqIF10Factory.eINSTANCE.createSpecObjectType();
		specType.setLongName(type.getName());
		specType.setIdentifier(type.getUuid());
		return specType;
	}
	
	/**
	 * Create a reqif attribute definition from a requirement concept requirement attribute
	 * @param conceptAtt the concept attribute
	 * @return the reqif attribute definition
	 */
	public AttributeDefinition createAttributeDef(RequirementAttribute conceptAtt) {
		AttributeDefinition att; 
		switch (conceptAtt.getType()) {
			case RequirementAttribute.TYPE_String_NAME:
				att = ReqIF10Factory.eINSTANCE.createAttributeDefinitionString();
				break;
			case RequirementAttribute.TYPE_Boolean_NAME:
				att = ReqIF10Factory.eINSTANCE.createAttributeDefinitionBoolean();
				break;
			case RequirementAttribute.TYPE_Date_NAME:
				att = ReqIF10Factory.eINSTANCE.createAttributeDefinitionDate();
				break;
			case RequirementAttribute.TYPE_Integer_NAME:
				att = ReqIF10Factory.eINSTANCE.createAttributeDefinitionInteger();
				break;
			case RequirementAttribute.TYPE_Real_NAME:
				att = ReqIF10Factory.eINSTANCE.createAttributeDefinitionReal();
				break;
			case RequirementAttribute.TYPE_Enumeration_NAME:
				att = ReqIF10Factory.eINSTANCE.createAttributeDefinitionEnumeration();
				break;
	
			default:
				att = null;
				break;
		}
		
		if (att != null) {
			att.setIdentifier(conceptAtt.getUuid());
			att.setLongName(conceptAtt.getName());
		}
		return att;
	}
	
	/**
	 * Create a reqif spec object from a requirement concept requirement 
	 * @param requirement the concept requirement
	 * @return the reqif requirement 
	 */
	public SpecObject createSpecObject(Requirement requirement) {
		SpecObject specObject = ReqIF10Factory.eINSTANCE.createSpecObject();
		specObject.setIdentifier(requirement.getUuid());
		specObject.setLongName(requirement.getName());
		specObject.setType((SpecObjectType) typeElementProvider.getElementById(requirement.getReqType().getUuid()));
		
		return specObject;
	}
	
	/**
	 * Create a reqif attribute value object from a requirement attribute value 
	 * @param conceptAttributeValue the concept AttributeValue
	 * @return the reqif attribute value 
	 */
	public AttributeValue createAttributeValue(de.dlr.sc.virsat.model.extension.requirements.model.AttributeValue conceptAttributeValue) {
		RequirementAttribute attType = conceptAttributeValue.getAttType();
		
		AttributeValue valueElement;
		
		switch (attType.getType()) {
			case RequirementAttribute.TYPE_String_NAME:
				AttributeValueString valueElementString = ReqIF10Factory.eINSTANCE.createAttributeValueString();
				valueElementString.setTheValue(conceptAttributeValue.getValue());
				valueElementString.setDefinition((AttributeDefinitionString) typeElementProvider.getElementById(attType.getUuid()));
				valueElement = valueElementString;
				break;
			case RequirementAttribute.TYPE_Boolean_NAME:
				AttributeValueBoolean valueElementBoolean = ReqIF10Factory.eINSTANCE.createAttributeValueBoolean();
				valueElementBoolean.setTheValue(Boolean.parseBoolean(conceptAttributeValue.getValue()));
				valueElementBoolean.setDefinition((AttributeDefinitionBoolean) typeElementProvider.getElementById(attType.getUuid()));
				valueElement = valueElementBoolean;
				break;
			case RequirementAttribute.TYPE_Date_NAME:
				AttributeValueDate valueElementDate = ReqIF10Factory.eINSTANCE.createAttributeValueDate();
				valueElementDate.setDefinition((AttributeDefinitionDate) typeElementProvider.getElementById(attType.getUuid()));
				valueElementDate.setTheValue(null); //TODO
				valueElement = valueElementDate;
				break;
			case RequirementAttribute.TYPE_Integer_NAME:
				AttributeValueInteger valueElementInteger = ReqIF10Factory.eINSTANCE.createAttributeValueInteger();
				valueElementInteger.setDefinition((AttributeDefinitionInteger) typeElementProvider.getElementById(attType.getUuid()));
				valueElementInteger.setTheValue(new BigInteger(conceptAttributeValue.getValue()));
				valueElement = valueElementInteger;
				break;
			case RequirementAttribute.TYPE_Real_NAME:
				AttributeValueReal valueElementReal = ReqIF10Factory.eINSTANCE.createAttributeValueReal();
				valueElementReal.setDefinition((AttributeDefinitionReal) typeElementProvider.getElementById(attType.getUuid()));
				valueElementReal.setTheValue(Double.parseDouble(conceptAttributeValue.getValue()));
				valueElement = valueElementReal;
				break;
			case RequirementAttribute.TYPE_Enumeration_NAME:
				AttributeValueEnumeration valueElementEnumeration = ReqIF10Factory.eINSTANCE.createAttributeValueEnumeration();
				valueElementEnumeration.setDefinition((AttributeDefinitionEnumeration) typeElementProvider.getElementById(attType.getUuid()));
				//TODO set value
				valueElement = valueElementEnumeration;
				break;
	
			default:
				valueElement = null;
				break;
		}
		return valueElement;
	}
	
}
