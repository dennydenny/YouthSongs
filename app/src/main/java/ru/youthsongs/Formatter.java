package ru.youthsongs;

import android.content.Context;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Lobzik on 06.02.2016.
 */
public class Formatter {



    /**
     * Makes a substring of a string bold.
     * @param text          Full text
     * @param textToBold    Text you want to make bold
     * @return              String with bold substring
     */

    public static SpannableStringBuilder makeSectionOfTextBold(String text, String textToBold){

        SpannableStringBuilder builder=new SpannableStringBuilder();


            if(textToBold.length() > 0 && !textToBold.trim().equals("")){

                //for counting start/end indexes
                String testText = text.toLowerCase(Locale.US);
                String testTextToBold = textToBold.toLowerCase(Locale.US);

                int startingIndex = testText.indexOf(testTextToBold);
                int endingIndex = startingIndex + testTextToBold.length();
                //for counting start/end indexes

                if(startingIndex < 0 || endingIndex <0){
                    return builder.append(text);
                }
                else if(startingIndex >= 0 && endingIndex >=0){

                    builder.append(text);
                    builder.setSpan(new StyleSpan(Typeface.BOLD), startingIndex, endingIndex, 0);
                }
            }else{
                return builder.append(text);
            }

        return builder;
    }

    /**
     * Makes a span of special substrings in a string (underline italic)
     * @param context          Context (need for getting resourses)
     * @param textToChange    String, where we wanna to span "special words"
     * @return              SpannableString with "special words"
     */
    public static SpannableString makeFormatForAuthors (Context context, String textToChange) {
        long timeout= System.currentTimeMillis();
        SpannableStringBuilder result = new SpannableStringBuilder();
        result.append(textToChange);
        result.setSpan(new StyleSpan(Typeface.ITALIC), 0, textToChange.length(), 0);

        String [] KeyWordsInAuthtorsText = context.getResources().getStringArray(R.array.KeyWordsInAuthtorsText);
                //Resources.getSystem().getStringArray(R.array.KeyWordsInAuthtorsText);

        for (String KeyWord : KeyWordsInAuthtorsText) {
            int startingIndex = textToChange.indexOf(KeyWord);
            int endingIndex = startingIndex + KeyWord.length();

                if (startingIndex > -1) {
                    Log.i("makeFormatForAuthors", "Wow! we fount next key word: " + KeyWord);
                    result.setSpan(new UnderlineSpan(), startingIndex, endingIndex, 0);
                }

        }
        timeout = System.currentTimeMillis() - timeout;
        Log.i("makeFormatForAuthors", "Time of makeFormatForAuthors " + timeout + " ms");
        return SpannableString.valueOf(result);

        }

    public static SpannableString makeFormatForText (Context context, String textToChange) {
        long timeout= System.currentTimeMillis();
        SpannableStringBuilder result = new SpannableStringBuilder();
        result.append(textToChange);

        String [] KeyWordsInSongsText = context.getResources().getStringArray(R.array.KeyWordsInSongsText);
        //Resources.getSystem().getStringArray(R.array.KeyWordsInAuthtorsText);



        for (String KeyWord : KeyWordsInSongsText) {
            int startingIndex = textToChange.indexOf(KeyWord);
            int endingIndex = startingIndex + KeyWord.length();

            if (startingIndex > -1) {
                Log.i("makeFormatForAuthors", "Wow! we fount next key word: " + KeyWord);
                result.setSpan(new UnderlineSpan(), startingIndex, endingIndex, 0);
                result.setSpan(new StyleSpan(Typeface.BOLD), startingIndex, endingIndex, 0);
            }
        }
        timeout = System.currentTimeMillis() - timeout;
        Log.i("makeFormatForText", "Time of makeFormatForText " + timeout + " ms");
        return SpannableString.valueOf(result);

    }

    public static SpannableString makeFormatForText_v2 (Context context, String textToChange) {
        long timeout= System.currentTimeMillis();
        SpannableStringBuilder result = new SpannableStringBuilder();

        final List<String> tagValues = new ArrayList<String>();
        Pattern TAG_REGEX = Pattern.compile("<(.+?)>");
        final Matcher matcher = TAG_REGEX.matcher(textToChange);
        while (matcher.find()) {
            tagValues.add(matcher.group(1));
        }

        /*
        int startingIndex;
        int endingIndex;

        while (startingIndex > 0 || endingIndex > 0) {
            startingIndex = textToChange.indexOf("<");
            endingIndex = startingIndex + 1;
        }
        */
        int count1 = textToChange.length() - textToChange.replace("<", "").length();
        int count2 = textToChange.length() - textToChange.replace(">", "").length();
        Log.i("makeFormatForText_v2", "count1 is " + count1 + " and count2 is " + count2);

        if (count1 > 0) {
            textToChange = textToChange.replace("<", "");
            textToChange = textToChange.replace(">", "");
            Log.i("makeFormatForText_v2", "< and > were deleted");
        }

        result.append(textToChange);

        //String [] KeyWordsInSongsText = context.getResources().getStringArray(R.array.KeyWordsInSongsText);
        //Resources.getSystem().getStringArray(R.array.KeyWordsInAuthtorsText);


        for (String KeyWord : tagValues) {
            int startingIndex = textToChange.indexOf(KeyWord);
            int endingIndex = startingIndex + KeyWord.length();

            if (startingIndex > -1) {
                Log.i("makeFormatForText_v2", "Wow! we fount next key word: " + KeyWord);
                Log.i("makeFormatForText_v2", "Wow! key word lenght: " + KeyWord.length());
                //result.setSpan(new UnderlineSpan(), startingIndex, endingIndex, 0);
                result.setSpan(new StyleSpan(Typeface.BOLD_ITALIC), startingIndex, endingIndex, 0);
            }
        }

        /*
        Pattern pattern1 = Pattern.compile("<");
        Pattern pattern2 = Pattern.compile(">");

            Matcher matcher1 = pattern1.matcher(result);
            while( matcher.find() )
            {
                int start = matcher1.start();
                int end = matcher1.end();
                result.delete(start, end);
            }

        Matcher matcher2 = pattern2.matcher(result);
        while( matcher.find() )
        {
            int start = matcher2.start();
            int end = matcher2.end();
            result.delete(start, end);
        }
        */


        timeout = System.currentTimeMillis() - timeout;
        Log.i("makeFormatForText_v2", "Time of makeFormatForText_v2 " + timeout + " ms");
        return SpannableString.valueOf(result);

    }


    };






