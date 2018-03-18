package com.teamclicker.authservice.dao

import java.util.*
import javax.persistence.*
import javax.validation.constraints.NotNull

@Entity
@Table(name = "userAccountDeletion")
class UserAccountDeletionDAO {
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @NotNull
    @Column(name = "createdAt", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    var createdAt: Date? = null

    @PrePersist
    protected fun onCreate() {
        createdAt = Date()
    }
}
