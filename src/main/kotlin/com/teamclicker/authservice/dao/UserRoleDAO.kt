package com.teamclicker.authservice.dao

import javax.persistence.*

@Entity
@Table(name = "userRole")
class UserRoleDAO {
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    var id: String? = null

    @ManyToMany(mappedBy = "roles")
    var users: List<UserAccountDAO> = emptyList()

    constructor()

    constructor(id: String) {
        this.id = id
    }
}