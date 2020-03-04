/*******************************************************************************
 * Copyright (c) 2008-2019 German Aerospace Center (DLR), Simulation and Software Technology, Germany.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package de.dlr.sc.virsat.model.concept.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.transaction.TransactionalEditingDomain;

import de.dlr.sc.virsat.commons.datastructures.DependencyTree;
import de.dlr.sc.virsat.model.concept.migrator.ConceptMigrator;
import de.dlr.sc.virsat.model.concept.migrator.CreateMigrateConceptToLatestCommand;
import de.dlr.sc.virsat.model.concept.migrator.IMigrator;
import de.dlr.sc.virsat.model.dvlm.Repository;
import de.dlr.sc.virsat.model.dvlm.categories.propertydefinitions.EReferencePropertyHelper;
import de.dlr.sc.virsat.model.dvlm.concepts.Concept;
import de.dlr.sc.virsat.model.dvlm.concepts.IConceptTypeDefinition;
import de.dlr.sc.virsat.model.dvlm.concepts.registry.ActiveConceptConfigurationElement;
import de.dlr.sc.virsat.model.dvlm.concepts.util.ActiveConceptHelper;
import de.dlr.sc.virsat.model.edit.Activator;

/**
 * This class helps activating concepts. Concepts are copied into the 
 * repository. Furthermore, It redirects references so that these can be 
 * resolved from a platform plugin. References to concepts are redirected 
 * to their active version within the repository
 *
 */
public class ConceptActivationHelper {
	
	private static final String CONCEPT_PATH = "/concept/concept.xmi";
	protected Repository repository;
	
	public ConceptActivationHelper(Repository repository) {
		this.repository = repository;
	}
	
	public ConceptActivationHelper(Concept concept) {
		if (concept.eContainer() != null && concept.eContainer() instanceof Repository) {
			this.repository = ((Repository) concept.eContainer());
		}
	}
	
	/**
	 * Activate type by redirecting it to type in the repository
	 * @param type the type to be activates
	 * @return the activated type
	 */
	public EObject getActiveType(EObject type) {
		
		// For EReferences to external EClasses ignore concept 
		// activation and convert URIs instead. The URI needs to be
		// transformed from a PluginResourceURI to a PlattformPluginURI so 
		// that it can be resolved from the VirSat project in the eclipse runtime
		// instance.
		// VirSat does not ensure external model's storage.
		// We only enable non-containment references.
		if (type instanceof EClass) {
			return new EReferencePropertyHelper().activateEClassType((EClass) type);
		}

		// In case we try to create a reference to an object which was not copied
		// we should try to redirect that reference to an already active and existing concept
		if (repository != null && type instanceof IConceptTypeDefinition) {
			IConceptTypeDefinition typeDefinition = (IConceptTypeDefinition) type;
	
			// Get the fragment URI of the concept we want to reference to
			String uriFragment = EcoreUtil.getURI(typeDefinition).fragment();

			// ask the repository if there is such an object with the given URI fragment
			Resource repoResource = repository.eResource();
			EObject repoTypeDefinition = repoResource.getEObject(uriFragment);

			// If not throw a warning that there is something missing
			if (repoTypeDefinition == null) {
				String fqId = ActiveConceptHelper.getFullQualifiedId(typeDefinition);
			
				throw new RuntimeException("Install missing concept first! Missing concept: " + fqId);
			}
			return repoTypeDefinition;
		}
		
		return type;
	}
	
	/**
	 * Handle the activation of concept configuration elements
	 * @param conceptConfigurationElements an iterable of concept configurations
	 * @param editingDomain the editing domain
	 */
	public void handleAddConcepts(Object[] conceptConfigurationElements, EditingDomain editingDomain, IProgressMonitor progressMonitor) {
		
		List<Concept> concepts = new ArrayList<Concept>();
		for (Object acce : conceptConfigurationElements) {
			if (acce instanceof ActiveConceptConfigurationElement) {
				concepts.add(((ActiveConceptConfigurationElement) acce).loadConceptFromPlugin());
			}
		}
		handleAddConcepts(concepts, editingDomain, progressMonitor);
	}
	
