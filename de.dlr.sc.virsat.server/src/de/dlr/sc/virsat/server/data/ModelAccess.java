/*******************************************************************************
 * Copyright (c) 2008-2019 German Aerospace Center (DLR), Simulation and Software Technology, Germany.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package de.dlr.sc.virsat.server.data;

import java.util.ArrayList;
import java.util.List;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;

import de.dlr.sc.virsat.apps.api.external.ModelAPI;
import de.dlr.sc.virsat.model.concept.types.structural.ABeanStructuralElementInstance;

public class ModelAccess {

	private static ModelAccess modelAccess = null;

	private ModelAccess() {
	}

	public static ModelAccess getInstance() {
		if (modelAccess == null) {
			modelAccess = new ModelAccess();
		}

		return modelAccess;
	}

	/**
	 * Takes the current VirSat repository and converts the rootSeis to json, which is then returned.
	 * @param projectdirectory directory of the VirSat project
	 * @return json-string of the rootSeis of the VirSat repository at the given directory
	 */
	public String showRepository(String projectdirectory) {
		List<String> uuids = new ArrayList<String>();
		ModelAPI modelAPI = new ModelAPI(projectdirectory);
		List<ABeanStructuralElementInstance> rootBeanSeis = modelAPI.getRootSeis(ABeanStructuralElementInstance.class);
		for (ABeanStructuralElementInstance sei : rootBeanSeis) {
			uuids.add(sei.getUuid());
		}
		
		Jsonb jsonb = JsonbBuilder.create();
		return jsonb.toJson(uuids);
	}

}
