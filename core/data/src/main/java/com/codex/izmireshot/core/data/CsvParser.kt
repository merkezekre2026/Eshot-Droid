package com.codex.izmireshot.core.data

class CsvParser {
    fun parse(content: String): List<Map<String, String>> {
        val normalized = content.removePrefix("\uFEFF").replace("\r\n", "\n")
        val lines = normalized.lineSequence().filter { it.isNotBlank() }.toList()
        if (lines.isEmpty()) return emptyList()
        val delimiter = detectDelimiter(lines.first())
        val headers = splitLine(lines.first(), delimiter).map { it.trim().removeSurrounding("\"") }
        return lines.drop(1).mapNotNull { line ->
            val cells = splitLine(line, delimiter)
            if (cells.isEmpty()) return@mapNotNull null
            headers.mapIndexed { index, header -> header to cells.getOrElse(index) { "" }.trim() }.toMap()
        }
    }

    private fun detectDelimiter(header: String): Char {
        val semicolons = header.count { it == ';' }
        val commas = header.count { it == ',' }
        return if (semicolons > commas) ';' else ','
    }

    private fun splitLine(line: String, delimiter: Char): List<String> {
        val result = mutableListOf<String>()
        val current = StringBuilder()
        var inQuotes = false
        var index = 0
        while (index < line.length) {
            val char = line[index]
            when {
                char == '"' && index + 1 < line.length && line[index + 1] == '"' -> {
                    current.append('"')
                    index++
                }
                char == '"' -> inQuotes = !inQuotes
                char == delimiter && !inQuotes -> {
                    result += current.toString().removeSurrounding("\"")
                    current.clear()
                }
                else -> current.append(char)
            }
            index++
        }
        result += current.toString().removeSurrounding("\"")
        return result
    }
}
