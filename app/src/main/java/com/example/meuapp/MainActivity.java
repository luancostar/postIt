package com.example.meuapp;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements TarefaAdapter.OnItemClickListener {

    private RecyclerView recyclerViewTarefas;
    private FloatingActionButton fabAdicionar;
    private TarefaAdapter adapter;
    private TextView textViewContadorAndamento, textViewContadorConcluidas;
    private CircleImageView profileImage;
    private TextView profileName;
    private SharedPreferences sharedPreferences;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private DatabaseReference databaseUsuarios;
    private List<Usuario> listaTarefas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayShowTitleEnabled(false);

        databaseUsuarios = FirebaseDatabase.getInstance().getReference("usuarios");
        textViewContadorAndamento = findViewById(R.id.textViewContadorAndamento);
        textViewContadorConcluidas = findViewById(R.id.textViewContadorConcluidas);
        recyclerViewTarefas = findViewById(R.id.recyclerViewTarefas);
        fabAdicionar = findViewById(R.id.fabAdicionar);
        profileImage = findViewById(R.id.profile_image);
        profileName = findViewById(R.id.profile_name);
        sharedPreferences = getSharedPreferences("PerfilApp", Context.MODE_PRIVATE);

        listaTarefas = new ArrayList<>();
        adapter = new TarefaAdapter(this);
        recyclerViewTarefas.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewTarefas.setAdapter(adapter);

        carregarPerfil();
        configurarSwipeActions();

        fabAdicionar.setOnClickListener(v -> abrirDialogTarefa(null));
        profileName.setOnClickListener(v -> abrirDialogNome());

        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        profileImage.setImageURI(imageUri);
                        salvarUriDaFoto(imageUri.toString());
                    }
                });

        profileImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            galleryLauncher.launch(intent);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        databaseUsuarios.orderByChild("orderIndex").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listaTarefas.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Usuario tarefa = postSnapshot.getValue(Usuario.class);
                    if (tarefa != null) listaTarefas.add(tarefa);
                }
                adapter.setTarefas(listaTarefas);
                atualizarContadores();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "Falha ao carregar tarefas.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void configurarSwipeActions() {
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false; // Não usamos reordenação
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Usuario tarefa = adapter.getTarefaAt(position);

                if (direction == ItemTouchHelper.LEFT) {
                    onDeleteClick(tarefa);
                } else if (direction == ItemTouchHelper.RIGHT) {
                    boolean isChecked = tarefa.getEmail() != null && tarefa.getEmail().startsWith("[OK]");
                    onStatusChange(tarefa, !isChecked);
                }
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                View itemView = viewHolder.itemView;
                Drawable icon;
                ColorDrawable background;

                if (dX > 0) { // Direita
                    background = new ColorDrawable(Color.parseColor("#4CAF50"));
                    icon = ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_check_circle);
                } else { // Esquerda
                    background = new ColorDrawable(Color.parseColor("#F44336"));
                    icon = ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_delete);
                }

                int iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
                int iconTop = itemView.getTop() + (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
                int iconBottom = iconTop + icon.getIntrinsicHeight();

                if (dX > 0) {
                    int iconLeft = itemView.getLeft() + iconMargin;
                    int iconRight = itemView.getLeft() + iconMargin + icon.getIntrinsicWidth();
                    icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                    background.setBounds(itemView.getLeft(), itemView.getTop(), itemView.getLeft() + ((int) dX), itemView.getBottom());
                } else if (dX < 0) {
                    int iconLeft = itemView.getRight() - iconMargin - icon.getIntrinsicWidth();
                    int iconRight = itemView.getRight() - iconMargin;
                    icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                    background.setBounds(itemView.getRight() + ((int) dX), itemView.getTop(), itemView.getRight(), itemView.getBottom());
                } else {
                    background.setBounds(0,0,0,0);
                }
                background.draw(c);
                icon.draw(c);
            }
        }).attachToRecyclerView(recyclerViewTarefas);
    }

    // ✅ MÉTODO DA INTERFACE ATUALIZADO
    @Override
    public void onCardLongClick(Usuario tarefa) {
        abrirDialogTarefa(tarefa);
    }

    public void onDeleteClick(Usuario tarefa) {
        if (tarefa == null || tarefa.getId() == null) return;
        new AlertDialog.Builder(this)
                .setTitle("Excluir Tarefa")
                .setMessage("Você tem certeza que deseja excluir \"" + tarefa.getNome() + "\"?")
                .setPositiveButton("Sim, excluir", (dialog, which) -> {
                    databaseUsuarios.child(tarefa.getId()).removeValue()
                            .addOnSuccessListener(aVoid -> Toast.makeText(this, "Tarefa excluída", Toast.LENGTH_SHORT).show());
                })
                .setNegativeButton("Cancelar", (dialog, which) -> {
                    adapter.notifyItemChanged(listaTarefas.indexOf(tarefa));
                })
                .setOnCancelListener(dialog -> adapter.notifyItemChanged(listaTarefas.indexOf(tarefa)))
                .show();
    }

    public void onStatusChange(Usuario tarefa, boolean isChecked) {
        String observacao = tarefa.getEmail() != null ? tarefa.getEmail() : "";
        String dataFinalizacao;
        if (isChecked) {
            observacao = "[OK] " + (observacao.startsWith("[OK] ") ? observacao.substring(5) : observacao);
            dataFinalizacao = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        } else {
            observacao = observacao.startsWith("[OK] ") ? observacao.substring(5) : observacao;
            dataFinalizacao = "";
        }
        if (tarefa.getId() != null) {
            Map<String, Object> updates = new HashMap<>();
            updates.put("email", observacao);
            updates.put("dataFinalizacao", dataFinalizacao);
            databaseUsuarios.child(tarefa.getId()).updateChildren(updates);
        }
    }

    // --- O resto dos métodos auxiliares ---
    private void carregarPerfil() {
        String nomeSalvo = sharedPreferences.getString("USER_NAME", "Seu Nome");
        String uriFotoSalva = sharedPreferences.getString("USER_PHOTO_URI", null);
        profileName.setText("Olá, " + nomeSalvo + "! 👋");
        if (uriFotoSalva != null) {
            profileImage.setImageURI(Uri.parse(uriFotoSalva));
        }
    }

    private void abrirDialogTarefa(final Usuario tarefa) {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_tarefa, null);
        final EditText editTextTitulo = dialogView.findViewById(R.id.editTextTituloDialog);
        final EditText editTextObservacao = dialogView.findViewById(R.id.editTextObservacaoDialog);
        final EditText editTextData = dialogView.findViewById(R.id.editTextDataDialog);
        editTextData.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            new DatePickerDialog(MainActivity.this, (view, year, month, day) -> {
                editTextData.setText(String.format(Locale.getDefault(), "%02d/%02d/%d", day, month + 1, year));
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        builder.setTitle(tarefa == null ? "Nova Tarefa" : "Editar Tarefa");
        if (tarefa != null) {
            editTextTitulo.setText(tarefa.getNome());
            String obs = tarefa.getEmail() != null && tarefa.getEmail().startsWith("[OK] ") ? tarefa.getEmail().substring(5) : tarefa.getEmail();
            editTextObservacao.setText(obs);
            editTextData.setText(tarefa.getDataEntrega());
        }
        builder.setPositiveButton(tarefa == null ? "Adicionar" : "Salvar", (dialog, which) -> {
            String titulo = editTextTitulo.getText().toString().trim();
            String observacao = editTextObservacao.getText().toString().trim();
            String data = editTextData.getText().toString().trim();
            if (TextUtils.isEmpty(titulo)) {
                Toast.makeText(this, "O título é obrigatório.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (tarefa == null) {
                adicionarTarefa(titulo, observacao, data);
            } else {
                boolean isChecked = tarefa.getEmail() != null && tarefa.getEmail().startsWith("[OK] ");
                if (isChecked) observacao = "[OK] " + observacao;
                atualizarTarefa(tarefa.getId(), titulo, observacao, data, tarefa.getDataFinalizacao(), tarefa.getOrderIndex());
            }
        });
        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());
        builder.create().show();
    }

    private void adicionarTarefa(String titulo, String observacao, String data) {
        String id = databaseUsuarios.push().getKey();
        long orderIndex = -System.currentTimeMillis();
        Usuario novaTarefa = new Usuario(id, titulo, observacao, data, "", orderIndex);
        if (id != null) databaseUsuarios.child(id).setValue(novaTarefa);
    }

    private void atualizarTarefa(String id, String titulo, String observacao, String data, String dataFinalizacao, long orderIndex) {
        Usuario tarefaAtualizada = new Usuario(id, titulo, observacao, data, dataFinalizacao, orderIndex);
        databaseUsuarios.child(id).setValue(tarefaAtualizada);
    }

    private void atualizarContadores() {
        int emAndamento = 0, concluidas = 0;
        for (Usuario tarefa : listaTarefas) {
            if (tarefa.getEmail() != null && tarefa.getEmail().startsWith("[OK]")) {
                concluidas++;
            } else {
                emAndamento++;
            }
        }
        textViewContadorAndamento.setText(String.valueOf(emAndamento));
        textViewContadorConcluidas.setText(String.valueOf(concluidas));
    }

    private void abrirDialogNome() {
        //...
    }
    private void salvarNome(String nome) {
        //...
    }
    private void salvarUriDaFoto(String uriString) {
        //...
    }
}