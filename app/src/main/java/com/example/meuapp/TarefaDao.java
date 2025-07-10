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

    // Retorna um LiveData, que a UI pode "observar"
    @Query("SELECT * FROM tarefas ORDER BY orderIndex DESC")
    LiveData<List<Usuario>> getTodasAsTarefas();

    // OnConflictStrategy.REPLACE garante que se uma tarefa com o mesmo ID
    // vier do Firebase, ela será atualizada em vez de criar uma duplicata.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void inserir(Usuario tarefa);

    @Update
    void atualizar(Usuario tarefa);

    @Delete
    void deletar(Usuario tarefa);

    // Novo método para deletar pelo ID do Firebase
    @Query("DELETE FROM tarefas WHERE id = :firebaseId")
    void deletarPeloFirebaseId(String firebaseId);
}