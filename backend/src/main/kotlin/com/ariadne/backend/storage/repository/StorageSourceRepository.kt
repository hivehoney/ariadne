package com.ariadne.backend.storage.repository

import com.ariadne.backend.storage.domain.StorageSource
import org.springframework.data.jpa.repository.JpaRepository

interface StorageSourceRepository : JpaRepository<StorageSource, Long> {

}