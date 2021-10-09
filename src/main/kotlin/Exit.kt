import kotlin.system.exitProcess

fun exitHelp() {
    TODO("Write help")
    exitProcess(0)
}

fun exitInvalidArgs() {
    println("Incorrect arguments. Use -h or --help to see possible options.")
    exitProcess(1)
}