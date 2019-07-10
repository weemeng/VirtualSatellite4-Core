/*******************************************************************************
 * Copyright (c) 2008-2019 German Aerospace Center (DLR), Simulation and Software Technology, Germany.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package de.dlr.sc.virsat.model.extension.requirements.reqif.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.rmf.reqif10.SpecObject;

import de.dlr.sc.virsat.requirements.reqif.util.ReqIFUtil;

/**
 * @author Tobias Franz tobias.franz@dlr.de
 *
 */
public class ReqIFBuilder extends IncrementalProjectBuilder {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.resources.IncrementalProjectBuilder#build(int,
	 * java.util.Map, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	protected IProject[] build(int kind, Map<String, String> args, IProgressMonitor monitor) throws CoreException {
		if (kind == IncrementalProjectBuilder.FULL_BUILD) {
			fullBuild(monitor);
		} else {
			IResourceDelta delta = getDelta(getProject());
			if (delta == null) {
				fullBuild(monitor);
			} else {
				incrementalBuild(delta, monitor);
			}
		}
		return null;
	}

	/**
	 * @param delta
	 *            the differences
	 * @param monitor
	 *            monitor
	 * @throws CoreException
	 *             exception
	 */
	private void incrementalBuild(IResourceDelta delta, IProgressMonitor monitor) throws CoreException {

		// validate all existing traces
		// validateAllTraceabilityLinkContainers(getProject());

		// validate changed requirements
		List<IResourceDelta> leafChildren;
		leafChildren = (ArrayList<IResourceDelta>) getDeepChildren(delta);
		for (IResourceDelta newDelta : leafChildren) {
			URI fileUri = URI.createPlatformResourceURI(newDelta.getFullPath().toString(), true);

			// If requirements changed validate their trace
			List<SpecObject> changedSpecs = null;
			if (ReqIFUtil.isReqIF(fileUri)) {
			}
		}
	}

	/**
	 * The full build
	 * 
	 * @param monitor
	 *            the monitor
	 * @throws CoreException
	 *             a core exception if it cannot be validated
	 */
	protected void fullBuild(IProgressMonitor monitor) throws CoreException {

		//Also check for recent changes in requirements
		IResourceDelta delta = getDelta(getProject());
		List<IResourceDelta> leafChildren;
		leafChildren = (ArrayList<IResourceDelta>) getDeepChildren(delta);
		for (IResourceDelta newDelta : leafChildren) {
			URI fileUri = URI.createPlatformResourceURI(newDelta.getFullPath().toString(), true);
			if (ReqIFUtil.isReqIF(fileUri)) {
			}
		}
	}

	/**
	 * @param delta
	 *            the changes
	 * @return all of the changes done recently
	 */
	List<IResourceDelta> getDeepChildren(IResourceDelta delta) {
		List<IResourceDelta> newList = new ArrayList<>();
		if (delta != null) {
			if (delta.getAffectedChildren().length == 0) {
				newList.add(delta);
				return newList;
			}
			for (IResourceDelta children : delta.getAffectedChildren()) {
				newList.addAll(getDeepChildren(children));
			}
		}
		return newList;
	}


}
