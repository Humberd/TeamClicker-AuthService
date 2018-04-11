package com.teamclicker.authservice.dao

import java.util.*
import javax.persistence.*
import javax.validation.constraints.NotNull

@Entity
@Table(
    name = "passwordReset",
    indexes = [Index(name = "validTokenIndex", columnList = "expiresAt,token")]
)
class PasswordResetDAO {
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @NotNull
    @Column(name = "createdAt", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    var createdAt: Date? = null

    @NotNull
    @Column(name = "expiresAt", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    var expiresAt: Date? = null

    @NotNull
    @Column(name = "token", nullable = false)
    var token: String? = null

    @PrePersist
    protected fun onCreate() {
        createdAt = Date()
    }
}