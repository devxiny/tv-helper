package com.tools.tvhelper

import java.io.Serializable

data class TvControlConfig(
    val title: String,
    val elements: List<UiElement>
) : Serializable {

    data class UiElement(
        val type: String, // "button" or "input"
        val id: String,
        val label: String,
        val style: String? = null, // e.g. "primary", "danger"
        val action: String? = null,
        val bindInput: String? = null // For buttons, ID of input to send with
    ) : Serializable

    class Builder {
        private var title: String = "TV Remote"
        private val elements = mutableListOf<UiElement>()

        fun setTitle(title: String) = apply { this.title = title }

        fun addButton(id: String, label: String, style: String = "default", bindInput: String? = null) = apply {
            elements.add(UiElement("button", id, label, style, null, bindInput))
        }

        fun addInput(id: String, label: String) = apply {
            elements.add(UiElement("input", id, label))
        }

        fun build() = TvControlConfig(title, elements)
    }
}
