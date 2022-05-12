package org.spreadme.pdfgadgets.repository

import org.bouncycastle.asn1.ASN1InputStream
import org.bouncycastle.asn1.util.ASN1Dump
import java.io.ByteArrayInputStream

class ASN1Parser {

    fun parse(byteArray: ByteArray): String {
        val din = ASN1InputStream(ByteArrayInputStream(byteArray))
        val obj = din.readObject()
        return ASN1Dump.dumpAsString(obj, false)
    }
}