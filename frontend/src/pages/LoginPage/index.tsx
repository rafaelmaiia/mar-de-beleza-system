import React, { useState } from 'react';
import { useForm, SubmitHandler } from 'react-hook-form';
import { useAuth } from '../../hooks/useAuth';
import type { AuthRequest } from '../../dtos/AuthRequest';
import logoImage from '../../assets/logo.png';

export function LoginPage() {
    const { register, handleSubmit, formState: { errors } } = useForm<AuthRequest>();
    const { login } = useAuth();
    const [apiError, setApiError] = useState<string | null>(null);

    const handleLogin: SubmitHandler<AuthRequest> = async (data) => {
        try {
        await login(data);
        } catch (err: any) {
        setApiError(err.message);
        }
    };

    return (
        <div className="flex items-center justify-center min-h-screen bg-gray-50 px-4">
            <div className="w-full max-w-md">
                <div className="bg-white p-8 rounded-2xl shadow-lg">
                    <div className="flex justify-center mb-6">
                        <img src={logoImage} alt="Logo Mar de Beleza" className="w-24 h-24" />
                    </div>

                    <h2 className="text-2xl font-bold text-center text-gray-800 mb-2">Acesse sua Conta</h2>
                    <p className="text-center text-sm text-gray-500 mb-8">Bem-vinda de volta! Faça o login para continuar.</p>

                    <form onSubmit={handleSubmit(handleLogin)}>
                        <div className="space-y-5">
                            <div>
                                <label htmlFor="email" className="block text-sm font-medium text-gray-700">Email</label>
                                <input
                                    type="email"
                                    id="email"
                                    placeholder="voce@exemplo.com"
                                    className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
                                    {...register("email", { required: "O email é obrigatório" })}
                                />
                                {errors.email && <p className="text-red-500 text-xs mt-1">{errors.email.message}</p>}
                            </div>

                            <div>
                                <label htmlFor="password" className="block text-sm font-medium text-gray-700">Senha</label>
                                <input
                                    type="password"
                                    id="password"
                                    placeholder="Sua senha"
                                    className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm placeholder-gray-400 focus:outline-none focus:ring-indigo-500 focus:border-indigo-500 sm:text-sm"
                                    {...register("password", { required: "A senha é obrigatória" })}
                                />
                                {errors.password && <p className="text-red-500 text-xs mt-1">{errors.password.message}</p>}
                            </div>
                            
                            {/* Mostra erros da API */}
                            {apiError && <p className="text-red-500 text-sm text-center">{apiError}</p>}

                            <div>
                                <button
                                    type="submit"
                                    className="w-full flex justify-center py-3 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500 transition-colors"
                                >
                                    Entrar
                                </button>
                            </div>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    );
}