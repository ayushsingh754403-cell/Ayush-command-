package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CommandDao {
    @Query("SELECT * FROM saved_commands ORDER BY timestamp DESC")
    fun getAllCommands(): Flow<List<CommandEntity>>

    @Query("SELECT * FROM saved_commands WHERE isFavorite = 1 ORDER BY timestamp DESC")
    fun getFavoriteCommands(): Flow<List<CommandEntity>>

    @Query("SELECT * FROM saved_commands WHERE id = :id")
    suspend fun getCommandById(id: Long): CommandEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCommand(command: CommandEntity): Long

    @Update
    suspend fun updateCommand(command: CommandEntity)

    @Delete
    suspend fun deleteCommand(command: CommandEntity)

    @Query("DELETE FROM saved_commands")
    suspend fun deleteAll()
}
