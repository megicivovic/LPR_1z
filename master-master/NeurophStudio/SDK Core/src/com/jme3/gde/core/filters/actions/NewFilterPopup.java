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
package com.jme3.gde.core.filters.actions;

import com.jme3.gde.core.filters.FilterPostProcessorNode;
import com.jme3.gde.core.undoredo.AbstractUndoableSceneEdit;
import com.jme3.gde.core.undoredo.SceneUndoRedoManager;
import com.jme3.post.Filter;
import com.jme3.post.FilterPostProcessor;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import org.openide.util.Lookup;
import org.openide.util.actions.Presenter;

/**
 *
 * @author normenhansen
 */
public class NewFilterPopup extends AbstractAction implements Presenter.Popup {

    protected FilterPostProcessorNode filterNode;

    public NewFilterPopup(FilterPostProcessorNode node) {
        this.filterNode = node;
    }

    public void actionPerformed(ActionEvent e) {
    }

    public JMenuItem getPopupPresenter() {
        JMenu result = new JMenu("Add Filter..");
        for (NewFilterAction di : Lookup.getDefault().lookupAll(NewFilterAction.class)) {
            result.add(new JMenuItem(di.getAction(filterNode)));
        }
        return result;
    }

    private void addFilterUndo(final FilterPostProcessor fpp, final Filter filter) {
        //add undo
        if (fpp != null && filter != null) {
            Lookup.getDefault().lookup(SceneUndoRedoManager.class).addEdit(this, new AbstractUndoableSceneEdit() {

                @Override
                public void sceneUndo() throws CannotUndoException {
                    fpp.removeFilter(filter);
                }

                @Override
                public void sceneRedo() throws CannotRedoException {
                    fpp.addFilter(filter);
                }

                @Override
                public void awtRedo() {
                    filterNode.refresh();
                }

                @Override
                public void awtUndo() {
                    filterNode.refresh();
                }
            });
        }
    }

    private void setModified() {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                filterNode.refresh();
            }
        });
    }
}
