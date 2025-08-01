import { useState, useEffect, useMemo } from 'react';
import toast from 'react-hot-toast';
import { useAuth } from '../../hooks/useAuth';
import type { Payment } from '../../types/payment';
import type { SystemUser } from '../../types/user';
import { Header } from '../../components/Header';
import { PaymentCard } from '../../components/PaymentCard';
import { PaymentModal } from '../../components/PaymentModal';
import Select from 'react-select';

// Funções utilitárias
const formatDate = (date: Date): string => date.toISOString().split('T')[0];

const getWeekRange = (): { start: string, end: string } => {
  const today = new Date();
  const day = today.getDay();
  const diffToMonday = today.getDate() - day + (day === 0 ? -6 : 1);
  const monday = new Date(today.setDate(diffToMonday));
  const sunday = new Date(monday);
  sunday.setDate(monday.getDate() + 6);
  return { start: formatDate(monday), end: formatDate(sunday) };
};

export function FinancialPage() {
  const { token, user } = useAuth();
  const [payments, setPayments] = useState<Payment[]>([]);
  const [isLoading, setIsLoading] = useState(true);

  // Estados dos filtros
  const initialRange = getWeekRange();

  // Estado para os filtros que o usuário está editando
  const [filters, setFilters] = useState({
    startDate: initialRange.start,
    endDate: initialRange.end,
    professionalId: '',
  });
  // Estado para os filtros que foram de fato APLICADOS
  const [activeFilters, setActiveFilters] = useState(filters);
  
  const [professionals, setProfessionals] = useState<{ value: string, label: string }[]>([]);
  const [refreshTrigger, setRefreshTrigger] = useState(0); // Gatilho para recarregar

  // Estado para o modal de edição
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [paymentToEdit, setPaymentToEdit] = useState<Payment | null>(null);

  const handleOpenEditModal = (payment: Payment) => {
    setPaymentToEdit(payment);
    setIsModalOpen(true);
  };

  const handleRefresh = () => {
    setRefreshTrigger(prev => prev + 1);
  };

  const handleSaveSuccess = () => {
    setIsModalOpen(false);
    handleRefresh();
  };

  useEffect(() => {
    if (!token) return;
    const headers = { 'Authorization': `Bearer ${token}` };

    // Busca profissionais para o filtro
    fetch('http://localhost:8080/api/v1/users?canBeScheduled=true&size=100', { headers })
      .then(res => res.json())
      .then(data => setProfessionals((data.content || data || []).map((p: SystemUser) => ({ value: String(p.id), label: p.name }))));

    const fetchPayments = async () => {
      setIsLoading(true);
      // Usa o 'activeFilters' para a busca
      const params = new URLSearchParams({
        startDate: activeFilters.startDate,
        endDate: activeFilters.endDate,
        sort: 'paymentDate,desc',
      });

      let professionalIdToFilter = activeFilters.professionalId;
      if (user?.role === 'STAFF') {
        professionalIdToFilter = user.id.toString();
      }
      if (professionalIdToFilter) {
        params.append('professionalId', professionalIdToFilter);
      }

      try {
        const response = await fetch(`http://localhost:8080/api/v1/payments?${params.toString()}`, { headers });
        if (!response.ok) throw new Error('Falha ao buscar pagamentos.');
        const data = await response.json();

        setPayments(data.content || []);
      } catch (error: any) { toast.error(error.message); }
      finally { setIsLoading(false); }
    };
    fetchPayments();
  }, [token, user, activeFilters, refreshTrigger]); // Depende dos filtros ATIVOS e do gatilho

  const totalAmount = useMemo(() => payments.reduce((sum, p) => p.status === 'PAID' ? sum + p.totalAmount : sum, 0), [payments]);
  const paidPaymentsCount = useMemo(() => payments.filter(p => p.status === 'PAID').length, [payments]);
  
  const handleFilterSubmit = () => setActiveFilters(filters);
  const handleCancelPayment = async (paymentId: number) => {
    if (window.confirm('Tem certeza que deseja cancelar este pagamento?')) {
        try {
            const response = await fetch(`http://localhost:8080/api/v1/payments/${paymentId}/cancel`, {
                method: 'PATCH',
                headers: { 'Authorization': `Bearer ${token}` },
            });
            if (!response.ok) throw new Error('Falha ao cancelar pagamento.');
            toast.success('Pagamento cancelado com sucesso!');
            setRefreshTrigger(prev => prev + 1); // Dispara o refresh
        } catch (error: any) { toast.error(error.message); }
    }
  };

  // --- INÍCIO DAS FUNÇÕES DE COPIA E EXPORTAÇÃO ---
  // Função para copiar os dados para a área de transferência
  const copyToClipboard = () => {
        const header = "Cliente\tProfissional\tData\tValor\tForma de Pagamento\n";
        const rows = payments
            .filter(p => p.status === 'PAID') // Garante que só copie os pagos
            .map(p => 
                `${p.appointment.client.name}\t${p.appointment.professional.name}\t${new Date(p.paymentDate).toLocaleDateString('pt-BR')}\t${p.totalAmount.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })}\t${p.paymentMethod}`
            ).join('\n');
        
        navigator.clipboard.writeText(header + rows).then(() => {
            toast.success('Dados copiados para a área de transferência!');
        });
    };

  // Função para exportar os dados para CSV
    const exportToCSV = () => {
        const header = "Cliente,Profissional,Data,Valor,Forma de Pagamento\n";
        const rows = payments
            .filter(p => p.status === 'PAID') // Garante que só exporte os pagos
            .map(p => `"${p.appointment.client.name}","${p.appointment.professional.name}","${new Date(p.paymentDate).toLocaleDateString('pt-BR')}","${p.totalAmount.toFixed(2)}","${p.paymentMethod}"`).join('\n');
    
    const csvContent = "data:text/csv;charset=utf-8," + encodeURIComponent(header + rows);
    const link = document.createElement("a");
    link.setAttribute("href", csvContent);
    link.setAttribute("download", `pagamentos_${filters.startDate}_a_${filters.endDate}.csv`);
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  };
  // --- FIM DAS FUNÇÕES ---
  
  return (
    <div className="bg-gray-50 min-h-screen">
      <Header title="Financeiro" />

      <main className="pt-20 pb-24 px-4 max-w-4xl mx-auto">
        {/* Painel de Filtros */}
        <div className="bg-white p-4 rounded-lg shadow-md mb-6">
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <div>
                    <label className="block text-sm font-medium text-gray-700">Data de Início</label>
                    <input type="date" 
                        value={filters.startDate} 
                        onChange={e => setFilters(f => ({ ...f, startDate: e.target.value }))} 
                        className="mt-1 block w-full border-gray-300 rounded-md shadow-sm"
                    />
                </div>
                <div>
                    <label className="block text-sm font-medium text-gray-700">Data Final</label>
                    <input type="date" 
                        value={filters.endDate} 
                        onChange={e => setFilters(f => ({ ...f, endDate: e.target.value }))} 
                        className="mt-1 block w-full border-gray-300 rounded-md shadow-sm"
                    />
                </div>
                {user?.role === 'ADMIN' && (
                    <div className="sm:col-span-2">
                        <label className="block text-sm font-medium text-gray-700">Profissional</label>
                        <Select
                            options={[{ value: '', label: 'Todas' }, ...professionals]}
                            onChange={(option) => setFilters(f => ({ ...f, professionalId: option?.value || ''}))}
                            value={professionals.find(p => p.value === filters.professionalId) || null}
                            placeholder="Todas as profissionais"
                            className="mt-1"
                        />
                    </div>
                )}
            </div>
            <div className="mt-4 flex justify-end">
                <button onClick={handleFilterSubmit} disabled={isLoading} className="bg-indigo-600 text-white px-4 py-2 rounded-md hover:bg-indigo-700 text-sm font-medium disabled:bg-indigo-400">
                    {isLoading ? 'Filtrando...' : 'Filtrar'}
                </button>
            </div>
        </div>
        
        {/* Resumo do Período (Pagos) */}
        <div className="bg-white p-4 rounded-lg shadow-md mb-6">
          <h2 className="text-lg font-semibold text-gray-800">Resumo do Período (Pagos)</h2>
          <div className="mt-2 flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
            <div>
              <p className="text-gray-600">Total Arrecadado:</p>
              <p className="text-3xl font-bold text-green-600">{totalAmount.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })}</p>
              <p className="text-sm text-gray-500">{paidPaymentsCount} pagamentos encontrados</p>
            </div>
            <div className="flex items-center gap-2">
                <button onClick={copyToClipboard} className="text-sm font-medium text-gray-700 bg-gray-100 hover:bg-gray-200 px-3 py-2 rounded-md">Copiar</button>
                <button onClick={exportToCSV} className="text-sm font-medium text-gray-700 bg-gray-100 hover:bg-gray-200 px-3 py-2 rounded-md">Exportar CSV</button>
            </div>
          </div>
        </div>

        {/* Lista de Pagamentos */}
        <div className="space-y-4">
          {isLoading ? <p>Carregando...</p> : payments.map(payment => (
            <PaymentCard 
              key={payment.id} 
              payment={payment}
              isAdmin={user?.role === 'ADMIN'}
              onEdit={() => handleOpenEditModal(payment)}
              onCancel={() => handleCancelPayment(payment.id)}
            />
          ))}
        </div>
      </main>
      <PaymentModal
        isOpen={isModalOpen}
        onRequestClose={() => setIsModalOpen(false)}
        onSaveSuccess={handleSaveSuccess}
        paymentToEdit={paymentToEdit}
      />
    </div>
  );
}