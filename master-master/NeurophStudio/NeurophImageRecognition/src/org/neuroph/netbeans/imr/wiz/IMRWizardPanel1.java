/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.neuroph.netbeans.imr.wiz;

import java.awt.Component;
import javax.swing.event.ChangeListener;
import org.neuroph.imgrec.ColorMode;
//import org.neuroph.contrib.imgrec.ColorMode;
import org.openide.WizardDescriptor;
import org.openide.util.HelpCtx;

public class IMRWizardPanel1 implements WizardDescriptor.Panel {

    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private IMRVisualPanel1 component;

    // Get the visual component for the panel. In this template, the component
    // is kept separate. This can be more efficient: if the wizard is created
    // but never displayed, or not all panels are displayed, it is better to
    // create only those which really need to be visible.
    public Component getComponent() {
        if (component == null) {
            component = new IMRVisualPanel1();
        }
        return component;
    }

    public HelpCtx getHelp() {
        // Show no Help button for this panel:
        return HelpCtx.DEFAULT_HELP;
        // If you have context help:
        // return new HelpCtx(SampleWizardPanel1.class);
    }

    public boolean isValid() {
        // If it is always OK to press Next or Finish, then:
        return true;
        // If it depends on some condition (form filled out...), then:
        // return someCondition();
        // and when this condition changes (last form field filled in...) then:
        // fireChangeEvent();
        // and uncomment the complicated stuff below.
    }

    public final void addChangeListener(ChangeListener l) {
    }

    public final void removeChangeListener(ChangeListener l) {
    }
    /*
    private final Set<ChangeListener> listeners = new HashSet<ChangeListener>(1); // or can use ChangeSupport in NB 6.0
    public final void addChangeListener(ChangeListener l) {
    synchronized (listeners) {
    listeners.add(l);
    }
    }
    public final void removeChangeListener(ChangeListener l) {
    synchronized (listeners) {
    listeners.remove(l);
    }
    }
    protected final void fireChangeEvent() {
    Iterator<ChangeListener> it;
    synchronized (listeners) {
    it = new HashSet<ChangeListener>(listeners).iterator();
    }
    ChangeEvent ev = new ChangeEvent(this);
    while (it.hasNext()) {
    it.next().stateChanged(ev);
    }
    }
     */

    // You can use a settings object to keep track of state. Normally the
    // settings object will be the WizardDescriptor, so you can use
    // WizardDescriptor.getProperty & putProperty to store information entered
    // by the user.
    public void readSettings(Object settings) {
    }

    public void storeSettings(Object settings) {
        String imageDir = component.getImageDirField().getText();
        String junkDir = component.getJunkDirField().getText();
        ((WizardDescriptor) settings).putProperty("imageDir", imageDir);
        ((WizardDescriptor)settings).putProperty("junkDir", junkDir);
        ColorMode colorMode = getColorMode();
        ((WizardDescriptor) settings).putProperty("colorMode", colorMode);
    }

    private ColorMode getColorMode(){
        ColorMode colorMode;
        String selectedColorMode = component.getColorModelButtonGroup().getSelection().getActionCommand();

        if (selectedColorMode.equalsIgnoreCase("Color")) colorMode = ColorMode.FULL_COLOR;
            else colorMode = ColorMode.BLACK_AND_WHITE;
        return colorMode;
       
    }
}

