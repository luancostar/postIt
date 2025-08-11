package com.example.meuapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;

public class NotificationReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "TAREFA_REMINDER_CHANNEL";
    private static final String CHANNEL_NAME = "Lembretes de Tarefas";
    private static final String CHANNEL_DESCRIPTION = "Notificações para lembrar das tarefas próximas do vencimento";

    @Override
    public void onReceive(Context context, Intent intent) {
        String tituloTarefa = intent.getStringExtra("titulo_tarefa");
        int notificacaoId = intent.getIntExtra("notificacao_id", 0);

        if (tituloTarefa != null) {
            criarCanalDeNotificacao(context);
            exibirNotificacao(context, tituloTarefa, notificacaoId);
        }
    }

    private void criarCanalDeNotificacao(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            
            if (notificationManager.getNotificationChannel(CHANNEL_ID) == null) {
                NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
                );
                channel.setDescription(CHANNEL_DESCRIPTION);
                channel.enableVibration(true);
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private void exibirNotificacao(Context context, String tituloTarefa, int notificacaoId) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        
        // Intent para abrir o app quando a notificação for tocada
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, notificacaoId, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_calendar)
            .setContentTitle("📋 Lembrete de Tarefa")
            .setContentText("A tarefa \"" + tituloTarefa + "\" vence amanhã!")
            .setStyle(new NotificationCompat.BigTextStyle().bigText("A tarefa \"" + tituloTarefa + "\" vence amanhã! Não se esqueça de finalizá-la."))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setVibrate(new long[]{0, 500, 250, 500})
            .setContentIntent(pendingIntent);

        notificationManager.notify(notificacaoId, builder.build());
    }
}
