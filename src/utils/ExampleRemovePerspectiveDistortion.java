/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import boofcv.abst.geo.Estimate1ofEpipolar;
import boofcv.alg.distort.DistortImageOps;
import boofcv.alg.distort.PointToPixelTransform_F32;
import boofcv.alg.distort.PointTransformHomography_F32;
import boofcv.alg.interpolate.TypeInterpolate;
import boofcv.core.image.ConvertBufferedImage;
import boofcv.factory.geo.FactoryMultiView;
import boofcv.gui.image.ShowImages;
import boofcv.io.image.UtilImageIO;
import boofcv.struct.distort.PixelTransform_F32;
import boofcv.struct.geo.AssociatedPair;
import boofcv.struct.image.ImageFloat32;
import boofcv.struct.image.MultiSpectral;
import georegression.struct.point.Point2D_F64;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import org.ejml.data.DenseMatrix64F;

/**
 *
 * @author Megi
 */
/**
 * Certain image processing techniques, such as Optical Character Recognition (OCR), can be performed better if
 * perspective distortion is remove from an image.  In this example a homography is computed from the cour corners
 * of a bulletin board and the image is projected into a square image without perspective distortion.
 *
 * @author Peter Abeles
 */
public class ExampleRemovePerspectiveDistortion {
	public static void main(String[] args) {
 
		// load a color image
		BufferedImage buffered = UtilImageIO.loadImage("data/housenumber.jpg");
		MultiSpectral<ImageFloat32> input = ConvertBufferedImage.convertFromMulti(buffered, null, true, ImageFloat32.class);
 
		// Create a smaller output image for processing later on
		MultiSpectral<ImageFloat32> output = input._createNew(400,500);
 
		// Homography estimation algorithm.  Requires a minimum of 4 points
		Estimate1ofEpipolar computeHomography = FactoryMultiView.computeHomography(true);
 
		// Specify the pixel coordinates from destination to target
		ArrayList<AssociatedPair> associatedPairs = new ArrayList<AssociatedPair>();
		associatedPairs.add(new AssociatedPair(new Point2D_F64(0,0),new Point2D_F64(267,182)));
		associatedPairs.add(new AssociatedPair(new Point2D_F64(output.width-1,0),new Point2D_F64(542,68)));
		associatedPairs.add(new AssociatedPair(new Point2D_F64(output.width-1,output.height-1),new Point2D_F64(519,736)));
		associatedPairs.add(new AssociatedPair(new Point2D_F64(0,output.height-1),new Point2D_F64(276,570)));
 
		// Compute the homography
		DenseMatrix64F H = new DenseMatrix64F(3,3);
		computeHomography.process(associatedPairs, H);
 
		// Create the transform for distorting the image
		PointTransformHomography_F32 homography = new PointTransformHomography_F32(H);
		PixelTransform_F32 pixelTransform = new PointToPixelTransform_F32(homography);
 
		// Apply distortion and show the results
		DistortImageOps.distortMS(input,output,pixelTransform,true, TypeInterpolate.BILINEAR);
 
		BufferedImage flat = ConvertBufferedImage.convertTo_F32(output,null,true);
		ShowImages.showWindow(buffered,"Original Image");
		ShowImages.showWindow(flat,"Without Perspective Distortion");
	}
}