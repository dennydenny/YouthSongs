package ru.youthsongs.fragment;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import ru.youthsongs.R;

import static android.support.constraint.Constraints.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static boolean result = true;
    private SharedPreferences sp;
    private SwitchPreference switchPreference = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        this.sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        initChordsSwitchPreference();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Set up a listener whenever a key changes
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        // Set up a listener whenever a key changes
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(final SharedPreferences sharedPreferences, String key) {
        if (key.equals("showChords")) {

            //Toast.makeText(getActivity(), "Changed!", Toast.LENGTH_LONG).show();
        }
    }

    private void setShowChords(SwitchPreference switchPreference, boolean value) {
        SharedPreferences.Editor edit = sp.edit();
        edit.putBoolean("showChords", value);
        switchPreference.setChecked(value);
        edit.apply();
    }

    protected void somethingGoesWrong() {
        Toast.makeText(getActivity(),
                getResources().getString(R.string.pdfChordsExtractingFailed),
                Toast.LENGTH_LONG)
                .show();
        setShowChords(switchPreference, false);
    }

    private void initChordsSwitchPreference() {
        this.switchPreference = (SwitchPreference) findPreference("showChords");
        switchPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, final Object newValue) {
                //TODO: Implement case when chords already downloaded.
                // If new setting is turned on...
                if (newValue instanceof Boolean && ((Boolean) newValue) != sp.getBoolean("showChords", true)) {
                    final boolean isEnabled = (Boolean) newValue;
                    if (isEnabled) {
                        // Ask user only if chords not at phone yet.
                        if (!isChordsAlreadyOnPhone()) {
                            // Building the dialog because we need to upload chords at first.
                            new AlertDialog.Builder(getActivity())
                                    .setIcon(R.drawable.ic_cloud_download_black_24dp)
                                    .setTitle(R.string.chordsDownloadTitle)
                                    .setMessage(R.string.chordsDownloadDesc)
                                    .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            try {
                                                // Downloading archive with chords. After finish it's starts unzipping.
                                                if (!isChordsArchiveAlreadyDownloaded()) {
                                                    downdloadChords();
                                                }
                                                // Unzipping archive.

                                            /*
                                            1. Качаем архив
                                            2. Разархивируем его в отдельную папку.
                                            3. Удостоверяемся, что кол-во файлов нужное (сравниваем с кол-вом песен)
                                            4. Удаляем архив.
                                             */
                                                setShowChords(switchPreference, true);
                                            } catch (Exception e) {

                                            }
                                        }
                                    })
                                    // If no, then we turn switcher to off.
                                    .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            setShowChords(switchPreference, false);
                                        }
                                    })
                                    .create().show();
                            return false;
                        }
                    }
                }
                return true;
            }
        });
    }

    private boolean isChordsAlreadyOnPhone() {
        String filePath = getActivity().getFilesDir().toString();
        // Check only pdf files.
        int actualNumberOfFiles = new File(filePath).listFiles(
                (dir, name) -> name.toLowerCase().endsWith(".pdf")).length;
        int expectedNumberOfFiles = Integer.valueOf(getString(R.string.pdfChordsControlQuantity));
        return actualNumberOfFiles == expectedNumberOfFiles;
    }

    private void downdloadChords() {
        String url = getString(R.string.pdfChordsUrl) + getString(R.string.pdfChordsFileName);
        new DownloadHelper().execute(url);
    }

    private void unzip() {
        new UnzipTask().execute(
                getString(R.string.pdfChordsFileName),
                getActivity().getFilesDir().toString());
    }

    private boolean isChordsArchiveAlreadyDownloaded() {
        String filePath = getActivity().getFilesDir() + File.separator + getString(R.string.pdfChordsFileName);
        File archive = new File(filePath);
        return archive.exists();
    }

    private void deleteBrokenChordsArchive() {
        String filePath = getActivity().getFilesDir() + File.separator + getString(R.string.pdfChordsFileName);
        File archive = new File(filePath);
        archive.delete();
    }

    // Class for downloading file (http://www.androiddeft.com/android-download-file-from-url-with-progress-bar/)
    private class DownloadHelper extends AsyncTask<String, String, Boolean> {

        private ProgressDialog progressDialog;
        private String fileName;
        private String folder;
        private String location;

        /**
         * Before starting background thread
         * Show Progress Bar Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.progressDialog = new ProgressDialog(getActivity());
            this.progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            this.progressDialog.setCancelable(false);
            this.progressDialog.setTitle(R.string.pdfChordsDownloadingTitle);
            this.progressDialog.show();
        }

        /**
         * Downloading file in background thread
         */
        @Override
        protected Boolean doInBackground(String... f_url) {
            int count;
            InputStream input = null;
            OutputStream output = null;

            try {
                URL url = new URL(f_url[0]);
                URLConnection connection = url.openConnection();
                connection.connect();
                // getting file length
                int lengthOfFile = connection.getContentLength();


                // input stream to read file - with 8k buffer
                input = new BufferedInputStream(url.openStream(), 8192);

                //Extract file name from URL
                fileName = f_url[0].substring(f_url[0].lastIndexOf('/') + 1, f_url[0].length());

                //External directory path to save file
                folder = getActivity().getFilesDir() + File.separator;

                this.location = folder + fileName;

                // Output stream to write file
                output = new FileOutputStream(location);

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress("" + (int) ((total * 100) / lengthOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();

                return true;

            } catch (Exception e) {
                Log.e("Error: ", "Downloading chords", e);
                // Remove archive
                deleteBrokenChordsArchive();
            }
            return false;
        }

        /**
         * Updating progress bar
         */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            progressDialog.setProgress(Integer.parseInt(progress[0]));
        }

        @Override
        protected void onPostExecute(Boolean message) {
            // dismiss the dialog after the file was downloaded
            this.progressDialog.dismiss();
            if (!message) {
                somethingGoesWrong();
            } else {
                unzip();
            }
        }
    }

    // https://stackoverflow.com/questions/21061419/extracting-a-zip-file-in-apps-internal-storage-android
    private class UnzipTask extends AsyncTask<String, String, Boolean> {
        private ProgressDialog progressDialog;
        private String zipFile;
        private String location;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.progressDialog = new ProgressDialog(getActivity());
            this.progressDialog.setMessage("Распаковываем аккорды...");
            this.progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            this.progressDialog.setCancelable(false);
            this.progressDialog.show();
        }

        /**
         * Unzipping file in background thread
         */
        @Override
        protected Boolean doInBackground(String... params) {
            try {
                this.zipFile = params[0];
                this.location = params[1];
                FileInputStream fin = new FileInputStream(location + File.separator + this.zipFile);
                ZipInputStream zin = new ZipInputStream(fin);
                ZipEntry ze = null;
                while ((ze = zin.getNextEntry()) != null) {
                    Log.v("Decompress", "Unzipping " + ze.getName());

                    FileOutputStream fout =
                            new FileOutputStream(this.location + File.separator + ze.getName());

                    byte[] buffer = new byte[8192];
                    int len;
                    while ((len = zin.read(buffer)) != -1) {
                        fout.write(buffer, 0, len);
                    }
                    fout.close();
                    zin.closeEntry();
                }
                zin.close();
                return true;
            } catch (Exception e) {
                Log.e("Decompress", "unzip", e);
            }
            return false;
        }

        /**
         * Updating progress bar
         */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            //progressDialog.setProgress(Integer.parseInt(progress[0]));
        }

        @Override
        protected void onPostExecute(Boolean result) {
            //deleteBrokenChordsArchive();
            this.progressDialog.dismiss();

            // If something goes wrong
            if (!result) {
                somethingGoesWrong();
            }
        }
    }
}
