import kotlin.system.exitProcess

/**
 * This package contains exit functions, which print a certain message,
 * and then call [exitProcess] with a corresponding exit status.
 *
 * Exit status 0 means the program finished with no errors.
 * Exit status 1 means the program finished with an error.
 */



fun exitHelp() {
    println("viz.jar is a program for drawing diagrams, based on skiko and skija libs.      ")
    println("                                                                               ")
    println("Usage:                                                                         ")
    println("java -jar viz.jar [-i INPUT_FILE] [-d DIAGRAM_TYPE] [-s SCALE] [-o OUTPUT_FILE]")
    println("                                                                               ")
    println("If input file is not specified, or can't be read, the data is taken from stdin.")
    println("Input format: each diagram record is on a separate line.                       ")
    println("Each record consists of a text label and a real number, separated by spaces.   ")
    println("Default scale is 400.                                                          ")
    println("                                                                               ")
    println("Supported diagram types:                                                       ")
    println("  BAR diagram (-d bar, -d column, -d histogram)                                ")
    println("  LINE diagram (-d line, -d graph)                                             ")
    println("  PIE diagram (-d pie, -d circle, -d round)                                    ")
    println("                                                                               ")
    println("If output file is specified, writes the diagram to this file in PNG format.    ")
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