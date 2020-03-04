/*******************************************************************************
 * Copyright (c) 2008-2019 German Aerospace Center (DLR), Simulation and Software Technology, Germany.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package de.dlr.sc.virsat.server.resources;

import java.io.File;
import java.util.UUID;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import de.dlr.sc.virsat.server.dataaccess.RepositoryModelAPI;
import de.dlr.sc.virsat.server.dataaccess.RepositoryConfiguration;
import de.dlr.sc.virsat.server.dataaccess.ServerRepositoryDAO;

/**
 * Resource for users to access a specific repostory
 */
@Path("/repository")
public class UserRepositoryResource {

	ServerRepositoryDAO dao;
	UUID testUUID;
	
	public UserRepositoryResource() {
		dao = new ServerRepositoryDAO(new File(""));
		testUUID = dao.post(new RepositoryConfiguration("remote", "git", "func1", "password"));
		dao.post(new RepositoryConfiguration("remote2", "svn", "func2", "password"));
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public RepositoryConfiguration get(@QueryParam("uuid") UUID uuid) {
		return dao.get(uuid);
	}
	
	@GET
	@Path("/model")
	@Produces(MediaType.APPLICATION_JSON)
	public RepositoryModelAPI getModelTest() {
		return new RepositoryModelAPI(dao.get(testUUID));
	}
}
