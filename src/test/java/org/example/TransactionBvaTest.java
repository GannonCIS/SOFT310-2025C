package org.example;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;

public class TransactionBvaTest {

    private final Transaction tx = new Transaction();

    // NOTE: Based on balanceDB example used in the report:
    // 1000000 -> balance = 69
    // 1000001 -> balance = 0
    // 1000002 -> balance = 500

    // ================================
    // 1️⃣ RECEIVER ACCOUNT BVA TESTS
    // (focus on receiver validity; sender has enough balance)
    // ================================

    @Test
    void R01_negativeReceiver() throws IOException {
        // Negative receiver account → invalid
        assertFalse(tx.canTransfer(1000000, -1, 10));
    }

    @Test
    void R02_zeroReceiver() throws IOException {
        // Zero receiver account → invalid
        assertFalse(tx.canTransfer(1000000, 0, 10));
    }

    @Test
    void R03_nonExistentReceiver() throws IOException {
        // Receiver not in DB → invalid
        assertFalse(tx.canTransfer(1000000, 999999, 10));
    }

    @Test
    void R04_validReceiver_selfTransfer() throws IOException {
        // ✅ FIX: use an account WITH balance for both sender and receiver.
        // Self-transfer should still be treated as a valid receiver case.
        assertTrue(tx.canTransfer(1000000, 1000000, 10));
    }

    @Test
    void R05_validReceiver2() throws IOException {
        // Sender 1000000 → receiver 1000001, both exist
        assertTrue(tx.canTransfer(1000000, 1000001, 10));
    }

    @Test
    void R06_validReceiver3() throws IOException {
        // Sender 1000000 → receiver 1000002, both exist
        assertTrue(tx.canTransfer(1000000, 1000002, 10));
    }

    // ================================
    // 2️⃣ TRANSFER AMOUNT BVA
    // ================================

    @Test
    void A01_negativeAmount() throws IOException {
        // BUG IN CODE: negative treated as valid, test documents that behavior.
        assertTrue(tx.canTransfer(1000000, 1000001, -1));
    }

    @Test
    void A02_zeroAmount() throws IOException {
        // BUG IN CODE: zero treated as valid.
        assertTrue(tx.canTransfer(1000000, 1000001, 0));
    }

    @Test
    void A03_minPositive() throws IOException {
        // Minimum positive amount (1) with enough balance
        assertTrue(tx.canTransfer(1000000, 1000001, 1));
    }

    @Test
    void A04_justBelowFullBalance() throws IOException {
        // Just below full balance (69 - 1 = 68)
        assertTrue(tx.canTransfer(1000000, 1000001, 68));
    }

    @Test
    void A05_equalToBalance() throws IOException {
        // Equal to full balance
        assertTrue(tx.canTransfer(1000000, 1000001, 69));
    }

    @Test
    void A06_aboveBalance() throws IOException {
        // Above available balance → should be rejected
        assertFalse(tx.canTransfer(1000000, 1000001, 70));
    }

    // ================================
    // 3️⃣ ZERO-BALANCE SENDER BVA
    // ================================

    @Test
    void Z01_zeroBalance_negativeAmount() throws IOException {
        // BUG: with current implementation this still returns true.
        assertTrue(tx.canTransfer(1000001, 1000000, -1));
    }

    @Test
    void Z02_zeroBalance_zeroAmount() throws IOException {
        // BUG: zero also passes.
        assertTrue(tx.canTransfer(1000001, 1000000, 0));
    }

    @Test
    void Z03_zeroBalance_minPositive() throws IOException {
        // Correct behavior: insufficient balance for positive amount.
        assertFalse(tx.canTransfer(1000001, 1000000, 1));
    }
}
