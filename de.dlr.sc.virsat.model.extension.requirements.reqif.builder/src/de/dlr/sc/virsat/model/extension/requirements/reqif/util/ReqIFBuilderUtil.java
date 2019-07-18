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

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.rmf.reqif10.ReqIF;

import de.dlr.sc.virsat.model.dvlm.categories.CategoryAssignment;
import de.dlr.sc.virsat.model.extension.requirements.model.RequirementsSpecification;
import de.dlr.sc.virsat.requirements.reqif.util.ReqIFUtil;

/**
 * @author fran_tb
 *
 */
public class ReqIFBuilderUtil {
	
	/**
	 * Restricted constructor
	 */
	protected ReqIFBuilderUtil() {
		
	}
	
	/**
	 * Get the filename of a reqif file from a DVLM concept specification
	 * @param ca the CategoryAssignment
	 * @return the filename
	 */
	public static String getReqIFFilenameFromSpecification(CategoryAssignment ca) {
		
		String reqIFFileName = ca.getName();
		
		if (!reqIFFileName.contains(".")) {
			reqIFFileName += "." + ReqIFUtil.REQ_IF_FILE_EXTENSION;
		}
		
		return reqIFFileName;
		
	}
	
	/**
	 * Get the URI of the reqIF model from the specifications resource 
	 * @param ca the specification as Category Assignement
	 * @return the new URI
	 */
	public static URI getReqIFUriFromDVLMModelSpecification(CategoryAssignment ca) {
		URI reqIFURI = null;
		
		RequirementsSpecification specification = new RequirementsSpecification(ca);
		if (specification.getExportFile() != null) {
			reqIFURI = specification.getExportFile();
		} else {
			reqIFURI = ca.eResource().getURI().trimSegments(1);
			reqIFURI = reqIFURI.appendSegment("documents");
			reqIFURI = reqIFURI.appendSegment(getReqIFFilenameFromSpecification(ca));
		}
		
		return reqIFURI;
	}
	
	
	/**
	 * Check if file content is a ReqIF model
	 * @param resource the resource
	 * @return true if it is a model, false otherwise
	 */
	public static boolean isReqIFModel(Resource resource) {
		boolean isModel = false;
		
		if (resource == null) {
			return false;
		}
		
		if (resource.getAllContents() == null) {
			return false;
		}
		
		if (resource.getContents().size() > 0) {
			if (resource.getContents().get(0) instanceof ReqIF) {
				isModel = true;
			}
		}
		
		return isModel;
	}
	

}
