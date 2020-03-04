/*******************************************************************************
 * Copyright (c) 2008-2019 German Aerospace Center (DLR), Simulation and Software Technology, Germany.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package de.dlr.sc.virsat.server.dataaccess;

/**
 * API to the Virtual Satellite data model and to the backend
 */
public class RepositoryModelAPI {
	public static final String BACKEND_SVN = "svn";
	public static final String BACKEND_GIT = "git";
	
	private RepositoryConfiguration repository;
	
	public RepositoryModelAPI(RepositoryConfiguration repository) {
		this.repository = repository;
		initBackend();
	}
	
	/**
	 * Initializes the local backend with the corresponding working directory and pulls
	 */
	private void initBackend() {
		repository.getBackend();
		repository.getRemoteUri();
		repository.getFunctionalAccountName();
		repository.getFunctionalAccountPassword();
		// Check if the repository already hast a working directory
		// If not clone the project from the git or svn backend here
		
		// Then fetch and pull
	}
	
	/**
	 * Pushes the changes made to the local model to the remote repository
	 */
	public void update() {
		// Call backend.update() that stages all changes and commits them
		// This is where we have to check for / handle merge conflicts
	}
	
	public void getBean() { 
		// Model get
	};
	
	public void putBean() { 
		// Model put
		update();
	};
	
	public void postBean() { 
		// Model post
		update();
	};
	
	public void deleteBean() { 
		// Model delete
		update();
	};
}