	/**
	 * Handle the activation of concept configuration elements
	 * @param conceptConfigurationElements a list of concept configurations
	 * @param editingDomain the editing domain
	 */
	public void handleAddConcepts(List<Concept> concepts, EditingDomain editingDomain, IProgressMonitor progressMonitor) {
		
		// Correctly sort the selected concepts depending on their dependencies
		DependencyTree<String> dependencyTree = new DependencyTree<String>();
		Map<String, Concept> selectedConcepts = new HashMap<String, Concept>();
		
		// Create a map conceptName -> concept and add all concept names to the dependency tree
		for (Concept concept : concepts) {
			selectedConcepts.put(concept.getName(), concept);
			dependencyTree.addDependencies(concept.getName(), new String[] {});
		}
		
		// Now create the dependencies using the concept names as identifiers
		for (Concept concept : selectedConcepts.values()) {
			List<String> conceptDependencyIds = new ArrayList<>(ActiveConceptHelper.getConceptDependencies(concept));
			dependencyTree.addDependencies(concept.getName(), conceptDependencyIds);
		}
		
		List<String> orderedConcepts = dependencyTree.getLinearOrder();
		
		// And then install them by using the ordered list of concept names
		// and the map of concept names pointing to the already preloaded concepts
		for (String conceptName : orderedConcepts) {
			if (selectedConcepts.containsKey(conceptName)) {
				handleAddConcept(selectedConcepts.get(conceptName), editingDomain, progressMonitor);
			}
		}
	}
	
	/**
	 * Handle a selected concept for activation. Checks if an older version is already in the repository and if so
	 * migrates the existing concept to the latest version, otherwise the concept will be simply added to the active concepts
	 * This method does not check for dependency chains. Use handleAddConcepts() for that purpose
	 * @param concept the selected concept
	 * @param editingDomain the editing domain
	 */
	protected void handleAddConcept(Concept concept, EditingDomain editingDomain, IProgressMonitor progressMonitor) {
		boolean conceptIsInRepository = false;
		
		// Check if we already have this concept but with a different version added to the repository
		
		List<Concept> activeConcepts = repository.getActiveConcepts();
		for (Concept activeConcept : activeConcepts) {
			conceptIsInRepository = activeConcept.getName().equals(concept.getName());
			
			// There is a concept of an different version in the repository, ask if the user wants to migrate and do so
			if (conceptIsInRepository && !activeConcept.getVersion().equals(concept.getVersion())) {
					
				//If concept is active check if new dependencies have to be added before migration
				if (concept.eContainer() != null && concept.eContainer() instanceof Repository) {
					Repository repository = (Repository) concept.eContainer();
					new ConceptActivationHelper(repository).handleNewDependencies(concept, editingDomain, progressMonitor);
				}
				
				try {
					Command migrateToLatestCommand = CreateMigrateConceptToLatestCommand.create(concept, (TransactionalEditingDomain) editingDomain, progressMonitor);
					editingDomain.getCommandStack().execute(migrateToLatestCommand);
				} catch (CoreException e) {
					Activator.getDefault().getLog().log(new Status(Status.ERROR, Activator.getPluginId(), "Failed to do migration on active concept: " + concept.getDisplayName(), e));
				}
				
			}
		}
		
		if (!conceptIsInRepository) {
			Command cmd = ActiveConceptConfigurationElement.createCopyConceptToRepository(editingDomain, concept, repository);
			editingDomain.getCommandStack().execute(cmd);
		}
	}
	
	/**
	 * Activate new required concepts by also considering potential dependency chains 
	 * @param concept the concept to be prepared for migration
	 * @param editingDomain the editing domain
	 */
	public void handleNewDependencies(Concept concept, EditingDomain editingDomain, IProgressMonitor progressMonitor) {
		Set<String> newRequiredConcepts = new HashSet<String>();
		List<Concept> concepts = new ArrayList<Concept>();
		try {
			List<IMigrator> migrators = new ConceptMigrator(concept).getSortedMigrators(concept);
			IMigrator previousMigrator = migrators.remove(0);
			for (IMigrator migrator : migrators) {
				migrator.getNewDependencies(concept, previousMigrator).stream().forEach(key -> newRequiredConcepts.add(key));
			}
		} catch (CoreException e) {
			Activator.getDefault().getLog().log(new Status(Status.ERROR, Activator.getPluginId(), "Failed to perform loading new depencies for migration!", e));
		}
		
		for (String conceptName : newRequiredConcepts) {
			concepts.add(ActiveConceptConfigurationElement.loadConceptFromPlugin(conceptName + CONCEPT_PATH));
		}
		
		handleAddConcepts(concepts, editingDomain, progressMonitor);
	}
	

}