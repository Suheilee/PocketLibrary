package au.edu.curtin.madassignment2

data class UserEntity(
    val userId: String = "",
    val email: String? = null,
    val displayName: String? = null,
    val favoriteBookIds: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val lastSync: Long = System.currentTimeMillis()
)