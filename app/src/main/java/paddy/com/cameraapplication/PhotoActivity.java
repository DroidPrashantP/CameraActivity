package paddy.com.cameraapplication;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.FloatMath;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.regex.Pattern;


public class PhotoActivity extends ActionBarActivity {
    ImageView capture_img,middle_img;
    Button save_image_gallary;
    RelativeLayout camera_wrapper;
    private static final Pattern DIR_SEPORATOR = Pattern.compile("/");
    ViewPager viewPager;
    int[] flag;
    PagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        save_image_gallary = (Button) findViewById(R.id.save_to_gallary);
        capture_img = (ImageView) findViewById(R.id.capture_img);
//        middle_img = (ImageView) findViewById(R.id.middle_img);
        camera_wrapper = (RelativeLayout) findViewById(R.id.camera_wrapper);
//        middle_img.setOnTouchListener(new Touch());
        Intent intent = getIntent();
        String image_path = intent.getExtras().getString("BitmapImagePath");

//        Bitmap bitmap = (Bitmap) intent.getParcelableExtra("BitmapImage");
//        if(bitmap!=null) {
//            capture_img.setImageBitmap(bitmap);
//        }
        Bitmap bitmap = ConvertIntoBitmap(image_path);
        if (bitmap != null) {
            capture_img.setImageBitmap(bitmap);
        }

