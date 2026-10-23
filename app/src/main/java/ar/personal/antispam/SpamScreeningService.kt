
package ar.personal.antispam

import android.content.Context
import android.provider.ContactsContract
import android.telecom.Call
import android.telecom.CallScreeningService

class SpamScreeningService : CallScreeningService() {

    override fun onScreenCall(callDetails: Call.Details) {
        val handle = callDetails.handle
        val number = handle?.schemeSpecificPart.orEmpty()

        if (number.isBlank()) {
            allowCall(callDetails)
            return
        }

        val prefs = getSharedPreferences(
            "antispam_rules", Context.MODE_PRIVATE
        )

        val contactsException =
            prefs.getBoolean("contacts_exception", true)

        if (contactsException && isInContacts(number)) {
            allowCall(callDetails)
            return
        }

        val blockedNumbers = prefs.getString(
            "blocked_numbers", ""
        ).orEmpty().lines().map { normalize(it) }.filter { it.isNotEmpty() }

        val blockedPrefixes = prefs.getString(
            "blocked_prefixes", ""
        ).orEmpty().lines().map { normalize(it) }.filter { it.isNotEmpty() }

        val cleanNumber = normalize(number)

        val shouldBlock =
            blockedNumbers.contains(cleanNumber) ||
            blockedPrefixes.any { cleanNumber.startsWith(it) }

        val response = CallResponse.Builder()
            .setDisallowCall(shouldBlock)
            .setRejectCall(shouldBlock)
            .setSkipCallLog(false)
            .setSkipNotification(false)
            .build()

        respondToCall(callDetails, response)
    }

    private fun allowCall(callDetails: Call.Details) {
        val response = CallResponse.Builder()
            .setDisallowCall(false)
            .setRejectCall(false)
            .setSkipCallLog(false)
            .setSkipNotification(false)
            .build()

        respondToCall(callDetails, response)
    }

    private fun normalize(value: String): String =
        value.filter { it.isDigit() }

    private fun isInContacts(number: String): Boolean {
        val uri = ContactsContract.PhoneLookup.CONTENT_FILTER_URI
            .buildUpon()
            .appendPath(number)
            .build()

        return try {
            contentResolver.query(
                uri,
                arrayOf(ContactsContract.PhoneLookup._ID),
                null,
                null,
                null
            )?.use { it.moveToFirst() } ?: false
        } catch (_: Exception) {
            false
        }
    }
}
