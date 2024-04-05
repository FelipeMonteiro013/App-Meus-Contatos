package com.example.meuscontatos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.meuscontatos.database.repository.ContatoRepository
import com.example.meuscontatos.model.Contato
import com.example.meuscontatos.ui.theme.MeusContatosTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MeusContatosTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    ContatosScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ContatosScreen() {

    val idState = remember {
        mutableLongStateOf(0)
    }

    val nomeState = remember {
        mutableStateOf("")
    }

    val telefoneState = remember {
        mutableStateOf("")
    }

    val amigoState = remember {
        mutableStateOf(false)
    }

    val context = LocalContext.current
    val contatoRepository = ContatoRepository(context)

    val listaContatoState = remember {
        mutableStateOf(contatoRepository.listarContatos())
    }

    val isEdit = remember {
        mutableStateOf(false)
    }

    val controller = LocalSoftwareKeyboardController.current

    Column {
        ContatoForm(
            id = idState.longValue,
            nome = nomeState.value,
            telefone = telefoneState.value,
            amigo = amigoState.value,
            onNomeChange = {
                nomeState.value = it
            },
            onTelefoneChange = {
                telefoneState.value = it
            },
            onAmigoChange = {
                amigoState.value = it
            },
            atualizar = {
                listaContatoState.value = contatoRepository.listarContatos()
            },
            editar = isEdit.value,
            limparCampos = {
                idState.longValue = 0
                nomeState.value = ""
                telefoneState.value = ""
                amigoState.value = false

                isEdit.value = false


                controller?.hide()
            }
        )
        ContatoList(
            listaContatoState,
            atualizar = {
                listaContatoState.value = contatoRepository.listarContatos()
            },
            editar = {
                isEdit.value = true

                idState.longValue = it.id
                nomeState.value = it.nome
                telefoneState.value = it.telefone
                amigoState.value = it.isAmigo

            }
        )
    }
}

@Composable
fun ContatoForm(
    id: Long,
    nome: String,
    telefone: String,
    amigo: Boolean,
    onNomeChange: (String) -> Unit,
    onTelefoneChange: (String) -> Unit,
    onAmigoChange: (Boolean) -> Unit,
    atualizar: () -> Unit,
    editar: Boolean,
    limparCampos: () -> Unit
) {

//  Obter contexto
    val context = LocalContext.current
    val contatoRepository = ContatoRepository(context)

    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Text(
            text = "Cadastro de contatos",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(
                0xFFE91E63
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = nome,
            onValueChange = { onNomeChange(it) },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text(text = "Nome do contato")
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text, capitalization = KeyboardCapitalization.Words
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = telefone,
            onValueChange = { onTelefoneChange(it) },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text(text = "Telefone do contato")
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Phone
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()
        ) {
            Checkbox(checked = amigo, onCheckedChange = {
                onAmigoChange(it)
            })
            Text(text = "Amigo")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                val contato = Contato(
                    id = id, nome = nome, telefone = telefone, isAmigo = amigo
                )
                if (nome.isNotEmpty() and telefone.isNotEmpty()) {
                    if (editar) {
                        contatoRepository.atualizar(contato)
                        atualizar()
                    } else {
                        contatoRepository.salvar(contato)
                        atualizar()
                    }

                    limparCampos()
                }

            }, modifier = Modifier.fillMaxWidth()
        ) {
            if (editar) {

                Text(
                    text = "ALTERAR", modifier = Modifier.padding(8.dp)
                )
            } else {
                Text(
                    text = "CADASTAR", modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}

@Composable
fun ContatoList(
    listaContatos: MutableState<List<Contato>>, atualizar: () -> Unit, editar: (Contato) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        for (contato in listaContatos.value) {
            ContatoCard(contato, atualizar, editar = {
                editar(contato)
            })
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

@Composable
fun ContatoCard(
    contato: Contato, atualizar: () -> Unit, editar: () -> Unit
) {

    val context = LocalContext.current
    val contatoRepository = ContatoRepository(context)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                editar()
            }, colors = CardDefaults.cardColors(
            containerColor = Color.LightGray
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .weight(2f)
            ) {
                Text(
                    text = contato.nome, fontSize = 24.sp, fontWeight = FontWeight.Bold
                )
                Text(
                    text = contato.telefone, fontSize = 16.sp, fontWeight = FontWeight.Bold
                )
                if (contato.isAmigo) {
                    Text(
                        text = "Amigo", fontSize = 16.sp, fontWeight = FontWeight.Bold
                    )
                } else {
                    Text(
                        text = "Contato", fontSize = 16.sp, fontWeight = FontWeight.Bold
                    )
                }
            }
            IconButton(onClick = {
                contatoRepository.excluir(contato)
                atualizar()
            }) {
                Icon(
                    imageVector = Icons.Default.Delete, contentDescription = ""
                )
            }
        }
    }
}