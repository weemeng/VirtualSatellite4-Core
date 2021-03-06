/*******************************************************************************
 * Copyright (c) 2008-2019 German Aerospace Center (DLR), Simulation and Software Technology, Germany.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/

package de.dlr.sc.virsat.project.ui.structure.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.command.CompoundCommand;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;

import de.dlr.sc.virsat.model.dvlm.resource.command.RemoveResourceCommand;
import de.dlr.sc.virsat.model.dvlm.structural.StructuralElementInstance;
import de.dlr.sc.virsat.model.dvlm.structural.command.DeleteStructuralElementInstanceCommand;
import de.dlr.sc.virsat.project.Activator;
import de.dlr.sc.virsat.project.editingDomain.VirSatEditingDomainRegistry;
import de.dlr.sc.virsat.project.editingDomain.VirSatTransactionalEditingDomain;
import de.dlr.sc.virsat.project.resources.VirSatResourceSet;
import de.dlr.sc.virsat.project.structure.VirSatProjectCommons;

/**
 * Class that creates a command to remove a StructuralElementInstance and its file structure
 */
public class CreateRemoveSeiWithFileStructureCommand {
	
	/**
	 * Hidden constructor
	 */
	private CreateRemoveSeiWithFileStructureCommand() {
	}
	

	/**
	 * Creates a command to remove a given StructuralElementInstance and its file structure
	 * @param seiToRemove StructuralElementInstance to remove
	 * @return Command that removes seiToRemove and its file structure
	 */
	public static Command create(StructuralElementInstance seiToRemove) {
		CompoundCommand removeSeiCommand = doCreate(seiToRemove);
		return removeSeiCommand;
	}

	/**
	 * Creates a command to remove multiple StructuralElementInstances and their file structures
	 * @param seisToRemove 
	 * @return Command
	 */
	public static Command create(Collection<StructuralElementInstance> seisToRemove) {
		CompoundCommand removeAllSeisCommand = new CompoundCommand();
		ArrayList<StructuralElementInstance> seisToDelete = eliminateSelectedChildrenOfSelectedParentsThatWillBeDeletedAnyway(seisToRemove);
		for (StructuralElementInstance sei : seisToDelete) {
			if (sei.eResource() != null) {
				removeAllSeisCommand.append(CreateRemoveSeiWithFileStructureCommand.create(sei));
			}
		}
	
		return removeAllSeisCommand;
	}
	
	/**
	 * Creates a command to remove a given StructuralElementInstance and its file structure
	 * @param seiToRemove StructuralElementInstance to remove
	 * @return Command that removes seiToRemove and its file structure
	 */
	private static CompoundCommand doCreate(StructuralElementInstance seiToRemove) {
		VirSatTransactionalEditingDomain editingDomain = VirSatEditingDomainRegistry.INSTANCE.getEd(seiToRemove);
		CompoundCommand compoundCommand =  new CompoundCommand();

		if (editingDomain != null) {
			VirSatResourceSet virSatResourceSet = editingDomain.getResourceSet();
			IProject project = virSatResourceSet.getProject();
	
			List<StructuralElementInstance> seisToRemove = seiToRemove.getDeepChildren();
			seisToRemove.add(seiToRemove);
			 
			for (StructuralElementInstance iste : seisToRemove) {
				Command recordedRemoveCommand = DeleteStructuralElementInstanceCommand.create(editingDomain, iste);	
				Command removeDvlmResourcesCommand = createRemoveDvlmResourcesCommand(project, virSatResourceSet, iste);
				Command removeFilesCommand = new RemoveFileStructureCommand(project, iste);
				
				//compoundCommand.append(resyncSeiResourceCommand);
				compoundCommand.append(recordedRemoveCommand);
				compoundCommand.append(removeDvlmResourcesCommand);
				compoundCommand.append(removeFilesCommand);
			}
		}
	
		return compoundCommand;
	}
	
	/**
	 * Creates a compound command for removing all .dvlm* resources in the folder of the structural element instance
	 * that is to be deleted. 
	 * @param project the project
	 * @param virSatResourceSet the resource seit
	 * @param iste the structural element instance
	 * @return a compound command for removing the resources
	 */
	private static Command createRemoveDvlmResourcesCommand(IProject project, VirSatResourceSet virSatResourceSet, StructuralElementInstance iste)  {
		VirSatProjectCommons projectCommons = new VirSatProjectCommons(project);
		Set<Resource> emfResources = new HashSet<>();
		IFolder seiFolder = projectCommons.getStructuralElemntInstanceFolder(iste);
		IResourceVisitor dvlmResourceVisitor = new IResourceVisitor() {
			@Override
			public boolean visit(IResource resource) throws CoreException {
				if ((resource instanceof IFile) && VirSatProjectCommons.isDvlmFile((IFile) resource)) {
					URI resourceUri = URI.createPlatformResourceURI(resource.getFullPath().toString(), true);
					Resource emfResource = virSatResourceSet.getResource(resourceUri, false);
					emfResources.add(emfResource);
				}
				return true;
			}
		};
		
		try {
			seiFolder.accept(dvlmResourceVisitor);
		} catch (CoreException e) {
			Activator.getDefault().getLog().log(new Status(Status.ERROR, Activator.getPluginId(),
					"Could not create command to remove resource" + e.getMessage()));
		}
		
		return new RemoveResourceCommand(virSatResourceSet, emfResources);
	}
	
	
	
	/**
	 * Creates a new list which does not contain seis that are marked for deletion whose parents are also marked for deletion
	 * for example, A contains B, and both are selected for deletion. In this case we need to remove B as it will be deleted anyway with A
	 * @param seisToDelete Collection of seis that are selected for deletion
	 * @return list of seis without children that will be deleted anyway
	 */
	private static ArrayList<StructuralElementInstance> eliminateSelectedChildrenOfSelectedParentsThatWillBeDeletedAnyway(Collection<StructuralElementInstance> seisToDelete) {
		Set<EObject> selectedToBeDeleted = new HashSet<>(seisToDelete);
		ArrayList<StructuralElementInstance> seisToDeleteWithoutDuplicateChildren = new ArrayList<>();
		for (StructuralElementInstance sei : seisToDelete) {
			EObject parent = sei.eContainer();
			while (parent instanceof StructuralElementInstance && !selectedToBeDeleted.contains(parent)) {
				parent = parent.eContainer();
			}
			if (!(parent instanceof StructuralElementInstance)) {
				seisToDeleteWithoutDuplicateChildren.add(sei); //parents of eobject are not marked for deletion 
			}
		}
		return seisToDeleteWithoutDuplicateChildren;
	}
	
}
