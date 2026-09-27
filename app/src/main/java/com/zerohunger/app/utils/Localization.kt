package com.zerohunger.app.utils

import androidx.compose.runtime.mutableStateOf

enum class Language { ENGLISH, HINDI, MARATHI }

object LanguageManager {
    var currentLanguage = mutableStateOf(Language.ENGLISH)
}

object AppStrings {
    // Welcome Screen
    val zeroHunger = mapOf(Language.ENGLISH to "Zero Hunger", Language.HINDI to "ज़ीरो हंगर", Language.MARATHI to "झिरो हंगर")
    val donateFood = mapOf(Language.ENGLISH to "Donate", Language.HINDI to "दान करें", Language.MARATHI to "दान करा")
    val requestFood = mapOf(Language.ENGLISH to "Request", Language.HINDI to "अनुरोध करें", Language.MARATHI to "विनंती करा")
    val liveMap = mapOf(Language.ENGLISH to "Live Map", Language.HINDI to "लाइव मैप", Language.MARATHI to "थेट नकाशा")
    val donateDesc = mapOf(Language.ENGLISH to "Share surplus food", Language.HINDI to "अतिरिक्त भोजन साझा करें", Language.MARATHI to "अतिरिक्त अन्न सामायिक करा")
    val requestDesc = mapOf(Language.ENGLISH to "Get meals nearby", Language.HINDI to "आसपास भोजन प्राप्त करें", Language.MARATHI to "जवळपास जेवण मिळवा")
    val mapDesc = mapOf(Language.ENGLISH to "Find food around you", Language.HINDI to "अपने आस-पास भोजन खोजें", Language.MARATHI to "तुमच्या आजूबाजूला अन्न शोधा")

    // Community Hub
    val communityHub = mapOf(Language.ENGLISH to "Community Hub", Language.HINDI to "कम्युनिटी हब", Language.MARATHI to "कम्युनिटी हब")
    val howCanWeHelp = mapOf(Language.ENGLISH to "How can we help today?", Language.HINDI to "हम आज आपकी कैसे मदद कर सकते हैं?", Language.MARATHI to "आम्ही आज तुमची कशी मदत करू शकतो?")
    val donateAction = mapOf(Language.ENGLISH to "Donate Food", Language.HINDI to "खाना दान करें", Language.MARATHI to "अन्न दान करा")
    val requestAction = mapOf(Language.ENGLISH to "Request Food", Language.HINDI to "खाने का अनुरोध करें", Language.MARATHI to "अन्नाची विनंती करा")
    val findNearbyAction = mapOf(Language.ENGLISH to "Find Nearby Food", Language.HINDI to "पास में खाना खोजें", Language.MARATHI to "जवळचे अन्न शोधा")
    val trackRequestAction = mapOf(Language.ENGLISH to "Track My Request", Language.HINDI to "मेरा अनुरोध ट्रैक करें", Language.MARATHI to "माझ्या विनंतीचा मागोवा घ्या")
    val helpAction = mapOf(Language.ENGLISH to "Get Help", Language.HINDI to "सहायता प्राप्त करें", Language.MARATHI to "मदत मिळवा")
    val trackDesc = mapOf(Language.ENGLISH to "View your live pickup status", Language.HINDI to "अपनी लाइव पिकअप स्थिति देखें", Language.MARATHI to "तुमची थेट पिकअप स्थिती पहा")
    val helpDesc = mapOf(Language.ENGLISH to "Food safety and emergency contact", Language.HINDI to "खाद्य सुरक्षा और आपातकालीन संपर्क", Language.MARATHI to "अन्न सुरक्षा आणि आपत्कालीन संपर्क")

    // Helper
    fun get(map: Map<Language, String>): String {
        return map[LanguageManager.currentLanguage.value] ?: map[Language.ENGLISH]!!
    }
}
