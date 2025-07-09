package com.example.meuapp;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TarefaAdapter extends RecyclerView.Adapter<TarefaAdapter.TarefaViewHolder> {

    private List<Usuario> tarefas = new ArrayList<>();
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Usuario tarefa); // Para edição no clique longo
        void onDeleteClick(Usuario tarefa);
        // O onStatusChange não é mais necessário na interface, pois é tratado pelo swipe na MainActivity
    }

    public TarefaAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setTarefas(List<Usuario> novasTarefas) {
        this.tarefas = novasTarefas;
        notifyDataSetChanged();
    }

    // Método para o gesto de arrastar na MainActivity
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
        Usuario tarefaAtual = tarefas.get(position);
        holder.bind(tarefaAtual, listener);
    }

    @Override
    public int getItemCount() {
        return tarefas.size();
    }

    // --- ViewHolder Interno ---
    class TarefaViewHolder extends RecyclerView.ViewHolder {
        // Componentes do novo layout item_tarefa.xml
        TextView textViewTitulo, textViewObservacao, textViewDataEntrega;
        ImageView iconeAlerta;
        ImageButton btnExcluir;
        ProgressBar progressBar;

        public TarefaViewHolder(@NonNull View itemView) {
            super(itemView);
            // Linkando os componentes do layout
            textViewTitulo = itemView.findViewById(R.id.textViewTitulo);
            textViewObservacao = itemView.findViewById(R.id.textViewObservacao);
            btnExcluir = itemView.findViewById(R.id.btnExcluir);
            textViewDataEntrega = itemView.findViewById(R.id.textViewDataEntrega); // NOVO
            iconeAlerta = itemView.findViewById(R.id.iconeAlerta);             // NOVO
            progressBar = itemView.findViewById(R.id.progressBar);             // NOVO
        }

        public void bind(final Usuario tarefa, final OnItemClickListener listener) {
            // Mapeando: nome -> título, email -> observação
            textViewTitulo.setText(tarefa.getNome());

            // Lógica para status e observação
            boolean isChecked = tarefa.getEmail() != null && tarefa.getEmail().startsWith("[OK]");
            if (isChecked) {
                // Se concluída, remove o prefixo para exibir a observação limpa
                textViewObservacao.setText(tarefa.getEmail().substring(5));
            } else {
                textViewObservacao.setText(tarefa.getEmail());
            }

            // --- LÓGICA PARA EXIBIR A DATA E O ALERTA ---
            if (tarefa.getDataEntrega() != null && !tarefa.getDataEntrega().isEmpty()) {
                textViewDataEntrega.setText(tarefa.getDataEntrega());
                // Verifica se a data da tarefa é hoje para mostrar o alerta
                if (isHoje(tarefa.getDataEntrega())) {
                    iconeAlerta.setVisibility(View.VISIBLE);
                } else {
                    iconeAlerta.setVisibility(View.GONE);
                }
            } else {
                textViewDataEntrega.setText("Sem data");
                iconeAlerta.setVisibility(View.GONE);
            }

            // Atualiza o efeito de riscado no texto e a opacidade
            updateStrikethrough(isChecked);

            itemView.setOnClickListener(v -> {
                // Verifica se a observação está visível
                boolean isVisible = textViewObservacao.getVisibility() == View.VISIBLE;
                // Inverte a visibilidade
                textViewObservacao.setVisibility(isVisible ? View.GONE : View.VISIBLE);
            });

            // --- LISTENERS ---
            // Clique longo para editar
            itemView.setOnLongClickListener(v -> {
                listener.onItemClick(tarefa);
                return true;
            });

            // Botão de excluir
            btnExcluir.setOnClickListener(v -> listener.onDeleteClick(tarefa));
        }

        private void updateStrikethrough(boolean isChecked) {
            if (isChecked) {
                textViewTitulo.setPaintFlags(textViewTitulo.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                itemView.setAlpha(0.6f); // Deixa o card inteiro um pouco transparente
            } else {
                textViewTitulo.setPaintFlags(textViewTitulo.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                itemView.setAlpha(1.0f); // Restaura a opacidade normal
            }
        }

        // Função auxiliar para verificar se a data é hoje
        private boolean isHoje(String dataTarefa) {
            if(dataTarefa == null || dataTarefa.isEmpty()) return false;

            // Pega a data de hoje no formato dd/MM/yyyy
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String dataDeHoje = sdf.format(new Date());

            return dataDeHoje.equals(dataTarefa);
        }
    }
}