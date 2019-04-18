package es.udc.fic.muei.atopate.db;

import android.arch.persistence.room.TypeConverter;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;

public class Converters {
    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
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