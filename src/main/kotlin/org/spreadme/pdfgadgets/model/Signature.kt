package org.spreadme.pdfgadgets.model

data class Signature(
    val fieldName: String,
    val signedLength: Long,
    val signatureResult: SignatureResult,
    val signatureCoversWholeDocument: Boolean,
    var position: Position? = null,
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Signature

        if (fieldName != other.fieldName) return false

        return true
    }

    override fun hashCode(): Int {
        return fieldName.hashCode()
    }
}