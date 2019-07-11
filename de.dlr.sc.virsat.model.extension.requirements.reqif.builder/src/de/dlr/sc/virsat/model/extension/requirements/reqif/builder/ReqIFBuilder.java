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
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.transaction.TransactionalEditingDomain;

import de.dlr.sc.virsat.model.dvlm.Repository;
import de.dlr.sc.virsat.model.dvlm.categories.CategoryAssignment;
import de.dlr.sc.virsat.model.dvlm.concepts.util.ActiveConceptHelper;
import de.dlr.sc.virsat.model.ecore.VirSatEcoreUtil;
import de.dlr.sc.virsat.model.extension.requirements.model.RequirementsSpecification;
import de.dlr.sc.virsat.model.extension.requirements.reqif.transformation.RequirementsTransformer;
import de.dlr.sc.virsat.model.extension.requirements.reqif.util.ReqIFBuilderUtil;
import de.dlr.sc.virsat.project.editingDomain.VirSatEditingDomainRegistry;
import de.dlr.sc.virsat.project.editingDomain.VirSatTransactionalEditingDomain;
import de.dlr.sc.virsat.requirements.reqif.util.ReqIFUtil;

/**
 * @author Tobias Franz tobias.franz@dlr.de
 *
 */
public class ReqIFBuilder extends IncrementalProjectBuilder {

	protected static final String REQUIREMENTS_CONCEPT_NAME = "de.dlr.sc.virsat.model.extension.requirements";
	protected static final String REQUIREMENTSPECIFICATION_CATEGORY_NAME = "RequirementsSpecification";
	public static final String EDITING_DOMAIN_ID = "de.dlr.sc.virsat.model.extension.requirements.reqif.builder.ReqIFEditingDomain";
	protected VirSatTransactionalEditingDomain virSatTed;
	
	protected ResourceSet externalReqIFResourceSet;
	protected TransactionalEditingDomain externalEditingDomain;
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.resources.IncrementalProjectBuilder#build(int,
	 * java.util.Map, org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	protected IProject[] build(int kind, Map<String, String> args, IProgressMonitor monitor) throws CoreException {
		
		virSatTed = VirSatEditingDomainRegistry.INSTANCE.getEd(getProject());
		if (virSatTed == null) {
			virSatTed = VirSatEditingDomainRegistry.INSTANCE.createEd(getProject(), new ResourceSetImpl());
		}
		externalEditingDomain = TransactionalEditingDomain.Registry.INSTANCE.getEditingDomain(EDITING_DOMAIN_ID);
		externalReqIFResourceSet = externalEditingDomain.getResourceSet();

		
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

		List<IResourceDelta> leafChildren;
		leafChildren = (ArrayList<IResourceDelta>) getDeepChildren(delta);
		for (IResourceDelta newDelta : leafChildren) {
			URI fileUri = URI.createPlatformResourceURI(newDelta.getFullPath().toString(), true);

			if (ReqIFUtil.isReqIF(fileUri)) {
				Path path = new Path(fileUri.toPlatformString(false));
				
				if (ResourcesPlugin.getWorkspace().getRoot().exists(path)) {
					Resource reqIFresource = externalReqIFResourceSet.getResource(fileUri, true);
					updateDVLMmodelFromReqIF(reqIFresource);
				} 
				
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
			Path path = new Path(fileUri.toPlatformString(false));
			
			if (ResourcesPlugin.getWorkspace().getRoot().exists(path)) {
				Resource reqIFresource = externalReqIFResourceSet.getResource(fileUri, true);
				updateDVLMmodelFromReqIF(reqIFresource);
			} 
		}
		
		updateReqIFfromDVLM(virSatTed.getResourceSet().getRepository(), virSatTed.getResourceSet(), externalReqIFResourceSet);
	}
	

	/**
	 * Update RegIF files from DVLM model
	 * @param projectRepro the repository
	 * @param virsatResourceSet the resource set of the DVLM model
	 * @param externalResourceSet the resource set of the reqif files
	 */
	protected void updateReqIFfromDVLM(Repository projectRepro, ResourceSet virsatResourceSet, ResourceSet externalResourceSet) {
		ActiveConceptHelper helper = new ActiveConceptHelper(projectRepro);
		List<CategoryAssignment> caList = VirSatEcoreUtil.getAllContentsOfType(virsatResourceSet, projectRepro.eResource(), CategoryAssignment.class, true);

		RequirementsTransformer transformer = new RequirementsTransformer(externalEditingDomain);
		
		for (CategoryAssignment ca : caList) {
			if (ca.getType().equals(helper.getCategory(REQUIREMENTS_CONCEPT_NAME, REQUIREMENTSPECIFICATION_CATEGORY_NAME))) {
				RequirementsSpecification specification = new RequirementsSpecification(ca);
				URI reqIFUri = ReqIFBuilderUtil.getReqIFUriFromDVLMModelSpecification(ca);
				
				//Load or create resource
				Resource reqIFResource;
				if (externalResourceSet.getURIConverter().exists(reqIFUri, Collections.EMPTY_MAP)) {
					reqIFResource = externalResourceSet.getResource(reqIFUri, true);
					if (ReqIFBuilderUtil.isReqIFModel(reqIFResource)) {
						transformer.updateReqIFModel(reqIFResource, specification);
					} else {
						transformer.createReqIFModel(reqIFResource, specification);
					}
					
				} else {
					reqIFResource = externalResourceSet.createResource(reqIFUri);
					transformer.createReqIFModel(reqIFResource, specification);
				}
				
			}
		}
		
	}
	
	/**
	 * Takes care that all reqIF requirements are in the DVLM model
	 * @param reqIFresource the reqIF resource
	 */
	protected void updateDVLMmodelFromReqIF(Resource reqIFresource) {
		
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
