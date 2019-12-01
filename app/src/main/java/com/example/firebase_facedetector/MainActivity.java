package com.example.firebase_facedetector;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceLandmark;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    Context mContext;

    RelativeLayout relativeLayout_main;
    ImageView imageView_main;
    TextView textView_main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;

        relativeLayout_main = findViewById(R.id.RelativeLayout_Main);
        imageView_main = findViewById(R.id.ImageView_Main);
        textView_main = findViewById(R.id.TextView_main);

        textView_main.setText("로딩중");
        //얼굴 인식 함수
        FaceDetect();
    }

    private void FaceDetect()
    {
        // High-accuracy landmark detection and face classification
        FirebaseVisionFaceDetectorOptions options = new FirebaseVisionFaceDetectorOptions.Builder()
                        .setPerformanceMode(FirebaseVisionFaceDetectorOptions.ACCURATE)
                        .setLandmarkMode(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS)
                        .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
                        .build();


        imageView_main.setImageResource(R.drawable.face4);
        final Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.face4);
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);

        FirebaseVisionFaceDetector detector = FirebaseVision.getInstance().getVisionFaceDetector(options);

        Task<List<FirebaseVisionFace>> result =
                detector.detectInImage(image)
                        .addOnSuccessListener(
                                new OnSuccessListener<List<FirebaseVisionFace>>() {
                                    @Override
                                    public void onSuccess(List<FirebaseVisionFace> faces) {
                                        // Task completed successfully
                                        Log.d("     Faces       : ",faces.toString());

                                        Point p = new Point();
                                        Display display = getWindowManager().getDefaultDisplay();
                                        display.getSize(p);

                                        for (FirebaseVisionFace face : faces) {
                                            FirebaseVisionFaceLandmark leftEye= face.getLandmark(FirebaseVisionFaceLandmark.RIGHT_EYE);
                                            float lex = leftEye.getPosition().getX();
                                            float ley = leftEye.getPosition().getY();

                                            Log.d("     Size         : ",bitmap.getWidth() + "  //  "+bitmap.getHeight());

                                            // 오른쪽 눈에 스티커 부착
                                            ImageView imageRE = new ImageView(mContext);
                                            imageRE.setImageResource(R.drawable.p2);
                                            imageRE.setX((lex / bitmap.getWidth()) * p.x );
                                            imageRE.setY(p.y / (float)bitmap.getHeight() *ley );
                                            imageRE.setX(p.x * lex / bitmap.getWidth() + 150);
                                            imageRE.setY(p.y * ley /bitmap.getHeight() - 190);
                                            imageRE.setLayoutParams(new RelativeLayout.LayoutParams(200,200));
                                            relativeLayout_main.addView(imageRE);
                                        }
                                        textView_main.setText("완료");
                                    }
                                })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Task failed with an exception
                }
        });
    }
}
