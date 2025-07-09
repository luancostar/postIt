package com.example.meuapp;
// Usuario.java
// Mantendo a classe Usuario conforme solicitado, mas ela representa uma Tarefa

public class Usuario {
    private String id;
    private String nome;
    private String email;
    private String dataEntrega;
    private String dataFinalizacao;
    private long orderIndex; // ✅ NOVO CAMPO PARA SALVAR A ORDEM
    public Usuario() {
        // Construtor vazio é obrigatório para o Firebase
    }

    // Construtor completo
    public Usuario(String id, String nome, String email, String dataEntrega, String dataFinalizacao, long orderIndex) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.dataEntrega = dataEntrega;
        this.dataFinalizacao = dataFinalizacao;
        this.orderIndex = orderIndex;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getDataEntrega() { return dataEntrega; }
    public void setDataEntrega(String dataEntrega) { this.dataEntrega = dataEntrega; }
    public String getDataFinalizacao() { return dataFinalizacao; }
    public void setDataFinalizacao(String dataFinalizacao) { this.dataFinalizacao = dataFinalizacao; }
    public long getOrderIndex() { return orderIndex; }
    public void setOrderIndex(long orderIndex) { this.orderIndex = orderIndex; }


    @Override
    public String toString() {
        return nome + " (Entrega: " + dataEntrega + ")";
    }
}