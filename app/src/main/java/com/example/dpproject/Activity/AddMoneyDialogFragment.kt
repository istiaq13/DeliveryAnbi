package com.example.dpproject.Activity

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import com.example.dpproject.R

class AddMoneyDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.dialog_add_money, null)

        val amountEditText = view.findViewById<EditText>(R.id.amountEditText)

        builder.setView(view)
            .setTitle("Add Money")
            .setPositiveButton("Add") { dialog, _ ->
                val amount = amountEditText.text.toString().toDoubleOrNull()
                if (amount != null && amount > 0) {
                    // Call a function to add money to the wallet
                    (activity as WalletActivity).addMoney(amount)
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }

        return builder.create()
    }
}