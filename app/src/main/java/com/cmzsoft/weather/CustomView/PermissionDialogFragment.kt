package com.cmzsoft.weather.CustomView

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import androidx.fragment.app.DialogFragment
import com.cmzsoft.weather.R;

class PermissionDialogFragment(
    private val onAccept: () -> Unit
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_permission, null)

        val btnAccept = view.findViewById<Button>(R.id.btn_accept)
        val btnCancel = view.findViewById<Button>(R.id.btn_cancel)

        btnAccept.setOnClickListener {
            onAccept.invoke()
            dismiss()
        }

        btnCancel.setOnClickListener {
            dismiss()
        }

        builder.setView(view)
        return builder.create().apply {
            window?.setBackgroundDrawableResource(android.R.color.transparent)
        }
    }
}
