import type { Contact } from './contact';

export type SystemUser = {
  id: number;
  name: string;
  email: string;
  role: string;
  contact: Contact;
  specialties: string[];
};