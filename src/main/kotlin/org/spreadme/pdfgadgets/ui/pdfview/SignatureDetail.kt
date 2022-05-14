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
import org.spreadme.pdfgadgets.utils.choose
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
            VerifyDetail(signature.signatureResult.verifySignature)
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
fun VerifyDetail(verify: Boolean) {
    Row(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth().height(42.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(
                verify.choose(
                    LocalExtraColors.current.successBackground,
                    LocalExtraColors.current.errorBackground
                )
            )
            .border(1.dp, LocalExtraColors.current.successBorder, RoundedCornerShape(8.dp)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(R.Icons.signature_verify),
            contentDescription = "verify result",
            tint = verify.choose(LocalExtraColors.current.success, LocalExtraColors.current.error),
            modifier = Modifier.padding(start = 16.dp).size(16.dp)
        )
        Text(
            verify.choose("签名有效", "签名无效"),
            modifier = Modifier.padding(8.dp),
            style = MaterialTheme.typography.subtitle2,
            color = verify.choose(LocalExtraColors.current.onSuccess, LocalExtraColors.current.onError),
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
        CertificateRow("版本", certificate.version.toString())
        CertificateRow("序列号", certificate.serialNumber.toString(16))
        CertificateRow("签名算法", certificate.sigAlgName)
        CertificateRow("主题", certificate.subjectX500Principal.toString())
        CertificateRow("颁发者", certificate.issuerX500Principal.toString())
        CertificateRow("有效起始日期", certificate.notBefore.format())
        CertificateRow("有效截止日期", certificate.notAfter.format())
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
                    "时间戳时间：",
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