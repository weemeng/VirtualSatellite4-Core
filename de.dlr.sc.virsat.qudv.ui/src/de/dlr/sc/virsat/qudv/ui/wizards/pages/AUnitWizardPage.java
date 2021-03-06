/*******************************************************************************
 * Copyright (c) 2008-2019 German Aerospace Center (DLR), Simulation and Software Technology, Germany.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package de.dlr.sc.virsat.qudv.ui.wizards.pages;

import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import de.dlr.sc.virsat.model.dvlm.qudv.AQuantityKind;
import de.dlr.sc.virsat.model.dvlm.qudv.AUnit;
import de.dlr.sc.virsat.model.dvlm.qudv.SystemOfUnits;
import de.dlr.sc.virsat.model.dvlm.units.UnitManagement;
import de.dlr.sc.virsat.model.dvlm.qudv.util.QudvModelCommandFactory;

/**
 * abstract super class to define general information for the unit wizard page
 * @author scha_vo
 *
 */
public abstract class AUnitWizardPage extends WizardPage {
	
	protected Text name;
	protected Text symbol;
	protected Text description;
	protected Text definitionURI;

	protected ComboViewer comboViewer;
	private AQuantityKind selectedRefQuantityKind;
	
	protected Label separator;
	
	protected GridData gd;
	
	protected Composite container;

	protected AUnit unit = null;
	protected SystemOfUnits systemOfUnits = null;
	
	/**
	 * constructor
	 * @param pageName the name of the page
	 */
	protected AUnitWizardPage(String pageName) {
		super(pageName);
	}
	/**
	 * public constructor
	 * @param unit the unit to work on
	 * @param um the unit management
	 * @param pageName the name of the page
	 */
	public AUnitWizardPage(AUnit unit, UnitManagement um, String pageName) {
		super(pageName);
		this.unit = unit;
		this.systemOfUnits = um.getSystemOfUnit();
	}

	@Override
	public void createControl(Composite parent) {
		container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 2;
		gd = new GridData(GridData.FILL_HORIZONTAL);
		
		//First line of widgets
		Label label1 = new Label(container, SWT.NULL);
		label1.setText("Name");
		name = new Text(container, SWT.BORDER | SWT.SINGLE); 
		name.setText("");
		name.setLayoutData(gd);
		name.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				updateWidgets();
			}

		});
		
		
		//Second line of widgets
		Label label2 = new Label(container, SWT.NULL);
		label2.setText("Symbol");
		symbol = new Text(container, SWT.BORDER | SWT.SINGLE);
		symbol.setText("");
		symbol.setLayoutData(gd);
		
		//Third line of widgets
		Label label3 = new Label(container, SWT.NULL);
		label3.setText("Description");
		description = new Text(container, SWT.BORDER | SWT.SINGLE);
		description.setText("");
		description.setLayoutData(gd);
		
		//Forth line of widgets
		Label label4 = new Label(container, SWT.NULL);
		label4.setText("DefinitionURI");
		definitionURI = new Text(container, SWT.BORDER | SWT.SINGLE);
		definitionURI.setText("");
		definitionURI.setLayoutData(gd);
		
		//Separation Line
    	GridData gridData = new GridData(GridData.FILL, GridData.CENTER, true, false);
    	gridData.horizontalSpan = 2;
    	separator = new Label(container, SWT.SEPARATOR | SWT.HORIZONTAL);
    	separator.setLayoutData(gridData);
		
    	//add a Label:
    	Label myLabel = new Label(container, SWT.NULL);
    	myLabel.setText("Quantity Kind");

    	//add comboBox and comboViewer
    	Combo combo = new Combo(container, SWT.NULL);
    	
    	comboViewer = new ComboViewer(combo);
    	comboViewer.setLabelProvider(new LabelProvider() {
    		@Override
    		public String getText(Object element) {
    			if (element instanceof AQuantityKind) {
    				AQuantityKind u = (AQuantityKind) element;
    				String result = u.getName() + " - " + u.getSymbol();
    				return result;
    			}
    			return super.getText(element);
    		}
    	});
    	
    	comboViewer.setContentProvider(new ArrayContentProvider());
    	List<AQuantityKind> listOfQuantity = QudvModelCommandFactory.getListOfQuantities(systemOfUnits);
    	comboViewer.setInput(listOfQuantity);
    	comboViewer.add("no reference");
    	comboViewer.addSelectionChangedListener(new ISelectionChangedListener() {
    		@Override
    		public void selectionChanged(SelectionChangedEvent event) {
    			ISelection selection = event.getSelection();
    			if (!selection.isEmpty() && selection instanceof IStructuredSelection) {
    				Object o = ((IStructuredSelection) selection).getFirstElement();
    				if (o instanceof AQuantityKind) {
    					selectedRefQuantityKind = (AQuantityKind) o;
    					//updateWidgets();
    				} else if (o instanceof String) {
    					selectedRefQuantityKind = null;
    				}
    			}
    		}
    	});
	}
	
	/**
	 * method to initialize the values
	 */
	protected abstract void initializeValues();	
	
	/**
	 * method to update the widgets 
	 */
	protected abstract void updateWidgets();
	
	/**
	 * public getter method to get the name
	 * @return the name as String
	 */
	public String getName() {
		return name.getText();
	}
	
	/**
	 * public getter method to get the symbol
	 * @return the symbol as String
	 */
	public String getSymbol() {
		return symbol.getText();
	}
	
	/**
	 * public getter method to get the description
	 * @return the description as String
	 */
	public String getDescription() {
		return description.getText();
	}
	
	/**
	 * public getter method to get the definition URI
	 * @return the uri as String
	 */
	public String getDefinitionURI() {
		return definitionURI.getText();
	}
	
	/**
	 * public getter method to get the referenced quantity kind
	 * @return the referenced quantityKind
	 */
	public AQuantityKind getRefQuantityKind() {
		return selectedRefQuantityKind;
	}
}
