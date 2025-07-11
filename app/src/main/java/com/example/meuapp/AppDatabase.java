package com.example.meuapp;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Usuario.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract TarefaDao tarefaDao();

    private static volatile AppDatabase INSTANCE;
    // Executor para rodar operações do banco em segundo plano
    public static final ExecutorService databaseWriteExecutor =
            Executors.newSingleThreadExecutor();

    public static AppDatabase getDatabase(final Context context) {

        // Dentro do método getDatabase
        INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                        AppDatabase.class, "tarefas_database")
                // ✅ ADICIONE ESTA LINHA
                .fallbackToDestructiveMigration()
                .build();


        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "tarefas_database")
                            .build();
                }
            }
        }

        return INSTANCE;
    }
}