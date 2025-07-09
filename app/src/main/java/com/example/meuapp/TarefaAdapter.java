package com.example.meuapp;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TarefaAdapter extends RecyclerView.Adapter<TarefaAdapter.TarefaViewHolder> {

    private final List<Usuario> tarefas = new ArrayList<>();
    private final OnItemClickListener listener;

    // ✅ INTERFACE SIMPLIFICADA
    public interface OnItemClickListener {
        void onCardLongClick(Usuario tarefa); // Única ação de clique necessária
    }

    public TarefaAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setTarefas(List<Usuario> novasTarefas) {
        this.tarefas.clear();
        this.tarefas.addAll(novasTarefas);
        notifyDataSetChanged();
    }

    public Usuario getTarefaAt(int position) {
        return tarefas.get(position);
    }

    @NonNull
    @Override
    public TarefaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tarefa, parent, false);
        return new TarefaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TarefaViewHolder holder, int position) {
        holder.bind(tarefas.get(position));
    }

    @Override
    public int getItemCount() {
        return tarefas.size();
    }

    class TarefaViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitulo, textViewObservacao, textViewDiaEntrega, textViewMesEntrega, textViewDataFinalizacao;
        ImageView iconeAlerta, iconeConclusao;
        ConstraintLayout cardBody;

        public TarefaViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitulo = itemView.findViewById(R.id.textViewTitulo);
            textViewObservacao = itemView.findViewById(R.id.textViewObservacao);
            textViewDiaEntrega = itemView.findViewById(R.id.textViewDiaEntrega);
            textViewMesEntrega = itemView.findViewById(R.id.textViewMesEntrega);
            textViewDataFinalizacao = itemView.findViewById(R.id.textViewDataFinalizacao);
            iconeAlerta = itemView.findViewById(R.id.iconeAlerta);
            iconeConclusao = itemView.findViewById(R.id.iconeConclusao);
            cardBody = itemView.findViewById(R.id.card_body);
        }

        public void bind(final Usuario tarefa) {
            boolean isChecked = tarefa.getEmail() != null && tarefa.getEmail().startsWith("[OK]");

            textViewTitulo.setText(tarefa.getNome());
            textViewObservacao.setText(isChecked ? tarefa.getEmail().substring(5) : tarefa.getEmail());

            if (tarefa.getDataEntrega() != null && !tarefa.getDataEntrega().isEmpty()) {
                try {
                    SimpleDateFormat formatoEntrada = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    Date data = formatoEntrada.parse(tarefa.getDataEntrega());
                    String dia = new SimpleDateFormat("dd", Locale.getDefault()).format(data);
                    String mes = new SimpleDateFormat("MMM", Locale.getDefault()).format(data);
                    mes = mes.substring(0, 1).toUpperCase() + mes.substring(1).toLowerCase();
                    textViewDiaEntrega.setText(dia);
                    textViewMesEntrega.setText(mes);
                } catch (ParseException e) {
                    textViewDiaEntrega.setText("--");
                    textViewMesEntrega.setText("");
                }
                iconeAlerta.setVisibility(isHoje(tarefa.getDataEntrega()) && !isChecked ? View.VISIBLE : View.GONE);
            } else {
                textViewDiaEntrega.setText("--");
                textViewMesEntrega.setText("");
                iconeAlerta.setVisibility(View.GONE);
            }

            if (isChecked && tarefa.getDataFinalizacao() != null && !tarefa.getDataFinalizacao().isEmpty()) {
                textViewDataFinalizacao.setText("Finalizado em " + tarefa.getDataFinalizacao());
                textViewDataFinalizacao.setVisibility(View.VISIBLE);
                iconeConclusao.setVisibility(View.VISIBLE);
            } else {
                textViewDataFinalizacao.setVisibility(View.GONE);
                iconeConclusao.setVisibility(View.GONE);
            }

            updateStrikethrough(isChecked);

            // ✅ PRESSIONAR O CARD AGORA ABRE A EDIÇÃO
            cardBody.setOnLongClickListener(v -> {
                listener.onCardLongClick(tarefa);
                return true; // Retorna true para consumir o evento
            });

            // ❌ Listener de clique simples foi removido
        }

        private void updateStrikethrough(boolean isChecked) {
            if (isChecked) {
                textViewTitulo.setPaintFlags(textViewTitulo.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                itemView.setAlpha(0.7f);
            } else {
                textViewTitulo.setPaintFlags(textViewTitulo.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                itemView.setAlpha(1.0f);
            }
        }

        private boolean isHoje(String dataTarefa) {
            if(dataTarefa == null || dataTarefa.isEmpty()) return false;
            return new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date()).equals(dataTarefa);
        }
    }
}