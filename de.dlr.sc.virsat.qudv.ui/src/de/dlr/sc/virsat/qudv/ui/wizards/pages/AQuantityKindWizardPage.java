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

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import de.dlr.sc.virsat.model.dvlm.qudv.AQuantityKind;
import de.dlr.sc.virsat.model.dvlm.qudv.SystemOfUnits;
import de.dlr.sc.virsat.model.dvlm.units.UnitManagement;

/**
 * abstract super class to define general parts of the quantity kind wizard 
 * @author scha_vo
 *
 */
public abstract class AQuantityKindWizardPage extends WizardPage {
	
	protected Text name;
	protected Text symbol;
	protected Text description;
	protected Text definitionURI;
	
	protected Label separator;
	
	protected GridData gd;
	
	protected Composite container;

	protected AQuantityKind quantityKind = null;
	protected SystemOfUnits systemOfUnits = null;
	
	/**
	 * protected constructor
	 * @param pageName the name of the page
	 */
	protected AQuantityKindWizardPage(String pageName) {
		super(pageName);
	}
	
	/**
	 * public constructor
	 * @param quantityKind the quantity kind 
	 * @param um the unit management
	 * @param pageName the name of the page
	 */
	public AQuantityKindWizardPage(AQuantityKind quantityKind, UnitManagement um, String pageName) {
		super(pageName);
		this.quantityKind = quantityKind;
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
}
