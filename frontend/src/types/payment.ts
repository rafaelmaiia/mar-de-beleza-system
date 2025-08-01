import type { Appointment } from './appointment';

export type PaymentMethod = 'PIX' | 'CREDIT_CARD' | 'DEBIT_CARD' | 'CASH';
export type PaymentStatus = 'PAID' | 'CANCELED';

export type Payment = {
  id: number;
  appointment: Appointment;
  totalAmount: number;
  paymentDate: string; // ex: "2025-07-30T21:45:00"
  paymentMethod: PaymentMethod;
  status: PaymentStatus;
  observations?: string;
};