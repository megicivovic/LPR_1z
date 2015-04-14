/*
 * Copyright (c) 2009-2010 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors
 *   may be used to endorse or promote products derived from this software
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.jme3.gde.core.util;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;

/**
 * A custom toggle button group that allows you to deselect
 * a button that is already selected.
 * 
 * @author bowens
 */
public class ToggleButtonGroup extends ButtonGroup {
    private ButtonModel modifiedSelection;

    @Override
    public void add(AbstractButton b) {
        if (b == null) {
            return;
        }
        buttons.addElement(b);

        if (b.isSelected()) {
            if (modifiedSelection == null) {
                modifiedSelection = b.getModel();
            } else {
                b.setSelected(false);
            }
        }

        b.getModel().setGroup(this);
    }

    @Override
    public void remove(AbstractButton b) {
        if (b == null) {
            return;
        }
        buttons.removeElement(b);
        if (b.getModel() == modifiedSelection) {
            modifiedSelection = null;
        }
        b.getModel().setGroup(null);
    }

    @Override
    public ButtonModel getSelection() {
        return modifiedSelection;
    }

    @Override
    public void setSelected(ButtonModel m, boolean b) {
        if (!b && m == modifiedSelection) {
            modifiedSelection = null;
            return;
        }
        if (b && m != null && m != modifiedSelection) {
            ButtonModel oldSelection = modifiedSelection;
            modifiedSelection = m;
            if (oldSelection != null) {
                oldSelection.setSelected(false);
            }
            m.setSelected(true);
        }
    }

    @Override
    public boolean isSelected(ButtonModel m) {
        return (m == modifiedSelection);
    }
}
