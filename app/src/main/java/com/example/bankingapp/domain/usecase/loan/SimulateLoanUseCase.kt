package com.example.bankingapp.domain.usecase.loan

import kotlin.math.pow

/**
 * Use Case para simular un préstamo.
 *
 * RF07 - HU09: Simulador de préstamo con cálculo de cuota y total a pagar.
 *
 * Fórmula utilizada: Sistema Francés (Cuota Fija)
 *
 * Cuota Mensual = P × [r(1+r)^n] / [(1+r)^n - 1]
 *
 * Donde:
 * - P = Monto del préstamo (principal)
 * - r = Tasa de interés mensual (tasa anual / 12 / 100)
 * - n = Número de pagos (meses)
 *
 * Total a Pagar = Cuota Mensual × Número de Meses
 *
 * Ejemplo:
 * - Monto: $10,000
 * - Tasa anual: 12%
 * - Plazo: 12 meses
 *
 * Tasa mensual = 12 / 12 / 100 = 0.01 (1%)
 * Cuota ≈ $888.49
 * Total ≈ $10,661.88
 */
class SimulateLoanUseCase {

    /**
     * Resultado de la simulación de préstamo.
     */
    data class LoanSimulation(
        val monthlyPayment: Double,
        val totalToPay: Double,
        val totalInterest: Double,
        val effectiveAnnualRate: Double
    )

    /**
     * Simula un préstamo y calcula la cuota mensual y el total a pagar.
     *
     * @param amount Monto del préstamo
     * @param annualRate Tasa de interés anual (porcentaje, ej: 12.5 para 12.5%)
     * @param termMonths Plazo en meses
     * @return Result con LoanSimulation o error si los parámetros son inválidos
     */
    operator fun invoke(
        amount: Double,
        annualRate: Double,
        termMonths: Int
    ): Result<LoanSimulation> {
        // Validaciones
        if (amount <= 0) {
            return Result.failure(IllegalArgumentException("El monto debe ser mayor a 0"))
        }

        if (annualRate < 0) {
            return Result.failure(IllegalArgumentException("La tasa de interés no puede ser negativa"))
        }

        if (termMonths <= 0) {
            return Result.failure(IllegalArgumentException("El plazo debe ser mayor a 0 meses"))
        }

        return try {
            // Caso especial: tasa 0% (préstamo sin intereses)
            if (annualRate == 0.0) {
                val monthlyPayment = amount / termMonths
                return Result.success(
                    LoanSimulation(
                        monthlyPayment = monthlyPayment,
                        totalToPay = amount,
                        totalInterest = 0.0,
                        effectiveAnnualRate = 0.0
                    )
                )
            }

            // Convertir tasa anual a tasa mensual decimal
            val monthlyRate = annualRate / 12.0 / 100.0

            // Aplicar fórmula del sistema francés
            val numerator = monthlyRate * (1 + monthlyRate).pow(termMonths.toDouble())
            val denominator = (1 + monthlyRate).pow(termMonths.toDouble()) - 1
            val monthlyPayment = amount * (numerator / denominator)

            // Calcular total a pagar
            val totalToPay = monthlyPayment * termMonths

            // Calcular intereses totales
            val totalInterest = totalToPay - amount

            // Calcular tasa efectiva anual (TEA)
            val effectiveAnnualRate = ((1 + monthlyRate).pow(12.0) - 1) * 100

            Result.success(
                LoanSimulation(
                    monthlyPayment = roundToTwoDecimals(monthlyPayment),
                    totalToPay = roundToTwoDecimals(totalToPay),
                    totalInterest = roundToTwoDecimals(totalInterest),
                    effectiveAnnualRate = roundToTwoDecimals(effectiveAnnualRate)
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Redondea un número a 2 decimales.
     */
    private fun roundToTwoDecimals(value: Double): Double {
        return kotlin.math.round(value * 100) / 100
    }
}