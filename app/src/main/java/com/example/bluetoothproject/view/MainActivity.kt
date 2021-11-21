package com.example.bluetoothproject.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.example.bluetoothproject.R
import com.example.bluetoothproject.databinding.ActivityMainBinding
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener

class MainActivity : AppCompatActivity() {


    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if(savedInstanceState != null) return

        // Dexter ile hem izin kontrol ediyoruz, hemde izin yoksa istiyoruz, sonuca göre hareket ediyoruz
        checkPermissionsForRequest()

    }

    private fun checkPermissionsForRequest(){

        Dexter.withContext(this)
            .withPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {
                    if (p0!!.areAllPermissionsGranted()) {
                        Log.i("tag_permission", "Tüm izinler verildi")
                        continueToActivity()
                    } else {
                        // Tüm izinler geçmediyse eğer en az biri reddedilmiş demektir
                        if (p0.isAnyPermissionPermanentlyDenied) {
                            // eger bidaha gösterme denilen bir izin varsa doğrudan ayarlara yönlendirilmeli
                            Log.i("tag_permission", "İzinlerden birine birdaha gösterme denilmiş.")
                            showDialogToGoToSettings()
                        } else {
                            // yok eğer bir izine red verilmişse, dialog ile tekrar hatıraltma yapabiliriz
                            showDialogReasonForRequest()
                        }
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: MutableList<PermissionRequest>?, p1: PermissionToken?
                ) {
                    Log.i("tag_permisson", "izinlerden biri reddedildi")
                    p1!!.continuePermissionRequest() // her defasında yinede sorar
//                    p1!!.cancelPermissionRequest() // bidaha hiç bir zaman sormaz
                }
            })
            .withErrorListener { p0 -> Log.i("tag_permisson", "Error : $p0") }
            .check()

    }

    private fun continueToActivity() {
        supportFragmentManager.commit { add<HomeFragment>(R.id.nav_host_fragment,null)}

        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.homeFragment -> goToHome()

                R.id.historyFragment -> goToHistory()

                else -> false
            }
        }
    }

    private fun goToHistory(): Boolean {
        supportFragmentManager.commit { replace<HistoryFragment>(R.id.nav_host_fragment,null,null)
            addToBackStack("homeToHistory")}
        return true
    }

    private fun goToHome(): Boolean {
        supportFragmentManager.commit { replace<HomeFragment>(R.id.nav_host_fragment,null,null) }
        return true
    }

    private fun showDialogReasonForRequest() {

        AlertDialog.Builder(this@MainActivity)
            .setTitle(getString(R.string.gps_kullanim_izni_gerekli))    //text : "GPS Kullanım İzni Gerekli !"
            .setMessage(getString(R.string.lokasyon_bilignize_ihtiyac))     //text: "Programı kullanmaya devam etmek için lokasyon bilginize ihtiyacımız var."
            .setPositiveButton(
                getString(R.string.izin_ver)    //text: "İzin Ver"
            ) { dialog, _ ->
                dialog!!.cancel()
                checkPermissionsForRequest()
            }
            .setNegativeButton(
                getString(R.string.iptal) //text: "İptal"
            ) { dialog, _ ->
                dialog!!.cancel()
                finish()
            }
            .setCancelable(false)
            .show()

    }

    private fun showDialogToGoToSettings() {

        AlertDialog.Builder(this@MainActivity)
            .setTitle(getString(R.string.gps_kullanim_izni_gerekli))    //text : "GPS Kullanım İzni Gerekli !"
            .setMessage(getString(R.string.programi_kullanmaya_devam))    //text: "Programı kullanmaya devam etmek için Ayalar kısmından manuel olarak izin vermeniz gerekli"
            .setPositiveButton(
                getString(R.string.ayarlara_git)  //text : "Ayarlara Git"
            ) { dialog, _ ->
                dialog!!.cancel()
                val intent =
                    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
//                                            finish()
            }
            .setNegativeButton(
                getString(R.string.iptal) //text: "İptal"
            ) { dialog, _ ->
                dialog!!.cancel()
                finish()
            }
            .setCancelable(false)
            .show()
    }




}