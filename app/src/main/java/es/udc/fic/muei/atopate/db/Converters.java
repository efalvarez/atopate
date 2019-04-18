package es.udc.fic.muei.atopate.db;

import android.arch.persistence.room.TypeConverter;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Converters {
    @TypeConverter
    public static Calendar fromTimestamp(Long value) {
        if (value == null) {
            return null;
        } else {
            Calendar res = Calendar.getInstance();
            res.setTimeInMillis(value);
            return res;
        }
    }

    @TypeConverter
    public static Long calendarToTimestamp(Calendar date) {
        return date == null ? null : date.getTimeInMillis();
    }

    @TypeConverter
    public static List<LatLng> fromString(String value) {
        Type listType = new TypeToken<List<LatLng>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String listLatLngToString(List<LatLng> value) {
        Type listType = new TypeToken<List<LatLng>>() {}.getType();
        return new Gson().toJson(value, listType);
    }
}