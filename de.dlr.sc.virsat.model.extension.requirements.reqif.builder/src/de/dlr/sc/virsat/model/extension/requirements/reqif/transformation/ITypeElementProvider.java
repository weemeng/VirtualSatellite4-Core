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

import org.eclipse.emf.ecore.EObject;

/**
 * @author fran_tb
 *
 */
public interface ITypeElementProvider {
	
	/**
	 * Get the type element by accessing the id
	 * @param uuid the element's id as UUID
	 * @return the type element
	 */
	EObject getElementById(String uuid);

}
