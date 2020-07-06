package com.alat.helpers;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.core.app.ActivityCompat;
import androidx.core.util.Predicate;

import com.alat.BuildConfig;

import org.json.JSONArray;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created by Mbariah on 6/24/18.
 */
public class Utils {




    //Ensures class is never instantiated
    public Utils() {
    }
    public static String getPathFromURI(Uri contentUri, Context context) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
        if (Objects.requireNonNull(cursor).moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }

   /* public static SharedPreferences fetchPrefs(@NonNull Context context) {
        return context.getSharedPreferences("user_status",Context.MODE_PRIVATE);
    }*/

    public static SharedPreferences userDetails(@NonNull Context context) {
        return context.getSharedPreferences("user_data", Context.MODE_PRIVATE);
    }

    public static SharedPreferences firebaseID(@NonNull Context context) {
        return context.getSharedPreferences("cloud_token_id", Context.MODE_PRIVATE);
    }


    //Works when called from an Activity
    public static void hideKeyboardinActivity(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    //Works in fragments
    public static void hideKeyboardinFragments(Context context, View view) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    public static int getTime(long seconds) {

        /*long hours = TimeUnit.SECONDS.toHours(seconds) - (day *24);
        long minute = TimeUnit.SECONDS.toMinutes(seconds) - (TimeUnit.SECONDS.toHours(seconds)* 60);
        long second = TimeUnit.SECONDS.toSeconds(seconds) - (TimeUnit.SECONDS.toMinutes(seconds) *60);
        */

        return (int) TimeUnit.SECONDS.toDays(seconds);

    }

    public static String getProperNumber(String number, String countryCode) {

        String first = number.substring(0, Math.min(number.length(), 3));

        if (first.equalsIgnoreCase(countryCode)) {
            return number;
        } else {
            return number.replaceAll("[^0-9+]", "")        //remove all the non numbers (brackets dashes spaces etc.) except the + signs
                    .replaceAll("(^[1-9].+)", countryCode + "$1") //if the number is starting with no zero and +, its a local number. prepend cc
                    .replaceAll("(.)(\\++)(.)", "$1$3")         //if there are left out +'s in the middle by mistake, remove them
                    .replaceAll("(^0{2}|^\\+)(.+)", "$2")       //make 00XXX... numbers and +XXXXX.. numbers into XXXX...
                    .replaceAll("^0([1-9])", countryCode + "$1");         //make 0XXXXXXX numbers into CCXXXXXXXX numbers
        }

    }

    public static String betterDate(String _date) {
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(_date);
            return new SimpleDateFormat("dd MMM, yy", Locale.ENGLISH).format(date);
        } catch (ParseException pe) {
            return "N/A";
        }
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public static String simplerDate(String _date) {
        //2017-04-27 13:35:12
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(_date);
            return new SimpleDateFormat("MMM dd ''yy, HH:mm", Locale.ENGLISH).format(date);
        } catch (ParseException pe) {
            //Log.i(Utils.class.getSimpleName(), "simplerDate: " + pe);
            return "N/A";
        }
    }

    public static String formatDate(Date format_me) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return formatter.format(format_me);
    }

    public static String simplifiedDate(Date format_me) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMMM, yyyy", Locale.getDefault());
        return formatter.format(format_me);
    }

    public static String formatDateFilters(Date format_me) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.getDefault());
        return formatter.format(format_me);
    }

    public static boolean checkIfEmptyString(String data) {
        //True when Empty,
        //False when NOT empty
        boolean result = false;
        if (data != null && !data.isEmpty() && !data.equals("null")) {
            result = true;
        }
        return !result;
    }


    public static JSONArray deletePosition(JSONArray array, int position) {

        JSONArray temp_list = new JSONArray();
        try {
            int len = array.length();
            if (array.length() != 0) {
                for (int i = 0; i < len; i++) {
                    //Excluding the item at position
                    if (i != position) {
                        temp_list.put(array.get(i));
                    }
                }
            }

        } catch (Exception e) {
            Log.e("ERROR", e + "");

        }

        return temp_list;
    }

    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = new HashSet<>();
        return t -> seen.add(keyExtractor.apply(t));
    }

    public static String base64Encode(final byte[] input) {
        return Base64.encodeToString(input, Base64.DEFAULT);
    }

    private static byte[] base64Decode(String input) {
        return Base64.decode(input, Base64.DEFAULT);
    }

