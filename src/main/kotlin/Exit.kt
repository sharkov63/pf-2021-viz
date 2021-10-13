import kotlin.system.exitProcess

fun exitHelp() {
    TODO("Write help")
    exitProcess(0)
}

fun exitInvalidArgs() {
    println("Incorrect arguments. Use -h or --help to see possible options.")
    exitProcess(1)
}

fun exitEmptyData() {
    println("Empty data. Nothing to draw.")
    exitProcess(0)
}

fun exitNegativeValues(diagramType: DiagramType, el: DataElement) {
    println("Cannot create diagram of type $diagramType. Record \"${el.label}\" has negative value ${el.value}")
    exitProcess(1)
}

fun exitPieZeroSum() {
    println("Cannot create pie diagram. The values have zero sum.")
    exitProcess(1)
}