package com.josecalvo.application.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class UpdateTrackRequest(
    val title: String? = null,
    val duration: Int? = null
)