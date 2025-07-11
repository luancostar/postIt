package com.example.meuapp;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

@Dao
public interface TarefaDao {

    @Query("SELECT * FROM tarefas ORDER BY orderIndex")
    LiveData<List<Usuario>> getTodasAsTarefas();

    // ✅ MÉTODO FALTANTE ADICIONADO AQUI
    @Query("SELECT * FROM tarefas WHERE id = :id LIMIT 1")
    Usuario getTarefaPeloId(String id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void inserir(Usuario tarefa);

    @Update
    void atualizar(Usuario tarefa);

    @Delete
    void deletar(Usuario tarefa);
}