package com.example.meuscontatos.database.repository

import android.content.Context
import android.util.Log
import com.example.meuscontatos.database.dao.ContatoDb
import com.example.meuscontatos.model.Contato

class ContatoRepository(context: Context) {

    var db = ContatoDb.getDatabase(context).ContatoDao()

    fun salvar(contato: Contato): Long {
        return db.salvar(contato)
    }

    fun atualizar(contato: Contato): Int {
        Log.i("REPOSITORY", contato.toString())
        return db.atualizar(contato)
    }

    fun excluir(contato: Contato): Int {
        return db.excluir(contato)
    }

    fun buscarContatoPeloId(id: Long): Contato {
        return db.buscaContatoPeloId(id)
    }

    fun listarContatos(): List<Contato> {
        return db.listarContatos()
    }

}