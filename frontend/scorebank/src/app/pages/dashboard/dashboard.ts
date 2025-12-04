import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { DataService } from '../../shared/loading/data.service';

// Interface atualizada conforme sua imagem
interface BackendResponse {
  customerId: number;
  score: number;        // Ex: 60
  riskTier: string;     // Ex: "MEDIUM"
  approvedLimit: number; // Ex: 15896.27
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

  dashboardData = {
    trustLevel: 0, 
    trustLabel: '...',
    creditLimit: 0,
    limitIncrease: 0,
    score: 0, 
    scoreMax: 100, // Régua visual vai até 100
    scoreClassification: '...'
  };

  financialHistory: any[] = [];

  constructor(private router: Router, private dataService: DataService) {}

  ngOnInit(): void {
    this.user.fullName = this.dataService.user.fullName || 'Visitante';

    const result = this.dataService.analysisResult;

    if (result) {
      this.updateDashboardFromResponse(result);
      this.setupHistory();
    } else {
      this.router.navigate(['/signin']);
    }
  }

  recalculate(): void {
    this.router.navigate(['/simulation']);
  }

  logout(): void {
    this.router.navigate(['/signin']);
  }

  updateDashboardFromResponse(data: BackendResponse): void {
    // Mapeia o Limite
    this.dashboardData.creditLimit = data.approvedLimit;
    
    // Mapeia o Score (Backend 0-100 -> Visual 0-100)
    // Se o backend manda 60, mostramos 600 na régua para ficar no meio
    this.dashboardData.score = data.score; 
    
    // Mapeia o Nível de Confiança (Backend 0-100 -> Círculo 0-100)
    // Usamos o valor original (60)
    this.dashboardData.trustLevel = data.score;

    // Traduz o RiskTier para Português
    this.dashboardData.scoreClassification = this.translateRisk(data.riskTier);
    this.dashboardData.trustLabel = this.dashboardData.scoreClassification;
    
    // Simulação visual de aumento
    this.dashboardData.limitIncrease = data.approvedLimit * 0.10; 
  }

  private translateRisk(tier: string): string {
    if (tier === 'LOW') return 'Excelente';
    if (tier === 'MEDIUM') return 'Bom';
    return 'Alto Risco';
  }

  private setupHistory() {
    this.financialHistory = [
      {
        title: 'Análise Concluída',
        date: 'Hoje',
        description: 'Perfil analisado com sucesso.',
        type: 'positive', 
        tags: [{ label: 'Limite Definido', color: 'green' }]
      }
    ];
  }
}