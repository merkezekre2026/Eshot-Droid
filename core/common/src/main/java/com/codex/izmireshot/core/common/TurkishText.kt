package com.codex.izmireshot.core.common

import java.text.Normalizer
import java.util.Locale

object TurkishText {
    private val turkish = Locale("tr", "TR")

    fun normalizeForSearch(value: String): String {
        val lowered = value.lowercase(turkish)
            .replace('ı', 'i')
            .replace('İ', 'i')
        return Normalizer.normalize(lowered, Normalizer.Form.NFD)
            .replace("\\p{Mn}+".toRegex(), "")
            .replace('ğ', 'g')
            .replace('ü', 'u')
            .replace('ş', 's')
            .replace('ö', 'o')
            .replace('ç', 'c')
            .trim()
    }

    fun parseLineNumbers(value: String): List<Int> = value
        .split(',', ';', '/', '-', ' ')
        .mapNotNull { it.trim().toIntOrNull() }
        .distinct()
}
