package com.project.himanshu.equeue.data

data class QrCodeReadRespons(
    var code_id: String?,
    var code_reading_status: Boolean?,
    var ticket_price: String?,
    var ticket_category: String?
) {
}