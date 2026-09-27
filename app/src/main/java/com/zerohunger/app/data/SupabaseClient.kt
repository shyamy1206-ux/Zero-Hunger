package com.zerohunger.app.data

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.serializer.KotlinXSerializer
import io.github.jan.supabase.storage.Storage
import kotlinx.serialization.json.Json

object SupabaseClient {
    // I grabbed this URL from your screenshot!
    const val SUPABASE_URL = "https://srwxjtbenuajfukcllgg.supabase.co"
    
    const val SUPABASE_KEY = "sb_publishable_AZz4aNx0GyhMi_z3e2lveQ_q91cMDtk"

    val client = createSupabaseClient(
        supabaseUrl = SUPABASE_URL,
        supabaseKey = SUPABASE_KEY
    ) {
        defaultSerializer = KotlinXSerializer(Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
            encodeDefaults = true
        })
        install(Postgrest)
        install(Auth)
        install(Storage)
        install(Realtime)
    }
}
