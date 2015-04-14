// HandwritingOCR.java
// Copyright (c) 2010 William Whitney
// All rights reserved.
// This software is released under the BSD license.
// Please see the accompanying LICENSE.txt for details.
package net.sourceforge.javaocr.ocrPlugins.handWriting;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sourceforge.javaocr.ocrPlugins.aspectRatio.AspectRatioOCR;
import net.sourceforge.javaocr.ocrPlugins.mseOCR.OCRScanner;
import net.sourceforge.javaocr.ocrPlugins.mseOCR.TrainingImage;
import net.sourceforge.javaocr.scanner.accuracy.AccuracyListener;
import net.sourceforge.javaocr.scanner.accuracy.OCRComp;
import net.sourceforge.javaocr.scanner.accuracy.OCRIdentification;

/**
 * Employs the help of many plug-ins to help recognize hand writing.
 * @author William Whitney
 */
public class HandwritingOCR implements AccuracyListener
{

    private final HashMap<Character, ArrayList<TrainingImage>> trainingImages;
    private boolean isDoMSE;
    private boolean isDoAspect;
    private boolean isDoNeural;
    private final ArrayList<OCRIdentification> mseCharIdentList;
    private final ArrayList<OCRIdentification> aspectCharIdentList;
    private final ArrayList<OCRIdentification> neuralCharIdentList;

    public HandwritingOCR(HashMap<Character, ArrayList<TrainingImage>> trainingImages)
    {
        this.trainingImages = trainingImages;
        this.mseCharIdentList = new ArrayList<OCRIdentification>();
        this.aspectCharIdentList = new ArrayList<OCRIdentification>();
        this.neuralCharIdentList = new ArrayList<OCRIdentification>();
    }

    public String scan(BufferedImage targetBfImage)
    {
        if (isDoMSE)
        {
            //Make scan pass using MSE OCR
            doMSEScan(targetBfImage);
        }

        if (isDoAspect)
        {
            //Make aspect ratio pass
            doAspectRatioScan(targetBfImage);
        }

        if (isDoNeural)
        {
            //TODO
            LOG.log(Level.INFO, "Neural Net OCR Is Not Implemented!!");
        }

        //Analyze the info that came back from processCharOrSpace
        return produceFinalString();

    }

    public void processCharOrSpace(OCRIdentification charIdent)
    {
        if (charIdent.getOCRType() == OCRComp.MSE)
        {
            this.mseCharIdentList.add(charIdent);
        }
        else if (charIdent.getOCRType() == OCRComp.ASPECT_RATIO)
        {
            this.aspectCharIdentList.add(charIdent);
        }
        else if (charIdent.getOCRType() == OCRComp.NEURAL)
        {
            this.neuralCharIdentList.add(charIdent);
        }
    }

    private String produceFinalString()
    {
        ResultAnalyzer resultAnalyzer = new ResultAnalyzer(mseCharIdentList, neuralCharIdentList, aspectCharIdentList);
        return resultAnalyzer.calculateResultAndReturnString();
    }

    private void doMSEScan(BufferedImage targetBfImage)
    {
        OCRScanner ocrScanner = new OCRScanner();
        ocrScanner.acceptAccuracyListener(this);
        ocrScanner.addTrainingImages(trainingImages);
        ocrScanner.scan(targetBfImage, 0, 0, 0, 0, null);
    }

    private void doAspectRatioScan(BufferedImage targetBfImage)
    {
        AspectRatioOCR ocrScanner = new AspectRatioOCR(trainingImages);
        ocrScanner.acceptAccuracyListener(this);
        ocrScanner.scan(targetBfImage);
    }

    public void setEnableMSEOCR(boolean enable)
    {
        this.isDoMSE = enable;
    }

    public void setEnableAspectOCR(boolean enable)
    {
        this.isDoAspect = enable;
    }

    public void setEnableNeuralOCR(boolean enable)
    {
        this.isDoNeural = enable;
    }
    private static final Logger LOG = Logger.getLogger(HandwritingOCR.class.getName());
}
