// src/types/appointment.ts
import type { Professional } from './professional';
import type { Client } from './client';
import type { SalonService } from './salonService';

export type Appointment = {
  id: number;
  appointmentDate: string;
  status: string;
  observations?: string;
  price: number;
  client: Client;
  professional: Professional;
  service: SalonService;
};