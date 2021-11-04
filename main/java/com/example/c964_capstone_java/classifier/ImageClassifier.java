package com.example.c964_capstone_java.classifier;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.common.FileUtil;
import org.tensorflow.lite.support.common.TensorProcessor;
import org.tensorflow.lite.support.common.ops.NormalizeOp;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.ResizeOp;
import org.tensorflow.lite.support.image.ops.ResizeWithCropOrPadOp;
import org.tensorflow.lite.support.image.ops.Rot90Op;
import org.tensorflow.lite.support.label.TensorLabel;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.math.RoundingMode;
import java.nio.MappedByteBuffer;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ImageClassifier {

    private static final float PROBABILITY_MEAN = 0.0f;
    private static final float PROBABILITY_STD = 255.0f;
    private static final float IMAGE_STD = 1.0f;
    private static final float IMAGE_MEAN = 0.0f;
    private static final int MAX_SIZE = 3;
    private final Interpreter tensorClassifier;
    private final int imageResizeX;
    private final int imageResizeY;
    private TensorImage inputImageBuffer;
    private final TensorBuffer probabilityImageBuffer;
    private final TensorProcessor probabilityProcessor;
    private final  List<String> labels;
    private final DecimalFormat df = new DecimalFormat("##.##");

    public ImageClassifier(Activity activity) throws IOException {
        // Load the model
        MappedByteBuffer classifierModel = FileUtil.loadMappedFile(activity, "model.tflite");
        labels = FileUtil.loadLabels(activity, "labels.txt");

        tensorClassifier = new Interpreter(classifierModel, null);

        int imageTensorIndex = 0; // Input
        int probabilityTensorIndex = 0; // Output

        int [] inputImageShape = tensorClassifier.getInputTensor(imageTensorIndex).shape();
        DataType inputDataType = tensorClassifier.getInputTensor(imageTensorIndex).dataType();

        int[] outputImageShape = tensorClassifier.getOutputTensor(probabilityTensorIndex).shape();
        DataType outputDataType = tensorClassifier.getOutputTensor(probabilityTensorIndex).dataType();

        imageResizeX = inputImageShape[1];
        imageResizeY = inputImageShape[2];

        inputImageBuffer = new TensorImage(inputDataType);

        probabilityImageBuffer = TensorBuffer.createFixedSize(outputImageShape, outputDataType);

        probabilityProcessor = new TensorProcessor.Builder().add(new NormalizeOp(PROBABILITY_MEAN, PROBABILITY_STD)).build();
    }

    public List<Recognition> recognizeImage(final Bitmap bitmap){
        List<Recognition> recognitions = new ArrayList<>();
        inputImageBuffer = loadImage(bitmap);
        tensorClassifier.run(inputImageBuffer.getBuffer(), probabilityImageBuffer.getBuffer().rewind());
        Map<String, Float> labeledProbability = new TensorLabel(labels,
                probabilityProcessor.process(probabilityImageBuffer)).getMapWithFloatValue();
        for(Map.Entry<String, Float> entry : labeledProbability.entrySet()){
            recognitions.add(new Recognition(entry.getKey(), entry.getValue()));
        }
        // Based sort predictions on confidence
        Collections.sort(recognitions);
        // Return top predictions change MAX_SIZE to display more or less results
        //recognitions.subList(0, MAX_SIZE > recognitions.size() ? recognitions.size() : MAX_SIZE).clear();
        return recognitions;
    }

    private TensorImage loadImage(Bitmap bitmap) {
        Bitmap resizedBitmap =  Bitmap.createScaledBitmap(bitmap, 224, 224, false);

        inputImageBuffer.load(resizedBitmap);
        TensorImage image = TensorImage.fromBitmap(resizedBitmap);
        /*
        int cropSize = Math.min(resizedBitmap.getWidth(), resizedBitmap.getHeight());
        ImageProcessor imageProcessor = new ImageProcessor.Builder()

                .add(new ResizeWithCropOrPadOp(cropSize, cropSize))
                .add(new ResizeOp(imageResizeX, imageResizeY, ResizeOp.ResizeMethod.NEAREST_NEIGHBOR))
                .add(new NormalizeOp(IMAGE_MEAN, IMAGE_STD))


                .build();



        return imageProcessor.process(image);
        */
        return image;
    }

    public class Recognition implements Comparable{
        private String name;
        private float confidence;

        public Recognition() {
        }

        public Recognition(String name, float confidence) {
            this.name = name;
            this.confidence = confidence;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public float getConfidence() {
            Float bimmy = confidence;
            df.format(bimmy);
            return bimmy;
        }

        public void setConfidence(float confidence) {
            this.confidence = confidence;
        }

        @Override
        public String toString() {
            return "Recognition{" +
                    "name='" + name + '\'' +
                    ", confidence=" + df.format(confidence) +
                    '}';
        }

        @Override
        public int compareTo(Object o) {
            return Float.compare(((Recognition)o).confidence, this.confidence);
        }
    }

}
