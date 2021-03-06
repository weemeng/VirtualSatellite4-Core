/*******************************************************************************
 * Copyright (c) 2008-2019 German Aerospace Center (DLR), Simulation and Software Technology, Germany.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package de.dlr.sc.virsat.model.dvlm.roles;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;

import de.dlr.sc.virsat.model.dvlm.general.IAssignedDiscipline;

/**
 * Simple Helper method to determine the access rights on an EObject
 * @author fisc_ph
 *
 */
public class RightsHelper implements IRightsHelper {
	
	/**
	 * Private Constructor for Helper Class
	 */
	public RightsHelper() {
	}

	/**
	 * this method get the discipline of an object
	 * @param eObj the object which has an discipline 
	 * @return the discipline of the object
	 */
	public static synchronized Discipline getDiscipline(EObject eObj) {
		EObject currentObject = eObj;
		while (!(currentObject instanceof IAssignedDiscipline)) {
			currentObject = currentObject.eContainer();
			
			if ((currentObject == null) || (currentObject instanceof Resource)) {
				return null;
			}
		}
		
		return ((IAssignedDiscipline) currentObject).getAssignedDiscipline();
	}
	
	/**
	 * This method checks if there exists a parent of eObj that can have a discipline.
	 * If no such parent exists, we dont need to do any rights management.
	 * @param eObj the object we check
	 * @return true iff a parent (or grandparent, etc.) that is an IAssignableDiscipline exists
	 */
	private static synchronized boolean hasParentThatCanHaveDiscipline(EObject eObj) {
		EObject currentObject = eObj;
		while (currentObject != null) {
			if (currentObject instanceof IAssignedDiscipline) {
				return true;
			}
			currentObject = currentObject.eContainer();
		}
		
		return false;
	}
	
	/**
	 * this method told if the object has write permissions
	 * @param eObject the object it worked on
	 * @return true if the object has write permissions, else false
	 */
	public static synchronized boolean hasWritePermission(EObject eObject) {
		if (!hasParentThatCanHaveDiscipline(eObject)) {
			// If in the container hierarchy there exists no object with an assignable discipline
			// We can skip any further checks to the write permission since we cannot obtain a discipline
			// Note that this may be different than having an assignable discipline parent but no discipline assigned
			return true;
		}
		
		UserRegistry userRegistry = UserRegistry.getInstance();
		boolean hasWritePermission = false;
		String registeredUserInApplication = userRegistry.getUserName();
		
		// the owner of the command is our object we work on
		Discipline disc = getDiscipline(eObject);
		if (disc != null) {
			String userAssignedToDispline = disc.getUser();
			if (!userAssignedToDispline.isEmpty()) {
				hasWritePermission = userAssignedToDispline.equals(registeredUserInApplication);
			}	
		}
		
		return hasWritePermission || userRegistry.isSuperUser(); 
	}

	@Override
	public boolean hasWriteAccess(EObject eObject) {
		return hasWritePermission(eObject);
	}
}
