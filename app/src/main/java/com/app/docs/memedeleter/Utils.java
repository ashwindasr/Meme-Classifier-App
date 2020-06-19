package com.app.docs.memedeleter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.MergeCursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.util.DisplayMetrics;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

class Utils {
    static final String KEY_ALBUM = "album_name";
    static final String KEY_PATH = "path";
    static final String KEY_TIMESTAMP = "timestamp";
    static final String KEY_TIME = "date";
    static final String KEY_COUNT = "date";

    private static final String TAG = "Utils";

    private static Classifier classifier;



    public static  boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }


    public static HashMap<String, String> mappingInbox(String album, String path, String timestamp, String time, String count)
    {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(KEY_ALBUM, album);
        map.put(KEY_PATH, path);
        map.put(KEY_TIMESTAMP, timestamp);
        map.put(KEY_TIME, time);
        map.put(KEY_COUNT, count);
        return map;
    }



    public static String getCount(Context c, String album_name)
    {
        Uri uriExternal = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        Uri uriInternal = MediaStore.Images.Media.INTERNAL_CONTENT_URI;

        String[] projection = { MediaStore.MediaColumns.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.MediaColumns.DATE_MODIFIED };
        Cursor cursorExternal = c.getContentResolver().query(uriExternal, projection, "bucket_display_name = \""+album_name+"\"", null, null);
        Cursor cursorInternal = c.getContentResolver().query(uriInternal, projection, "bucket_display_name = \""+album_name+"\"", null, null);
        Cursor cursor = new MergeCursor(new Cursor[]{cursorExternal,cursorInternal});


        return cursor.getCount()+" Photos";
    }

    public static String converToTime(String timestamp)
    {
        long datetime = Long.parseLong(timestamp);
        Date date = new Date(datetime);
        DateFormat formatter = new SimpleDateFormat("dd/MM HH:mm");
        return formatter.format(date);
    }

    public static float convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }

    public static Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        if(bm == null) return bm;
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }

    public static boolean isNotMeme(String path) {
        // Insert Code HERE
        // tflite.run(imgData, labelProbArray);
        // return new Random().nextBoolean();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);

        bitmap = getResizedBitmap(bitmap, 224, 224);

        List<Classifier.Recognition> results = classifier.recognizeImage(bitmap);

        if (results != null && results.size() >= 1) {
            Classifier.Recognition recognition = results.get(0);
            if (recognition != null) {
                if (recognition.getTitle() != null) {
                    Log.d(TAG, "Score: " + recognition.getConfidence()/10.0);
                    if (recognition.getConfidence() > 0) {
                        return true;
                    } else {
                        return false;
                    }
//                    return (recognition.getTitle().equals("Memes"));
                }
            }
        }

        return false;

        // Classifier.Recognition recognition = results.get(0);
        // Log.d(TAG, "isMeme: " + recognition.getTitle());
        // return recognition.getTitle().equals("Meme");
    }

    public static void loadClassifier(Activity activity) throws IOException {
        classifier = new ClassifierFloatMobileNet(activity, Classifier.Device.CPU, 4);
    }

    static void moveFile(Context context, String inputPath, String outputPath) {

        Log.d(TAG, "moveFile() called with: inputPath = [" + inputPath + "], outputPath = [" + outputPath + "]");

        // if(true) return;
        InputStream in = null;
        OutputStream out = null;
        try {

            // //create output directory if it doesn't exist
            // File dir = new File (outputPath);
            // if (!dir.exists())
            // {
            //     dir.mkdirs();
            // }


            in = new FileInputStream(inputPath);
            out = new FileOutputStream(outputPath);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;

            // write the output file
            out.flush();
            out.close();
            out = null;

            // delete the original file
            new File(inputPath).delete();

            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            intent.setData(Uri.fromFile(new File(outputPath)));
            context.sendBroadcast(intent);

            Log.d(TAG, "moveFile: File : " + inputPath + " MOVED");

        }

        catch (FileNotFoundException fnfe1) {
            Log.e("tag", fnfe1.getMessage());
        }
        catch (Exception e) {
            Log.e("tag", e.getMessage());
        }

    }

    public static String getFileName(String srcPath, String destPath) {

        String name = srcPath.substring(srcPath.lastIndexOf("/") + 1);
        String baseName = name.substring(0, name.lastIndexOf('.'));
        String extn = name.substring(name.lastIndexOf('.') + 1);
        int counter = 1;
        File dest = new File(destPath, name);
        while (dest.exists()){
            name = baseName + " (" + counter + ")." + extn;
            counter++;
            dest = new File(destPath, name);
        }
        return name;
    }

    public static void createMemeDir() {
        File file = new File(Environment.getExternalStorageDirectory(), "Memes_");
        Log.d(TAG, "createMemeDir: " + file.getAbsolutePath());
        if(file.exists()) return;
        else{
            file.mkdir();
        }
    }
}
