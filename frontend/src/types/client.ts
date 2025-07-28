import type { Contact } from './contact';

export type Client = {
  id: number;
  name: string;
  birthDate: string;
  gender: string;
  contact: Contact;
};