/*
    public void hideMaterialKeyboard(DialogInterface di, final MaterialDialog.Builder builder) {
        final MaterialDialog dialog = (MaterialDialog) di;
        if (dialog.getInputEditText() == null) return;
        dialog.getInputEditText().post(new Runnable() {
            @Override
            public void run() {
                dialog.getInputEditText().requestFocus();
                InputMethodManager imm = (InputMethodManager) builder.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(dialog.getInputEditText().getWindowToken(), 0);
                }
            }
        });
    }
*/


    public static List<Integer> stepsList(int min, int max, int steps) {

        List<Integer> intList = new ArrayList<>();
        int gap = (max - min) / steps;

        for (int i = min; i < max; i++) {

            int value = ((i + 99) / 100) * 100;
            intList.add(value);
            i = value + gap;
        }

        intList.add(max);

        return intList;
    }


//    public static Intent verifyOwner(Context context) {
//        return new Intent(context, LockScreenAuth.class);
//    }

    public static Cursor createCursor(Context context) {

        String DISPLAY_NAME = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY;
        String HAS_PHONE_NUMBER = ContactsContract.CommonDataKinds.Phone.HAS_PHONE_NUMBER;

        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Phone._ID,
                DISPLAY_NAME,
                //ContactsContract.CommonDataKinds.Phone.PHOTO_URI,
                //ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI,
                HAS_PHONE_NUMBER,
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                //ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                ContactsContract.CommonDataKinds.Phone.TYPE,
                ContactsContract.CommonDataKinds.Phone.LABEL,
                ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER,

        };

        ContentResolver mResolver = context.getContentResolver();

        return mResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                PROJECTION,
                null,
                null,
                null //ContactsContract.CommonDataKinds.Phone._ID
        );
    }

    public static Double round2(Double val) {
        return new BigDecimal(val.toString()).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

   /* public static String encrypt(Context context, String entity, byte[] plainTextBytes) {
        KeyChain keyChain = new SharedPrefsBackedKeyChain(context, CryptoConfig.KEY_256);
        keyChain.destroyKeys(); //GET A NEW KEY
        try {
            //recreate keys if any
            keyChain.getCipherKey();
            //byte[] plainTextBytes = plainText.getBytes("UTF-8");
            Crypto crypto = AndroidConceal.get().createDefaultCrypto(keyChain);
            if (!crypto.isAvailable()) {
                throw new RuntimeException("Facebook Conceal Library Error");
            }

            if (BuildConfig.DEBUG) {
                Log.i(Utils.class.getSimpleName(), "CipherKey: " + Base64.encodeToString(keyChain.getCipherKey(), 0));
            }

            byte[] cipherText = crypto.encrypt(plainTextBytes, Entity.create(entity));
            return Base64.encodeToString(cipherText, Base64.DEFAULT);
        } catch (Exception e) {
            throw new RuntimeException("Facebook Conceal Library Error");
        }
    }


    public static void decrypt(Context context, String entity, String cipherTextAsEncodedBase64String) {
        KeyChain keyChain = new SharedPrefsBackedKeyChain(context, CryptoConfig.KEY_256);
        Crypto crypto = AndroidConceal.get().createDefaultCrypto(keyChain);
        if (!crypto.isAvailable()) {
            throw new RuntimeException("Facebook Conceal Library Error");
        }
        try {
            byte[] cipherTextBytes = Base64.decode(cipherTextAsEncodedBase64String, Base64.DEFAULT);
            byte[] decryptedText = crypto.decrypt(cipherTextBytes, Entity.create(entity));
            //return new String(decryptedText, "UTF-8");

            try {
                FileOutputStream outputStream = context.openFileOutput("de_data", Context.MODE_PRIVATE);
                outputStream.write(decryptedText);
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            throw new RuntimeException("Facebook Conceal Library Error");
        }

    }*/

}





