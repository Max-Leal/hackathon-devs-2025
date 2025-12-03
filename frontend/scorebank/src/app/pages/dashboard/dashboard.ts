import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

//Como o JSON vem do Backend
interface CreditAnalysisResponse {
  analysisId: number;
  approved: boolean;
  score: number;
  approvedLimit: number;
  approvedInterestRate: number;
  maxInstallments: number;
  message: string;
  date: string;
}

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard.html',
  styleUrls: ['./dashboard.css']
})
export class DashboardComponent implements OnInit {

  user = {
    name: '' // Futuramente virá do login
  };

  // Dados visuais do Dashboard (Inicializados vazios ou zerados)
  dashboardData = {
    trustLevel: 0, 
    trustLabel: 'Calculando...',
    creditLimit: 0,
    limitIncrease: 0, // Não vem no JSON atual, manteremos zerado ou simulado
    score: 0, 
    scoreMax: 100, // IMPORTANTE: Ajustado para 100 conforme regra do PDF
    scoreClassification: '...'
  };

  // Histórico (Mantido estático por enquanto, pois o JSON não trouxe lista)
  financialHistory = [
    {
      title: 'Análise de Crédito Recente',
      date: 'Hoje',
      description: 'Sua solicitação foi processada com sucesso.',
      type: 'positive', 
      tags: [
        { label: 'Score Atualizado', color: 'blue' },
        { label: 'Proposta Gerada', color: 'green' }
      ]
    }
    
  ];

  constructor(private router: Router) {}

  ngOnInit(): void {
    // SIMULAÇÃO: Aqui é onde vamos receber os dados do Service/API
    // Vamos simular que o Backend respondeu exatamente o JSON recebeu
    const mockBackendResponse: CreditAnalysisResponse = {
      "analysisId": 1234,
      "approved": true,
      "score": 95,
      "approvedLimit": 50000.00,
      "approvedInterestRate": 0.0175,
      "maxInstallments": 48,
      "message": "Parabéns, seu perfil é EXCELENTE! Liberamos a nossa melhor taxa.",
      "date": "2025-12-02T14:15:53.385"
    };

    // Processa os dados recebidos
    this.updateDashboardFromResponse(mockBackendResponse);
  }

  // Transforma o JSON do Backend nos dados visuais
  updateDashboardFromResponse(data: CreditAnalysisResponse): void {
    
    // Mapeia o Score (0-100)
    this.dashboardData.score = data.score;
    
    // O Nível de Confiança visual será igual ao Score
    this.dashboardData.trustLevel = data.score;

    // Define a classificação (Baseada na tabela do PDF ou na mensagem)
    // Como a mensagem já diz "EXCELENTE", podemos derivar do score também:
    this.dashboardData.scoreClassification = this.getClassification(data.score);
    this.dashboardData.trustLabel = this.dashboardData.scoreClassification;

    // Mapeia o Limite
    this.dashboardData.creditLimit = data.approvedLimit;
  }

  // Helper para classificar o texto baseado na regra do PDF
  private getClassification(score: number): string {
    if (score >= 90) return 'Excelente';
    if (score >= 70) return 'Bom';
    if (score >= 55) return 'Médio';
    if (score >= 30) return 'Alto Risco';
    return 'Péssimo';
  }

  logout(): void {
    this.router.navigate(['/signin']);
  }
}