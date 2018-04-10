package com.teamclicker.authservice.dao

import java.util.*
import javax.persistence.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Entity
@Table(name = "emailPasswordAuth")
class EmailPasswordAuthDAO {
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @NotNull
    @Column(name = "createdAt", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    var createdAt: Date? = null

    @NotBlank
    @Column(name = "email", nullable = false)
    var email: String? = null

    @NotBlank
    @Column(name = "emailLc", nullable = false, unique = true)
    var emailLc: String? = null

    @NotBlank
    @Column(name = "password", nullable = false)
    var password: String? = null

    @OneToOne(fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true, optional = true)
    @JoinColumn(name = "passwordResetId", nullable = true)
    var passwordReset: PasswordResetDAO? = null

    @PrePersist
    protected fun onCreate() {
        createdAt = Date()
        lowerCaseFields()
    }

    @PreUpdate
    protected fun onUpdate() {
        lowerCaseFields()
    }

    private fun lowerCaseFields() {
        emailLc = email?.toLowerCase()
    }
}
