/*
 *  Copyright (c) 2009-2010 jMonkeyEngine
 *  All rights reserved.
 * 
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are
 *  met:
 * 
 *  * Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 
 *  * Neither the name of 'jMonkeyEngine' nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 * 
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 *  "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 *  TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 *  PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 *  CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 *  EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 *  PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 *  PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 *  LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 *  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.jme3.gde.core.appstates;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppState;
import com.jme3.gde.core.assets.ProjectAssetManager;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;
import java.util.List;
import javax.swing.JComponent;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.WizardDescriptor;
import org.openide.util.Exceptions;

// An example action demonstrating how the wizard could be called from within
// your code. You can move the code below wherever you need, or register an action:
// @ActionID(category="...", id="com.jme3.gde.core.sceneexplorer.nodes.actions.impl.NewCustomControlWizardAction")
// @ActionRegistration(displayName="Open NewCustomControl Wizard")
// @ActionReference(path="Menu/Tools", position=...)
public final class NewAppStateWizardAction implements ActionListener {

    private WizardDescriptor.Panel[] panels;
    ProjectAssetManager mgr;
    Application fakeApp;

    public NewAppStateWizardAction(ProjectAssetManager mgr, Application fakeApp) {
        this.mgr = mgr;
        this.fakeApp = fakeApp;
    }

    public void actionPerformed(ActionEvent e) {
        //TODO: actual action to open/close via keyboard etc?
    }

    protected void showWizard() {
        WizardDescriptor wizardDescriptor = new WizardDescriptor(getPanels());
        // {0} will be replaced by WizardDesriptor.Panel.getComponent().getName()
        wizardDescriptor.setTitleFormat(new MessageFormat("{0}"));
        wizardDescriptor.setTitle("Add New AppState");
        wizardDescriptor.putProperty("asset_manager", mgr);
        wizardDescriptor.putProperty("fake_app", fakeApp);
        wizardDescriptor.putProperty("project", mgr.getProject());
        Dialog dialog = DialogDisplayer.getDefault().createDialog(wizardDescriptor);
        dialog.setVisible(true);
        dialog.toFront();
        boolean cancelled = wizardDescriptor.getValue() != WizardDescriptor.FINISH_OPTION;
        if (!cancelled) {
            AppState state = doCreateAppState(wizardDescriptor);
            if (state != null) {
                fakeApp.getStateManager().attach(state);
            }
        }
    }

    protected AppState doCreateAppState(WizardDescriptor configuration) {
        if (configuration == null) {
            return null;
        }
        WizardDescriptor wizardDescriptor = (WizardDescriptor) configuration;
        ProjectAssetManager manager = (ProjectAssetManager) wizardDescriptor.getProperty("asset_manager");
        List<ClassLoader> loaders = manager.getClassLoaders();

        String className = (String) wizardDescriptor.getProperty("class_name");
        Class clazz = null;
        try {
            clazz = getClass().getClassLoader().loadClass(className);
        } catch (ClassNotFoundException ex) {
        }
        for (ClassLoader classLoader : loaders) {
            if (clazz == null) {
                try {
                    clazz = classLoader.loadClass(className);
                } catch (ClassNotFoundException ex) {
                }
            }
        }
        if (clazz != null) {
            try {
                Object contr = clazz.newInstance();
                //TODO: remove sillyness-test
                if (contr instanceof AppState || contr instanceof AbstractAppState) {
                    return (AppState) contr;
                } else {
                    DialogDisplayer.getDefault().notifyLater(new NotifyDescriptor.Message("This is no AppState class!"));
                }
            } catch (InstantiationException ex) {
                Exceptions.printStackTrace(ex);
                DialogDisplayer.getDefault().notifyLater(new NotifyDescriptor.Message("Error instatiating class!"));
            } catch (IllegalAccessException ex) {
                Exceptions.printStackTrace(ex);
                DialogDisplayer.getDefault().notifyLater(new NotifyDescriptor.Message("Error instatiating class!"));
            }
        } else {
            DialogDisplayer.getDefault().notifyLater(new NotifyDescriptor.Message("Cannot find class: " + className + "\nMake sure the name is correct and the project is compiled,\nbest enable 'Save on Compile' in the project preferences."));
        }
        return null;
    }

    /**
     * Initialize panels representing individual wizard's steps and sets various
     * properties for them influencing wizard appearance.
     */
    private WizardDescriptor.Panel[] getPanels() {
        if (panels == null) {
            panels = new WizardDescriptor.Panel[]{
                new NewAppStateWizardPanel1()
            };
            String[] steps = new String[panels.length];
            for (int i = 0; i < panels.length; i++) {
                Component c = panels[i].getComponent();
                // Default step name to component name of panel. Mainly useful
                // for getting the name of the target chooser to appear in the
                // list of steps.
                steps[i] = c.getName();
                if (c instanceof JComponent) { // assume Swing components
                    JComponent jc = (JComponent) c;
                    // Sets step number of a component
                    // TODO if using org.openide.dialogs >= 7.8, can use WizardDescriptor.PROP_*:
                    jc.putClientProperty("WizardPanel_contentSelectedIndex", new Integer(i));
                    // Sets steps names for a panel
                    jc.putClientProperty("WizardPanel_contentData", steps);
                    // Turn on subtitle creation on each step
                    jc.putClientProperty("WizardPanel_autoWizardStyle", Boolean.TRUE);
                    // Show steps on the left side with the image on the background
                    jc.putClientProperty("WizardPanel_contentDisplayed", Boolean.TRUE);
                    // Turn on numbering of all steps
                    jc.putClientProperty("WizardPanel_contentNumbered", Boolean.TRUE);
                }
            }
        }
        return panels;
    }
}
