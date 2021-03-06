package org.spreadme.pdfgadgets.ui.pdfview

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.rememberDialogState
import org.bouncycastle.tsp.TimeStampToken
import org.spreadme.pdfgadgets.model.Signature
import org.spreadme.pdfgadgets.resources.R
import org.spreadme.pdfgadgets.ui.common.Dialog
import org.spreadme.pdfgadgets.ui.common.Tipable
import org.spreadme.pdfgadgets.ui.theme.LocalExtraColors
import org.spreadme.pdfgadgets.utils.format
import java.security.cert.X509Certificate

@Composable
fun SignatureDetail(
    signature: Signature,
    enable: MutableState<Boolean> = mutableStateOf(false)
) {
    val enableState = remember { enable }
    if (enableState.value) {
        Dialog(
            onClose = { enable.value = false },
            title = signature.fieldName,
            resizable = true,
            state = rememberDialogState(width = 560.dp, height = 420.dp)
        ) {
            //Verify Info
            VerifyDetail(signature)
            //Certificate Info
            CertificateDetail(signature.signatureResult.signingCertificate)
            //Timestamp info
            signature.signatureResult.timeStampToken?.let {
                TimeStampDetail(it)
            }
        }
    }
}

@Composable
fun VerifyDetail(signature: Signature) {
    val signatureVerifyUIState = if (signature.signatureResult.verifySignature) {
        if (signature.lastSignatureCoversWholeDocument) {
            SignatureVerifyUIState("????????????", verifySuccess())
        } else {
            SignatureVerifyUIState("?????????????????????", verifyWarning())
        }
    } else {
        SignatureVerifyUIState("????????????", verifyError())
    }

    val signatureVerifyUIColor = signatureVerifyUIState.uiColor
    Row(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth().height(42.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(signatureVerifyUIColor.backgound)
            .border(
                1.dp,
                signatureVerifyUIColor.border,
                RoundedCornerShape(8.dp)
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(R.Icons.signature_verify),
            contentDescription = "verify result",
            tint = signatureVerifyUIColor.iconColor,
            modifier = Modifier.padding(start = 16.dp).size(16.dp)
        )
        Text(
            signatureVerifyUIState.message,
            modifier = Modifier.padding(8.dp),
            style = MaterialTheme.typography.subtitle2,
            color = signatureVerifyUIColor.textColor,
        )
    }
}

@Composable
fun CertificateDetail(certificate: X509Certificate) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colors.surface),
    ) {
        CertificateRow("??????", certificate.version.toString())
        CertificateRow("?????????", certificate.serialNumber.toString(16))
        CertificateRow("????????????", certificate.sigAlgName)
        CertificateRow("??????", certificate.subjectX500Principal.toString())
        CertificateRow("?????????", certificate.issuerX500Principal.toString())
        CertificateRow("??????????????????", certificate.notBefore.format())
        CertificateRow("??????????????????", certificate.notAfter.format())
    }
}

@Composable
fun CertificateRow(
    title: String,
    content: String
) {
    Row(
        modifier = Modifier.fillMaxWidth().height(32.dp).padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.weight(0.2f),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                title,
                color = MaterialTheme.colors.onSurface,
                style = MaterialTheme.typography.caption,
            )
        }
        Box(
            modifier = Modifier.weight(0.8f),
            contentAlignment = Alignment.CenterStart
        ) {
            Tipable(content) {
                Text(
                    content,
                    color = MaterialTheme.colors.onSurface,
                    style = MaterialTheme.typography.overline,
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun TimeStampDetail(timeStampToken: TimeStampToken) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colors.surface),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().height(32.dp).padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.weight(0.2f),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    "??????????????????",
                    color = MaterialTheme.colors.onSurface,
                    style = MaterialTheme.typography.caption
                )
            }
            Box(
                modifier = Modifier.weight(0.8f),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    timeStampToken.timeStampInfo.genTime.toString(),
                    color = MaterialTheme.colors.onSurface,
                    style = MaterialTheme.typography.overline
                )
            }
        }
    }
}

data class SignatureVerifyUIState(
    val message: String,
    val uiColor: SignatureVerifyUIColor
)

data class SignatureVerifyUIColor(
    val backgound: Color,
    val border: Color,
    val iconColor: Color,
    val textColor: Color
)

@Composable
fun verifySuccess(): SignatureVerifyUIColor = SignatureVerifyUIColor(
    LocalExtraColors.current.successBackground,
    LocalExtraColors.current.successBorder,
    LocalExtraColors.current.success,
    LocalExtraColors.current.onSuccess
)

@Composable
fun verifyWarning(): SignatureVerifyUIColor = SignatureVerifyUIColor(
    LocalExtraColors.current.warningBackground,
    LocalExtraColors.current.warningBorder,
    LocalExtraColors.current.warning,
    LocalExtraColors.current.onWarning
)

@Composable
fun verifyError(): SignatureVerifyUIColor = SignatureVerifyUIColor(
    LocalExtraColors.current.errorBackground,
    LocalExtraColors.current.errorBorder,
    LocalExtraColors.current.error,
    LocalExtraColors.current.onError
)