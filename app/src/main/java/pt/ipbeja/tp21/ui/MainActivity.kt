package pt.ipbeja.tp21.ui

import android.content.Context
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import pt.ipbeja.tp21.R
import pt.ipbeja.tp21.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navController = findNavController(R.id.nav_host_fragment)
        setupActionBarWithNavController(navController)
    }

    //TODO - Fazer Video Explicativo do Funcionamento da aplicação
    fun getActivityContext(): Context? {
        return applicationContext
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> {
                //println("BOTAO VOLTAR ATRAS CARREGADO")
                //Toast.makeText(requireContext(), "BOTAO VOLTAR ATRAS CARREGADO", Toast.LENGTH_SHORT).show()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}