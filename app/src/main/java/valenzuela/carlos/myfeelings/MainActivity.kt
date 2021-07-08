package valenzuela.carlos.myfeelings

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import valenzuela.carlos.myfeelings.utilities.CustomBarDrawable
import valenzuela.carlos.myfeelings.utilities.CustomCircleDrawable
import valenzuela.carlos.myfeelings.utilities.Emociones
import valenzuela.carlos.myfeelings.utilities.JSONFile

class MainActivity : AppCompatActivity() {

    var jsonFile: JSONFile? = null
    var veryHappy = 0.0F
    var happy = 0.0F
    var neutral = 0.0F
    var sad = 0.0F
    var verySad = 0.0F
    var data: Boolean = false
    var lista = ArrayList<Emociones>()

    lateinit var graph: ConstraintLayout
    lateinit var graphVeryHappy: View
    lateinit var graphHappy: View
    lateinit var graphNeutral: View
    lateinit var graphSad: View
    lateinit var graphVerySad: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        jsonFile = JSONFile()

        graph = findViewById(R.id.graph)
        graphVeryHappy = findViewById(R.id.veryHappyGraph)
        graphHappy = findViewById(R.id.happyGraph)
        graphNeutral = findViewById(R.id.neutralGraph)
        graphSad = findViewById(R.id.sadGraph)
        graphVerySad = findViewById(R.id.verySadGraph)

        fetchingData()
        if(!data){
            val emociones = ArrayList<Emociones>()
            val fondo = CustomCircleDrawable(this, emociones)

            graph.background = fondo
            graphVeryHappy.background = CustomBarDrawable(this, Emociones("Muy feliz", 0.0F, R.color.mustard, veryHappy))
            graphHappy.background = CustomBarDrawable(this, Emociones("Feliz", 0.0F, R.color.orange, happy))
            graphNeutral.background = CustomBarDrawable(this,Emociones("Neutral", 0.0F, R.color.green, neutral))
            graphSad.background = CustomBarDrawable(this, Emociones("Triste", 0.0F, R.color.blue, sad))
            graphVerySad.background = CustomBarDrawable(this, Emociones("Muy triste", 0.0F, R.color.deepBlue, verySad))
        }else {
            actualizarGrafica()
            iconoMayoria()
        }

        val saveButton: Button = findViewById(R.id.saveButton)
        val veryHappyButton: ImageButton = findViewById(R.id.veryHappyButton)
        val happyButton: ImageButton = findViewById(R.id.happyButton)
        val neutralButton: ImageButton = findViewById(R.id.neutralButton)
        val sadButton: ImageButton = findViewById(R.id.sadButton)
        val verySadButton: ImageButton = findViewById(R.id.verySadButton)

        saveButton.setOnClickListener {
            guardar()
        }

        veryHappyButton.setOnClickListener{
            veryHappy++
            iconoMayoria()
            actualizarGrafica()
        }

        happyButton.setOnClickListener{
            happy++
            iconoMayoria()
            actualizarGrafica()
        }

        neutralButton.setOnClickListener{
            neutral++
            iconoMayoria()
            actualizarGrafica()
        }

        sadButton.setOnClickListener{
            sad++
            iconoMayoria()
            actualizarGrafica()
        }

