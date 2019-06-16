package ru.youthsongs.util;

import android.content.Context;

import java.io.File;

import ru.youthsongs.R;

public class FileHepler {

    // Method which retrieves pdf chords by song number.
    public static File getChordsFileByNumber(Context context, String number) {
        String fileName = number + ".pdf";
        String filePath = context.getApplicationContext().getFilesDir() + File.separator + fileName;
        File file = new File(filePath);
        if (file.exists()) return file;
        return null;
    }
}
