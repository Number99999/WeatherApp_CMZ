package com.cmzsoft.weather

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import androidx.fragment.app.DialogFragment


class PermissionDialogFragment(
    private val onRequestPermission: () -> Unit
) : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = LayoutInflater.from(context).inflate(R.layout.permission_custom_dialog, null)
        val btn = view.findViewById<Button>(R.id.btnGrantPermission)
        btn.setOnClickListener {
            dismiss()
            onRequestPermission()
        }
        return AlertDialog.Builder(requireContext())
            .setView(view)
            .setCancelable(true)
            .create()
    }
}