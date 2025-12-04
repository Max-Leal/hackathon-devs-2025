import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { DataService } from '../../shared/loading/data.service';
import { LoadingComponent } from '../../shared/loading/loading';

@Component({
  selector: 'app-simulation',
  standalone: true,
  imports: [CommonModule, FormsModule, LoadingComponent],
  templateUrl: './simulation.html',
  styleUrls: ['./simulation.css']
})
export class SimulationComponent implements OnInit {
  financialData = {
    age: null,
    monthlyIncome: null,
    profession: '',
    dependents: null
  };

  isLoading = false;
  loadingPhrases = [
    "Conectando ao servidor...",
    "Analisando Perfil...",
    "Consultando Serasa...",
    "Calculando Risco..."
  ];

  constructor(private router: Router, private data: DataService) {}

  ngOnInit() {
    if (this.data.financial.monthlyIncome) {
      this.financialData = { ...this.data.financial } as any;
    }
  }

  onSubmit() {
    if (!this.financialData.age || !this.financialData.monthlyIncome) {
      alert('Idade e Renda são obrigatórios.');
      return;
    }
    this.data.setFinancialData(this.financialData as any);
    this.isLoading = true;
  }

  onLoadingComplete() {
    const customerId = this.data.getCustomerId();
    if (!customerId) {
      this.isLoading = false;
      alert('Você precisa estar logado para simular.');
      this.router.navigate(['/signin']);
      return;
    }

    this.data.sendAnalysis(customerId).subscribe({
      next: (response: any) => {
        this.data.analysisResult = response;
        this.isLoading = false;
        this.router.navigate(['/dashboard']);
      },
      error: (error: any) => {
        console.error("Erro na API:", error);
        this.isLoading = false;
        alert("Erro ao conectar com o servidor. Verifique se o Backend está rodando.");
      }
    });
  }
}
