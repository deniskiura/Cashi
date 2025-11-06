package ke.kiura.cashi

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform