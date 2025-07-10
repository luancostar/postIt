package com.example.meuapp;

import androidx.annotation.NonNull; // ✅ Importe esta anotação
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "tarefas", indices = {@Index(value = {"id"}, unique = true)})
public class Usuario {

    @PrimaryKey(autoGenerate = true)
    public int localId;

    // ✅ Garante que o ID do Firebase nunca será nulo para o Room
    @NonNull
    private String id = "";

    @NonNull
    private String nome = "";

    @NonNull
    private String email = "";

    @NonNull
    private String dataEntrega = "";

    @NonNull
    private String dataFinalizacao = "";

    private long orderIndex;

    // Construtor vazio inicializa os campos para evitar valores nulos
    public Usuario() {
        this.id = "";
        this.nome = "";
        this.email = "";
        this.dataEntrega = "";
        this.dataFinalizacao = "";
    }

    public Usuario(String id, String nome, String email, String dataEntrega, String dataFinalizacao, long orderIndex) {
        this.id = id != null ? id : "";
        this.nome = nome != null ? nome : "";
        this.email = email != null ? email : "";
        this.dataEntrega = dataEntrega != null ? dataEntrega : "";
        this.dataFinalizacao = dataFinalizacao != null ? dataFinalizacao : "";
        this.orderIndex = orderIndex;
    }

    // Getters e Setters
    @NonNull
    public String getId() { return id; }
    public void setId(@NonNull String id) { this.id = id; }

    @NonNull
    public String getNome() { return nome; }
    public void setNome(@NonNull String nome) { this.nome = nome; }

    @NonNull
    public String getEmail() { return email; }
    public void setEmail(@NonNull String email) { this.email = email; }

    @NonNull
    public String getDataEntrega() { return dataEntrega; }
    public void setDataEntrega(@NonNull String dataEntrega) { this.dataEntrega = dataEntrega; }

    @NonNull
    public String getDataFinalizacao() { return dataFinalizacao; }
    public void setDataFinalizacao(@NonNull String dataFinalizacao) { this.dataFinalizacao = dataFinalizacao; }

    public long getOrderIndex() { return orderIndex; }
    public void setOrderIndex(long orderIndex) { this.orderIndex = orderIndex; }
}