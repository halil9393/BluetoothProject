package com.example.bluetoothproject.data;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.bluetoothproject.models.ConnectedDeviceModel;
import com.example.bluetoothproject.models.FondDeviceModel;
import com.metinozcura.rickandmorty.data.db.converter.SourceTypeConverter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Database(entities = {ConnectedDeviceModel.class, FondDeviceModel.class}, version = 1, exportSchema = false)
@TypeConverters(SourceTypeConverter.class)
public abstract class BluetoothHistoryDB2 extends RoomDatabase {

    public abstract DevicesDao devicesDao();

    private static volatile BluetoothHistoryDB2 INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static BluetoothHistoryDB2 getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (BluetoothHistoryDB2.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            BluetoothHistoryDB2.class,
                            "bluetoothdevices_database") //Not: Telefonda hangi isimde tutulacağını yazdık
                            .build();
                }
            }
        }
        return INSTANCE;
    }

}
