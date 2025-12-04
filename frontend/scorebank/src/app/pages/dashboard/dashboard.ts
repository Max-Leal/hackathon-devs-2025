import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';

interface CustomerResponse {
  customerId: number;
  score: number;
  riskTier: string;
  approvedLimit: number;
  maxMonthlyInstallment: number;
  maxInstallments: number;
  interestRate: number;
  feedback: string[];
  scoreAudit: Array<{
    attribute: string;
    valueLog: string;
    points: number;
  }>;
}

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard.html',
  styleUrls: ['./dashboard.css']
})
export class DashboardComponent implements OnInit {

  user = { fullName: '' };
  
  isLoading = true;
  error = '';

  dashboardData = {
    trustLevel: 0, 
    trustLabel: '...',
    creditLimit: 0,
    limitIncrease: 0,
    score: 0, 
    scoreMax: 100,
    scoreClassification: '...',
    maxMonthlyInstallment: 0,
    maxInstallments: 0,
    interestRate: 0
  };

  feedback: string[] = [];
  scoreAudit: any[] = [];
  financialHistory: any[] = [];

  constructor(private router: Router, private http: HttpClient) {}

  ngOnInit(): void {
    const userId = localStorage.getItem('userId');
    this.user.fullName = localStorage.getItem('userName') || 'Visitante';

    if (!userId) {
      this.router.navigate(['/signin']);
      return;
    }

    this.loadCustomerData(userId);
  }

  loadCustomerData(customerId: string): void {
    console.log(customerId)
    this.http.get<CustomerResponse>(`http://localhost:8080/api/customers/${customerId}/dashboard`).subscribe({
      next: (data) => {
        console.log(data)
        this.updateDashboardFromResponse(data);
        this.setupHistory(data);
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Erro ao carregar dados:', err);
        this.error = 'Erro ao carregar suas informações.';
        this.isLoading = false;
      }
    });
  }

  updateDashboardFromResponse(data: CustomerResponse): void {
    this.dashboardData.creditLimit = data.approvedLimit;
    this.dashboardData.score = data.score;
    this.dashboardData.trustLevel = data.score;
    this.dashboardData.maxMonthlyInstallment = data.maxMonthlyInstallment;
    this.dashboardData.maxInstallments = data.maxInstallments;
    this.dashboardData.interestRate = data.interestRate * 100;
    
    this.dashboardData.scoreClassification = this.translateRisk(data.riskTier);
    this.dashboardData.trustLabel = this.dashboardData.scoreClassification;
    this.dashboardData.limitIncrease = data.approvedLimit * 0.10;
    
    this.feedback = data.feedback;
    this.scoreAudit = data.scoreAudit;
  }

  private translateRisk(tier: string): string {
    if (tier === 'LOW') return 'Excelente';
    if (tier === 'MEDIUM') return 'Bom';
    return 'Alto Risco';
  }

  private setupHistory(data: CustomerResponse) {
    this.financialHistory = [
      {
        title: 'Análise de Crédito Concluída',
        date: 'Hoje',
        description: `Score calculado: ${data.score} pontos. Limite aprovado de ${this.formatCurrency(data.approvedLimit)}.`,
        type: 'positive',
        tags: [
          { label: `Score: ${data.score}`, color: 'purple' },
          { label: this.translateRisk(data.riskTier), color: this.getRiskColor(data.riskTier) }
        ]
      }
    ];

    // Adiciona eventos baseados no feedback
    if (data.feedback.length > 0) {
      data.feedback.forEach((item, index) => {
        const isPositive = item.startsWith('✅');
        this.financialHistory.push({
          title: isPositive ? 'Ponto Positivo' : 'Atenção',
          date: 'Hoje',
          description: item.substring(2).trim(),
          type: isPositive ? 'positive' : 'negative',
          tags: [{ 
            label: isPositive ? 'Favorável' : 'Ajuste Necessário', 
            color: isPositive ? 'green' : 'blue' 
          }]
        });
      });
    }
  }

  private getRiskColor(tier: string): string {
    if (tier === 'LOW') return 'green';
    if (tier === 'MEDIUM') return 'blue';
    return 'purple';
  }

  recalculate(): void {
    this.router.navigate(['/simulation']);
  }

  logout(): void {
    localStorage.removeItem('userId');
    localStorage.removeItem('userName');
    localStorage.removeItem('userEmail');
    this.router.navigate(['/signin']);
  }

  formatCurrency(value: number): string {
    return new Intl.NumberFormat('pt-BR', {
      style: 'currency',
      currency: 'BRL'
    }).format(value);
  }

  getPointsColor(points: number): string {
    if (points > 0) return 'text-green-600';
    if (points < 0) return 'text-red-600';
    return 'text-gray-600';
  }
}