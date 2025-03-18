package com.example.dpproject.Activity

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import com.example.dpproject.R

class WithdrawDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.dialog_withdraw, null)

        val amountEditText = view.findViewById<EditText>(R.id.amountEditText)

        builder.setView(view)
            .setTitle("Withdraw Money")
            .setPositiveButton("Withdraw") { dialog, _ ->
                val amount = amountEditText.text.toString().toDoubleOrNull()
                if (amount != null && amount > 0) {
                    // Call a function to withdraw money from the wallet
                    (activity as WalletActivity).withdrawMoney(amount)
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }

        return builder.create()
    }
}