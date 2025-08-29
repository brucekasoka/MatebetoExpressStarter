package com.matebeto.express.util

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

fun seedTestUsers() {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    val testUsers = listOf(
        TestUser("admin@example.com", "McDevnold123", "Admin Chileshe", "admin", "+260971000001"),
        TestUser("mwansa@matebeto.com", "Matebeto123", "Chef Mwansa", "restaurateur", "+260971000002"),
        TestUser("lombe@matebeto.com", "Staff123", "Staff Lombe", "staff", "+260971000003"),
        TestUser("banda@gmail.com", "Customer123", "Customer Banda", "customer", "+260971000004")
    )

    seedNextUser(auth, db, testUsers, 0)
}

private fun seedNextUser(
    auth: FirebaseAuth,
    db: FirebaseFirestore,
    users: List<TestUser>,
    index: Int
) {
    if (index >= users.size) {
        Log.d("Seeder", "✅ All users processed.")
        return
    }

    val user = users[index]

    db.collection("users")
        .whereEqualTo("email", user.email)
        .get()
        .addOnSuccessListener { snapshot ->
            if (snapshot.isEmpty) {
                auth.createUserWithEmailAndPassword(user.email, user.password)
                    .addOnSuccessListener { result ->
                        val uid = result.user?.uid ?: return@addOnSuccessListener
                        val userData = mapOf(
                            "uid" to uid,
                            "name" to user.name,
                            "email" to user.email,
                            "role" to user.role,
                            "phone" to user.phone,
                            "createdAt" to FieldValue.serverTimestamp()
                        )
                        db.collection("users").document(uid).set(userData)
                            .addOnSuccessListener {
                                Log.d("Seeder", "✅ ${user.name} seeded.")
                                seedNextUser(auth, db, users, index + 1)
                            }
                            .addOnFailureListener { e ->
                                Log.e("Seeder", "❌ Firestore write failed for ${user.email}", e)
                                seedNextUser(auth, db, users, index + 1)
                            }
                    }
                    .addOnFailureListener { e ->
                        Log.e("Seeder", "❌ Auth creation failed for ${user.email}", e)
                        seedNextUser(auth, db, users, index + 1)
                    }
            } else {
                Log.d("Seeder", "⚠️ ${user.email} already exists.")
                seedNextUser(auth, db, users, index + 1)
            }
        }
        .addOnFailureListener { e ->
            Log.e("Seeder", "❌ Firestore lookup failed for ${user.email}", e)
            seedNextUser(auth, db, users, index + 1)
        }
}

data class TestUser(
    val email: String,
    val password: String,
    val name: String,
    val role: String,
    val phone: String
)