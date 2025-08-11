package com.example.meuapp;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;
import java.util.List;

@Entity(tableName = "tarefas", indices = {@Index(value = {"id"}, unique = true)})
@TypeConverters(Converters.class) // <-- A anotação viria aqui
public class Usuario {

    @PrimaryKey(autoGenerate = true)
    public int localId;

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

    private List<String> anexos; // Campo para a lista de anexos
    
    @NonNull
    private String prioridade = "MEDIA"; // Campo para prioridade: BAIXA, MEDIA, ALTA

    public Usuario() {}

    public Usuario(String id, String nome, String email, String dataEntrega, String dataFinalizacao, long orderIndex, List<String> anexos, String prioridade) {
        this.id = id != null ? id : "";
        this.nome = nome != null ? nome : "";
        this.email = email != null ? email : "";
        this.dataEntrega = dataEntrega != null ? dataEntrega : "";
        this.dataFinalizacao = dataFinalizacao != null ? dataFinalizacao : "";
        this.orderIndex = orderIndex;
        this.anexos = anexos;
        this.prioridade = prioridade != null ? prioridade : "MEDIA";
    }

    // Getters e Setters para todos os campos...
    @NonNull public String getId() { return id; }
    public void setId(@NonNull String id) { this.id = id; }
    @NonNull public String getNome() { return nome; }
    public void setNome(@NonNull String nome) { this.nome = nome; }
    @NonNull public String getEmail() { return email; }
    public void setEmail(@NonNull String email) { this.email = email; }
    @NonNull public String getDataEntrega() { return dataEntrega; }
    public void setDataEntrega(@NonNull String dataEntrega) { this.dataEntrega = dataEntrega; }
    @NonNull public String getDataFinalizacao() { return dataFinalizacao; }
    public void setDataFinalizacao(@NonNull String dataFinalizacao) { this.dataFinalizacao = dataFinalizacao; }
    public long getOrderIndex() { return orderIndex; }
    public void setOrderIndex(long orderIndex) { this.orderIndex = orderIndex; }
    public List<String> getAnexos() { return anexos; }
    public void setAnexos(List<String> anexos) { this.anexos = anexos; }
    @NonNull public String getPrioridade() { return prioridade; }
    public void setPrioridade(@NonNull String prioridade) { this.prioridade = prioridade; }
}