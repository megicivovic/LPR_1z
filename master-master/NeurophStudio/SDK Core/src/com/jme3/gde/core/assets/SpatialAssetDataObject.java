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
package com.jme3.gde.core.assets;

import com.jme3.asset.AssetKey;
import com.jme3.asset.ModelKey;
import com.jme3.gde.core.util.SpatialUtil;
import com.jme3.scene.Spatial;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileLock;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.MultiFileLoader;
import org.openide.util.Exceptions;

/**
 *
 * @author normenhansen
 */
public class SpatialAssetDataObject extends AssetDataObject {

    public SpatialAssetDataObject(FileObject pf, MultiFileLoader loader) throws DataObjectExistsException, IOException {
        super(pf, loader);
        saveExtension = "j3o";
//        CookieSet cookies = getCookieSet();
//        cookies.add((Node.Cookie) new SpatialAssetOpenSupport(this));
//        cookies.assign(OpenCookie.class, new SpatialAssetOpenSupport(this));
//        cookies.assign(CloseCookie.class, new SpatialAssetCloseSupport(this));
    }

    @Override
    public synchronized ModelKey getAssetKey() {
        AssetKey superKey = super.getAssetKey();
        if (superKey instanceof ModelKey) {
            return (ModelKey) superKey;
        } else {
            ProjectAssetManager mgr = getLookup().lookup(ProjectAssetManager.class);
            if (mgr == null) {
                return null;
            }
            String assetKey = mgr.getRelativeAssetPath(getPrimaryFile().getPath());
            return new ModelKey(assetKey);
        }
    }

    @Override
    public synchronized Spatial loadAsset() {
        if (savable != null) {
            return (Spatial) savable;
        }
        ProjectAssetManager mgr = getLookup().lookup(ProjectAssetManager.class);
        if (mgr == null) {
            DialogDisplayer.getDefault().notifyLater(new NotifyDescriptor.Message("File is not part of a project!\nCannot load without ProjectAssetManager."));
            return null;
        }
        //make sure its actually closed and all data gets reloaded
        closeAsset();
        try {
            listListener.start();
            Spatial spatial = mgr.loadModel(getAssetKey());
            listListener.stop();
            if (!(this instanceof BinaryModelDataObject)) {
                SpatialUtil.storeOriginalPathUserData(spatial);
            }
            savable = spatial;
            logger.log(Level.INFO, "Loaded asset {0}", getName());
            return spatial;
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
        return null;
    }

    @Override
    public synchronized void saveAsset() throws IOException {
        super.saveAsset();
        ProjectAssetManager mgr = getLookup().lookup(ProjectAssetManager.class);
        if (mgr == null) {
            DialogDisplayer.getDefault().notifyLater(new NotifyDescriptor.Message("File is not part of a project!\nCannot load without ProjectAssetManager."));
            return;
        }
        FileObject outFile = null;
        if (saveExtension == null) {
            outFile = getPrimaryFile();
        } else {
            outFile = getPrimaryFile().getParent().getFileObject(getPrimaryFile().getName(), saveExtension);
            if (outFile == null) {
                Logger.getLogger(SpatialAssetDataObject.class.getName()).log(Level.SEVERE, "Could not locate saved file.");
                return;
            }
        }
        try {
            DataObject targetModel = DataObject.find(outFile);
            AssetData properties = targetModel.getLookup().lookup(AssetData.class);
            if (properties != null) {
                if (properties.getProperty("ORIGINAL_PATH") == null) {
                    String path = mgr.getRelativeAssetPath(getPrimaryFile().getPath());
                    properties.setProperty("ORIGINAL_PATH", path);
                    logger.log(Level.INFO, "Set original path for {0} to {1}", new Object[]{getName(), path});
                }
            } else {
                logger.log(Level.WARNING, "New object {0} has no AssetData?", getName());
            }
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }
}
