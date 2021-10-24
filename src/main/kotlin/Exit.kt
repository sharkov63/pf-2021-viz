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
    println("java -jar viz.jar [-i INPUT_FILE] [SORT_FLAG] [-d DIAGRAM_TYPE] ...            ")
    println("                                   ... [-p PADDING] [-s SCALE] [-o OUTPUT_FILE]")
    println("                                                                               ")
    println("If input file is not specified, or can't be read, the data is taken from stdin.")
    println("Input format: each diagram record is on a separate line.                       ")
    println("Each record consists of a text label and a real number, separated by spaces.   ")
    println("                                                                               ")
    println("--sort flag sorts records by value (--rsort in reverse order,                  ")
    println("                                   --lsort in lexicographical order by labels) ")
    println("                                                                               ")
    println("Supported diagram types:                                                       ")
    println("  BAR diagram (-d bar, -d column, -d histogram)                                ")
    println("  LINE diagram (-d line, -d graph, -d plot, -d curve)                          ")
    println("  PIE diagram (-d pie, -d circle, -d round)                                    ")
    println("  AREA diagram (-d area, -d fill)                                              ")
    println("                                                                               ")
    println("Default scale is 400. Default padding is 50.                                   ")
    println("                                                                               ")
    println("Use --no-window to hide window and --silent or --quiet to enable silent mode.  ")
    println("                                                                               ")
    println("If output file is specified, writes the diagram to this file in PNG format.    ")
    exitProcess(0)
}

fun exitUnknownOption(option: String) {
    println("Unknown option \"$option\". Use -h or --help to see possible options.")
    exitProcess(1)
}

fun exitOptionRequiresParameter(option: Option) {
    println("Option \"$option\" requires a parameter specified after it. Use -h or --help to see possible options.")
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