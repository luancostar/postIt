package com.example.meuapp;
// Usuario.java
// Mantendo a classe Usuario conforme solicitado, mas ela representa uma Tarefa

public class Usuario {
    private String id;
    private String nome;    // Usado como Título da Tarefa
    private String email;   // Usado como Observação/Subtítulo da Tarefa
    private String dataEntrega; // NOVO CAMPO

    public Usuario() {
        // Construtor vazio é obrigatório para o Firebase
    }

    // Construtor completo
    public Usuario(String id, String nome, String email, String dataEntrega) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.dataEntrega = dataEntrega;
    }

    // Getters e Setters para todos os campos
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public String getDataEntrega() { return dataEntrega; }
    public void setDataEntrega(String dataEntrega) { this.dataEntrega = dataEntrega; }

    @Override
    public String toString() {
        return nome + " (Entrega: " + dataEntrega + ")";
    }
}