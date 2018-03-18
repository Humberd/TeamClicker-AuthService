package com.teamclicker.authservice.mappers

abstract class AbstractMapper<FROM, TO> {
    abstract fun parse(from: FROM): TO
}