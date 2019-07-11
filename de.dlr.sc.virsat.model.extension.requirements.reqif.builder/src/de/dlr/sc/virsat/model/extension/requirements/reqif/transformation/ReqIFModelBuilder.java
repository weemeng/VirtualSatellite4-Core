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

import org.eclipse.rmf.reqif10.ReqIF10Factory;
import org.eclipse.rmf.reqif10.ReqIFHeader;

import de.dlr.sc.virsat.model.extension.requirements.model.RequirementsSpecification;

/**
 * @author fran_tb
 *
 */
public class ReqIFModelBuilder {
	
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

	
	
}
