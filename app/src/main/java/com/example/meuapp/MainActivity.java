package com.example.meuapp;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements TarefaAdapter.OnItemClickListener {

    // Componentes da UI
    private RecyclerView recyclerViewTarefas;
    private FloatingActionButton fabAdicionar;
    private TarefaAdapter adapter;
    private TextView textViewContadorAndamento, textViewContadorConcluidas;

    // --- NOVOS COMPONENTES DE PERFIL ---
    private CircleImageView profileImage;
    private TextView profileName;
    private SharedPreferences sharedPreferences;
    private ActivityResultLauncher<Intent> galleryLauncher;

    // Firebase
    private DatabaseReference databaseUsuarios;
    private List<Usuario> listaTarefas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // --- Inicialização dos Componentes ---
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false); // Desabilita o título padrão

        databaseUsuarios = FirebaseDatabase.getInstance().getReference("usuarios");
        textViewContadorAndamento = findViewById(R.id.textViewContadorAndamento);
        textViewContadorConcluidas = findViewById(R.id.textViewContadorConcluidas);
        recyclerViewTarefas = findViewById(R.id.recyclerViewTarefas);
        fabAdicionar = findViewById(R.id.fabAdicionar);

        // --- INICIALIZAÇÃO DO PERFIL ---
        profileImage = findViewById(R.id.profile_image);
        profileName = findViewById(R.id.profile_name);
        sharedPreferences = getSharedPreferences("PerfilApp", Context.MODE_PRIVATE);

        // --- Configuração do RecyclerView ---
        listaTarefas = new ArrayList<>();
        adapter = new TarefaAdapter(this);
        recyclerViewTarefas.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewTarefas.setAdapter(adapter);

        // Carrega nome e foto salvos
        carregarPerfil();

        // --- CONFIGURAÇÃO DOS LISTENERS ---
        fabAdicionar.setOnClickListener(v -> abrirDialogTarefa(null));
        profileName.setOnClickListener(v -> abrirDialogNome());

        // --- LÓGICA PARA PEGAR IMAGEM DA GALERIA ---
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        profileImage.setImageURI(imageUri);
                        // Salva o caminho da imagem para uso futuro
                        salvarUriDaFoto(imageUri.toString());
                    }
                });

        profileImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            galleryLauncher.launch(intent);
        });

        configurarGestoDeArrastar();
    }

    @Override
    protected void onStart() {
        super.onStart();
        databaseUsuarios.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listaTarefas.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    listaTarefas.add(postSnapshot.getValue(Usuario.class));
                }
                Collections.reverse(listaTarefas);
                adapter.setTarefas(listaTarefas);
                atualizarContadores();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "Falha ao carregar tarefas.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // --- MÉTODOS DE PERFIL ---
    private void carregarPerfil() {
        String nomeSalvo = sharedPreferences.getString("USER_NAME", "Seu Nome");
        String uriFotoSalva = sharedPreferences.getString("USER_PHOTO_URI", null);

        profileName.setText("Olá, " + nomeSalvo + "! 👋");
        if (uriFotoSalva != null) {
            profileImage.setImageURI(Uri.parse(uriFotoSalva));
        }
    }

    private void salvarNome(String nome) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("USER_NAME", nome);
        editor.apply();
    }

    private void salvarUriDaFoto(String uriString) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("USER_PHOTO_URI", uriString);
        editor.apply();
    }

    private void abrirDialogNome() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Digite seu nome");

        View viewInflated = LayoutInflater.from(this).inflate(R.layout.dialog_input_nome, (ViewGroup) findViewById(android.R.id.content), false);
        final EditText input = viewInflated.findViewById(R.id.editTextNomeDialog);
        builder.setView(viewInflated);

        builder.setPositiveButton("Salvar", (dialog, which) -> {
            String nome = input.getText().toString();
            if (!nome.isEmpty()) {
                salvarNome(nome);
                profileName.setText("Olá, " + nome + "! 👋");
            }
        });
        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());
        builder.show();
    }


    // --- MÉTODOS DE TAREFAS (abrirDialogTarefa, adicionarTarefa, etc.) ---
    // (O resto do seu código de tarefas continua aqui, sem alterações)
    // ...
    // colar aqui o resto dos seus metodos de tarefa: abrirDialogTarefa, adicionarTarefa, etc.

    private void abrirDialogTarefa(final Usuario tarefa) {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_tarefa, null);

        final EditText editTextTitulo = dialogView.findViewById(R.id.editTextTituloDialog);
        final EditText editTextObservacao = dialogView.findViewById(R.id.editTextObservacaoDialog);
        final EditText editTextData = dialogView.findViewById(R.id.editTextDataDialog);

        editTextData.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int ano = calendar.get(Calendar.YEAR);
            int mes = calendar.get(Calendar.MONTH);
            int dia = calendar.get(Calendar.DAY_OF_MONTH);

            new DatePickerDialog(MainActivity.this, (view, year, month, dayOfMonth) -> {
                String dataFormatada = String.format("%02d/%02d/%d", dayOfMonth, (month + 1), year);
                editTextData.setText(dataFormatada);
            }, ano, mes, dia).show();
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        builder.setTitle(tarefa == null ? "Nova Tarefa" : "Editar Tarefa");

        if (tarefa != null) {
            editTextTitulo.setText(tarefa.getNome());
            String observacao = tarefa.getEmail() != null && tarefa.getEmail().startsWith("[OK] ") ? tarefa.getEmail().substring(5) : tarefa.getEmail();
            editTextObservacao.setText(observacao);
            editTextData.setText(tarefa.getDataEntrega());
        }

        builder.setPositiveButton(tarefa == null ? "Adicionar" : "Salvar", (dialog, which) -> {
            String titulo = editTextTitulo.getText().toString().trim();
            String observacao = editTextObservacao.getText().toString().trim();
            String data = editTextData.getText().toString().trim(); // Pega a data

            if (TextUtils.isEmpty(titulo)) {
                Toast.makeText(this, "O título é obrigatório.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (tarefa == null) {
                // Adiciona uma nova tarefa, passando a data
                adicionarTarefa(titulo, observacao, data);
            } else {
                // Atualiza uma tarefa existente, passando a data
                boolean isChecked = tarefa.getEmail() != null && tarefa.getEmail().startsWith("[OK] ");
                if (isChecked) {
                    observacao = "[OK] " + observacao;
                }
                atualizarTarefa(tarefa.getId(), titulo, observacao, data);
            }
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    private void adicionarTarefa(String titulo, String observacao, String data) {
        String id = databaseUsuarios.push().getKey();
        // Mapeamento: Título -> nome, Observação -> email, Data -> dataEntrega
        Usuario novaTarefa = new Usuario(id, titulo, observacao, data); // CORRIGIDO

        if (id != null) {
            databaseUsuarios.child(id).setValue(novaTarefa)
                    .addOnSuccessListener(aVoid -> Toast.makeText(MainActivity.this, "Tarefa adicionada!", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(MainActivity.this, "Erro ao adicionar.", Toast.LENGTH_SHORT).show());
        }
    }

    private void atualizarTarefa(String id, String titulo, String observacao, String data) {
        Usuario tarefaAtualizada = new Usuario(id, titulo, observacao, data); // CORRIGIDO
        databaseUsuarios.child(id).setValue(tarefaAtualizada)
                .addOnSuccessListener(aVoid -> Toast.makeText(MainActivity.this, "Tarefa atualizada!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(MainActivity.this, "Erro ao atualizar.", Toast.LENGTH_SHORT).show());
    }

    // --- NOVO: Método para configurar o gesto de arrastar ---
    private void configurarGestoDeArrastar() {
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false; // Não usamos a função de mover
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Usuario tarefa = adapter.getTarefaAt(position);

                boolean isChecked = tarefa.getEmail() != null && tarefa.getEmail().startsWith("[OK]");
                // Inverte o status: se está concluída, reabre; se está aberta, conclui.
                onStatusChange(tarefa, !isChecked);

                // Notifica o adapter para redesenhar o item, mostrando a mudança de status (ex: texto riscado)
                adapter.notifyItemChanged(position);
            }
        };

        new ItemTouchHelper(simpleCallback).attachToRecyclerView(recyclerViewTarefas);
    }

    // --- NOVO: Método para contar e atualizar os cards de resumo ---
    private void atualizarContadores() {
        int contadorAndamento = 0;
        int contadorConcluidas = 0;

        for (Usuario tarefa : listaTarefas) {
            if (tarefa.getEmail() != null && tarefa.getEmail().startsWith("[OK]")) {
                contadorConcluidas++;
            } else {
                contadorAndamento++;
            }
        }
        textViewContadorAndamento.setText(String.valueOf(contadorAndamento));
        textViewContadorConcluidas.setText(String.valueOf(contadorConcluidas));
    }


    // --- Implementação dos Métodos da Interface do Adapter ---
    @Override
    public void onItemClick(Usuario tarefa) {
        // Ao clicar longo em um item, abre o dialog para edição
        abrirDialogTarefa(tarefa);
    }

    @Override
    public void onDeleteClick(Usuario tarefa) {
        new AlertDialog.Builder(this)
                .setTitle("Excluir Tarefa")
                .setMessage("Você tem certeza que deseja excluir esta tarefa?")
                .setPositiveButton("Sim", (dialog, which) -> {
                    if (tarefa.getId() != null) {
                        databaseUsuarios.child(tarefa.getId()).removeValue()
                                .addOnSuccessListener(aVoid -> Toast.makeText(MainActivity.this, "Tarefa excluída.", Toast.LENGTH_SHORT).show())
                                .addOnFailureListener(e -> Toast.makeText(MainActivity.this, "Erro ao excluir.", Toast.LENGTH_SHORT).show());
                    }
                })
                .setNegativeButton("Não", null)
                .show();
    }


    public void onStatusChange(Usuario tarefa, boolean isChecked) {
        String observacao = tarefa.getEmail();
        // Garante que a observação não seja nula para evitar erros
        if (observacao == null) {
            observacao = "";
        }

        String novoStatusObservacao;

        // Adiciona ou remove o prefixo [OK] para marcar como concluída
        if (isChecked) {
            novoStatusObservacao = observacao.startsWith("[OK] ") ? observacao : "[OK] " + observacao;
        } else {
            novoStatusObservacao = observacao.startsWith("[OK] ") ? observacao.substring(5) : observacao;
        }

        if (tarefa.getId() != null) {
            databaseUsuarios.child(tarefa.getId()).child("email").setValue(novoStatusObservacao);
        }
    }


}