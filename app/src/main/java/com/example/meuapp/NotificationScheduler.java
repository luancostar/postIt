package com.example.meuapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class NotificationScheduler {

    public static final String TAG_ALARME = "POSTIT_APP_DEBUG";

    public static void agendarNotificacao(Context context, Usuario tarefa) {
        if (tarefa.getDataEntrega() == null || tarefa.getDataEntrega().isEmpty()) {
            Log.d(TAG_ALARME, "Agendamento ignorado: tarefa '" + tarefa.getNome() + "' não tem data.");
            return;
        }

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.putExtra("titulo_tarefa", tarefa.getNome());
        int notificacaoId = tarefa.localId;
        intent.putExtra("notificacao_id", notificacaoId);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notificacaoId, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        try {
            Date dataEntrega = sdf.parse(tarefa.getDataEntrega());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dataEntrega);
            calendar.add(Calendar.DAY_OF_YEAR, -1); // 1 dia antes
            calendar.set(Calendar.HOUR_OF_DAY, 9);    // às 9h
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);

            if (calendar.getTimeInMillis() > System.currentTimeMillis()) {
                if (alarmManager != null) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                    Log.d(TAG_ALARME, "SUCESSO! Alarme agendado para a tarefa '" + tarefa.getNome() + "' em " + calendar.getTime().toString());
                    Toast.makeText(context, "Lembrete agendado!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.w(TAG_ALARME, "FALHA AO AGENDAR: O horário " + calendar.getTime().toString() + " já passou.");
            }
        } catch (ParseException e) {
            Log.e(TAG_ALARME, "Erro ao converter a data: " + tarefa.getDataEntrega(), e);
        }
    }

    public static void cancelarNotificacao(Context context, Usuario tarefa) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationReceiver.class);
        int notificacaoId = tarefa.localId;
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notificacaoId, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        if (alarmManager != null && pendingIntent != null) {
            alarmManager.cancel(pendingIntent);
            Log.d(TAG_ALARME, "Alarme CANCELADO para a tarefa '" + tarefa.getNome() + "'.");
        }
    }
}