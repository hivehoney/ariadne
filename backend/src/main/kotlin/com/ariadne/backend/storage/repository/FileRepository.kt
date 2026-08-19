package com.ariadne.backend.storage.repository

import com.ariadne.backend.storage.domain.File
import org.springframework.data.jpa.repository.JpaRepository

interface FileRepository : JpaRepository<File, Long> {

}