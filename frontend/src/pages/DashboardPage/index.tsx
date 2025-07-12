import { AppointmentCard } from '../../components/AppointmentCard';
import { Header } from '../../components/Header';

// Dados falsos para simular a tela completa
const DUMMY_LOGGED_IN_USER = { name: 'Ana Paula' };

const dummyAppointments = [
    { id: 1, time: '11:30', clientName: 'Juliana Lima', professionalName: 'Ana Paula', services: 'Manicure', status: 'CONFIRMED' },
    { id: 2, time: '14:00', clientName: 'Fernanda Souza', professionalName: 'Carla', services: 'Sobrancelha', status: 'DONE' },
    { id: 3, time: '15:30', clientName: 'Beatriz Almeida', professionalName: 'Ana Paula', services: 'Progressiva', status: 'CANCELED' },
    { id: 4, time: '17:00', clientName: 'Laura Martins', professionalName: 'Ana Paula', services: 'Corte', status: 'SCHEDULED' },
    { id: 5, time: '18:00', clientName: 'Ana Clara', professionalName: 'Beatriz Costa', services: 'Cílios', status: 'NO_SHOW' },
];

const nextAppointment = dummyAppointments.find(app => app.status !== 'Cancelado' && app.status !== 'Concluído');

export function DashboardPage() {
    return (
        <div className="bg-gray-50 min-h-screen">
            <Header title="Home" />

            <main className="pt-20 pb-8 px-4 max-w-4xl mx-auto">
                <div className="mb-6">
                    <p className="text-xl text-gray-700">Bem-vinda, <span className="font-semibold">{DUMMY_LOGGED_IN_USER.name}</span></p>
                </div>

                {nextAppointment && (
                    <div className="mb-8">
                        <h2 className="text-lg font-semibold text-gray-800 mb-3">Próximo agendamento</h2>
                        <div className="bg-gradient-to-r from-purple-500 to-indigo-600 text-white p-5 rounded-xl shadow-lg">
                            <div className="flex justify-between items-start">
                                <div>
                                    <p className="text-2xl font-bold">{nextAppointment.clientName}</p>
                                    <p className="text-sm opacity-90">{nextAppointment.services}</p>
                                </div>
                                <div className="text-right">
                                    <p className="text-3xl font-bold">{nextAppointment.time}</p>
                                    <p className="text-xs uppercase tracking-wider opacity-80">Hoje</p>
                                </div>
                            </div>
                        </div>
                    </div>
                )}

                <div className="flex justify-between items-center mb-4">
                    <h2 className="text-lg font-semibold text-gray-800">Agenda do dia</h2>
                </div>

                <div className="space-y-4">
                    {dummyAppointments.map(app => (
                        <AppointmentCard
                            key={app.id}
                            time={app.time}
                            clientName={app.clientName}
                            professionalName={app.professionalName}
                            services={app.services}
                            status={app.status}
                            observations={"Cliente pediu para usar o esmalte novo."}
                        />
                    ))}
                </div>
            </main>
        </div>
    );
}