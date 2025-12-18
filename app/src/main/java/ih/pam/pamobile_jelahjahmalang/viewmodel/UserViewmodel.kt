package ih.pam.pamobile_jelahjahmalang.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import ih.pam.pamobile_jelahjahmalang.model.FirestoreArrayContent
import ih.pam.pamobile_jelahjahmalang.model.FirestoreArray
import ih.pam.pamobile_jelahjahmalang.model.IntegerValue
import ih.pam.pamobile_jelahjahmalang.model.StringValue
import ih.pam.pamobile_jelahjahmalang.model.UserFields
import ih.pam.pamobile_jelahjahmalang.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import ih.pam.pamobile_jelahjahmalang.model.UserDocuments


class UserViewmodel : ViewModel() {
    private val _auth = FirebaseAuth.getInstance()

    private val _user = MutableStateFlow<UserDocuments?>(null)
    val user: StateFlow<UserDocuments?> = _user

    private val _authStatus = MutableStateFlow<String?>(null)
    val authStatus: StateFlow<String?> = _authStatus

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading


    fun loginUser(email: String, pass: String) {
        if (email.isBlank() || pass.isBlank()) {
            _authStatus.value = "Email dan Password wajib diisi"
            return
        }

        _isLoading.value = true
        _auth.signInWithEmailAndPassword(email, pass)
            .addOnSuccessListener {
                _isLoading.value = false
                _authStatus.value = "success"
            }
            .addOnFailureListener {
                _isLoading.value = false
                _authStatus.value = "Login Gagal: ${it.message}"
            }
    }

    fun registerUser(email: String, password: String, userName: String) {
        _isLoading.value = true
        _auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val uid = result.user?.uid
                if (uid != null) {
                    saveUserToFirestore(uid = uid, email, userName)
                }
            }
            .addOnFailureListener { exception ->
                _isLoading.value = false
                _authStatus.value = "Register Gagal : ${exception.message}"
            }
    }

    private fun saveUserToFirestore(uid: String, email: String, name: String) {
        viewModelScope.launch {
            try {
                // Siapkan data JSON
                val userData = UserDocuments(
                    fields = UserFields(
                        userName = StringValue(name),
                        email = StringValue(email),
                        totalPoints = IntegerValue("0")
                    )
                )

                // Panggil API (Pastikan fungsi createUserProfile ada di MissionApiService)
                RetrofitClient.instance.createUserProfile(uid, userData)

                _isLoading.value = false
                _authStatus.value = "success"

            } catch (e: Exception) {
                _isLoading.value = false
                _authStatus.value = "Gagal simpan profil: ${e.message}"
                e.printStackTrace()
            }
        }
    }

    fun fetchUser() {
        val currentUid: String = _auth.currentUser?.uid.toString()
        if (currentUid != null) {
            viewModelScope.launch {
                _isLoading.value = true
                try {
                    val response = RetrofitClient.instance.getUser(uid = currentUid)

                    _user.value = response

                    println("User Fetch Success: ${response.fields?.userName?.value}")
                } catch (e: Exception) {
                    e.printStackTrace()
                    _user.value = null
                } finally {
                    _isLoading.value = false
                }
            }
        }
    }

    fun resetAuthStatus() {
        _authStatus.value = null
    }

    fun completedMission(missionId: String, pointsToAdd: Int) {
        val currentUid: String? = _auth.currentUser?.uid
        if (currentUid != null) {
            viewModelScope.launch {
                try {
                    val currentUser = RetrofitClient.instance.getUser(uid = currentUid)
                    val currentField = currentUser.fields

                    val currentPoint = currentField?.totalPoints?.value?.toInt() ?: 0
                    val newPoint = currentPoint + pointsToAdd


                    val currentMissionList =
                        currentField?.completedMissions?.arrayValue?.values?.map { it.value }?.toMutableList()
                            ?: mutableListOf()

                    if (!currentMissionList.contains(missionId)) {

                        currentMissionList.add(missionId)

                        val listStringValues = currentMissionList.map { StringValue(it) }

                        val content = FirestoreArrayContent(
                            values = listStringValues
                        )

                        val newCompletedMissionList = FirestoreArray(
                            arrayValue = content
                        )
                        val updatedUser = UserDocuments(
                            fields = UserFields(
                                userName = currentField?.userName ?: StringValue("User"),
                                email = currentField?.email ?: StringValue(""),
                                totalPoints = IntegerValue(newPoint.toString()),
                                completedMissions = newCompletedMissionList
                            )
                        )
                        Log.d("UserViewModel", "Mengirim update: $updatedUser")

                        RetrofitClient.instance.createUserProfile(currentUid, updatedUser)

                        // F. Refresh UI
                        fetchUser()
                        _authStatus.value = "mission_completed"
                    } else {
                        _authStatus.value = "mission_already_completed"
                    }


                    val response = RetrofitClient.instance.getUser(uid = currentUid)

                    _user.value = response
                } catch (e: Exception) {
                    e.printStackTrace()
                    _user.value = null
                }
            }
        }
    }

}




