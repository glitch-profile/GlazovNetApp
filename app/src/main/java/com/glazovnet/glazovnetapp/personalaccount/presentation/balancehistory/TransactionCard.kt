package com.glazovnet.glazovnetapp.personalaccount.presentation.balancehistory

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.glazovnet.glazovnetapp.R
import com.glazovnet.glazovnetapp.personalaccount.domain.model.TransactionModel
import java.time.OffsetDateTime
import java.time.ZoneId

@Composable
fun TransactionCard(
    transaction: TransactionModel,
    onCardClicked: (transactionId: String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCardClicked.invoke(transaction.id) }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            val titleText = if (transaction.amount > 0 )
                stringResource(id = R.string.transaction_card_incoming_transaction_title)
            else stringResource(id = R.string.transaction_card_outcoming_transaction_title)
            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                text = titleText,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            val descriptionText = when (transaction.note) {
                "new_service_connected" -> stringResource(id = R.string.transaction_card_note_new_service)
                "monthly_payment" -> stringResource(id = R.string.transaction_card_note_montly_payment)
                null -> stringResource(id = R.string.transaction_card_note_empty)
                else -> transaction.note
            }
            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                text = descriptionText,
                overflow = TextOverflow.Ellipsis,
                maxLines = 2,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = String.format("%.2f", transaction.amount) + " ₽",
            style = MaterialTheme.typography.titleMedium,
//            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
@Preview(showBackground = true)
fun TransactionCardPreview() {
    TransactionCard(
        transaction = TransactionModel(
            id = "122323",
            transactionTimestamp = OffsetDateTime.now(ZoneId.systemDefault()),
            amount = -600f,
            note = "monthly_payment"
        ),
        onCardClicked = {}
    )
}