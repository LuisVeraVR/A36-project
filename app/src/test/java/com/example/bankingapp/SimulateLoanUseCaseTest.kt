package com.example.bankingapp

import com.example.bankingapp.domain.usecase.loan.SimulateLoanUseCase
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
/**
 * Tests unitarios para SimulateLoanUseCase.
 *
 * Verifica los cálculos del simulador de préstamos.
 */
class SimulateLoanUseCaseTest {

    private lateinit var simulateLoanUseCase: SimulateLoanUseCase

    @Before
    fun setup() {
        simulateLoanUseCase = SimulateLoanUseCase()
    }

    @Test
    fun `simulate loan with valid parameters returns success`() {
        // Given
        val amount = 10000.0
        val annualRate = 12.0
        val termMonths = 12

        // When
        val result = simulateLoanUseCase(amount, annualRate, termMonths)

        // Then
        assertTrue(result.isSuccess)
        val simulation = result.getOrNull()
        assertNotNull(simulation)

        // Verificar que la cuota mensual esté en el rango esperado
        // Para $10,000 al 12% anual en 12 meses, la cuota debería ser ~$888
        assertTrue(simulation!!.monthlyPayment > 880.0)
        assertTrue(simulation.monthlyPayment < 900.0)

        // Verificar total a pagar
        assertTrue(simulation.totalToPay > 10600.0)
        assertTrue(simulation.totalToPay < 10700.0)
    }

    @Test
    fun `simulate loan with zero interest rate`() {
        // Given
        val amount = 5000.0
        val annualRate = 0.0
        val termMonths = 10

        // When
        val result = simulateLoanUseCase(amount, annualRate, termMonths)

        // Then
        assertTrue(result.isSuccess)
        val simulation = result.getOrNull()
        assertNotNull(simulation)

        // Con tasa 0%, la cuota debería ser exactamente amount/months
        assertEquals(500.0, simulation!!.monthlyPayment, 0.01)
        assertEquals(5000.0, simulation.totalToPay, 0.01)
        assertEquals(0.0, simulation.totalInterest, 0.01)
    }

    @Test
    fun `simulate loan with high interest rate`() {
        // Given
        val amount = 20000.0
        val annualRate = 24.0  // 24% anual
        val termMonths = 24

        // When
        val result = simulateLoanUseCase(amount, annualRate, termMonths)

        // Then
        assertTrue(result.isSuccess)
        val simulation = result.getOrNull()
        assertNotNull(simulation)

        // Verificar que hay intereses significativos
        assertTrue(simulation!!.totalInterest > 5000.0)
        assertTrue(simulation.totalToPay > 25000.0)
    }

    @Test
    fun `simulate loan with short term`() {
        // Given
        val amount = 1000.0
        val annualRate = 12.0
        val termMonths = 3

        // When
        val result = simulateLoanUseCase(amount, annualRate, termMonths)

        // Then
        assertTrue(result.isSuccess)
        val simulation = result.getOrNull()
        assertNotNull(simulation)

        // Con plazo corto, la cuota mensual debería ser alta
        assertTrue(simulation!!.monthlyPayment > 330.0)
        assertTrue(simulation.monthlyPayment < 350.0)
    }

    @Test
    fun `simulate loan with long term`() {
        // Given
        val amount = 50000.0
        val annualRate = 9.0
        val termMonths = 60  // 5 años

        // When
        val result = simulateLoanUseCase(amount, annualRate, termMonths)

        // Then
        assertTrue(result.isSuccess)
        val simulation = result.getOrNull()
        assertNotNull(simulation)

        // Con plazo largo, la cuota mensual debería ser más baja
        assertTrue(simulation!!.monthlyPayment > 1000.0)
        assertTrue(simulation.monthlyPayment < 1100.0)

        // Pero el total a pagar será mayor por más intereses
        assertTrue(simulation.totalToPay > 62000.0)
    }

    @Test
    fun `simulate loan with negative amount returns failure`() {
        // Given
        val amount = -1000.0
        val annualRate = 12.0
        val termMonths = 12

        // When
        val result = simulateLoanUseCase(amount, annualRate, termMonths)

        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
    }

    @Test
    fun `simulate loan with zero amount returns failure`() {
        // Given
        val amount = 0.0
        val annualRate = 12.0
        val termMonths = 12

        // When
        val result = simulateLoanUseCase(amount, annualRate, termMonths)

        // Then
        assertTrue(result.isFailure)
    }

    @Test
    fun `simulate loan with negative interest rate returns failure`() {
        // Given
        val amount = 1000.0
        val annualRate = -5.0
        val termMonths = 12

        // When
        val result = simulateLoanUseCase(amount, annualRate, termMonths)

        // Then
        assertTrue(result.isFailure)
    }

    @Test
    fun `simulate loan with zero term returns failure`() {
        // Given
        val amount = 1000.0
        val annualRate = 12.0
        val termMonths = 0

        // When
        val result = simulateLoanUseCase(amount, annualRate, termMonths)

        // Then
        assertTrue(result.isFailure)
    }

    @Test
    fun `simulate loan with negative term returns failure`() {
        // Given
        val amount = 1000.0
        val annualRate = 12.0
        val termMonths = -6

        // When
        val result = simulateLoanUseCase(amount, annualRate, termMonths)

        // Then
        assertTrue(result.isFailure)
    }

    @Test
    fun `verify monthly payment multiplied by term equals total to pay`() {
        // Given
        val amount = 15000.0
        val annualRate = 15.0
        val termMonths = 18

        // When
        val result = simulateLoanUseCase(amount, annualRate, termMonths)

        // Then
        assertTrue(result.isSuccess)
        val simulation = result.getOrNull()!!

        // Verificar relación matemática
        val calculatedTotal = simulation.monthlyPayment * termMonths
        assertEquals(simulation.totalToPay, calculatedTotal, 0.01)
    }

    @Test
    fun `verify total interest is total minus principal`() {
        // Given
        val amount = 8000.0
        val annualRate = 18.0
        val termMonths = 15

        // When
        val result = simulateLoanUseCase(amount, annualRate, termMonths)

        // Then
        assertTrue(result.isSuccess)
        val simulation = result.getOrNull()!!

        // Verificar que interés = total - principal
        val calculatedInterest = simulation.totalToPay - amount
        assertEquals(simulation.totalInterest, calculatedInterest, 0.01)
    }
}