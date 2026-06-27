package com.agon.app.data.remote

import android.app.Activity
import android.util.Base64
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class GoogleSignInHelper(private val activity: Activity) {

    private val credentialManager = CredentialManager.create(activity)

    suspend fun signIn(serverClientId: String): GoogleProfile? = withContext(Dispatchers.IO) {
        if (serverClientId.isBlank()) return@withContext null

        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(serverClientId)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        try {
            val result = credentialManager.getCredential(activity, request)
            val credential = result.credential
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
            val idToken = googleIdTokenCredential.idToken
            parseIdToken(idToken)
        } catch (e: GetCredentialException) {
            null
        } catch (e: Exception) {
            null
        }
    }

    private fun parseIdToken(idToken: String): GoogleProfile? {
        return try {
            val parts = idToken.split(".")
            if (parts.size != 3) return null
            val payload = String(Base64.decode(parts[1], Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP), Charsets.UTF_8)
            val json = JSONObject(payload)
            GoogleProfile(
                name = json.optString("name", json.optString("email", "User").substringBefore("@")),
                email = json.optString("email", ""),
                picture = json.optString("picture", ""),
            )
        } catch (e: Exception) {
            null
        }
    }
}

data class GoogleProfile(
    val name: String,
    val email: String,
    val picture: String,
)
