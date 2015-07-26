package paddy.com.cameraapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class MainActivity extends ActionBarActivity {
        private Camera cameraObject;
        private CameraView cameraView;
        private ImageView pic;
        private Handler handler;
        FrameLayout preview;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pic = (ImageView)findViewById(R.id.img_view);
        handler=new Handler();
    }

    private Camera getCamera() {
        int cameraId = -1;
        Camera object = null;
        // Search for front or back camera
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT || info.facing== Camera.CameraInfo.CAMERA_FACING_BACK) {
                cameraId = i;
                break;
            }
        }
        if(cameraId>=0) object=Camera.open(cameraId);
        return object;
    }

    // Callback interface used to supply image data from a photo capture.
    private Camera.PictureCallback PCallback= new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
             String image_path = null;
            final Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            if(bitmap==null){
                Toast.makeText(getApplicationContext(), "Photo not taken", Toast.LENGTH_SHORT).show();
            }
            else
            {
                // Show image
                pic.setImageBitmap(bitmap);
                // Save to file
                FileOutputStream outStream = null;
                try {
                    image_path = Environment.getExternalStorageDirectory()+"/image.jpg";
                    File file = null;
                    file = new File(image_path);
                    if (file.exists())
                        file.delete();

                    outStream = new FileOutputStream(image_path);
                    outStream.write(data);
                    outStream.close();

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            // Release camera
            releaseCamera();
            // Reset camera after a short time period
            final String finalImage_path = image_path;
//            handler.postDelayed(new Runnable(){
//                public void run(){
//                    // reset camera and preview
runOnUiThread(new Runnable() {
    @Override
    public void run() {
        Intent i = new Intent(MainActivity.this,PhotoActivity.class);
        i.putExtra("BitmapImagePath", finalImage_path);
        startActivity(i);
    }
});

                   // initCamera();

//                }
//            }, 300);

        }
    };
    // Get camera object and prepare preview
    public void initCamera(){
        cameraObject = getCamera();
        cameraView = new CameraView(this, cameraObject);
        preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(cameraView);


    }
    public void takePhoto(View view){
        cameraObject.takePicture(null, null, PCallback);
    }

    protected void onResume(){
        super.onResume();
        initCamera();

    }

    public void releaseCamera(){
        if(cameraObject!=null){
            // Release camera and remove surface view from the preview
            cameraObject.release();
            cameraObject=null;
            preview.removeAllViews();
        }
    }

    // Camera preview
    private class CameraView extends SurfaceView implements SurfaceHolder.Callback {

        private SurfaceHolder surfaceHolder;
        private Camera camera;

        public CameraView(Context context,Camera camera) {
            super(context);
            this.camera = camera;
            surfaceHolder = getHolder();
            surfaceHolder.addCallback(this);
        }

        @Override
        public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {

        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            try  {
                if (Build.VERSION.SDK_INT >= 8) {
                    camera.setDisplayOrientation(90);
                }

                camera.setPreviewDisplay(holder);
                camera.startPreview();
            } catch (IOException e) {
            }
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder arg0) {
        }

    }

    protected void onPause(){
        super.onPause();
        releaseCamera();
    }
}