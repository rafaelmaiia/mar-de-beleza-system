import type { Payment } from '../../types/payment';
import { format } from 'date-fns';
import { ptBR } from 'date-fns/locale';
import { paymentMethodTranslations } from '../../constants/paymentConstants';

type PaymentCardProps = {
  payment: Payment;
  isAdmin: boolean;
  onEdit: () => void;
  onCancel: () => void;
};

export function PaymentCard({ payment, isAdmin, onEdit, onCancel }: PaymentCardProps) {
  const isCanceled = payment.status === 'CANCELED';

  const translatedPaymentMethod = paymentMethodTranslations[payment.paymentMethod] || payment.paymentMethod;

  return (
    <div className={`bg-white p-4 rounded-lg shadow-md ${isCanceled ? 'opacity-60 bg-gray-100' : ''}`}>
      <div className="grid grid-cols-2 gap-4">
        <div>
          <p className="text-sm text-gray-500">{format(new Date(payment.paymentDate), 'dd/MM/yyyy')}</p>
          <p className={`font-bold text-lg text-gray-800 ${isCanceled ? 'line-through' : ''}`}>{payment.appointment.client.name}</p>
          <p className={`text-sm text-gray-600 ${isCanceled ? 'line-through' : ''}`}>Profissional: {payment.appointment.professional.name}</p>
        </div>
        <div className="text-right">
          <p className={`font-semibold text-lg ${isCanceled ? 'line-through text-gray-500' : 'text-green-600'}`}>
            {payment.totalAmount.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })}
          </p>
          {/* Forma de pagamento e o badge de status */}
          {!isCanceled ? (
            <p className="text-sm text-gray-500">{translatedPaymentMethod}</p>
          ) : (
            <span className="text-xs font-semibold inline-block px-2 py-1 bg-red-100 text-red-800 rounded-full mt-1">Cancelado</span>
          )}
        </div>
      </div>
      
      {/* Botões só aparecem se o pagamento NÃO estiver cancelado e o usuário for ADMIN */}
      {!isCanceled && isAdmin && (
          <div className="border-t mt-4 pt-3 flex justify-end gap-2">
              <button onClick={onEdit} className="text-sm font-medium text-blue-600 hover:bg-blue-50 px-3 py-1 rounded-md">Editar</button>
              <button onClick={onCancel} className="text-sm font-medium text-red-600 hover:bg-red-50 px-3 py-1 rounded-md">Cancelar Pagamento</button>
          </div>
      )}
    </div>
  );
}