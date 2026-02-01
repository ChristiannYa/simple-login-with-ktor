package com.example.db

import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

// `suspendTransaction()` takes a block of code and runs it within a database transaction
// through the IO dispatchers.
// This is designed to offload blocking jobs work onto the thread pool
/**
 * Executes a database transaction in a suspending context.
 * Offloads blocking database operations to the IO dispatcher.
 */
suspend fun <T> suspendTransaction(
    block: Transaction.() -> T
): T = newSuspendedTransaction(Dispatchers.IO, statement = block)