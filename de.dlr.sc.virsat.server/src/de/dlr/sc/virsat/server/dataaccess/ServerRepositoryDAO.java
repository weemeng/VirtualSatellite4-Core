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

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;

/**
 * This class takes care of storing all Repository Configuration for a server instance.
 * It also takes care of persisting the configuration into a JSON file.
 * The put get and post methods comply with a REST contract.
 *
 */
public class ServerRepositoryDAO {
	
	private Map<UUID, RepositoryConfiguration> repositories;
	
	private File persistenceFile;
	
	/**
	 * Constructor with a File for persistence. The constructor does not call load.
	 * @param persistenceFile the file to be used for persistence
	 */
	public ServerRepositoryDAO(File persistenceFile) {
		this.persistenceFile = persistenceFile;
		repositories = new HashMap<>();
	}
	
	/**
	 * Returns all repository configurations.
	 * @return an unmodifiable map of all repository configurations
	 */
	public Map<UUID, RepositoryConfiguration> get() {
		return repositories;
	}
	
	/**
	 * get a Repository Configuration by id
	 * @param id the UUID to the configuration
	 * @return the configuration or null if it does not exist.
	 */
	public RepositoryConfiguration get(UUID id) {
		return repositories.get(id);
	}
	
	/**
	 * Place a new Repository Configuration
	 * @param repositoryConfiguration the new configuration to be added
	 * @return the UUID of the newly added configuration
	 */
	public UUID post(RepositoryConfiguration repo) {
		UUID id = UUID.randomUUID();
		repositories.put(id, repo);
		return id;
	}
	
	/**
	 * Update an existing Configuration. Does nothing if the object does not exist.
	 * @param id the UUID of the configuration to be updated
	 * @param repositoryConfiguration the Configuration with the content to be used for update
	 * @return the UUID of the object that got updated.
	 */
	public UUID put(UUID id, RepositoryConfiguration repositoryConfiguration) {
		RepositoryConfiguration existingRepositoryConfiguration = repositories.get(id);
		if (existingRepositoryConfiguration != null) {
			existingRepositoryConfiguration.update(repositoryConfiguration);
		} 
		return id;
	}

	/**
	 * This method saves all repository configurations to the config file
	 * @throws IOException
	 */
	public void save() throws IOException {
		try (FileWriter fileWriter = new FileWriter(persistenceFile)) {
			Jsonb jsonBinding = JsonbBuilder.create();
			jsonBinding.toJson(repositories, fileWriter);
		} catch (IOException e) {
			throw e;
		}
	}
	
	/**
	 * Deletes a configuration
	 * @param repoUuid the UUID of the Repository to be deleted.
	 */
	public void delete(UUID repoUuid) {
		repositories.remove(repoUuid);
	}

	/**
	 * This method loads all Repository Configurations from a file.
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public void load() throws IOException {
		// In case there is no config file yet create one with correct content
		if (!persistenceFile.exists()) {
			save();
		}

		// Now start reading the file
		try (FileReader fileReader = new FileReader(persistenceFile)) {
			Jsonb jsonBinding = JsonbBuilder.create();
			repositories = jsonBinding.fromJson(fileReader, repositories.getClass());
		} catch (IOException e) {
			throw e;
		}
	}
}
