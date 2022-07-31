package ru.viktorxdev.easybackend

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.sql.ResultSet
import java.sql.Timestamp
import java.time.Instant

@Repository
class PaymentRepository(
    private val jdbcTemplate: NamedParameterJdbcTemplate,
) {

    fun savePaymentInfo(accountId: Long, amount: BigDecimal) {
        jdbcTemplate.update(
            """
                insert into payments(account_id, amount, datetime_transaction, is_unloaded) 
                values (:accountId, :amount, :time, false)
            """.trimIndent(),
            mapOf(
                "accountId" to accountId,
                "amount" to amount,
                "time" to Timestamp.from(Instant.now()),
            )
        )
    }

    fun getNewPayments(): List<PaymentInfo> = jdbcTemplate.query(
        """
            update payments set is_unloaded = true
            where is_unloaded = false
            returning id, account_id, amount, datetime_transaction
        """.trimIndent()
    ) { row, _ -> PaymentInfo.from(row) }

}

data class PaymentInfo(
    val id: Long,
    val accountId: Long,
    val amount: BigDecimal,
    val datetimeTransaction: Instant
) {
    companion object {
        fun from(resultSet: ResultSet) = PaymentInfo(
            id = resultSet.getLong("id"),
            accountId = resultSet.getLong("account_id"),
            amount = resultSet.getBigDecimal("amount"),
            datetimeTransaction = resultSet.getTimestamp("datetime_transaction").toInstant()
        )
    }
}