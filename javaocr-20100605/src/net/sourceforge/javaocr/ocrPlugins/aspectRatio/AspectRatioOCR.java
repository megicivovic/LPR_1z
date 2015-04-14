// AspectRatioOCR.java
// Copyright (c) 2010 William Whitney
// All rights reserved.
// This software is released under the BSD license.
// Please see the accompanying LICENSE.txt for details.
package net.sourceforge.javaocr.ocrPlugins.aspectRatio;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Stack;
import java.util.logging.Logger;
import net.sourceforge.javaocr.ocrPlugins.mseOCR.TrainingImage;
import net.sourceforge.javaocr.scanner.DocumentScanner;
import net.sourceforge.javaocr.scanner.DocumentScannerListenerAdaptor;
import net.sourceforge.javaocr.scanner.PixelImage;
import net.sourceforge.javaocr.scanner.accuracy.AccuracyListener;
import net.sourceforge.javaocr.scanner.accuracy.AccuracyProvider;
import net.sourceforge.javaocr.scanner.accuracy.OCRComp;
import net.sourceforge.javaocr.scanner.accuracy.OCRIdentification;

/**
 * Provides an OCR that can be used in addition to other OCR plug-ins to
 * increase accuracy.
 * @author William Whitney
 */
public class AspectRatioOCR extends DocumentScannerListenerAdaptor implements AccuracyProvider
{

    private AccuracyListener listener;
    private final HashMap<Character, ArrayList<TrainingImage>> trainingImages;
    private final ArrayList<CharacterRatio> charRatioList = new ArrayList<CharacterRatio>();
    private DocumentScanner documentScanner = new DocumentScanner();

    public AspectRatioOCR(HashMap<Character, ArrayList<TrainingImage>> trainingImages)
    {
        this.trainingImages = trainingImages;
        processTrainingImages();

    }

    public void scan(BufferedImage targetBfImage)
    {
        PixelImage pixelImage = new PixelImage(targetBfImage);
        pixelImage.toGrayScale(true);
        pixelImage.filter();
        documentScanner.scan(pixelImage, this, 0, 0, pixelImage.width, pixelImage.height);

    }

    public void acceptAccuracyListener(AccuracyListener listener)
    {
        this.listener = listener;
    }

    @Override
    public void endRow(PixelImage pixelImage, int y1, int y2)
    {
        //Send accuracy of this identification to the listener
        if (listener != null)
        {
            OCRIdentification identAccuracy = new OCRIdentification(OCRComp.ASPECT_RATIO);
            identAccuracy.addChar('\n', 0.0);
            listener.processCharOrSpace(identAccuracy);
        }
    }

    @Override
    public void processChar(PixelImage pixelImage, int x1, int y1, int x2, int y2, int rowY1, int rowY2)
    {
        int width = x2 - x1;
        int height = y2 - y1;
        double currRatio = getRatio(width, height);

        if (listener != null)
        {
            listener.processCharOrSpace(determineCharacterPossibilities(currRatio));
        }

    }

    @Override
    public void processSpace(PixelImage pixelImage, int x1, int y1, int x2, int y2)
    {
        if (listener != null)
        {
            OCRIdentification identAccuracy = new OCRIdentification(OCRComp.ASPECT_RATIO);
            identAccuracy.addChar(' ', 0.0);
            listener.processCharOrSpace(identAccuracy);
        }
    }

    private void processTrainingImages()
    {
        for (Iterator<Character> it = trainingImages.keySet().iterator(); it.hasNext();)
        {
            Character key = it.next();
            ArrayList<TrainingImage> charTrainingImages = trainingImages.get(key);
            if (charTrainingImages != null)
            {
                for (int i = 0; i < charTrainingImages.size(); i++)
                {
                    int width = charTrainingImages.get(i).width;
                    int height = charTrainingImages.get(i).height;
                    charRatioList.add(new CharacterRatio(key, getRatio(width, height)));
                }
            }
        }

        Collections.sort(charRatioList);
    }
    private static final Logger LOG = Logger.getLogger(AspectRatioOCR.class.getName());

    private double getRatio(int width, int height)
    {
        return ((double) width) / ((double) height);
    }

    private OCRIdentification determineCharacterPossibilities(double targetRatio)
    {
        double smallestError = Double.MAX_VALUE;
        Stack<CharacterRatio> bestResults = new Stack<CharacterRatio>();
        for (CharacterRatio currChar : charRatioList)
        {
            if (Math.abs(currChar.getRatio() - targetRatio) < smallestError)
            {
                smallestError = Math.abs(currChar.getRatio() - targetRatio);
                bestResults.push(currChar);
            }
        }

        OCRIdentification newIdent = new OCRIdentification(OCRComp.ASPECT_RATIO);

        int count = 0;
        while (!bestResults.isEmpty() && count < 5)
        {
            CharacterRatio currChar = bestResults.pop();
            double errorAmount = Math.abs(targetRatio - currChar.getRatio());
            newIdent.addChar(currChar.getCharacter(), errorAmount);
            count++;
        }

        return newIdent;
    }
}