        verySadButton.setOnClickListener{
            verySad++
            iconoMayoria()
            actualizarGrafica()
        }
    }

    fun fetchingData(){
        try{
            val json: String = jsonFile?.getData(this) ?: ""
            if(json != ""){
                this.data = true
                val jsonArray = JSONArray(json)

                this.lista = parseJson(jsonArray)

                for (i in lista){
                    when(i.nombre){
                        "Muy feliz" -> veryHappy = i.total
                        "Feliz"-> happy = i.total
                        "Neutral"-> neutral = i.total
                        "Triste"-> sad = i.total
                        "Muy triste"-> verySad = i.total
                    }
                }
            } else {
                this.data = false
            }
        } catch (exception: JSONException){
            exception.printStackTrace()
        }
    }

    fun iconoMayoria(){
        val icon: ImageView = findViewById(R.id.icon)

        if(veryHappy > happy && veryHappy > neutral && veryHappy > sad && veryHappy > verySad){
            icon.setImageDrawable(resources.getDrawable(R.drawable.ic_very_happy_40))
        } else if(happy > veryHappy && happy > neutral && happy > sad && happy > verySad){
            icon.setImageDrawable(resources.getDrawable(R.drawable.ic_happy_40))
        } else if(neutral > veryHappy && neutral > happy && neutral > sad && neutral > verySad){
            icon.setImageDrawable(resources.getDrawable(R.drawable.ic_neutral_40))
        } else if(sad > veryHappy && sad > happy && sad > neutral && sad > verySad){
            icon.setImageDrawable(resources.getDrawable(R.drawable.ic_sad_40))
        } else if(verySad > veryHappy && verySad > happy && verySad > neutral && verySad > sad){
            icon.setImageDrawable(resources.getDrawable(R.drawable.ic_very_sad_40))
        }

    }

    fun actualizarGrafica(){
        val total = veryHappy + happy + neutral + sad + verySad

        val pVH: Float = (veryHappy * 100/total)
        val pH: Float = (happy * 100/total)
        val pN: Float = (neutral * 100/total)
        val pS: Float = (sad * 100/total)
        val pVS: Float = (verySad * 100/total)

        Log.d("porcentajes", "very happy "+pVH)
        Log.d("porcentajes", "happy "+pH)
        Log.d("porcentajes", "neutral "+pN)
        Log.d("porcentajes", "sad "+pS)
        Log.d("porcentajes", "very sad "+pVS)

        lista.clear()
        lista.add(Emociones("Muy feliz", pVH, R.color.mustard, veryHappy))
        lista.add(Emociones("Feliz", pH, R.color.orange, happy))
        lista.add(Emociones("Neutral", pN, R.color.green, neutral))
        lista.add(Emociones("Triste", pS, R.color.blue, sad))
        lista.add(Emociones("Muy triste", pVS, R.color.deepBlue, verySad))

        val fondo = CustomCircleDrawable(this, lista)

        graphVeryHappy.background = CustomBarDrawable(this, Emociones("Muy feliz", pVH, R.color.mustard, veryHappy))
        graphHappy.background = CustomBarDrawable(this, Emociones("Feliz", pH, R.color.orange, happy))
        graphNeutral.background = CustomBarDrawable(this,Emociones("Neutral", pN, R.color.green, neutral))
        graphSad.background = CustomBarDrawable(this, Emociones("Triste", pS, R.color.blue, sad))
        graphVerySad.background = CustomBarDrawable(this, Emociones("Muy triste", pVS, R.color.deepBlue, verySad))

        graph.background = fondo
    }

    fun parseJson(jsonArray: JSONArray): ArrayList<Emociones>{

        val lista = ArrayList<Emociones>()

        for (i in 0..jsonArray.length()){
            try{
                val nombre = jsonArray.getJSONObject(i).getString("nombre")
                val porcentaje = jsonArray.getJSONObject(i).getDouble("porcentaje").toFloat()
                val color = jsonArray.getJSONObject(i).getInt("color")
                val total = jsonArray.getJSONObject(i).getDouble("total").toFloat()
                val emocion = Emociones(nombre, porcentaje, color, total)
                lista.add(emocion)
            } catch (exception : JSONException){
                exception.printStackTrace()
            }
        }
        return lista
    }

    fun guardar(){
        val jsonArray = JSONArray()
        var o = 0

        for(i in lista){
            Log.d("objetos", i.toString())
            val j = JSONObject()
            j.put("nombre", i.nombre)
            j.put("porcentaje", i.porcentaje)
            j.put("color", i.color)
            j.put("total", i.total)

            jsonArray.put(o, j)
            o++
        }

        jsonFile?.saveData(this, jsonArray.toString())

        Toast.makeText(this, "Datos guardados", Toast.LENGTH_SHORT).show()
    }
}