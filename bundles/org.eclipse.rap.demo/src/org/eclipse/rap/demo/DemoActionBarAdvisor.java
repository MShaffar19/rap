/*******************************************************************************
 * Copyright (c) 2002-2006 Innoopract Informationssysteme GmbH. All rights
 * reserved. This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v1.0 which accompanies this
 * distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 * Contributors: Innoopract Informationssysteme GmbH - initial API and
 * implementation
 ******************************************************************************/
package org.eclipse.rap.demo;

import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.rap.demo.wizard.SurveyWizard;
import org.eclipse.swt.SWT;
import org.eclipse.ui.*;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ContributionItemFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.plugin.AbstractUIPlugin;

public class DemoActionBarAdvisor extends ActionBarAdvisor {

  private IWorkbenchAction exitAction;
  private Action aboutAction;
  private MenuManager showViewMenuMgr;
  private Action wizardAction;
  private Action browserAction;
  
  private static int browserIndex;

  public DemoActionBarAdvisor( final IActionBarConfigurer configurer ) {
    super( configurer );
  }

  protected void makeActions( final IWorkbenchWindow window ) {
    ImageDescriptor image1 
      = AbstractUIPlugin.imageDescriptorFromPlugin( "org.eclipse.rap.demo", 
                                                    "icons/ttt.gif" );
    ImageDescriptor image2 
      = AbstractUIPlugin.imageDescriptorFromPlugin( "org.eclipse.rap.demo", 
                                                    "icons/help.gif" );
    ImageDescriptor image3 
    = AbstractUIPlugin.imageDescriptorFromPlugin( "org.eclipse.rap.demo", 
                                                  "icons/login.gif" );
    ImageDescriptor image4 
    = AbstractUIPlugin.imageDescriptorFromPlugin( "org.eclipse.rap.demo", 
                                                  "icons/internal_browser.gif" );
    
    exitAction = ActionFactory.QUIT.create( window );
    exitAction.setImageDescriptor( image1 );
    register( exitAction );
    
    aboutAction = new Action() {
      public void run() {
        MessageDialog.openInformation( window.getShell(), 
                                       "RAP Demo", "About action clicked", 
                                       null );
      }
    };
    aboutAction.setText( "About" );
    aboutAction.setId( "org.eclipse.rap.demo.about" );
    aboutAction.setImageDescriptor( image2 );
    register( aboutAction );
    
    showViewMenuMgr = new MenuManager("Show View", "showView"); //$NON-NLS-1$
    IContributionItem showViewMenu
      = ContributionItemFactory.VIEWS_SHORTLIST.create(window);
    showViewMenuMgr.add(showViewMenu);
    
    wizardAction = new Action() {
      public void run() {
        SurveyWizard wizard = new SurveyWizard();
        WizardDialog dlg = new WizardDialog(window.getShell(), wizard);
        dlg.open(null);
      }
    };
    wizardAction.setText("Open wizard");
    wizardAction.setId("org.eclipse.rap.demo.wizard");
    wizardAction.setImageDescriptor(image3);
    register(wizardAction);
    
    browserAction = new Action() {
        public void run() {
        	browserIndex++;
        	try {
				window.getActivePage().showView(
						"org.eclipse.rap.demo.DemoBrowserViewPart",
						String.valueOf( browserIndex ) ,
						IWorkbenchPage.VIEW_ACTIVATE );
			} catch (PartInitException e) {
				e.printStackTrace();
			}
        }
      };
      browserAction.setText( "Open new Browser View" );
      browserAction.setId( "org.eclipse.rap.demo.browser" );
      browserAction.setImageDescriptor( image4 );
      register( browserAction );
  }
  

  protected void fillMenuBar( final IMenuManager menuBar ) {
    MenuManager fileMenu = new MenuManager( "File",
                                            IWorkbenchActionConstants.M_FILE );
    MenuManager windowMenu = new MenuManager( "Window",
                                            IWorkbenchActionConstants.M_WINDOW );    
    MenuManager helpMenu = new MenuManager( "Help",
                                            IWorkbenchActionConstants.M_HELP );
    
    menuBar.add( fileMenu );
    fileMenu.add( exitAction );
    
    windowMenu.add(showViewMenuMgr);
    menuBar.add( windowMenu );
    
    menuBar.add( helpMenu );
    helpMenu.add( aboutAction );
  }
  
  protected void fillCoolBar( ICoolBarManager coolBar ) {
    createToolBar( coolBar, "main" );
    createToolBar( coolBar, "test" );
  }

  private void createToolBar( ICoolBarManager coolBar, final String name ) {
    IToolBarManager toolbar = new ToolBarManager( SWT.FLAT | SWT.RIGHT );
    coolBar.add( new ToolBarContributionItem( toolbar, name ) );
    toolbar.add( exitAction );
    toolbar.add( wizardAction );
    toolbar.add( browserAction );
    toolbar.add( aboutAction );
  }
}
