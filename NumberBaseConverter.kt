package converter
import java.math.BigInteger
import java.math.BigDecimal
import java.math.RoundingMode

fun convertFromDecimal(decimalNumber: BigInteger, targetBase: BigInteger): String {
    var conversionResult = ""
    var symbol: Char;
    var decimalNumber = decimalNumber
    while (decimalNumber / targetBase != BigInteger.ZERO) {
        val remainder = (decimalNumber % targetBase).toInt()
        if (remainder > 9) symbol = (remainder - 10 + 65).toChar()
        else symbol = (remainder + 48).toChar()
        conversionResult = symbol + conversionResult
        decimalNumber = decimalNumber / targetBase
    }
    if (decimalNumber >= BigInteger.ZERO) {
        var code = decimalNumber.toInt()
        if (code > 9) symbol = (code - 10 + 65).toChar()
        else symbol = (code + 48).toChar()
        conversionResult = symbol + conversionResult
    }
    return conversionResult
}

fun convertToDecimal(sourceNumber: String, sourceBase: Int): BigInteger {
    var decimalNumber = BigInteger.ZERO
    for (i in sourceNumber.indices) {
        val m: Int
        val indice = sourceNumber.length - 1 - i
        val code = sourceNumber[indice].code
        if (87 <= code && code < 122) m = code - 87
        else m = code - 48
        var factor: Long = 1
        if (i > 0) {
            repeat(i) { factor *= sourceBase }
        }
        decimalNumber += BigInteger.valueOf(m.toLong()) * BigInteger.valueOf(factor)
    }
    return decimalNumber
}

fun convertToDecimalFractional(sourceNumber: String, sourceBase: Int): BigDecimal {
    var decimalNumber = BigDecimal.ZERO 
    for (i in sourceNumber.indices) {
        val m: Int
        val code = sourceNumber[i].code
        if (87 <= code && code < 122) m = code - 87
        else m = code - 48
        var factor: Double = 1.0
        repeat(i + 1) { factor /= sourceBase }
        decimalNumber += BigDecimal.valueOf(m.toLong()) * BigDecimal.valueOf(factor)
    }
    return decimalNumber
}

fun convertFromDecimalFraction(decimalNumberI: BigDecimal, targetBase: BigDecimal): String {
    var conversionResult = ""
    var symbol: Char;
    var decimalNumber = decimalNumberI
    var fractionalPart = decimalNumber.remainder( BigDecimal.ONE )
    while (fractionalPart.compareTo(BigDecimal.ZERO) != 0) {
        decimalNumber = decimalNumber * targetBase
        fractionalPart = decimalNumber.remainder( BigDecimal.ONE )
        val intPart = decimalNumber.toInt()
        decimalNumber = fractionalPart
        if (intPart > 9) symbol = (intPart + 87).toChar()
        else symbol = (intPart + 48).toChar()
        conversionResult = conversionResult + symbol
        if (conversionResult.length >= 5) break
    }
    return conversionResult
}

fun main() {
    while (true) {
        println("Enter two numbers in format: {source base} {target base} (To quit type /exit)")
        var input = readln()
        if (input == "/exit") return
        val (sourceBase, targetBase) = input.split(" ").map{ it.toInt() }
        while (true) {
            println("Enter number in base $sourceBase to convert to base $targetBase (To go back type /back)")
            input = readln()
            if (input == "/back") {
                println()
                break
            }
            var sourceNumber = input
            var conversionResult: String;
            var fractionPart = ""
            if ('.' in sourceNumber) {
                fractionPart = input.substringAfter('.')
                sourceNumber = input.substringBefore('.')
                if (sourceBase != 10) {
                    fractionPart = convertToDecimalFractional(fractionPart, sourceBase).toString()
                }
                if (targetBase != 10) {
                    fractionPart = convertFromDecimalFraction(BigDecimal(fractionPart).setScale(5, RoundingMode.CEILING), BigDecimal(targetBase))
                }
                while (fractionPart.length < 5) fractionPart += '0'
            }
            if (sourceBase != 10) {
                sourceNumber = convertToDecimal(sourceNumber, sourceBase).toString()
            }
            if (targetBase == 10) {
                conversionResult = sourceNumber.toString()
            } else conversionResult = convertFromDecimal(BigInteger(sourceNumber), BigInteger.valueOf(targetBase.toLong()))
            if (fractionPart != "") conversionResult += '.' + fractionPart
            println("Conversion result: ${conversionResult}\n")
        }
    }
}
