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
import java.util.Map;
import java.util.UUID;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import de.dlr.sc.virsat.server.dataaccess.RepositoryConfiguration;
import de.dlr.sc.virsat.server.dataaccess.ServerRepositoryDAO;

/**
 * Resource for Admins accessing the server that provides advanced / critical Functionality
 */
@Path("/admin")
public class AdminResource {

	ServerRepositoryDAO dao;
	
	public AdminResource() {
		dao = new ServerRepositoryDAO(new File(""));
		dao.post(new RepositoryConfiguration("remote", "git", "func1", "password"));
		dao.post(new RepositoryConfiguration("remote2", "svn", "func2", "password"));
	}

	@GET
	@Path("/repositories")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<UUID, RepositoryConfiguration> getAll() {
		return dao.get();
	}
}
