package org.opentalking.drawskill;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.opentalking.drawskill.activity.MainActivity;


public class FileHelper {
    private static final String FILENAME_PATTERN = "sketch_%04d.png";
    private static final String CUR_FILE_NUM = "cur_file_num";

    private final MainActivity context;

    public FileHelper(MainActivity context) {
        this.context = context;
    }

    private File getSDDir() {
        String path = Environment.getExternalStorageDirectory()
                .getAbsolutePath() + "/sketcher_cha/";

        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }

        return file;
    }

    public Bitmap getSavedBitmap(String fileName) {
        if (!isStorageAvailable()) {
            return null;
        }

        File lastFile = new File(fileName);
        if (!lastFile.exists()) {
            return null;
        }

        Bitmap savedBitmap = null;

        try {
            FileInputStream fis = new FileInputStream(lastFile);
            savedBitmap = BitmapFactory.decodeStream(fis);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
        return savedBitmap;
    }

    private File getUniqueFilePath(File dir) {
        SharedPreferences preferences = context.getSharedPreferences(MainActivity.PREFS_NAME, Context.MODE_PRIVATE);
        int curFileNum = preferences.getInt(CUR_FILE_NUM, 0);

        int freeFileNum = findFreeFileNum(curFileNum + 1, dir);
        preferences.edit().putInt(CUR_FILE_NUM, freeFileNum).apply();

        return new File(dir, String.format(FILENAME_PATTERN, freeFileNum));
    }

    private int findFreeFileNum(int fileNum, File dir) {
        int result = fileNum;
        if (new File(dir, String.format(FILENAME_PATTERN, fileNum)).exists()) {
            result = findFreeFileNum(fileNum + 1, dir);
        }
        return result;
    }

    private boolean isStorageAvailable() {
        String externalStorageState = Environment.getExternalStorageState();
        if (!externalStorageState.equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(context, R.string.sd_card_is_not_available, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void share() {
        if (!isStorageAvailable()) {
            return;
        }

        new SaveTask() {
            protected void onPostExecute(Void aVoid) {
                Uri uri = Uri.fromFile(saveFile);

                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("image/png");
                i.putExtra(Intent.EXTRA_STREAM, uri);
                context.startActivity(Intent.createChooser(i,
                        context.getString(R.string.send_image_to)));

                super.onPostExecute(aVoid);
            }
        }.execute();
    }

    public void saveToSD() {
        if (!isStorageAvailable())
            return;
        new SaveTask().execute();
    }

    public boolean saveBitmap(File file) {
        try {
            Bitmap bitmap = context.getSurface().getBitmap();
            if (bitmap != null){
            	FileOutputStream fos = new FileOutputStream(file);
            	bitmap.compress(CompressFormat.PNG, 100, fos);
            	fos.close();
            	notifyMediaScanner(file);
            	return true;
			}
        } catch (FileNotFoundException e) {
        	e.printStackTrace();
        } catch (IOException e) {
			e.printStackTrace();
		}
       
        return false;
    }

    private void notifyMediaScanner(File file) {
        Uri uri = Uri.fromFile(file);
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
    }

    private class SaveTask extends AsyncTask<Void, Void, Void> {
        private ProgressDialog dialog;
        protected File saveFile;

        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(context, "", context.getString(R.string.saving_to_sd_please_wait), true);
            context.getSurface().getDrawThread().pauseDrawing();
        }

        @Override
        protected Void doInBackground(Void... none) {
            saveFile = getUniqueFilePath(getSDDir());
            if (!saveBitmap(saveFile))
                cancel(false);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            dialog.dismiss();
            Toast.makeText(context, context.getString(R.string.successfully_saved_to, saveFile.getAbsolutePath()), Toast.LENGTH_LONG).show();
            context.getSurface().getDrawThread().resumeDrawing();
        }

        @Override
        protected void onCancelled() {
            dialog.dismiss();
            Toast.makeText(context, context.getString(R.string.save_error), Toast.LENGTH_SHORT).show();
            context.getSurface().getDrawThread().resumeDrawing();
        }
    }

}