        save_image_gallary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap bit = getViewBitmap(camera_wrapper);
                SaveImage(bit);
            }
        });
        flag = new int[] { R.drawable.ring_one, R.drawable.ring_one,
                R.drawable.ring_one, R.drawable.ring_one,
                R.drawable.ring_one };
        // Locate the ViewPager in viewpager_main.xml
        viewPager = (ViewPager) findViewById(R.id.pager);
        // Pass results to ViewPagerAdapter Class
        adapter = new ViewPagerAdapter(PhotoActivity.this, flag);
        // Binds the Adapter to the ViewPager
        viewPager.setAdapter(adapter);
    }

    public Bitmap ConvertIntoBitmap(String image_path) {
        File file = new File(image_path);
        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());

        try {
            File f = new File(image_path);
            ExifInterface exif = new ExifInterface(f.getPath());
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);

            int angle = 0;

            if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                angle = 90;
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
                angle = 180;
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
                angle = 270;
            }else if (orientation == ExifInterface.ORIENTATION_UNDEFINED) {
                angle = 0;
            }else if (orientation == ExifInterface.ORIENTATION_NORMAL) {
                angle = 0;
            }

            Matrix mat = new Matrix();
            mat.postRotate(angle);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2;

            Bitmap bmp = BitmapFactory.decodeStream(new FileInputStream(f),
                    null, options);
            bitmap = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(),
                    bmp.getHeight(), mat, true);
            ByteArrayOutputStream outstudentstreamOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100,
                    outstudentstreamOutputStream);


        } catch (IOException e) {
            Log.w("TAG", "-- Error in setting image");
        } catch (OutOfMemoryError oom) {
            Log.w("TAG", "-- OOM Error in setting image");
        }
        return bitmap;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_photo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private static Bitmap decodeFile(File f) {
        try {
            //decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);

            //Find the correct scale value. It should be the power of 2.
            final int REQUIRED_SIZE = 70;
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            while (true) {
                if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE)
                    break;
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }

            //decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {
        }
        return null;
    }


    private Bitmap getViewBitmap(View v) {
        v.clearFocus();
        v.setPressed(false);
        boolean willNotCache = v.willNotCacheDrawing();
        v.setWillNotCacheDrawing(false);
        // Reset the drawing cache background color to fully transparent
        // for the duration of this operation
        int color = v.getDrawingCacheBackgroundColor();
        v.setDrawingCacheBackgroundColor(0);
        if (color != 0) {
            v.destroyDrawingCache();
        }
        v.buildDrawingCache();
        Bitmap cacheBitmap = v.getDrawingCache();
        if (cacheBitmap == null) {
            Log.e("Log", "failed getViewBitmap(" + v + ")",
                    new RuntimeException());
            return null;
        }

        Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);

        // Restore the view
        v.destroyDrawingCache();
        v.setWillNotCacheDrawing(willNotCache);
        v.setDrawingCacheBackgroundColor(color);

        return bitmap;
    }

    private String SaveImage(Bitmap saveBM) {
        // String root = Environment.getExternalStorageDirectory().toString();
        String[] allsdcardpath = getStorageDirectories();
        File myDir;
        // if (allsdcardpath[1] != null) {
        // myDir = new File(allsdcardpath[1] + "/DCIM/Kissify"); // sdcard1
        // } else {
        myDir = new File(allsdcardpath[0] + "/DCIM/Velvetcase"); // sdcard0
        // }
        myDir.mkdirs();
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        String fname = "Image-" + n + ".jpg";

        // File file = new File(myDir, fname);
        File file = null;

            file = new File(myDir, fname);



        if (file.exists())
            file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            saveBM.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

            ContentValues image = new ContentValues();
            image.put(MediaStore.Images.Media.TITLE, "Kissify");
            image.put(MediaStore.Images.Media.DISPLAY_NAME, fname);
            image.put(MediaStore.Images.Media.DESCRIPTION, "App Image");
            image.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis());
            image.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
            image.put(MediaStore.Images.Media.ORIENTATION, 0);
            File parent = file.getParentFile();
            image.put(MediaStore.Images.ImageColumns.BUCKET_ID, parent.toString()
                    .toLowerCase().hashCode());
            image.put(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME, parent.getName()
                    .toLowerCase());
            image.put(MediaStore.Images.Media.SIZE, file.length());
            image.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());
            Uri result = getContentResolver().insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, image);

            Toast.makeText(getApplicationContext(),
            "File is Saved in  " + file, Toast.LENGTH_SHORT).show();



        } catch (Exception e) {
            e.printStackTrace();
            Toast savedToast = Toast.makeText(getApplicationContext(),
                    "not working", Toast.LENGTH_SHORT);
            savedToast.show();
        }
        return file.getAbsolutePath();
    }

    public static String[] getStorageDirectories() {
        final Set<String> rv = new HashSet<String>();
        final String rawExternalStorage = System.getenv("EXTERNAL_STORAGE");
        // All Secondary SD-CARDs (all exclude primary) separated by ":"
        final String rawSecondaryStoragesStr = System
                .getenv("SECONDARY_STORAGE");
        // Primary emulated SD-CARD
        final String rawEmulatedStorageTarget = System
                .getenv("EMULATED_STORAGE_TARGET");
        if (TextUtils.isEmpty(rawEmulatedStorageTarget)) {
            // Device has physical external storage; use plain paths.
            if (TextUtils.isEmpty(rawExternalStorage)) {
                // EXTERNAL_STORAGE undefined; falling back to default.
                rv.add("/storage/sdcard0");
            } else {
                rv.add(rawExternalStorage);
            }
        } else {
            // Device has emulated storage; external storage paths should have
            // userId burned into them.
            final String rawUserId;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
                rawUserId = "";
            } else {
                final String path = Environment.getExternalStorageDirectory()
                        .getAbsolutePath();
                final String[] folders = DIR_SEPORATOR.split(path);
                final String lastFolder = folders[folders.length - 1];
                boolean isDigit = false;
                try {
                    Integer.valueOf(lastFolder);
                    isDigit = true;
                } catch (NumberFormatException ignored) {
                }
                rawUserId = isDigit ? lastFolder : "";
            }
            // /storage/emulated/0[1,2,...]
            if (TextUtils.isEmpty(rawUserId)) {
                rv.add(rawEmulatedStorageTarget);
            } else {
                rv.add(rawEmulatedStorageTarget + File.separator + rawUserId);
            }
        }
        // Add all secondary storages
        if (!TextUtils.isEmpty(rawSecondaryStoragesStr)) {
            // All Secondary SD-CARDs splited into array
            final String[] rawSecondaryStorages = rawSecondaryStoragesStr
                    .split(File.pathSeparator);
            Collections.addAll(rv, rawSecondaryStorages);
        }
        return rv.toArray(new String[rv.size()]);
    }
}
