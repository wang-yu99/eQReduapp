package com.example.eqr_edu.viewmodel

import android.app.Application
import androidx.compose.runtime.*
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.IOException

/**
 * Language enumeration
 */
enum class Language(val code: String, val displayName: String, val fileName: String) {
    ENGLISH("en", "English", "english.json"),
    ITALIAN("it", "Italiano", "italian.json"),
    CHINESE("zh", "中文", "chinese.json"),
}

/**
 * Language state data class
 */
data class LanguageState(
    val currentLanguage: Language = Language.ENGLISH,
    val translations: Map<String, String> = emptyMap(),
    val isLoading: Boolean = false,
    val error: String? = null
)

/**
 * ViewModel-based language manager
 * Uses StateFlow to manage state, follows MVVM architecture pattern
 */
class LanguageViewModel(application: Application) : AndroidViewModel(application) {

    // Private mutable state flow
    private val _languageState = MutableStateFlow(LanguageState())

    // Public read-only state flow
    val languageState: StateFlow<LanguageState> = _languageState.asStateFlow()

    init {
        // Load default language on initialization
        loadLanguage(Language.ENGLISH)
    }

    /**
     * Switch language
     */
    fun switchLanguage(language: Language) {
        if (_languageState.value.currentLanguage == language) {
            return
        }

        loadLanguage(language)
    }

    /**
     * Get translated text
     */
    fun getString(key: String): String {
        val translation = _languageState.value.translations[key]
        if (translation == null) {
            return key // Return key as fallback
        }
        return translation
    }

    /**
     * Get translated text with parameters
     */
    fun getString(key: String, vararg args: Any): String {
        val template = getString(key)
        return try {
            var result = template
            args.forEachIndexed { index, arg ->
                result = result.replace("{$index}", arg.toString())
                // Support common named parameters
                when (index) {
                    0 -> result = result.replace("{steps}", arg.toString())
                        .replace("{number}", arg.toString())
                        .replace("{error}", arg.toString())
                }
            }
            result
        } catch (e: Exception) {
            template
        }
    }

    /**
     * Get all available languages
     */
    fun getAvailableLanguages(): List<Language> = Language.values().toList()

    /**
     * Load translation file for specified language
     */
    private fun loadLanguage(language: Language) {
        viewModelScope.launch {
            try {
                // Set loading state
                _languageState.value = _languageState.value.copy(
                    isLoading = true,
                    error = null
                )
                // Read translation file from assets
                val translations = loadTranslationsFromAssets(language)

                // Update state
                _languageState.value = LanguageState(
                    currentLanguage = language,
                    translations = translations,
                    isLoading = false,
                    error = null
                )

            } catch (e: Exception) {
                // Set error state but keep previous language and translations
                _languageState.value = _languageState.value.copy(
                    isLoading = false,
                    error = "Failed to load ${language.displayName}: ${e.message}"
                )
            }
        }
    }

    /**
     * Load translation file from assets folder
     */
    private fun loadTranslationsFromAssets(language: Language): Map<String, String> {
        return try {
            val context = getApplication<Application>()
            val inputStream = context.assets.open(language.fileName)
            val jsonString = inputStream.bufferedReader().use { it.readText() }
            val jsonObject = JSONObject(jsonString)

            val translations = mutableMapOf<String, String>()
            jsonObject.keys().forEach { key ->
                translations[key] = jsonObject.getString(key)
            }

            translations.toMap()

        } catch (e: IOException) {
            throw e
        } catch (e: Exception) {
            throw e
        }
    }
}

/**
 * Compose helper function - Get current language state
 */
@Composable
fun LanguageViewModel.collectLanguageAsState(): State<LanguageState> {
    return languageState.collectAsState()
}

/**
 * Compose helper function - Get translated text
 */
@Composable
fun LanguageViewModel.stringResource(key: String): String {
    val languageState by collectLanguageAsState()
    return remember(languageState.translations, key) {
        getString(key)
    }
}

/**
 * Compose helper function - Get translated text with parameters
 */
@Composable
fun LanguageViewModel.stringResource(key: String, vararg args: Any): String {
    val languageState by collectLanguageAsState()
    return remember(languageState.translations, key, args.contentHashCode()) {
        getString(key, *args)
    }
}