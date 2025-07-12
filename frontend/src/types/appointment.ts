// src/types/appointment.ts
import type { Professional } from './professional';
import type { Client } from './client';
import type { SalonService } from './salonService';

type AppointmentItem = {
  id: number;
  service: SalonService;
  professional: Professional;
  price: number;
};

export type Appointment = {
  id: number;
  appointmentDate: string;
  status: string;
  observations?: string;
  client: Client;
  items: AppointmentItem[];
};