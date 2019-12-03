/*******************************************************************************
 * Copyright (c) 2008-2019 German Aerospace Center (DLR), Simulation and Software Technology, Germany.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package de.dlr.sc.virsat.model.concept.generator

import de.dlr.sc.virsat.model.dvlm.concepts.Concept
import de.dlr.sc.virsat.model.ext.core.infrastructure.ConceptLanguageImplicitSuperTypeHandler
import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.emf.common.util.URI
import org.eclipse.xtext.generator.IFileSystemAccess2
import de.dlr.sc.virsat.model.concept.generator.xmi.GenerateConceptXmi

/**
 * Prepares a concept for a enhanced serialization in XMI and activates 
 * concept language core features such as a GenericCategory
 * 
 */
class ConceptPreprocessor {
	
	public static final String MODEL_TYPE_XMI_EXTENSION = "xmi";
	val conceptLanguageHandler = new ConceptLanguageImplicitSuperTypeHandler
	val IFileSystemAccess2 fileSystemAcesss
	
	new(IFileSystemAccess2 fsa) {
		this.fileSystemAcesss = fsa
	}
	
	def Concept process(Resource resource) {
		val originalConcept = resource.contents.get(0) as Concept
		
		//Update the concept content
		val processedConcept = processContent(originalConcept)
		
		//Update its container
		val xmiURI = getXmiUriFromConcept(resource)
		serializeContent(processedConcept, xmiURI)
		val newContainer = resource.resourceSet.getResource(xmiURI, true)
		
		return newContainer.contents.get(0) as Concept
	}
	
	/**
	 * Update the concept model content
	 */
	def Concept processContent(Concept originalConcept) {
		val processedConcept = conceptLanguageHandler.addImplicitSuperType(originalConcept)
		return processedConcept
	}
	
	/**
	 * Serialize the processed concept into their new containers
	 */
	def serializeContent(Concept concept, URI xmiURI) {
		new GenerateConceptXmi().serializeModel(concept, xmiURI, fileSystemAcesss);
	}
	
	/**
	 * Derive the XMI URI from the original URI
	 */
	def getXmiUriFromConcept(Resource resource) {
		var rawUri = resource.URI
		var targetUri = rawUri.trimFileExtension;
		targetUri = targetUri.appendFileExtension(MODEL_TYPE_XMI_EXTENSION);
		return targetUri
	}
	
}