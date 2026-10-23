
package ar.personal.antispam

import android.app.Activity
import android.app.role.RoleManager
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

class MainActivity : Activity() {

    private val prefs by lazy {
        getSharedPreferences("antispam_rules", Context.MODE_PRIVATE)
    }

    private lateinit var numbers: EditText
    private lateinit var prefixes: EditText
    private lateinit var contacts: CheckBox
    private lateinit var status: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(28, 28, 28, 28)
            gravity = Gravity.TOP
        }

        fun addText(text: String, size: Float = 16f) {
            layout.addView(TextView(this).apply {
                this.text = text
                textSize = size
                setPadding(0, 12, 0, 12)
            })
        }

        fun addButton(text: String, action: () -> Unit) {
            layout.addView(Button(this).apply {
                this.text = text
                setOnClickListener { action() }
            })
        }

        addText("AntiSpamPersonal", 26f)
        addText("Bloqueá llamadas no deseadas")

        status = TextView(this)
        layout.addView(status)

        addButton("Activar filtro de llamadas") {
            if (android.os.Build.VERSION.SDK_INT >= 29) {
                val manager = getSystemService(RoleManager::class.java)
                if (manager.isRoleAvailable(RoleManager.ROLE_CALL_SCREENING)) {
                    if (!manager.isRoleHeld(RoleManager.ROLE_CALL_SCREENING)) {
                        @Suppress("DEPRECATION")
                        startActivityForResult(
                            manager.createRequestRoleIntent(
                                RoleManager.ROLE_CALL_SCREENING
                            ), 100
                        )
                    } else {
                        Toast.makeText(
                            this, "El filtro ya está activado",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        addText("Números completos para bloquear")
        numbers = EditText(this).apply {
            hint = "Ej.: +5491123456789"
            minLines = 2
            gravity = Gravity.TOP
        }
        layout.addView(numbers)

        addButton("Guardar números") {
            prefs.edit().putString(
                "blocked_numbers", numbers.text.toString()
            ).apply()
            Toast.makeText(this, "Números guardados", Toast.LENGTH_SHORT).show()
        }

        addText("Prefijos para bloquear")
        prefixes = EditText(this).apply {
            hint = "Un prefijo por línea. Ej.: 011"
            minLines = 2
            gravity = Gravity.TOP
        }
        layout.addView(prefixes)

        addButton("Guardar prefijos") {
            prefs.edit().putString(
                "blocked_prefixes", prefixes.text.toString()
            ).apply()
            Toast.makeText(this, "Prefijos guardados", Toast.LENGTH_SHORT).show()
        }

        contacts = CheckBox(this).apply {
            text = "No bloquear números guardados en contactos"
            isChecked = prefs.getBoolean("contacts_exception", true)
            setOnCheckedChangeListener { _, checked ->
                prefs.edit().putBoolean("contacts_exception", checked).apply()
            }
        }
        layout.addView(contacts)

        addText("Escribí un número o prefijo por línea.")
        addButton("Actualizar pantalla") { loadRules() }

        setContentView(
            android.widget.ScrollView(this).apply {
                addView(layout, ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ))
            }
        )

        loadRules()
    }

    private fun loadRules() {
        numbers.setText(prefs.getString("blocked_numbers", ""))
        prefixes.setText(prefs.getString("blocked_prefixes", ""))
        contacts.isChecked = prefs.getBoolean("contacts_exception", true)

        status.text = if (
            android.os.Build.VERSION.SDK_INT >= 29 &&
            getSystemService(RoleManager::class.java)
                .isRoleHeld(RoleManager.ROLE_CALL_SCREENING)
        ) {
            "Filtro de llamadas: activado"
        } else {
            "Filtro de llamadas: falta activarlo"
        }
    }

    @Deprecated("Usado para recibir el resultado de activación")
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: android.content.Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100) loadRules()
    }
}
