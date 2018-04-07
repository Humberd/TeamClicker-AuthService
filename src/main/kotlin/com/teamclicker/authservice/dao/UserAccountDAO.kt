package com.teamclicker.authservice.dao

import java.util.*
import javax.persistence.*
import javax.validation.constraints.NotNull

@Entity
@Table(name = "userAccount")
class UserAccountDAO {
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @NotNull
    @Column(name = "createdAt", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    var createdAt: Date? = null

    @OneToOne(fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true, optional = true)
    @JoinColumn(name = "deletionId", nullable = true)
    var deletion: UserAccountDeletionDAO? = null

    @OneToOne(fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true, optional = true)
    @JoinColumn(name = "emailPasswordAuthId", nullable = true)
    var emailPasswordAuth: EmailPasswordAuthDAO? = null

    @OneToOne(fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true, optional = true)
    @JoinColumn(name = "facebookAuthId", nullable = true)
    var facebookAuth: FacebookAuthDAO? = null

    @ManyToMany()
    @JoinTable(
        name = "userAccountUserRole",
        joinColumns = [JoinColumn(name = "userAccountId")],
        inverseJoinColumns = [JoinColumn(name = "userRoleId")]
    )
    var roles: Set<UserRoleDAO> = emptySet()

    @PrePersist
    protected fun onCreate() {
        createdAt = Date()
    }

    fun isDeleted() = deletion !== null
}
