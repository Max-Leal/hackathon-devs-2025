import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { DataService } from '../../services/data.service';
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

  constructor(private router: Router, private dataService: DataService) {}

  ngOnInit() {
    if (this.dataService.financial.monthlyIncome) {
      this.financialData = { ...this.dataService.financial } as any;
    }
  }

  onSubmit() {
    if (!this.financialData.age || !this.financialData.monthlyIncome) {
      alert('Idade e Renda são obrigatórios.');
      return;
    }

    // Salva os dados no service
    this.dataService.setFinancialData(this.financialData as any);
    
    // Ativa o Loading
    this.isLoading = true;
  }

  // === AQUI É A MUDANÇA CRÍTICA ===
  onLoadingComplete() {
    console.log("Iniciando requisição HTTP...");

    // A CORREÇÃO ESTÁ ABAIXO (adicionado : any)
    this.dataService.sendAnalysis().subscribe({
      
      next: (response: any) => { // <--- Adicione : any aqui
        console.log("Sucesso! Resposta:", response);
        this.dataService.analysisResult = response;
        this.isLoading = false;
        this.router.navigate(['/dashboard']);
      },

      error: (error: any) => { // <--- Adicione : any aqui
        console.error("Erro na API:", error);
        this.isLoading = false;
        alert("Erro ao conectar com o servidor. Verifique se o Backend está rodando.");
      }
    });
  }
}